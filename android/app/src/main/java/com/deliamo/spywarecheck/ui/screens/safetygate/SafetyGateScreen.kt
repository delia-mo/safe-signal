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
import com.deliamo.spywarecheck.ui.components.AppScaffold

@Composable
fun SafetyGateScreen(
    onContinue: () -> Unit,
    onCancel: () -> Unit,
    onQuickExit: () -> Unit
) {
    var showMoreInfo by remember { mutableStateOf(false) }

    AppScaffold(
        title = "Bist du gerade in Sicherheit?", // Todo change title
        onQuickExit = onQuickExit,
        showBack = false,
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Bist du gerade in einer sicheren Situation?",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Ein Scan des Handys oder das Entfernen einer App kann auffallen." +
                    "Mach das nur, wenn die Tatperson gerade nicht in deiner Nähe ist.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))

            Bullet(text = "Änderungen können auffallen, z.B. wenn die Tatperson deinen Standort nicht mehr sehen kann.")
            Bullet(text = "Wenn du Beweise sichern willst: Mache Screenshots oder Notizen bevor du etwas löschst oder änderst.")
            Bullet(text = "Quick Exit oder Abbrechen ist jederzeit möglich.")

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = { showMoreInfo = true}) {
                Text("Warum ist das wichtig?")
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ich bin sicher (Weiter)")
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Jetzt nicht.")
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
            title = { Text("Warum wird das gefragt?") },
            text = {
                Text(
                    "Manche Apps, mit denen überwacht werden kann, merken, wenn Berechtigungen " +
                    "entzogen werden oder sie entfernt werden. Das kann der Tatperson auffallen und " +
                    "im schlimmsten Fall zu Stress führen. Wenn du unsicher bist, stoppe hier und " +
                    "nutze später einen sicheren Moment."
                )
            }
        )
    }
}
@Composable
private fun Bullet(text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("• ")
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}