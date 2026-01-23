package com.deliamo.spywarecheck.ui.actions.registry

import com.deliamo.spywarecheck.domain.model.ScanFinding
import com.deliamo.spywarecheck.domain.actions.NextBestAction
import com.deliamo.spywarecheck.domain.actions.SettingsKind

object NextBestActionRegistry {
    fun forFindingId(finding: ScanFinding): NextBestAction {
        return when (finding.id) {

            // HIGH
            "accessibility_enabled" ->
                NextBestAction.OpenActionFlow(
                    label = "Zugriff entziehen",
                    flowId = finding.id
                )

            "device_admin_enabled" ->
                NextBestAction.OpenActionFlow(
                    label = "Zugriff entziehen",
                    flowId = finding.id
                )


            // MEDIUM
            "background_location_apps" ->
                NextBestAction.OpenActionFlow(
                    label = "Standort-Zugriff prüfen",
                    flowId = finding.id
                )

            // LOW
            "location_enabled" ->
                NextBestAction.OpenActionFlow(
                    label = "Standort-Einstellungen prüfen",
                    flowId = finding.id
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


    fun isActionableFinding(findingId: String): Boolean {
        return findingId in setOf(
            "device_admin_enabled",
            "accessibility_enabled",
            "background_location_apps",
            "location_enabled",
            "root_hint"
        )
    }

    fun preferFindingIdForMeasures(
        findingId: String,
        availableFindingIds: Set<String>
    ): String {
        return if (findingId == "location_enabled" && "background_location_apps" in availableFindingIds) {
            "background_location_apps"
        } else findingId
    }
}
