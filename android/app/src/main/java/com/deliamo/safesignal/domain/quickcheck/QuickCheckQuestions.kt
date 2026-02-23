package com.deliamo.safesignal.domain.quickcheck

import com.deliamo.safesignal.domain.model.QuickQuestion

object QuickCheckQuestions {
    fun defaultQuestions(): List<QuickQuestion> = listOf(
        QuickQuestion("q1", "Weiß die Person Dinge über dich, die sie eigentlich nicht wissen kann?"),
        QuickQuestion("q2", "Weiß die Person, wo du bist?"),
        QuickQuestion("q3", "Wirst du von der Person häufig ungewollt kontaktiert?"),
        QuickQuestion("q4", "Verhalten sich deine Accounts komisch (z.B. Meldungen wie falsches Passwort" +
                " eingegeben oder unbekannte Logins)?"),
        QuickQuestion("q5", "Verhält sich dein Handy plötzlich anders (z.B. Akku schnell leer," +
                " Handy wird heiß oder langsam)?"),
        QuickQuestion("q6", "Hatte die Person, die du verdächtigst, Zugriff zu deinem Handy?"),
        QuickQuestion("q7", "Glaubst du, dass etwas auf deinem Smartphone installiert wurde?"),
    )
}

