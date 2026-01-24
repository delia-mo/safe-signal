package com.deliamo.spywarecheck.ui.actions.flows.steps

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliamo.spywarecheck.data.session.SessionService
import com.deliamo.spywarecheck.data.session.SessionStore
import com.deliamo.spywarecheck.ui.screens.scan.ScanUiState
import com.deliamo.spywarecheck.ui.screens.scan.ScanViewModel
import kotlinx.coroutines.launch

@Composable
fun StepRescanOutcome(
    stepLabel: String,
    title: String,
    body: String,
    context: Context,
    scanVm: ScanViewModel,
    relevantFindingId: String,
    onBackToSettingsStep: () -> Unit,
    onFinish: () -> Unit
) {
    val state by scanVm.state.collectAsState()
    val isRunning = state is ScanUiState.Running

    var waitingForRescan by remember { mutableStateOf(false) }
    var sawRunning by remember { mutableStateOf(false) }
    var showOutcome by remember { mutableStateOf(false) }

    val onRescanClick = {
        showOutcome = false
        sawRunning = false
        waitingForRescan = true
        scanVm.startScan(context = context.applicationContext)
    }

    LaunchedEffect(state, waitingForRescan) {
        if (!waitingForRescan) return@LaunchedEffect
        when (state) {
            is ScanUiState.Running -> sawRunning = true
            is ScanUiState.Done -> {
                if (sawRunning) {
                    showOutcome = true
                    waitingForRescan = false
                }
            }

            else -> Unit
        }
    }

    Text(stepLabel, style = MaterialTheme.typography.labelLarge)
    Text(title, style = MaterialTheme.typography.titleLarge)
    Text(body, style = MaterialTheme.typography.bodyMedium)

    Spacer(Modifier.height(8.dp))

    val scanButtonText = if (isRunning) "Scan läuft ..." else "Erneut scannen"

    if (showOutcome) {
        OutlinedButton(
            onClick = onRescanClick,
            enabled = !isRunning,
            modifier = Modifier.fillMaxWidth()
        ) { Text(scanButtonText) }
    } else {
        Button(
            onClick = onRescanClick,
            enabled = !isRunning,
            modifier = Modifier.fillMaxWidth()
        ) { Text(scanButtonText) }
    }

    if (isRunning) {
        Spacer(Modifier.height(10.dp))
        Text(
            "Gerät wird gescannt. Das kann einen Moment dauern.",
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(6.dp))
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    }

    if (showOutcome) {
        val doneState = state as? ScanUiState.Done
        val stillThere = doneState?.result?.findings?.any { it.id == relevantFindingId } == true

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))

        if (stillThere) {
            Text("Noch aktiv", style = MaterialTheme.typography.titleMedium)
            Text(
                text = "Der Hinweis ist noch vorhanden. Gehe zurück und schalte den Zugriff in den Einstellungen aus.",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))
            Button(onClick = onBackToSettingsStep, modifier = Modifier.fillMaxWidth()) {
                Text("Zurück")
            }
            OutlinedButton(onClick = onFinish, modifier = Modifier.fillMaxWidth()) {
                Text("Zum normalen Ergebnis")
            }
        } else {
            Text("Geschafft 🎉", style = MaterialTheme.typography.titleMedium)
            Text(
                "Der Hinweis ist verschwunden. Gut gemacht!",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(12.dp))
            val scope = rememberCoroutineScope()

            Button(
                onClick = {
                    val decodedId = Uri.decode(relevantFindingId)

                    scope.launch {
                        val service = SessionService(SessionStore(context.applicationContext))
                        service.markActionStatus(decodedId, "DONE")
                        onFinish()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Fertig")
            }
        }
    }
}
