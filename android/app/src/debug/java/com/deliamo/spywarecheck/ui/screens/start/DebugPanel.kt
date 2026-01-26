package com.deliamo.spywarecheck.ui.screens.start

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deliamo.spywarecheck.debug.DebugSettings
import com.deliamo.spywarecheck.ui.components.AppCard
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults

@Composable
fun DebugPanel() {
  AppCard {
      Column(
          modifier = Modifier.padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
          Text("Debug", style = MaterialTheme.typography.titleSmall)

          Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
          ) {
              Text("Test Root")
            Switch(
              checked = DebugSettings.simulateRoot,
              onCheckedChange = { DebugSettings.simulateRoot = it },
              colors = SwitchDefaults.colors(
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,

                uncheckedTrackColor = MaterialTheme.colorScheme.surface,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedBorderColor = MaterialTheme.colorScheme.outline
              )
            )

          }

          Text(
              text = "Hinweis: Debug-Einstellungen gelten nur für diese Session.",
              style = MaterialTheme.typography.bodySmall
          )
      }
  }
}
