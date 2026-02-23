package com.deliamo.safesignal.ui.screens.actions

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliamo.safesignal.ui.components.AppScaffold
import com.deliamo.safesignal.ui.components.BulletItem
import com.deliamo.safesignal.ui.components.HomeFooterBar

@Composable
fun ActionFlowStubScreen(
    flowId: String,
    onBack: () -> Unit,
    onQuickExit: () -> Unit,
    onHome: () -> Unit,
) {
    AppScaffold(
        title = "Nächste Schritte",
        onQuickExit = onQuickExit,
        showBack = true,
        onBack = onBack,
        footer = { HomeFooterBar(onHome = onHome) }
    ) { padding: PaddingValues ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val decoded = Uri.decode(flowId)

            Text(
                text = "Schritte (Platzhalter)",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Führt in den Maßnahmen-Flow. Aktuell: Platzhalter für $decoded.",
                style = MaterialTheme.typography.bodyMedium
            )

            HorizontalDivider()

            when (decoded) {
                "root_hint" -> {
                    Text("Was du jetzt tun kannst", style = MaterialTheme.typography.titleMedium)
                    BulletItem("Wenn du Root selber eingerichtet hast: Dieser Hinweis kann daher kommen.")
                    BulletItem("Wenn nicht: Hole dir Unterstützung von einer Stalking-Beratungsstelle oder IT-Beratung.")
                    BulletItem("Wenn du schnell Sicherheit herstellen möchtest: Sichere deine Daten und ggf. Beweise und setze " +
                            "dein Handy auf Werkeinstellungen zurück. Das ist ein großer Schritt und sollte ggf. vorher besprochen werden.")
                }

                "device_management_active", "managed_device" -> {
                    Text("Was du jetzt tun kannst", style = MaterialTheme.typography.titleMedium)
                    BulletItem("Prüfe, ob eine Kindersicherung oder Firmenverwaltung aktiv ist.")
                    BulletItem("Wenn du das nicht erwartest: Hol dir Unterstützung, bevor du etwas deaktivierst.")
                }
                else -> {
                    Text("Was du jetzt tun kannst", style = MaterialTheme.typography.titleMedium)
                    BulletItem("Öffne die passenden Einstellungen und prüfe unbekannte Einträge.")
                    BulletItem("Wenn du unsicher bist: Hol dir Unterstützung.")
            }
        }
        }
    }
}