package com.deliamo.spywarecheck.ui.screens.actions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliamo.spywarecheck.domain.actions.MeasureItem
import com.deliamo.spywarecheck.domain.model.ScanFinding
import com.deliamo.spywarecheck.ui.actions.registry.NextBestActionRegistry
import com.deliamo.spywarecheck.ui.components.AppScaffold
import com.deliamo.spywarecheck.presentation.scan.ScanUiState
import com.deliamo.spywarecheck.presentation.scan.ScanViewModel
import com.deliamo.spywarecheck.domain.actions.NextBestAction
import com.deliamo.spywarecheck.ui.components.FindingListItem
import com.deliamo.spywarecheck.ui.components.HomeFooterBar


@Composable
fun MeasuresScreen(
    onBack: () -> Unit,
    onQuickExit: () -> Unit,
    onStartScan: () -> Unit,
    onOpenFlow: (flowId: String) -> Unit,
    onHome: () -> Unit,
    scanVm: ScanViewModel
) {
    val state by scanVm.state.collectAsState()

    AppScaffold(
        title = "Offene Maßnahmen",
        onQuickExit = onQuickExit,
        showBack = true,
        onBack = onBack,
        footer = { HomeFooterBar(onHome = onHome) }
    ) { padding: PaddingValues ->

        when (val s = state) {
            is ScanUiState.Idle -> {
                Column(
                    modifier = Modifier.padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Noch kein Scan vorhanden.", style = MaterialTheme.typography.titleMedium)
                    Text("Starte zuerst einen Scan, damit wir passende Maßnahmen anzeigen können.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Button(onClick = onStartScan, modifier = Modifier.fillMaxWidth()) {
                        Text("Scan starten")
                    }
                }
            }

            is ScanUiState.Running -> {
                Column(
                    modifier = Modifier.padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Scan läuft …", style = MaterialTheme.typography.titleMedium)
                    Text("Bitte kurz warten.", style = MaterialTheme.typography.bodyMedium)
                }
            }

            is ScanUiState.Done -> {
                val findings = s.result.findings

                val availableIds = findings.map { it.id }.toSet()

                // actionable + optional de-dup (location_enabled vs background_location_apps)
                val actionableFindings: List<ScanFinding> =
                    findings
                        .filter { NextBestActionRegistry.isActionableFinding(it.id) }
                        .map { f ->
                            val preferredId = NextBestActionRegistry.preferFindingIdForMeasures(f.id, availableIds)
                            if (preferredId == f.id) f else findings.firstOrNull { it.id == preferredId } ?: f
                        }
                        .distinctBy { it.id }

                // Map to MeasureItem
                val measures = actionableFindings
                    .mapNotNull { f ->
                        val action = NextBestActionRegistry.forFindingId(f)
                        val flowId = (action as? NextBestAction.OpenActionFlow)?.flowId ?: return@mapNotNull null
                        MeasureItem(
                            id = f.id,
                            title = f.title,
                            summary = f.summary,
                            severity = f.severity,
                            ctaLabel = action.label,
                            flowId = flowId
                        )
                    }
                    .sortedWith(
                        compareByDescending<MeasureItem> { it.severity }
                            .thenBy { it.title }
                    )

                if (measures.isEmpty()) {
                    Column(
                        modifier = Modifier.padding(padding).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Keine offenen Maßnahmen 🎉", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Im letzten Scan wurden keine behebbare Hinweise gefunden. Du kannst trotzdem erneut scannen.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = onStartScan, modifier = Modifier.fillMaxWidth()) {
                            Text("Erneut scannen")
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("Wir zeigen dir die nächsten Schritte aus dem letzten Scan.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            HorizontalDivider(modifier = Modifier.padding(top = 12.dp))
                        }

                        items(measures, key = { it.id }) { m ->
                            // reuse your FindingListItem UI (nice!)
                            FindingListItem(
                                findings = ScanFinding(
                                    id = m.id,
                                    title = m.title,
                                    summary = m.summary,
                                    severity = m.severity,
                                    affectedApps = emptyList(),
                                    affectedPackages = emptyList()
                                ),
                                onClick = { onOpenFlow(m.flowId) }
                            )
                            // Optional: you can show CTA label below or inside FindingListItem later
                        }

                        item {
                            Button(onClick = onStartScan, modifier = Modifier.fillMaxWidth()) {
                                Text("Scan erneut ausführen")
                            }
                        }
                    }
                }
            }
            is ScanUiState.Error -> {
                Column(
                    modifier = Modifier.padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Scan fehlgeschlagen.", style = MaterialTheme.typography.titleMedium)
                    Text(s.message, style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = onStartScan, modifier = Modifier.fillMaxWidth()) {
                        Text("Erneut versuchen")
                    }
                }
            }
        }
    }
}