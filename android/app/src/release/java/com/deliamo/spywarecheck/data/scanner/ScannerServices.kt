package com.deliamo.spywarecheck.data.scanner

import com.deliamo.spywarecheck.domain.model.RootDetector

object ScannerServices {
    // Default for release & debug (can be overridden in debug build via file in src/debug)
    fun rootDetector(): RootDetector = RealRootDetector()
}