package com.deliamo.spywarecheck.domain.quickcheck

import com.deliamo.spywarecheck.domain.model.*

object QuickCheckScoring {
    fun evaluate(answers: Map<String, QuickAnswer>): QuickCheckResult {
        val score: Int = answers.values.fold(0) { acc, answer ->
            acc + when (answer) {
                QuickAnswer.YES -> 2
                QuickAnswer.UNSURE -> 1
                QuickAnswer.NO -> 0
            }
        }

        val risk = when {
            score >= 10 -> QuickRisk.HIGH
            score >= 5 -> QuickRisk.MEDIUM
            else -> QuickRisk.LOW
        }

        val summary = when (risk) {
            QuickRisk.LOW -> "Aktuell gibt es eher wenig Hinweise. Wenn du unsicher bist, kannst du" +
                    " trotzdem dein Handy scannen."

            QuickRisk.MEDIUM -> "Es gibt einige Hinweise. Ein Scan kann helfen, typische Anzeichen " +
                    "zu prüfen."

            QuickRisk.HIGH -> "Es gibt viele Hinweise. Ein Scan könnte helfen, mehr herauszufinden. " +
                    "Gehe nur weiter, wenn du gerade sicher bist."
        }

        // TODO überarbeiten, Platzhalter
        val top3 = when (risk) {
            QuickRisk.LOW -> listOf(
                "Wenn du möchtest: Scan starten (prüft typische Anzeichen).",
                "Accounts absichern: Sicheres Passwort und Zwei-Faktor-Authentifizierung.",
                "Report speichern, falls du später Unterstützung brauchst."
            )

            QuickRisk.MEDIUM -> listOf(
                "Scan starten, wenn du gerade sicher bist.",
                "Wichtige Passwörter ändern und Zwei-Faktor-Authentifizierung einrichten (E-Mail zuerst).",
                "Notiere auffällige Dinge (Zeit und Beispiele), falls du später Beweise brauchst."
            )

            QuickRisk.HIGH -> listOf(
                "Wenn du sicher bist: Scan starten.",
                "Quick Exit bereithalten, falls du dich unwohl fühlst.",
                "Report speichern und ggf. Unterstützung holen."
            )
        }

        return QuickCheckResult(risk, summary, top3)
    }
}