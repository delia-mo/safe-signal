package com.deliamo.spywarecheck.ui.screens.scan

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ScanTransparencyCard(
    modifier: Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Was wir prüfen",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "• ... \n" +
                        "• ... \n" +
                        "• ... \n",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Wichtig",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "Das Ergebnis sind Hinweise, kein sicherer Beweis. \nDie App findet nicht jede" +
                        "Art der Überwachung und ersetzt keine forensische Untersuchung.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}