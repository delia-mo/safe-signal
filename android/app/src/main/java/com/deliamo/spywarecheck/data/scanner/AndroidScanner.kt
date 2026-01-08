package com.deliamo.spywarecheck.data.scanner

import android.app.AppOpsManager
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
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
        val deviceAdminApps = getActiveDeviceAdminApps(pm)
        if (deviceAdminApps.isNotEmpty()) {
            findings += ScanFinding(
                id = "device_admin_enabled",
                title = "Geräteadministrator-Rechte sind aktiv",
                summary = "Apps mit Geräteadministrator-Rechten können schwer zu entfernen sein.",//TODO überarbeiten
                severity = Severity.HIGH,
                affectedApps = deviceAdminApps
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
        val backgroundLocationApps = getAppsWithBackgroundLocation(pm)
        if (backgroundLocationApps.isNotEmpty()) {
            findings += ScanFinding(
                id = "background_location_apps",
                title = "Apps mit dauerhaftem Standortzugriff",
                summary = "Diese Apps dürfen auch im Hintergrund auf deinen Standort zugreifen.",
                severity = Severity.MEDIUM,
                affectedApps = backgroundLocationApps
            )
        }

        // Suspicious app names
        val suspiciousNameApps = getSuspiciousNameApps(pm)
        if (suspiciousNameApps.isNotEmpty()) {
            findings += ScanFinding(
                id = "suspicious_app_names",
                title = "Apps mit ungewöhnlichen Namen",
                summary = "Einige Apps wirken vom Namen her auffällig. Das ist kein Beweis. Prüfe, ob du sie kennst.",
                severity = Severity.LOW,
                affectedApps = suspiciousNameApps
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
            services.map { cn -> resolveDeviceAdminLabel(pm, cn) }
                .distinct()
                .sorted()
        } catch (_: Throwable) {
            emptyList()
        }
    }

    private fun getActiveDeviceAdminApps(pm: PackageManager): List<String> {
        return try {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val admins = dpm.activeAdmins ?: return emptyList()

            admins.map { cn -> resolveDeviceAdminLabel(pm, cn) }
                .distinct()
                .sorted()
        } catch (_: Throwable) {
            emptyList()
        }
    }

    private fun resolveDeviceAdminLabel(pm: PackageManager, cn: ComponentName): String {
        val receiverLabel = try {
            val ri = pm.getReceiverInfo(cn, PackageManager.GET_META_DATA)
            ri.loadLabel(pm)?.toString()
        } catch (_: Throwable) {
            null
        }

        val appLabel = appLabel(pm, cn.packageName)

        // For multiple admins belonging to the same app/package
        return when {
            !receiverLabel.isNullOrBlank() && receiverLabel != appLabel ->
                "$receiverLabel ($appLabel)"

            !receiverLabel.isNullOrBlank() ->
                "$receiverLabel (${cn.packageName})"

            !appLabel.isNullOrBlank() ->
                "$appLabel (${cn.packageName}"

            else ->
                cn.flattenToString()
        }
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

    private fun getAppsWithBackgroundLocation(pm: PackageManager): List<String> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return emptyList()
        return try {
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

                collected += AppRef(packageName = pkg, label = label, isSystem = isSystem)
            }
            val filtered = filterAppsForDisplay(collected)
            return filtered
                .map { it.label }
                .distinct()
                .sorted()
        } catch (_: Throwable) {
            emptyList()
        }
    }

    private fun filterAppsForDisplay(apps: List<AppRef>): List<AppRef> {
        return apps.filter { app ->
            if (!app.isSystem) return@filter true
            RiskySystemPolicy.shouldShowSystemApp(app.packageName, app.label)
        }
    }


    private fun getSuspiciousNameApps(pm: PackageManager): List<String> {
        val keywords = listOf(
            "update", "service", "sync", "system", "device", "helper", "support", "security"
        )

        return try {
            val packages = getInstalledPackagesWithPermissions(pm)
            val result = mutableListOf<String>()

            for (pi in packages) {
                val pkg = pi.packageName
                if (isSystemApp(pi.applicationInfo)) continue

                val label = appLabel(pm, pkg) ?: continue
                val lower = label.lowercase()

                val looksGeneric = keywords.any { kw -> lower.contains(kw) }
                val veryShort = label.length <= 3

                if (looksGeneric || veryShort) {
                    result += label
                }
            }

            result.distinct().sorted()
        } catch (_: Throwable) {
            emptyList()
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