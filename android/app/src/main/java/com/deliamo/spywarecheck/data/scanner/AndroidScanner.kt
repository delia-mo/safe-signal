package com.deliamo.spywarecheck.data.scanner

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import com.deliamo.spywarecheck.domain.model.ScanFinding
import com.deliamo.spywarecheck.domain.model.ScanResult
import com.deliamo.spywarecheck.domain.model.Severity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.Manifest
import com.deliamo.spywarecheck.domain.model.AppRef

class AndroidScanner(
    private val context: Context
) {
    suspend fun run(): ScanResult = withContext(Dispatchers.Default) {
        val findings = mutableListOf<ScanFinding>()
        val pm = context.packageManager

        // Accessibility enabled + apps
        val accessibilityApps = getEnabledAccessibilityApps(pm)
        if (accessibilityApps.isNotEmpty()) {
            findings += ScanFinding(
                id = "accessibility_enabled",
                title = "Bedienungshilfen sind aktiv",
                summary = "Apps mit Bedienungshilfen-Zugriff können Eingaben steuern oder Inhalte mitlesen.", //TODO überarbeiten
                severity = Severity.HIGH,
                affectedApps = accessibilityApps
            )
        }

        // Device admin enabled + apps
        val deviceAdminApps = getActiveDeviceAdminLabels(pm)
        if (deviceAdminApps.isNotEmpty()) {
            findings += ScanFinding(
                id = "device_admin_enabled",
                title = "Geräteadministrator-Rechte sind aktiv",
                summary = "Apps mit Geräteadministrator-Rechten können schwer zu entfernen sein.",//TODO überarbeiten
                severity = Severity.HIGH,
                affectedApps = deviceAdminApps,
                affectedPackages = getActiveDeviceAdminPackages(pm)
            )
        }

        // Location enabled
        val locationEnabled = isLocationEnabled()
        if (locationEnabled) {
            findings += ScanFinding(
                id = "location_enabled",
                title = "Standort ist eingeschaltet",
                summary = "Wenn dein Standort aktiv ist, können Apps (je nach Erlaubnis) Standortdaten nutzen. ",
                severity = Severity.LOW
            )
        }

        // Apps with background location access
        val backgroundLocationApps = getAppsWithBackgroundLocationLabels(pm)
        if (backgroundLocationApps.isNotEmpty()) {
            findings += ScanFinding(
                id = "background_location_apps",
                title = "Apps mit dauerhaftem Standortzugriff",
                summary = "Diese Apps dürfen auch im Hintergrund auf deinen Standort zugreifen.",
                severity = Severity.MEDIUM,
                affectedApps = backgroundLocationApps,
                affectedPackages = getAppsWithBackgroundLocationPackages(pm)
            )
        }

        // Root detection
        val rootedSignal = ScannerServices.rootDetector().detect()
        val rootManagers = RootManagerAppCheck.findInstalledRootManagers(pm)
        if (rootManagers.isNotEmpty() || rootedSignal.isRootLikely) {
            val severity = when {
                rootManagers.isNotEmpty() && rootedSignal.isRootLikely -> Severity.HIGH
                else -> Severity.MEDIUM
            }

            val reasons = buildList {
                if (rootManagers.isNotEmpty()) add("Root Tools gefunden: ${rootManagers.joinToString()}}.")
                addAll(rootedSignal.reasons)
            }
            findings += ScanFinding(
                id = "root_detected",
                title = "Hinweis: Dein Gerät könnte gerootet sein",
                summary = "Root kann Überwachungs-Apps mehr Möglichkeiten geben. " +
                        "Wenn du Root nicht bewusst eingerichtet hast, ist das ein stärkeres Warnsignal.",
                severity = severity,
                affectedApps = rootManagers,
                details = reasons
            )
        }

        ScanResult(
            timestampMills = System.currentTimeMillis(),
            findings = findings
        )
    }

    private fun getEnabledAccessibilityApps(pm: PackageManager): List<String> {
        return try {
            val enabled = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED,
                0
            ) == 1
            if (!enabled) return emptyList()

            val setting = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return emptyList()

            val services = setting.split(':')
                .mapNotNull { ComponentName.unflattenFromString(it) }

            val refs = services.map { resolveDeviceAdminLabel(pm, it) }
            refs.map { it.displayName() }
                .distinct()
                .sorted()
        } catch (_: Throwable) {
            emptyList()
        }
    }

    private fun getActiveDeviceAdminRefs(pm: PackageManager): List<AppRef> {
        return runCatching {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val admins = dpm.activeAdmins.orEmpty()

            admins
                .map { cn -> resolveDeviceAdminLabel(pm, cn) }
                .sortedBy { it.displayName().lowercase() }
        }.getOrElse { emptyList() }
    }

    private fun getActiveDeviceAdminPackages(pm: PackageManager): List<String> =
        getActiveDeviceAdminRefs(pm).map { it.packageName }

    private fun getActiveDeviceAdminLabels(pm: PackageManager): List<String> =
        getActiveDeviceAdminRefs(pm).map { it.displayName() }

    private fun resolveDeviceAdminLabel(pm: PackageManager, cn: ComponentName): AppRef {
        val receiverLabel = try {
            val ri = pm.getReceiverInfo(cn, PackageManager.GET_META_DATA)
            ri.loadLabel(pm)?.toString()
        } catch (_: Throwable) {
            null
        }

        val appLabel = appLabel(pm, cn.packageName)

        return AppRef(
            packageName = cn.packageName,
            appLabel = appLabel,
            receiverLabel = receiverLabel,
            isSystem = false
        )
    }

    private fun isLocationEnabled(): Boolean {
        return try {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                lm.isLocationEnabled
            } else {
                // For older devices
                val mode =
                    Settings.Secure.getInt(context.contentResolver, Settings.Secure.LOCATION_MODE)
                mode != Settings.Secure.LOCATION_MODE_OFF
            }
        } catch (_: Throwable) {
            false
        }
    }

    private fun getBackgroundLocationRefs(pm: PackageManager): List<AppRef> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return emptyList()
        return runCatching {
            val packages = getInstalledPackagesWithPermissions(pm)
            val collected = mutableListOf<AppRef>()

            for (pi in packages) {
                val ai = pi.applicationInfo ?: continue
                val pkg = pi.packageName
                val label = appLabel(pm, pkg) ?: pkg
                val isSystem = isSystemApp(ai)

                val hasFine = pm.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, pkg) ==
                        PackageManager.PERMISSION_GRANTED
                val hasCoarse =
                    pm.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, pkg) ==
                            PackageManager.PERMISSION_GRANTED

                if (!hasFine && !hasCoarse) continue

                val hasBackground =
                    pm.checkPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION, pkg) ==
                            PackageManager.PERMISSION_GRANTED

                if (!hasBackground) continue

                collected += AppRef(packageName = pkg, appLabel = label, isSystem = isSystem)
            }
            filterAppsForDisplay(collected)
        }.getOrElse { emptyList() }
    }

    private fun getAppsWithBackgroundLocationLabels(pm: PackageManager): List<String> {
        return getBackgroundLocationRefs(pm)
            .map { it.displayName() }
            .distinct()
            .sorted()
    }

    private fun getAppsWithBackgroundLocationPackages(pm: PackageManager): List<String> {
        return getBackgroundLocationRefs(pm)
            .map { it.packageName }
            .distinct()
            .sorted()
    }


    private fun filterAppsForDisplay(apps: List<AppRef>): List<AppRef> {
        return apps.filter { app ->
            if (!app.isSystem) return@filter true
            RiskySystemPolicy.shouldShowSystemApp(app.packageName, app.appLabel)
        }
    }

    private fun getInstalledPackagesWithPermissions(pm: PackageManager): List<PackageInfo> {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getInstalledPackages(
                    PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong())
                )
            } else {
                @Suppress("DEPRECATION")
                pm.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            }
        } catch (_: Throwable) {
            emptyList()
        }
    }

    private fun isSystemApp(ai: ApplicationInfo?): Boolean {
        if (ai == null) return false
        val flags = ai.flags
        val isSystem = (flags and ApplicationInfo.FLAG_SYSTEM) != 0
        val isUpdatedSystem = (flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
        return isSystem || isUpdatedSystem
    }

    private fun appLabel(pm: PackageManager, packageName: String): String? {
        return try {
            val ai = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(ai)?.toString()
        } catch (_: Throwable) {
            null
        }
    }
}