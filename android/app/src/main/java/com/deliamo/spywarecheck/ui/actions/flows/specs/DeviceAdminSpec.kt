package com.deliamo.spywarecheck.ui.actions.flows.specs

import com.deliamo.spywarecheck.R
import com.deliamo.spywarecheck.domain.actions.SettingsKind
import com.deliamo.spywarecheck.ui.actions.flows.GuidedFlowSpec
import com.deliamo.spywarecheck.ui.actions.flows.components.TutorialImgUi

val DeviceAdminFlowSpec = GuidedFlowSpec(
    findingId = "device_admin_enabled",
    title = "Schritte: Geräteverwaltung",

    explainTitle = "Was bedeutet das?",
    explainBody = "Eine App hat Verwaltungsrechte. Das kann das Entfernen erschweren.",
    explainBullets = listOf(
        "1) Deaktiviere zuerst die Verwaltungsrechte der App.",
        "2) Öffne danach die App-Infos und entferne die App.",
        "3) Starte anschließend erneut den Scan."
    ),

    settingsTitle = "Verwaltungsrechte ausschalten",
    settingsIntro = "Diese Rechte können missbraucht werden. Wir schalten sie jetzt aus.",
    settingsSteps = listOf(
        "1) Tippe auf „Einstellungen öffnen“.",
        "2) Suche nach „admin“ oder „Geräteadministrator“.",
        "3) Öffne unbekannte Einträge und deaktiviere sie."
    ),
    settingsHint = "Hinweis: Namen können je nach Handy anders heißen. Wenn du unsicher bist: ändere nichts.",
    tutorialImages = listOf(
        TutorialImgUi(R.drawable.admin_tutorial_1, "Schlagwort suchen und Einstellung auswählen"),
        TutorialImgUi(R.drawable.admin_tutorial_2, "Unerwartete App auswählen"),
        TutorialImgUi(R.drawable.admin_tutorial_3, "Admin-Modus deaktivieren")
    ),

    preferredSettingsKind = SettingsKind.GENERAL
)
