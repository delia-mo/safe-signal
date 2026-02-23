package com.deliamo.safesignal.ui.screens.actions

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.deliamo.safesignal.domain.model.ScanFinding
import com.deliamo.safesignal.ui.actions.flows.GuidedFlowSpec
import com.deliamo.safesignal.ui.components.AppScaffold
import com.deliamo.safesignal.presentation.scan.ScanUiState
import com.deliamo.safesignal.presentation.scan.ScanViewModel
import com.deliamo.safesignal.ui.actions.flows.steps.*
import com.deliamo.safesignal.ui.components.HomeFooterBar
import com.deliamo.safesignal.ui.platform.onOpenSettings

@Composable
fun GuidedRemovalFlowScreen(
    spec: GuidedFlowSpec,
    flowId: String,
    step: Int,
    onBack: () -> Unit,
    onQuickExit: () -> Unit,
    onNavigateStep: (Int) -> Unit,
    onFinish: () -> Unit,
    onHome: () -> Unit,
    scanVm: ScanViewModel
) {
    val context = LocalContext.current
    val state by scanVm.state.collectAsState()

    val finding: ScanFinding? = (state as? ScanUiState.Done)
        ?.result
        ?.findings
        ?.firstOrNull { it.id == Uri.decode(flowId) }

    val pkgs = finding?.affectedPackages.orEmpty()
    val labels = finding?.affectedApps.orEmpty()

    AppScaffold(
        title = spec.title,
        onQuickExit = onQuickExit,
        showBack = true,
        onBack = onBack,
        footer = { HomeFooterBar(onHome = onHome) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (step) {
                0 -> StepExplain(
                    stepLabel = "Schritt 1/${spec.stepCount}",
                    title = spec.explainTitle,
                    body = spec.explainBody,
                    bullets = spec.explainBullets,
                    onNext = { onNavigateStep(1) }
                )

                1 -> StepOpenSettingsWithSearch(
                    stepLabel = "Schritt 2/${spec.stepCount}",
                    title = spec.settingsTitle,
                    intro = spec.settingsIntro,
                    steps = spec.settingsSteps,
                    hint = spec.settingsHint,
                    tutorialImages = spec.tutorialImages,
                    settingsKind = spec.preferredSettingsKind,
                    onOpenSettings = { kind -> onOpenSettings(context, kind) },
                    onNext = { onNavigateStep(2) },
                    onQuickExit = onQuickExit
                )

                2 -> StepOpenAppDetailsAndUninstall(
                    stepLabel = "Schritt 3/${spec.stepCount}",
                    title = spec.uninstallTitle,
                    body = spec.uninstallBody,
                    context = context,
                    affectedApps = labels,
                    affectedPackages = pkgs,
                    onNext = { onNavigateStep(3) }
                )

                3 -> StepRescanOutcome(
                    stepLabel = "Schritt 4/${spec.stepCount}",
                    title = spec.rescanTitle,
                    body = spec.rescanBody,
                    context = context,
                    scanVm = scanVm,
                    relevantFindingId = spec.findingId,
                    onBackToSettingsStep = { onNavigateStep(1) },
                    onFinish = onFinish
                )

                else -> {
                    Text("Unbekannter Schritt.", style = MaterialTheme.typography.bodyMedium)
                    Button(onClick = onFinish) { Text("Zurück") }
                }
            }
        }
    }
}
