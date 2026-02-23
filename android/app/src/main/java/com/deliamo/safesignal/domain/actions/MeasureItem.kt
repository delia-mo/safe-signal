package com.deliamo.safesignal.domain.actions

import com.deliamo.safesignal.domain.model.Severity

data class MeasureItem(
    val id: String,
    val title: String,
    val summary: String,
    val severity: Severity,
    val ctaLabel: String,
    val flowId: String // -> Routes.actionFlowStep(flowId, 0)
)