package com.deliamo.safesignal.ui.screens.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliamo.safesignal.domain.model.QuickCheckResult
import com.deliamo.safesignal.domain.model.QuickRisk
import com.deliamo.safesignal.ui.components.AppScaffold
import com.deliamo.safesignal.ui.components.BulletItem
import com.deliamo.safesignal.ui.components.HomeFooterBar
import com.deliamo.safesignal.presentation.quickcheck.QuickCheckViewModel

@Composable
fun ResultScreen(
    onBack: () -> Unit,
    onQuickExit: () -> Unit,
    onStartScanGated: () -> Unit,
    onHome: () -> Unit,
    vm: QuickCheckViewModel
) {
    val result: QuickCheckResult = remember { vm.buildResult() }
    AppScaffold(
        title = "Ergebnis",
        onQuickExit = onQuickExit,
        showBack = true,
        onBack = onBack,
        footer = { HomeFooterBar(onHome = onHome) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val headline = when (result.risk) {
                QuickRisk.LOW -> "Eher wenige Hinweise"
                QuickRisk.MEDIUM -> "Einige Hinweise"
                QuickRisk.HIGH -> "Viele Hinweise"
            }

            Text(headline, style = MaterialTheme.typography.titleLarge)
            Text(result.summary, style = MaterialTheme.typography.bodyMedium)

            HorizontalDivider()

            Text("Nächste Schritte", style = MaterialTheme.typography.titleMedium)

            result.top3.take(3).forEach { item ->
                BulletItem(item)
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onStartScanGated,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Scan starten") }
            Text(
                text = "Scan startet immer über das Safety Gate.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
