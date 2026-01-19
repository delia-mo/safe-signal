package com.deliamo.spywarecheck.ui.actions

import android.R
import android.R.attr.onClick
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.deliamo.spywarecheck.domain.model.AdminEntry
import com.deliamo.spywarecheck.domain.model.AppCandidate
import com.deliamo.spywarecheck.domain.model.ScanFinding
import com.deliamo.spywarecheck.ui.components.AppScaffold
import com.deliamo.spywarecheck.ui.components.BulletItem
import com.deliamo.spywarecheck.ui.screens.scan.ScanUiState
import com.deliamo.spywarecheck.ui.screens.scan.ScanViewModel
import com.squareup.wire.WireField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

@Composable
fun DeviceAdminRemovalFlowScreen(
    flowId: String,
    step: Int,
    onBack: () -> Unit,
    onQuickExit: () -> Unit,
    onNavigateStep: (Int) -> Unit,
    onFinish: () -> Unit,
    scanVm: ScanViewModel
) {
    val context = LocalContext.current
    val state by scanVm.state.collectAsState()

    // Expectation: flowId == "device_admin_enabled"
    val finding: ScanFinding? = (state as? ScanUiState.Done)
        ?.result
        ?.findings
        ?.firstOrNull { it.id == Uri.decode(flowId) }

    AppScaffold(
        title = "Schritte: Geräteverwaltung",
        onQuickExit = onQuickExit,
        showBack = true,
        onBack = onBack,
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val pkgs: List<String> = finding?.affectedPackages.orEmpty()
            val labels: List<String> = finding?.affectedApps.orEmpty()

            when (step) {
                0 -> Step0_Explain(onNavigateStep)
                1 -> Step1_OpenDeviceAdminSettings(
                    context = context,
                    onNavigateStep = onNavigateStep
                )

                2 -> Step2_OpenAppDetailsAndUninstall(
                    context = context,
                    affectedApps = labels,
                    affectedPackages = pkgs,
                    onNavigateStep = onNavigateStep
                )

                3 -> Step3_Rescan(
                    context = context,
                    scanVm = scanVm,
                    onFinish = onFinish
                )

                else -> {
                    Text("Unbekannter Schritt.", style = MaterialTheme.typography.bodyMedium)
                    Button(
                        onClick = onFinish,
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Zurück") }
                }
            }
        }
    }
}

@Composable
private fun Step0_Explain(onNavigateStep: (Int) -> Unit) {
    Text(
        text = "Schritt 1/4",
        style = MaterialTheme.typography.labelLarge
    )
    Text(
        text = "Was bedeutet das?",
        style = MaterialTheme.typography.titleLarge
    )
    Text(
        text = "Eine App hat Verwaltungsrechte. Das kann das Entfernen erschweren.",
        style = MaterialTheme.typography.bodyMedium
    )

    HorizontalDivider()

    Text(
        text = "Unsere nächsten Schritte:",
        style = MaterialTheme.typography.titleMedium
    )
    BulletItem("1) Deaktiviere zuerst die Verwaltungsrechte der App.")
    BulletItem("2) Öffne danach die App-Infos und deinstalliere die App.")
    BulletItem("3) Starte anschließend erneut den Scan.")

    Spacer(Modifier.height(8.dp))

    Button(onClick = { onNavigateStep(1) }, modifier = Modifier.fillMaxWidth()) { Text("Weiter") }
}

@Composable
private fun Step1_OpenDeviceAdminSettings(
    context: Context,
    onNavigateStep: (Int) -> Unit,
) {
    Text(
        text = "Schritt 2/4",
        style = MaterialTheme.typography.labelLarge
    )
    Text(
        text = "Verwaltungsrechte ausschalten", style = MaterialTheme.typography.titleLarge
    )
    Text(
        text = "Tippe auf \"Einstellungen öffnen\". Suche in der Übersicht nach unbekannten Apps, " +
                "oder Apps, von denen du diese Funktion nicht erwartest. Deaktiviere sie. Dann " +
                "komm zurück in diese App.",
        style = MaterialTheme.typography.bodyMedium
    )

    Spacer(Modifier.height(8.dp))

    Button(
        onClick = { onOpenDeviceAdminSettings(context) },
        modifier = Modifier.fillMaxWidth()
    ) { Text("Einstellungen öffnen") }

    OutlinedButton(
        onClick = { onNavigateStep(2) },
        modifier = Modifier.fillMaxWidth()
    ) { Text("Ich bin zurück – weiter") }
}

data class MatchResult(
    val expectedName: String,
    val candidates: List<AppCandidate>
)

@Composable
private fun Step2_OpenAppDetailsAndUninstall(
    context: Context,
    affectedApps: List<String>,
    affectedPackages: List<String>,
    onNavigateStep: (Int) -> Unit
) {
    Text("Schritt 3/4", style = MaterialTheme.typography.labelLarge)
    Text("App(s) entfernen", style = MaterialTheme.typography.titleLarge)
    Text(
        "Öffne die App-Info der betroffenen App(s). Wenn du sie nicht kennst, stoppe oder deinstalliere sie. Komm danach zurück.",
        style = MaterialTheme.typography.bodyMedium
    )

    HorizontalDivider()

    val pairs: List<Pair<String, String>> =
        if (affectedApps.size == affectedPackages.size && affectedApps.isNotEmpty()) {
            affectedApps.zip(affectedPackages)
        } else {
            affectedApps.map { it to "" }.ifEmpty { affectedPackages.map { it to it } }
        }

    if (pairs.isEmpty()) {
        Text(
            "Keine App-Daten im Scan-Ergebnis verfügbar. Du kannst trotzdem in der App-Übersicht nach der App suchen.",
            style = MaterialTheme.typography.bodySmall
        )
        OutlinedButton(
            onClick = { openManageApps(context) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("App-Übersicht öffnen") }

        Button(onClick = { onNavigateStep(3) }, modifier = Modifier.fillMaxWidth()) { Text("Weiter") }
        return
    }

    val pm = context.packageManager

    // --- Loading state ---
    var isLoading by remember(pairs) { mutableStateOf(true) }
    var results by remember(pairs) { mutableStateOf<List<MatchResult>>(emptyList()) }

    LaunchedEffect(pairs) {
        isLoading = true
        val computed = withContext(Dispatchers.Default) {
            pairs.map { (rawLabel, pkgFromScan) ->
                val expectedName = normalizeLabelForMatch(rawLabel)
                val top = resolveTopPackagesForFinding(pm, expectedName, pkgFromScan, max = 2)
                MatchResult(expectedName = expectedName, candidates = top)
            }
        }
        results = computed
        isLoading = false
    }

    if (isLoading) {
        Text("Einen Moment bitte …", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))
    }

    results.forEach { r ->
        Text("• ${r.expectedName}", style = MaterialTheme.typography.titleSmall)

        Text(
            text = "Hinweis: Deinstalliere die App nur, wenn der Name in den App-Infos genau passt.",
            style = MaterialTheme.typography.bodySmall
        )

        if (r.candidates.isEmpty()) {
            Text(
                text = "Wir konnten die App nicht eindeutig zuordnen. Bitte suche in der App-Übersicht nach „${r.expectedName}“. " +
                        "Wenn du unsicher bist: nichts deinstallieren.",
                style = MaterialTheme.typography.bodySmall
            )
            OutlinedButton(
                onClick = { openManageApps(context) },
                modifier = Modifier.fillMaxWidth()
            ) { Text("App-Übersicht öffnen (bitte suchen)") }
        } else {
            r.candidates.forEach { c ->
                OutlinedButton(
                    onClick = { openAppDetails(context, c.packageName) },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("App-Infos öffnen: ${c.label}") }
            }
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider()
    }

    Button(onClick = { onNavigateStep(3) }, modifier = Modifier.fillMaxWidth()) {
        Text("Weiter")
    }
}



@Composable
private fun Step3_Rescan(
    context: Context,
    scanVm: ScanViewModel,
    onFinish: () -> Unit
) {
    val state by scanVm.state.collectAsState()
    val isRunning = state is ScanUiState.Running
    val isDone = state is ScanUiState.Done

    Text(
        text = "Schritt 4/4",
        style = MaterialTheme.typography.labelLarge
    )
    Text(
        "Erneut prüfen",
        style = MaterialTheme.typography.titleLarge
    )
    Text(
        text = "Starte einen neuen Scan. So siehst du, ob der Hinweis verschwunden ist.",
        style = MaterialTheme.typography.bodyMedium
    )

    Spacer(Modifier.height(8.dp))

    Button(
        onClick = { scanVm.startScan(context = context.applicationContext) },
        enabled = !isRunning,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            if (isRunning) "Scan läuft ..." else "Erneut scannen"
        )
    }

    if (isRunning) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CircularProgressIndicator()
            Text("Bitte kurz warten...", style = MaterialTheme.typography.bodySmall)
        }
    }

    if (isDone) {
        OutlinedButton(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Zurück zum Ergebnis") }
    }

    OutlinedButton(onClick = onFinish, modifier = Modifier.fillMaxWidth()) { Text("Fertig") }
}

private fun onOpenDeviceAdminSettings(context: Context) {
    val candidates = listOf(
        "android.settings.DEVICE_ADMIN_SETTINGS",
        Settings.ACTION_SECURITY_SETTINGS,
        Settings.ACTION_PRIVACY_SETTINGS,
        Settings.ACTION_SETTINGS
    )

    for (action in candidates) {
        try {
            context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        } catch (_: ActivityNotFoundException) {
            //try next
        }
    }
}

private fun openAppDetails(context: Context, packageName: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:$packageName")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

private fun normalizeLabelForMatch(raw: String): String {
    // remove parentheses: "X (Y)" -> "X"
    val noParens = raw.replace(Regex("\\s*\\(.*?\\)\\s*"), " ")
    return noParens.trim()
}

private fun norm(s: String): String =
    s.lowercase()
        .replace(Regex("[^a-z0-9äöüß ]"), " ") // keep letters/numbers/spaces
        .replace(Regex("\\s+"), " ")
        .trim()

private fun tokens(s: String): Set<String> =
    norm(s).split(" ").filter { it.length >= 2 }.toSet()

private fun safeLabelMatch(appLabel: String, expectedName: String): Boolean {
    val a = norm(appLabel)
    val e = norm(expectedName)

    if (a == e) return true

    val at = tokens(a)
    val et = tokens(e)

    // avoid super-generic single-token matches
    if (at.size < 2) return false

    // subset rule
    return et.containsAll(at)
}

private const val TAG_MATCH = "PKG_MATCH"

private fun resolveTopPackagesForFinding(
    pm: PackageManager,
    expectedNameRaw: String,
    pkgFromScan: String,
    max: Int = 2
): List<AppCandidate> {
    val expectedName = normalizeLabelForMatch(expectedNameRaw)
    val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

    fun isSystem(ai: ApplicationInfo): Boolean =
        (ai.flags and ApplicationInfo.FLAG_SYSTEM) != 0

    fun score(label: String, pkg: String, ai: ApplicationInfo): Int {
        if (pkg == "com.google.android.gms") return -1000

        val exact = norm(label) == norm(expectedName)
        val subset = safeLabelMatch(label, expectedName)
        if (!exact && !subset) return -1000

        var s = 0
        if (exact) s += 200 else s += 120
        if (!isSystem(ai)) s += 20

        // ✅ Tie-breaker: prefer candidate whose label shares more tokens with expectedName
        val overlap = tokens(label).intersect(tokens(expectedName)).size
        s += overlap * 3

        return s
    }

    val candidates = apps.mapNotNull { ai ->
        val label = runCatching { pm.getApplicationLabel(ai).toString() }.getOrNull()
            ?: return@mapNotNull null
        val sc = score(label, ai.packageName, ai)
        if (sc < 0) null else AppCandidate(ai.packageName, label, sc, isSystem(ai))
    }.sortedByDescending { it.score }

    // Special: if scan pkg is useful + safe, push it to the front
    val scanCandidate = pkgFromScan.takeIf { it.isNotBlank() && it != "com.google.android.gms" }
    val scanLabel = scanCandidate?.let {
        runCatching {
            val ai = pm.getApplicationInfo(it, 0)
            pm.getApplicationLabel(ai).toString()
        }.getOrNull()
    }
    val scanIsSafe =
        scanCandidate != null && scanLabel != null && safeLabelMatch(scanLabel, expectedName)

    val merged = if (scanIsSafe) {
        val scanApp = AppCandidate(scanCandidate!!, scanLabel!!, 999, isSystem = false)
        listOf(scanApp) + candidates.filterNot { it.packageName == scanCandidate }.take(max - 1)
    } else {
        candidates.take(max)
    }

    return merged
}


private fun appLabelEquals(pm: PackageManager, pkg: String, expectedName: String): Boolean {
    val target = expectedName.trim().lowercase()
    val label = runCatching {
        val ai = pm.getApplicationInfo(pkg, 0)
        pm.getApplicationLabel(ai).toString()
    }.getOrNull() ?: return false

    return label.trim().lowercase() == target
}

private fun openManageApps(context: Context) {
    val candidates = listOf(
        Settings.ACTION_APPLICATION_SETTINGS,
        Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS,
        Settings.ACTION_SETTINGS
    )

    for (action in candidates) {
        try {
            context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        } catch (_: ActivityNotFoundException) {
            // try next
        }
    }
}
