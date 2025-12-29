package com.deliamo.spywarecheck.ui.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlin.system.exitProcess

fun quickExitToBrowser(context: Context) {
    val url = "https://www.google.com/search?q=schnelle+einfache+rezepte"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
        // Opens browser in new task
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)

    // Cose app and remove from recents
    (context as? Activity)?.finishAndRemoveTask()
}