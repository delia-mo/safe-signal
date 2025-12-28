package com.deliamo.spywarecheck.ui.screens.start

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliamo.spywarecheck.ui.components.AppScaffold

@Composable
fun StartScreen(
    onStartQuickCheck: () -> Unit,
    onQuickExit: () -> Unit,
    debugContent: (@Composable () -> Unit)? = null
    ) {
    AppScaffold(title = "Start", onQuickExit = onQuickExit) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Hier wird Startseite stehen.")
            Spacer(Modifier.height(12.dp))
            // Button(onClick = onStartQuickCheck) { Text("Quick Check starten") }

            debugContent?.let {
                Spacer(Modifier.height(24.dp))
                it()
            }
        }
    }
}
