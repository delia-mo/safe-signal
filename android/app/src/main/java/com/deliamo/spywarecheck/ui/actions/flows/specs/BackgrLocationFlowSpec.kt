package com.deliamo.spywarecheck.ui.actions.flows.specs

import com.deliamo.spywarecheck.domain.actions.SettingsKind
import com.deliamo.spywarecheck.ui.actions.flows.GuidedFlowSpec

val BackgroundLocationFlowSpec = GuidedFlowSpec(
    findingId = "background_location_apps",
    title = "Schritte: Standort-Zugriff",

    preferredSettingsKind = SettingsKind.LOCATION,

    explainTitle = "Was bedeutet das?",
    explainBody = "Mindestens eine App kann deinen Standort auch im Hintergrund nutzen. Das kann zur Überwachung missbraucht werden.",
    explainBullets = listOf(
        "1) Öffne die Standort-Einstellungen (optional: Standort kurz aus).",
        "2) Öffne die App-Infos der betroffenen App(s) und ändere Standort-Berechtigungen.",
        "3) Starte anschließend erneut den Scan."
    ),

    settingsTitle = "Standort-Einstellungen öffnen",
    settingsIntro = "Wir öffnen die Standort-Einstellungen. Dort kannst du Standort deaktivieren oder App-Zugriffe prüfen.",
    settingsSteps = listOf(
        "1) Tippe auf „Einstellungen öffnen“.",
        "2) Falls du nicht im Standort-Menü bist: Suche nach „Standort“ oder „Location“.",
        "3) Tippe auf die einzelnen Apps und erlaube Standortnutzung nur bei Bedarf / Nutzung der App."
    ),
    settingsHint = "Hinweis: Menünamen unterscheiden sich je nach Handy. Wenn du unsicher bist: ändere nichts.",

    uninstallTitle = "Standort-Zugriff entziehen",
    uninstallBody =
        "Öffne die App-Infos der betroffenen App(s). Gehe zu „Berechtigungen → Standort“ und stelle auf " +
                "„Nicht zulassen“ oder „Nur während der Nutzung“. Wenn du unsicher bist: ändere nichts.",

    rescanTitle = "Erneut prüfen",
    rescanBody = "Starte einen neuen Scan. Wir prüfen danach, ob weiterhin Apps mit Hintergrund-Standortzugriff gefunden werden."
)

