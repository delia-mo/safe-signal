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
fun SafetyGateScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    onCancel: () -> Unit,
    onQuickExit: () -> Unit
) {
    AppScaffold(
        title = "Safety Gate",
        onQuickExit = onQuickExit,
        showBack = true,
        onBack = onBack
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Hier wird das Safety Gate vor Scan oder Entfernen angezeigt.")
            Spacer(Modifier.height(12.dp))
        }
    }
}