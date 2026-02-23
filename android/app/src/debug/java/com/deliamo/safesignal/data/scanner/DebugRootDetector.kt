package com.deliamo.safesignal.data.scanner

import com.deliamo.safesignal.domain.model.RootDetector
import com.deliamo.safesignal.domain.model.RootSignal

class DebugRootDetector(
    private val enabled: () -> Boolean
): RootDetector {

    override fun detect(): RootSignal {
        return if (enabled()) {
            RootSignal(
                isRootLikely = true,
                reasons = listOf("DEBUG: Root simuliert.")
            )
        } else {
            RootSignal(
                isRootLikely = false,
                reasons = emptyList()
            )
        }
    }
}