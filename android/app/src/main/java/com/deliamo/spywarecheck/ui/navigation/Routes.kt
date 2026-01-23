package com.deliamo.spywarecheck.ui.navigation

import android.net.Uri
import kotlinx.coroutines.flow.flow

object Routes {
    const val START = "start"
    const val QUICK_CHECK = "quick_check"
    const val RESULT = "result"
    const val SAFETY_GATE = "safety_gate"
    const val SCAN = "scan"
    const val FINDING_DETAIL = "finding/{findingId}"
    fun findingDetail(findingId: String) = "finding/$findingId"
    const val ACTION_FLOW = "actions/{flowId}"
    fun actionFlow(flowId: String) = "actions/${Uri.encode(flowId)}"
    const val ACTION_FLOW_STEP = "actions/{flowId}/{step}"
    fun actionFlowStep(flowId: String, step: Int) = "actions/${Uri.encode(flowId)}/$step"
    const val MEASURES = "measures"
}