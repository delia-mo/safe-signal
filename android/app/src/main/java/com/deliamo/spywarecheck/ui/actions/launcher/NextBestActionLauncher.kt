package com.deliamo.spywarecheck.ui.actions.launcher

import android.content.Context
import com.deliamo.spywarecheck.domain.actions.NextBestAction
import com.deliamo.spywarecheck.ui.actions.util.onOpenSettings
import com.deliamo.spywarecheck.ui.actions.util.openAppDetails

object NextBestActionLauncher {

    fun handle(
        context: Context,
        action: NextBestAction,
        navigateToFlow: (flowId: String) -> Unit
    ) {
        when (action) {
            is NextBestAction.OpenAppDetails ->
                openAppDetails(context, action.packageName)

            is NextBestAction.OpenSettings ->
                onOpenSettings(context, action.kind)

            is NextBestAction.OpenActionFlow ->
                navigateToFlow(action.flowId)

            is NextBestAction.ChooseApp -> navigateToFlow(action.flowId)

        }
    }
}
