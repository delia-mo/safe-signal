package com.deliamo.safesignal.ui.actions.flows.steps

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliamo.safesignal.ui.components.BulletItem

@Composable
fun StepExplain(
    stepLabel: String,
    title: String,
    body: String,
    bullets: List<String>,
    onNext: () -> Unit
) {
    Text(stepLabel, style = MaterialTheme.typography.labelLarge)
    Text(title, style = MaterialTheme.typography.titleLarge)
    Text(body, style = MaterialTheme.typography.bodyMedium)

    HorizontalDivider()

    Text("Unsere nächsten Schritte:", style = MaterialTheme.typography.titleMedium)
    bullets.forEach { BulletItem(it) }

    Spacer(Modifier.height(8.dp))
    Button(onClick = onNext, modifier = Modifier.fillMaxWidth()) { Text("Weiter") }
}
