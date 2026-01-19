package com.deliamo.spywarecheck.ui.actions.flows.specs

import com.deliamo.spywarecheck.R
import com.deliamo.spywarecheck.domain.actions.SettingsKind
import com.deliamo.spywarecheck.ui.actions.flows.GuidedFlowSpec

val AccessibilityFlowSpec = GuidedFlowSpec(
    findingId = "accessibility_enabled",
    title = "Schritte: Bedienungshilfen",

    explainTitle = "Was bedeutet das?",
    explainBody = "Bedienungshilfen sind für Unterstützung gedacht, können aber auch zur Überwachung missbraucht werden.",
    explainBullets = listOf(
        "1) Schalte Bedienungshilfen-Zugriff für unbekannte Apps aus.",
        "2) Öffne danach die App-Infos und entferne die App.",
        "3) Starte anschließend erneut den Scan."
    ),

    settingsTitle = "Bedienungshilfen ausschalten",
    settingsIntro = "Wenn eine unbekannte App Bedienungshilfen nutzen darf, kann sie sehr viel auf deinem Handy mitlesen/steuern.",
    settingsSteps = listOf(
        "1) Tippe auf „Einstellungen öffnen“.",
        "2) Suche nach „Bedienungshilfen“ oder „Accessibility“.",
        "3) Öffne „Installierte/Heruntergeladene Apps“ und schalte Unbekanntes aus."
    ),
    settingsHint = "Hinweis: Menünamen unterscheiden sich je nach Gerät. Wenn du unsicher bist: ändere nichts.",
    tutorialImages = emptyList(),
//    tutorialImages = listOf(
//        TutorialImgUi(R.drawable.access_tutorial_1, "Bedienungshilfen suchen"),
//        TutorialImgUi(R.drawable.access_tutorial_2, "Unerwartete App finden"),
//        TutorialImgUi(R.drawable.access_tutorial_3, "Zugriff ausschalten")
//    )
    preferredSettingsKind = SettingsKind.ACCESSIBILITY
)

