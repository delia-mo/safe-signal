package com.deliamo.spywarecheck.ui.actions.registry

import com.deliamo.spywarecheck.domain.model.ScanFinding
import com.deliamo.spywarecheck.domain.actions.NextBestAction
import com.deliamo.spywarecheck.domain.actions.SettingsKind

object NextBestActionRegistry {
    fun forFindingId(finding: ScanFinding): NextBestAction {
        return when (finding.id) {

            // HIGH
            "accessibility_enabled" ->
                NextBestAction.OpenSettings(
                    label = "Bedienungshilfen prüfen",
                    kind = SettingsKind.ACCESSIBILITY
                )

            "device_admin_enabled" ->
                NextBestAction.OpenActionFlow(
                    label = "Sicher entfernen (Schritte ansehen)",
                    flowId = finding.id
                )
//                when(finding.affectedPackages.size) {
//                    1 -> NextBestAction.OpenAppDetails("App prüfen", finding.affectedPackages.first())
//                    else -> NextBestAction.ChooseApp("Apps prüfen", flowId = finding.id)
//                }

            // MEDIUM
            "background_location_apps" ->
                when(finding.affectedPackages.size) {
                    1 -> NextBestAction.OpenAppDetails("Berechtigung bei App prüfen", finding.affectedPackages.first())
                    else -> NextBestAction.ChooseApp("Apps mit Zugriff prüfen", flowId = finding.id)
                }

            // LOW
            "location_enabled" ->
                NextBestAction.OpenSettings(
                    label = "Standort-Einstellungen prüfen",
                    kind = SettingsKind.LOCATION
                )

            // TODO root: guided flow
            "root_hint" ->
                NextBestAction.OpenActionFlow(
                    label = "Sicher handeln (Schritte ansehen)",
                    flowId = finding.id
                )

            else ->
                NextBestAction.OpenActionFlow(
                    label = "Nächste Schritte ansehen",
                    flowId = finding.id
                )
        }
    }
}