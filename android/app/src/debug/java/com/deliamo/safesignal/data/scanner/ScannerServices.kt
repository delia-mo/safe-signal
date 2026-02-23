package com.deliamo.safesignal.data.scanner

import com.deliamo.safesignal.debug.DebugSettings
import com.deliamo.safesignal.domain.model.RootDetector

object ScannerServices {
    fun rootDetector(): RootDetector = DebugRootDetector  { DebugSettings.simulateRoot }
}