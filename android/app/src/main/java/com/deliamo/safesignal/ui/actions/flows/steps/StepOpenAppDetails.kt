package com.deliamo.safesignal.ui.actions.flows.steps

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliamo.safesignal.domain.model.AppCandidate
import com.deliamo.safesignal.ui.actions.util.normalizeLabelForMatch
import com.deliamo.safesignal.ui.actions.util.resolveTopPackagesForFinding
import com.deliamo.safesignal.ui.platform.openAppDetails
import com.deliamo.safesignal.ui.platform.openManageApps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class MatchResultUi(
    val expectedName: String,
    val candidates: List<AppCandidate>
)

@Composable
fun StepOpenAppDetailsAndUninstall(
    stepLabel: String,
    title: String,
    body: String,
    context: Context,
    affectedApps: List<String>,
    affectedPackages: List<String>,
    onNext: () -> Unit
) {
    Text(stepLabel, style = MaterialTheme.typography.labelLarge)
    Text(title, style = MaterialTheme.typography.titleLarge)
    Text(body, style = MaterialTheme.typography.bodyMedium)

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
        OutlinedButton(onClick = { openManageApps(context) }, modifier = Modifier.fillMaxWidth()) {
            Text("App-Übersicht öffnen")
        }
        Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("Weiter") }
        return
    }

    val pm = context.packageManager

    var isLoading by remember(pairs) { mutableStateOf(true) }
    var results by remember(pairs) { mutableStateOf<List<MatchResultUi>>(emptyList()) }

    LaunchedEffect(pairs) {
        isLoading = true
        val computed = withContext(Dispatchers.Default) {
            pairs.map { (rawLabel, pkgFromScan) ->
                val expectedName = normalizeLabelForMatch(rawLabel)
                val top = resolveTopPackagesForFinding(pm, expectedName, pkgFromScan, max = 2)
                MatchResultUi(expectedName, top)
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
        Text(r.expectedName, style = MaterialTheme.typography.titleSmall)

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

        Spacer(Modifier.height(10.dp))
        HorizontalDivider()
    }

    Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("Weiter") }
}
