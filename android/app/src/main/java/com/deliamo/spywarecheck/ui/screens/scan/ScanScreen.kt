package com.deliamo.spywarecheck.ui.screens.start

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliamo.spywarecheck.ui.components.AppScaffold

@Composable
fun ScanScreen(
    onBack: () -> Unit,
    onStartScan: () -> Unit,
    onOpenFinding: () -> Unit,
    onQuickExit: () -> Unit
) {
    val onStartScan = onStartScan

    AppScaffold(
        title = "Scan",
        onQuickExit = onQuickExit,
        showBack = true,
        onBack = onBack,
    )
    { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp)) {

            Text("Hier wird der Scan gestartet.")

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onStartScan,
                modifier = Modifier.fillMaxWidth()
            )
            {
                Text("Scan starten")
            }
        }
    }
}
