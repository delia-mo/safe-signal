package com.deliamo.spywarecheck.domain.quickcheck

import com.deliamo.spywarecheck.domain.model.QuickQuestion

object QuickCheckQuestions {
    fun defaultQuestions(): List<QuickQuestion> = listOf(
        QuickQuestion("q1", "Weiß die Person Dinge über dich, die sie eigentlich nicht wissen kann?"),
        QuickQuestion("q2", "Weiß die Person, wo du bist?"),
        QuickQuestion("q3", "Sagen Leute, du hättest Nachrichten geschickt, die du nicht gesendet hast?"),
        QuickQuestion("q4", "Verhalten sich deine Accounts komisch (z.B. Meldungen wie falsches Passwort" +
                " eingegeben oder unbekannte Logins)?"),
        QuickQuestion("q5", "Verhält sich dein Handy plötzlich anders (z.B. Akku schnell leer," +
                " Handy wird heiß oder langsam)?"),
        QuickQuestion("q6", "Hatte die Person, die du verdächtigst, Zugriff zu deinem Handy?"),
    )
}

