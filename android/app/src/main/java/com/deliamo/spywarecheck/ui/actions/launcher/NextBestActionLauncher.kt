package com.deliamo.spywarecheck.ui.actions.launcher

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.deliamo.spywarecheck.domain.actions.NextBestAction
import com.deliamo.spywarecheck.domain.actions.SettingsKind

object NextBestActionLauncher {

    private const val ACTION_DEVICE_ADMIN_SETTINGS = "android.settings.DEVICE_ADMIN_SETTINGS"

    fun handle(
        context: Context,
        action: NextBestAction,
        navigateToFlow: (flowId: String) -> Unit
    ) {
        when (action) {
            is NextBestAction.OpenAppDetails -> openAppDetails(context, action.packageName)
            is NextBestAction.ChooseApp -> navigateToFlow(action.flowId)
            is NextBestAction.OpenActionFlow -> navigateToFlow(action.flowId)
            is NextBestAction.OpenSettings -> openSettings(context, action.kind)
        }
    }

    private fun openSettings(context: Context, kind: SettingsKind) {
        val intent = Intent(actionFor(kind)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            context.startActivity(
                Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }

        runCatching { ContextCompat.startActivity(context, intent, null) }
    }

    private fun actionFor(kind: SettingsKind): String {
        return when (kind) {
            SettingsKind.ACCESSIBILITY -> Settings.ACTION_ACCESSIBILITY_SETTINGS
            SettingsKind.LOCATION -> Settings.ACTION_LOCATION_SOURCE_SETTINGS
            SettingsKind.LOCATION_APP_PERMISSION ->
                Settings.ACTION_LOCATION_SOURCE_SETTINGS

            SettingsKind.SECURITY -> Settings.ACTION_SECURITY_SETTINGS
            SettingsKind.GENERAL -> Settings.ACTION_SETTINGS
        }
    }

    private fun openAppDetails(context: Context, pkg: String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$pkg")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}