package com.deliamo.safesignal.ui.platform

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.deliamo.safesignal.domain.actions.SettingsKind

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

fun onOpenSettings(context: Context, kind: SettingsKind) {
    val intent = Intent(actionFor(kind)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        context.startActivity(
            Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

fun openManageApps(context: Context) {
    val candidates = listOf(
        Settings.ACTION_APPLICATION_SETTINGS,
        Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS,
        Settings.ACTION_SETTINGS
    )
    for (action in candidates) {
        try {
            context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            return
        } catch (_: ActivityNotFoundException) { /* next */ }
    }
}

fun openAppDetails(context: Context, packageName: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:$packageName")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        onOpenSettings(context, SettingsKind.GENERAL)
    }
}
