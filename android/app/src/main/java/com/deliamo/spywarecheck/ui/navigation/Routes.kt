package com.deliamo.spywarecheck.ui.navigation

import android.net.Uri

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
}