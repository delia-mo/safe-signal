package com.deliamo.spywarecheck.ui.screens.finding

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.deliamo.spywarecheck.ui.actions.launcher.NextBestActionLauncher
import com.deliamo.spywarecheck.ui.actions.registry.NextBestActionRegistry
import com.deliamo.spywarecheck.ui.components.AppScaffold
import com.deliamo.spywarecheck.ui.components.HomeFooterBar
import com.deliamo.spywarecheck.ui.components.SeverityChip
import com.deliamo.spywarecheck.ui.screens.scan.ScanUiState
import com.deliamo.spywarecheck.ui.screens.scan.ScanViewModel

@Composable
fun FindingDetailScreen(
    findingId: String,
    onBack: () -> Unit,
    onQuickExit: () -> Unit,
    onOpenActionFlow: (String) -> Unit,
    onHome: () -> Unit,
    vm: ScanViewModel
) {
    val context = LocalContext.current
    val state by vm.state.collectAsState()

    val finding = when (val s = state) {
        is ScanUiState.Done -> s.result.findings.firstOrNull { it.id == findingId }
        else -> null
    }
    AppScaffold(
        title = "Details",
        onQuickExit = onQuickExit,
        showBack = true,
        onBack = onBack,
        footer = { HomeFooterBar(onHome = onHome) }
    ) { padding ->
        Column(
            modifier = Modifier
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp))
        {
            if (finding == null) {
                Text("Kein Detail gefunden.", style = MaterialTheme.typography.bodyMedium)
                return@Column
            }

            val action = NextBestActionRegistry.forFindingId(finding)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SeverityChip(finding.severity)
                Text(finding.title, style = MaterialTheme.typography.titleLarge)
            }

            Text(finding.summary, style = MaterialTheme.typography.bodyMedium)

            HorizontalDivider()

            Text("Betroffene Apps", style = MaterialTheme.typography.titleMedium)
            if (finding.affectedApps.isEmpty()) {
                Text("Keine konkreten Apps erkannt.", style = MaterialTheme.typography.bodyMedium)
            } else {
                finding.affectedApps.forEach { app ->
                    Text("• $app", style = MaterialTheme.typography.bodySmall)
                }
            }

            // TODO Maßnahmen
            HorizontalDivider()

            Text("Nächste Schritte", style = MaterialTheme.typography.titleMedium)
            Button(
                onClick = {
                    NextBestActionLauncher.handle(
                        context = context,
                        action = action,
                        navigateToFlow = onOpenActionFlow
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(action.label) }
        }
    }
}
