package com.deliamo.spywarecheck.ui.navigation

import android.net.Uri

object Routes {
    const val START = "start"
    const val QUICK_CHECK = "quick_check"
    const val RESULT = "result"
    const val SCAN_SAFETY_GATE = "safety_gate"
    const val ACTION_SAFETY_GATE = "action_gate/{flowId}"
    fun actionSafetyGate(flowId: String) = "action_gate/${Uri.encode(flowId)}"
    const val SCAN = "scan"
    const val FINDING_DETAIL = "finding/{findingId}"
    fun findingDetail(findingId: String) = "finding/${Uri.encode(findingId)}"
    const val ACTION_FLOW = "actions/{flowId}"
    const val ACTION_FLOW_STEP = "actions/{flowId}/{step}"
    fun actionFlowStep(flowId: String, step: Int) = "actions/${Uri.encode(flowId)}/$step"
    const val MEASURES = "measures"
}