package com.deliamo.spywarecheck.ui.screens.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.deliamo.spywarecheck.domain.model.ScanFinding
import com.deliamo.spywarecheck.domain.model.Severity
import com.deliamo.spywarecheck.ui.components.AppScaffold
import com.deliamo.spywarecheck.ui.components.FindingListItem
import com.deliamo.spywarecheck.ui.components.HomeFooterBar

@Composable
fun ScanScreen(
    onBack: () -> Unit,
    onStartScan: () -> Unit,
    onOpenFinding: (String) -> Unit,
    onQuickExit: () -> Unit,
    onHome: () -> Unit,
    vm: ScanViewModel
) {
    val context = LocalContext.current
    val state by vm.state.collectAsState()

    AppScaffold(
        title = "Scan",
        onQuickExit = onQuickExit,
        showBack = true,
        onBack = onBack,
        footer = { HomeFooterBar(onHome = onHome) }
    )
    { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (val s = state) {
                is ScanUiState.Idle -> {
                    Text(
                        text = "Der Scan prüft lokale Anzeichen auf dem Gerät.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    ScanTransparencyCard(modifier = Modifier.fillMaxWidth())

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = { vm.startScan(context) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Scan starten")
                    }
                }

                is ScanUiState.Running -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator()
                        Text(
                            text = "Scan läuft... ",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Text(
                        text = "Das kann einen Moment dauern.",
                        style = MaterialTheme.typography.bodySmall
                    )

                    ScanTransparencyCard(modifier = Modifier.fillMaxWidth())
                }

                is ScanUiState.Done -> {
                    ScanDoneContent(
                        findings = s.result.findings,
                        onOpenFinding = onOpenFinding,
                        onRescan = { vm.startScan(context) })
                }

                is ScanUiState.Error -> {
                    Text(
                        text = "Fehler",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = s.message,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Button(
                        onClick = { vm.startScan(context) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Erneut versuchen")
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanDoneContent(
    findings: List<ScanFinding>,
    onOpenFinding: (String) -> Unit,
    onRescan: () -> Unit
) {
    val high = remember(findings) { findings.filter { it.severity == Severity.HIGH } }
    val medium = remember(findings) { findings.filter { it.severity == Severity.MEDIUM } }
    val low = remember(findings) { findings.filter { it.severity == Severity.LOW } }

    // Show HIGH first, if no HIGH, show up to 3 MEDIUM
    val primary: List<ScanFinding> = remember(findings) {
        if (high.isNotEmpty()) high
        else medium.take(3)
    }

    val secondary: List<ScanFinding> = remember(findings) {
        val remaining = (findings - primary.toSet())
        // Ordered by severity (MEDIUM first, then LOW)
        remaining.sortedWith(compareByDescending<ScanFinding> { it.severity.ordinal })
    }

    var showMore by rememberSaveable { mutableStateOf(false) }

    val total = findings.size
    val headline = when {
        high.isNotEmpty() -> "Hohe Hinweise gefunden" // TODO reframe
        medium.isNotEmpty() -> "Mittlere Hinweise gefunden"
        else -> "Wenige Hinweise gefunden"
    }


    // Scrollable area (if there are many items)
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { Text("Scan abgeschlossen", style = MaterialTheme.typography.titleLarge) }
        item {
            Text(
                text = "$total Hinweis(e) • ${high.size} hoch • ${medium.size} mittel • ${low.size} niedrig",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        item { HorizontalDivider() }

        item { Text(headline, style = MaterialTheme.typography.bodyMedium) }

        // Primary finidings
        items(primary, key = { it.id }) { f ->
            FindingListItem(
                findings = f,
                onClick = { onOpenFinding(f.id) }
            )
        }

        // More toggle
        if (secondary.isNotEmpty()) {
            item {
                TextButton(onClick = { showMore = !showMore }) {
                    Text(if (showMore) "Weniger anzeigen" else "Mehr Hinweise anzeigen (${secondary.size})")
                }
            }

            if (showMore) {
                items(secondary, key = { it.id }) { f ->
                    FindingListItem(
                        findings = f,
                        onClick = { onOpenFinding(f.id) }
                    )
                }
            }
        }


        item { Spacer(Modifier.height(8.dp)) }

        item {
            OutlinedButton(
                onClick = onRescan,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Erneut scannen") }
        }
    }
}
