package com.deliamo.spywarecheck.ui.screens.start

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.deliamo.spywarecheck.ui.components.AppCard
import com.deliamo.spywarecheck.ui.components.AppScaffold
import com.deliamo.spywarecheck.ui.debug.DebugContent

@Composable
fun StartScreen(
    onStartQuickCheck: () -> Unit,
    onStartScanGated: () -> Unit,
    onOpenReport: () -> Unit,
    onOpenActions: () -> Unit,
    onQuickExit: () -> Unit,
    debugContent: @Composable (() -> Unit)? = null
) {
    AppScaffold(
        title = "Start",
        onQuickExit = onQuickExit,
        showBack = false
    )
    { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DebugContent()
            Text(
                text = "Du bist nicht allein. Digitale Gewalt ist real.",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = buildAnnotatedString {
                    append("Wir prüfen ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Einstellungen und App-Berechtigungen")
                    }
                    append(
                        ", die häufig von Überwachungs-Apps genutzt werden und helfen dir beim Absichern." +
                                "Die App liefert "
                    )
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("keine sicheren Beweise")
                    }
                    append(" und ersetzt keine forensische Untersuchung.")
                },
                style = MaterialTheme.typography.bodySmall
            )

            // Primary Action
            PrimaryActionCard(
                title = "Quick Check (2 Min)",
                subtitle = "Schnell einschätzen, ob ein Scan sinnvoll ist.",
                buttonText = "Starten",
                onClick = onStartQuickCheck
            )

            // Scan Action (gated)
            ActionCard(
                title = "Scan starten",
                subtitle = "Prüft Indikatoren, z.B. Geräteadministration oder Barrierefreiheitseinstellungen",
                buttonText = "Zum Safety Gate",
                onClick = onStartScanGated
            )

            // Secondary Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallActionCard(
                    title = "Offene Maßnahmen",
                    subtitle = "Weiter machen",
                    onClick = onOpenActions,
                    modifier = Modifier.weight(1f)
                )
                SmallActionCard(
                    title = "Report",
                    subtitle = "Ansehen / Export",
                    onClick = onOpenReport,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = buildAnnotatedString {
                    append("Du kannst jederzeit ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("Quick Exit")
                    }
                    append(" nutzen: Die App schließt sich und öffnet eine neutrale Seite.")
                },
                style = MaterialTheme.typography.bodySmall
            )
            // Debug panel area
            debugContent?.invoke()
        }
    }
}

@Composable
private fun PrimaryActionCard(
    title: String,
    subtitle: String,
    buttonText: String,
    onClick: () -> Unit
) {
  AppCard {
      Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(title, style = MaterialTheme.typography.titleMedium)
          Text(subtitle, style = MaterialTheme.typography.bodyMedium)
          Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
              Text(buttonText)
          }
      }
  }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    buttonText: String,
    onClick: () -> Unit
) {
    OutlinedCard {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            OutlinedButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
                Text(buttonText)
            }
        }
    }
}

@Composable
private fun SmallActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = onClick) { Text("Öffnen") }
        }
    }
}
