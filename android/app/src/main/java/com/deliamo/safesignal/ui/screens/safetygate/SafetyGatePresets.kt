package com.deliamo.safesignal.ui.screens.safetygate

import com.deliamo.safesignal.domain.safetygate.SafetyGateSpec

object SafetyGatePresets {
    val Scan = SafetyGateSpec(
        title = "Bevor du den Scan startest",
        body = "Ein Scan kann auffallen. Bitte führe den Scan nur durch, wenn die Tatperson nicht in deiner " +
                "Nähe ist und du in einer sicheren Umgebung bist. ",
    )

    val Actions = SafetyGateSpec(
        title = "Bevor du etwas änderst",
        body = "Das Entziehen von Berechtigungen oder Entfernen von Apps kann auffallen. Bitte mach " +
                "das nur in einem sicheren Moment, wenn die Tatperson nicht in der Nähe ist."
    )
}