package com.deliamo.safesignal.ui.actions.flows.specs

import com.deliamo.safesignal.domain.actions.SettingsKind
import com.deliamo.safesignal.ui.actions.flows.GuidedFlowSpec
import com.deliamo.safesignal.ui.actions.flows.components.TutorialImgUi

val LocationFlowSpec = GuidedFlowSpec(
    findingId = "location_enabled",
    title = "Schritte: Standort",

    preferredSettingsKind = SettingsKind.LOCATION,

    explainTitle = "Was bedeutet das?",
    explainBody = "Wenn Standort aktiv ist, kann das zur Überwachung missbraucht werden. Ein eingeschalteter " +
            "Standort ist aber erstmal allein kein Sicherheitsrisiko.",
    explainBullets = listOf(
        "Öffne die Standort-Einstellungen und schalte Standort aus (wenn möglich)."
    ),
    settingsSteps = listOf(
        "1) Tippe auf „Einstellungen öffnen“.",
        "2) Falls du nicht im Standort-Menü bist: Suche nach „Standort“ oder „Location“.",
        "3) Schalte Standort aus."
    ),
    settingsTitle = "Standort prüfen",
    settingsIntro = "Wir öffnen die Standort-Einstellungen. Dort kannst du den Standort ausschalten.",
    settingsHint = "Hinweis: Menünamen unterscheiden sich je nach Handy. Wenn du unsicher bist, ändere nichts.",
    tutorialImages = emptyList<TutorialImgUi>(),
    uninstallTitle = "Apps prüfen",
    uninstallBody = "Wenn dir eine App verdächtig vorkommt, öffne die App-Infos und prüfe „Berechtigungen → Standort“. " +
            "Stelle auf „Nicht zulassen“ oder „Nur während der Nutzung“.",
    rescanTitle = "Erneut prüfen",
    rescanBody = "Starte einen neuen Scan. Wir prüfen danach, ob Standort weiterhin aktiv ist."

)