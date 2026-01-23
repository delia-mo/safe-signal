package com.deliamo.spywarecheck.ui.screens.safetygate

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliamo.spywarecheck.domain.safetygate.SafetyGateSpec
import com.deliamo.spywarecheck.ui.components.AppScaffold
import com.deliamo.spywarecheck.ui.components.BulletItem
import com.deliamo.spywarecheck.ui.components.HomeFooterBar

@Composable
fun SafetyGateScreen(
    spec: SafetyGateSpec,
    onContinue: () -> Unit,
    onCancel: () -> Unit,
    onQuickExit: () -> Unit,
) {
    var showMoreInfo by remember { mutableStateOf(false) }

    AppScaffold(
        title = spec.title,
        onQuickExit = onQuickExit,
        showBack = false,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(spec.headline, style = MaterialTheme.typography.titleLarge)

            Spacer(Modifier.height(8.dp))

            Text(spec.body, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(16.dp))

            spec.bullets.forEach { BulletItem(it) }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = { showMoreInfo = true}) {
                Text(spec.moreInfoLabel)
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(spec.continueLabel)
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(spec.cancelLabel)
            }
        }
    }

    // More Info Dialog
    if(showMoreInfo) {
        AlertDialog(
            onDismissRequest = { showMoreInfo = false },
            confirmButton = {
                TextButton(onClick = { showMoreInfo = false }) {
                    Text("OK")
                }
            },
            title = { Text(spec.dialogTitle) },
            text = {
                Text(spec.dialogText)
            }
        )
    }
}