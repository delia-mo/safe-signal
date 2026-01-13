package com.deliamo.spywarecheck.ui.screens.finding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliamo.spywarecheck.ui.components.AppScaffold
import com.deliamo.spywarecheck.ui.screens.scan.ScanViewModel

@Composable
fun FindingDetailScreen(
    findingId: String,
    onBack: () -> Unit,
    onQuickExit: () -> Unit,
    vm: ScanViewModel
) {

    AppScaffold(
        title = "Findings",
        onQuickExit = onQuickExit,
        showBack = true,
        onBack = onBack
    ) { padding ->
        Column(Modifier
            .padding(padding)
            .padding(16.dp)) {
            Text("Hier werden nach dem Scan die gefundenen Auffälligkeiten stehen.")
            Spacer(Modifier.height(12.dp))
        }
    }
}
