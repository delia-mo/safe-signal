package com.deliamo.spywarecheck.debug

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object DebugSettings {
    var simulateRoot by mutableStateOf(false)
}