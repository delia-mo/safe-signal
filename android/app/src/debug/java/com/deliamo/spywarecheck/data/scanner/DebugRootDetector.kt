package com.deliamo.spywarecheck.data.scanner

import com.deliamo.spywarecheck.domain.model.RootDetector
import com.deliamo.spywarecheck.domain.model.RootSignal

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