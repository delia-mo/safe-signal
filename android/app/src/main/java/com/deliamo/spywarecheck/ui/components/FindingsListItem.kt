package com.deliamo.spywarecheck.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliamo.spywarecheck.domain.model.ScanFinding

@Composable
fun FindingListItem(
    findings: ScanFinding,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SeverityChip(findings.severity)
            Text(
                text = findings.title,
                style = MaterialTheme.typography.titleSmall
            )
        }

        Text(
            text = findings.summary,
            style = MaterialTheme.typography.bodySmall
        )

        if (findings.affectedApps.isNotEmpty()) {
            val n = findings.affectedApps.size
            Text(
                text = if (n == 1) "1 App betroffen" else "$n Apps betroffen",
                style = MaterialTheme.typography.labelMedium
            )
        }
        HorizontalDivider()
    }
} }
