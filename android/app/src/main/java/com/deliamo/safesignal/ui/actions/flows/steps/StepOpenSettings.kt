package com.deliamo.safesignal.ui.actions.flows.steps

import kotlin.collections.isNotEmpty

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliamo.safesignal.domain.actions.SettingsKind
import com.deliamo.safesignal.ui.actions.flows.components.TutorialImagesRow
import com.deliamo.safesignal.ui.actions.flows.components.TutorialImgUi

@Composable
fun StepOpenSettingsWithSearch(
    stepLabel: String,
    title: String,
    intro: String,
    steps: List<String>,
    hint: String,
    tutorialImages: List<TutorialImgUi>,
    settingsKind: SettingsKind,
    onOpenSettings: (SettingsKind) -> Unit,
    onNext: () -> Unit,
    onQuickExit: () -> Unit
) {
    var showExamples by remember { mutableStateOf(false) }

    Text(stepLabel, style = MaterialTheme.typography.labelLarge)
    Text(title, style = MaterialTheme.typography.titleLarge)

    Text(intro, style = MaterialTheme.typography.bodyMedium)

    Spacer(Modifier.height(10.dp))

    steps.forEach { Text(it, style = MaterialTheme.typography.bodyMedium) }

    Spacer(Modifier.height(10.dp))
    Text(hint, style = MaterialTheme.typography.bodySmall)

    if (tutorialImages.isNotEmpty()) {
        Spacer(Modifier.height(6.dp))
        TextButton(onClick = { showExamples = !showExamples }) {
            Text(if (showExamples) "Beispielbilder ausblenden" else "Beispielbilder anzeigen")
        }

        AnimatedVisibility(visible = showExamples) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TutorialImagesRow(
                    images = tutorialImages,
                    onQuickExit = onQuickExit
                )

            }
        }
    }

    Spacer(Modifier.height(12.dp))

    Button(onClick = { onOpenSettings(settingsKind) }, modifier = Modifier.fillMaxWidth()) {
        Text("Einstellungen öffnen")
    }
    OutlinedButton(onClick = onNext, modifier = Modifier.fillMaxWidth()) {
        Text("Ich bin zurück – weiter")
    }
}
