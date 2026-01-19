package com.deliamo.spywarecheck.ui.actions.flows

import com.deliamo.spywarecheck.ui.actions.flows.components.TutorialImgUi

data class GuidedFlowSpec(
    val findingId: String, // e.g. "device_admin_enabled" or "accessibility_enabled"
    val title: String,
    val stepCount: Int = 4,

    // Step 0
    val explainTitle: String,
    val explainBody: String,
    val explainBullets: List<String>,

    // Step 1
    val settingsTitle: String,
    val settingsIntro: String,
    val settingsSteps: List<String>,
    val settingsHint: String,
    val tutorialImages: List<TutorialImgUi> = emptyList(),

    // Step 2
    val uninstallTitle: String = "App(s) entfernen",
    val uninstallBody: String =
        "Öffne die App-Info der betroffenen App(s). Wenn du sie nicht kennst, stoppe oder deinstalliere sie. Komm danach zurück.",

    // Step 3
    val rescanTitle: String = "Erneut prüfen",
    val rescanBody: String = "Starte einen neuen Scan. So siehst du, ob der Hinweis verschwunden ist.",
)
