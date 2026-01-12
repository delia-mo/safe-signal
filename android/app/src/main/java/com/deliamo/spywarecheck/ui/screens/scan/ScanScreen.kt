package com.deliamo.spywarecheck.ui.screens.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deliamo.spywarecheck.ui.components.AppScaffold

@Composable
fun ScanScreen(
    onBack: () -> Unit,
    onStartScan: () -> Unit,
    onOpenFinding: (() -> Unit)? = null,
    onQuickExit: () -> Unit,
    vm: ScanViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by vm.state.collectAsState()

    AppScaffold(
        title = "Scan",
        onQuickExit = onQuickExit,
        showBack = true,
        onBack = onBack
    )
    { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
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
                    val findingsCount = s.result.findings.size
                    Text(
                        text = "Scan abgeschlossen.",
                        style = MaterialTheme.typography.titleLarge
                    )

                    val summary = if (findingsCount == 0) {
                        "Keine Hinweise gefunden."
                    } else {
                        "${findingsCount} Hinweis(e) gefunden."
                    }

                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // TODO
                    // Show affected apps inline
                    if (findingsCount > 0) {
                        HorizontalDivider()
                        // s.result.findings.take(3).forEach { f ->
                        s.result.findings.forEach { f -> // TODO only show highest risk level
                            Text(f.title, style = MaterialTheme.typography.titleSmall)
                            Text(f.summary, style = MaterialTheme.typography.bodySmall)
                            if (f.affectedApps.isNotEmpty()) {
                                Text(
                                    text = "Betroffene Apps: " + f.affectedApps.joinToString(),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            if (f.details.isNotEmpty()) {
                                Text(
                                    text = "Details: " + f.details.joinToString(),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    // View details
                    if (findingsCount > 0 && onOpenFinding != null) {
                        OutlinedButton(
                            onClick = onOpenFinding,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Details ansehen") }
                    }

                    Button(
                        onClick = { vm.startScan(context) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Erneut scannen") }

                    TextButton(onClick = { vm.reset() }) {
                        Text("Zurück zur Scan-Startseite")
                    }
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
