package com.deliamo.spywarecheck.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliamo.spywarecheck.domain.model.Severity

@Composable
fun SeverityChip(severity: Severity, modifier: Modifier = Modifier) {
    val label = when (severity) {
        Severity.HIGH -> "HOCH"
        Severity.MEDIUM -> "MITTEL"
        Severity.LOW -> "NIEDRIG"
    }

    AssistChip(
        onClick = { },
        label = { Text(label, style = MaterialTheme.typography.labelMedium) },
        modifier = modifier.padding(end = 6.dp),
        colors = AssistChipDefaults.assistChipColors()
    )
}