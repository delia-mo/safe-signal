package com.deliamo.safesignal.domain.safetygate

data class SafetyGateSpec(
    val title: String,
    val headline: String = "Bist du gerade in Sicherheit?",
    val body: String,
    val bullets: List<String> = listOf(
        "Änderungen (z.B. Admin / Standort-Entzug) können sofort sichtbar sein. ",
        "Wenn du unsicher bist: ändere nichts und hol dir Unterstützung.",
        "Du kannst jederzeit den Quick-Exit nutzen oder Aktionen abbrechen."
    ),
    val cancelLabel: String = "Zurück zur Übersicht",
    val continueLabel: String = "Ich bin sicher (Weiter)",
    val moreInfoLabel: String = "Warum ist das wichtig?",
    val dialogTitle: String = "Warum wird das gefragt?",
    val dialogText: String = "Das Entfernen von Berechtigungen oder Apps kann dazu führen, dass die Tatperson " +
            "merkt, dass etwas passiert. Wenn du Sorge hast, dass das gefährlich sein könnte, breche ab " +
            "und wende dich an eine Beratungsstelle."
)
