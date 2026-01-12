package com.deliamo.spywarecheck.data.scanner

import com.deliamo.spywarecheck.debug.DebugSettings
import com.deliamo.spywarecheck.domain.model.RootDetector

object ScannerServices {
    fun rootDetector(): RootDetector = DebugRootDetector  { DebugSettings.simulateRoot }
}