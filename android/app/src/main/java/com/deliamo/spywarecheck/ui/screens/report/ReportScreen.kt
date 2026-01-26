package com.deliamo.spywarecheck.ui.screens.report

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deliamo.spywarecheck.domain.report.ActionUiState
import com.deliamo.spywarecheck.domain.session.StoredFinding
import com.deliamo.spywarecheck.ui.components.AppScaffold
import com.deliamo.spywarecheck.ui.components.HomeFooterBar
import java.util.Date

@Composable
fun ReportScreen(
  onBack: () -> Unit,
  onQuickExit: () -> Unit,
  onOpenMeasures: () -> Unit,
  onOpenFinding: (String) -> Unit,
  onHome: () -> Unit,
  vm: ReportViewModel = viewModel()
) {
  val ctx = androidx.compose.ui.platform.LocalContext.current
  val state by vm.ui.collectAsState()
  var confirmDelete by remember { mutableStateOf(false) }

  LaunchedEffect(Unit) { vm.load(ctx) }

  AppScaffold(
    title = "Report",
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
      if (state.isLoading) {
        Text("Report wird geladen …", style = MaterialTheme.typography.bodyMedium)
        return@Column
      }

      if (state.error != null) {
        Text(state.error!!, style = MaterialTheme.typography.bodyMedium)
        return@Column
      }

      if (!state.hasBaseline) {
        Text("Noch kein Report verfügbar.", style = MaterialTheme.typography.titleMedium)
        Text(
          "Starte zuerst einen Scan. Danach speichern wir den ersten Scan als Report-Basis.",
          style = MaterialTheme.typography.bodyMedium
        )
        return@Column
      }

      ReportHeader(
        baselineAt = state.baslineAtMillis,
        lastScanAt = state.lastScanAtMillis
      )

      val open = openFindings(state.findingsBaseline, state.actionStatusByFindingId)
      val done = doneFindings(state.findingsBaseline, state.actionStatusByFindingId)

      AccordionSection(
        title = "Gefundene Hinweise (${state.findingsBaseline.size})",
        subtitle = "Alles, was beim ersten Scan gefunden wurde",
        defaultExpanded = false
      ) {
        FindingsList(
          findings = state.findingsBaseline,
          statusById = state.actionStatusByFindingId,
          onOpenFinding = onOpenFinding
        )
      }

      AccordionSection(
        title = "Offene Maßnahmen (${open.size})",
        subtitle = "Noch nicht abgeschlossen",
        defaultExpanded = false
      ) {
        if (open.isEmpty()) {
          Text("Keine offenen Maßnahmen.", style = MaterialTheme.typography.bodyMedium)
        } else {
          open.forEach { f ->
            ActionRow(
              title = f.title,
              status = state.actionStatusByFindingId[f.id] ?: ActionUiState.NOT_STARTED,
              onClick = { onOpenFinding(f.id) }
            )
            HorizontalDivider()
          }
        }

        Spacer(Modifier.padding(4.dp))
        TextButton(onClick = onOpenMeasures) { Text("Zu den Maßnahmen") }
      }

      AccordionSection(
        title = "Erledigte Maßnahmen (${done.size})",
        subtitle ="",
        defaultExpanded = false
      ) {
        if (done.isEmpty()) {
          Text("Noch nichts als erledigt markiert.", style = MaterialTheme.typography.bodyMedium)
        } else {
          done.forEach { f ->
            ActionRow(
              title = f.title,
              status = ActionUiState.DONE,
              onClick = { onOpenFinding(f.id) }
            )
            HorizontalDivider()
          }
        }
      }

      AccordionSection(
        title = "Empfohlene nächste Schritte",
        subtitle = "",
        defaultExpanded = false
      ) {
        val steps = buildNextSteps(open)
        steps.forEach { s -> Text("• $s", style = MaterialTheme.typography.bodyMedium) }
      }

      Text(
        "Hinweis: Der Report dient zur Orientierung und Dokumentation – er ist kein forensischer Beweis.",
        style = MaterialTheme.typography.bodySmall
      )

      OutlinedButton(
        onClick = { confirmDelete = true },
        modifier = Modifier.fillMaxWidth()
      ) {
        Text("Alles löschen")
      }

      if (confirmDelete) {
        AlertDialog(
          onDismissRequest = { confirmDelete = false },
          title = { Text("Report wirklich löschen?") },
          text = { Text("Damit werden alle gespeicherten Scan- und Maßnahmen-Daten gelöscht.") },
          confirmButton = {
            Button(
              onClick = {
                confirmDelete = false
                vm.clearAll(ctx)
              }
            ) { Text("Löschen") }
          },
          dismissButton = {
            TextButton(onClick = { confirmDelete = false }) { Text("Abbrechen") }
          }
        )
      }
    }
  }
}

@Composable
private fun ReportHeader(baselineAt: Long?, lastScanAt: Long?) {
  Card {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Text("Report-Stand", style = MaterialTheme.typography.titleMedium)

      Text(
        "Baseline: ${formatDateTime(baselineAt)}",
        style = MaterialTheme.typography.bodyMedium
      )
      Text(
        "Letzter Scan: ${formatDateTime(lastScanAt)}",
        style = MaterialTheme.typography.bodyMedium
      )

      Text(
        "Diese Ansicht bleibt erhalten, auch wenn du neu scannst.",
        style = MaterialTheme.typography.bodySmall
      )
    }
  }
}

@Composable
private fun FindingsList(
  findings: List<StoredFinding>,
  statusById: Map<String, ActionUiState>,
  onOpenFinding: (String) -> Unit
) {
  if (findings.isEmpty()) {
    Text("Keine Hinweise gespeichert.", style = MaterialTheme.typography.bodyMedium)
    return
  }

  findings.forEach { f ->
    val st = statusById[f.id] ?: ActionUiState.NOT_STARTED

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clickable { onOpenFinding(f.id) }
        .padding(vertical = 10.dp),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(Modifier.weight(1f)) {
        Text(f.title, style = MaterialTheme.typography.bodyMedium)
        Text(
          text = f.summary,
          style = MaterialTheme.typography.bodySmall
        )
      }

      StatusChip(st)
    }
    HorizontalDivider()
  }
}

@Composable
private fun ActionRow(
  title: String,
  status: ActionUiState,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    StatusChip(status)
  }
}

@Composable
private fun StatusChip(status: ActionUiState) {
  val label = when (status) {
    ActionUiState.DONE -> "Erledigt"
    ActionUiState.IN_PROGRESS -> "In Arbeit"
    ActionUiState.SKIPPED -> "Übersprungen"
    ActionUiState.NOT_STARTED -> "Offen"
  }
  Text(label, style = MaterialTheme.typography.labelMedium)
}

@Composable
private fun AccordionSection(
  title: String,
  subtitle: String? = null,
  defaultExpanded: Boolean = true,
  content: @Composable () -> Unit
) {
  var expanded by remember { mutableStateOf(defaultExpanded) }

  Card {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { expanded = !expanded },
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column(Modifier.weight(1f)) {
          Text(title, style = MaterialTheme.typography.titleMedium)
          if (!subtitle.isNullOrBlank()) {
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
          }
        }
        Text(if (expanded) "−" else "+", style = MaterialTheme.typography.titleLarge)
      }

      AnimatedVisibility(visible = expanded) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { content() }
      }
    }
  }
}

private fun formatDateTime(millis: Long?): String {
  if (millis == null) return "—"
  return DateFormat.format("dd.MM.yyyy  HH:mm", Date(millis)).toString()
}

private fun openFindings(
  findings: List<StoredFinding>,
  status: Map<String, ActionUiState>
): List<StoredFinding> {
  return findings.filter { (status[it.id] ?: ActionUiState.NOT_STARTED) != ActionUiState.DONE }
}

private fun doneFindings(
  findings: List<StoredFinding>,
  status: Map<String, ActionUiState>
): List<StoredFinding> {
  return findings.filter { (status[it.id] ?: ActionUiState.NOT_STARTED) == ActionUiState.DONE }
}

private fun buildNextSteps(open: List<StoredFinding>): List<String> {
  if (open.isEmpty()) return listOf("Aktuell sind keine offenen Maßnahmen vorhanden.")
  val ids = open.map { it.id }.toSet()

  // simple heuristics for MVP
  val steps = mutableListOf<String>()
  if ("device_admin_enabled" in ids) steps += "Geräteverwaltung prüfen und unbekannte Admin-Rechte deaktivieren."
  if ("accessibility_enabled" in ids) steps += "Bedienungshilfen prüfen und unbekannte Dienste ausschalten."
  if ("background_location_apps" in ids) steps += "Dauerhaften Standortzugriff verdächtiger Apps entfernen."
  if ("location_enabled" in ids) steps += "Standort nur bei Bedarf aktivieren und App-Rechte prüfen."
  if ("root_enabled" in ids) steps += "Wenn Root unerwartet ist: Hilfe holen / Werksreset erwägen."

  if (steps.isEmpty()) steps += "Wenn du unsicher bist: nichts ändern und eine Beratungsstelle kontaktieren."
  return steps
}
