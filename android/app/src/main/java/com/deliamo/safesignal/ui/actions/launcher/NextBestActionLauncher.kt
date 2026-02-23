package com.deliamo.safesignal.ui.actions.launcher

import android.content.Context
import com.deliamo.safesignal.domain.actions.NextBestAction
import com.deliamo.safesignal.ui.platform.onOpenSettings
import com.deliamo.safesignal.ui.platform.openAppDetails

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
