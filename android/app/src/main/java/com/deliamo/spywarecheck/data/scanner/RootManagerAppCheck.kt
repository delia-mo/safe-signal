package com.deliamo.spywarecheck.data.scanner

import android.content.pm.PackageManager
import android.text.TextDirectionHeuristics

object RootManagerAppCheck {

    // Known / common package names (not exhaustive)
    private val knownPackages = setOf(
        "com.topjohnwu.magisk", // Magisk
        "eu.chainfire.supersu", // SuperSU
        "com.noshufou.android.su", // Superuser
        "com.koushikdutta.superuser", // Superuser
        "com.yellowes.su", // Kingroot related
        "com.kingroot.kinguser", // Kingroot
        "com.kingo.root" // Kingroot
    )

    private val labelKeywords = listOf(
        "magisk",
        "supersu",
        "superuser",
        "kingroot",
        "kingo",
        "root manager"
    )

    fun findInstalledRootManagers(pm: PackageManager): List<String> {
        val found = mutableSetOf<String>()

        // Package allowlist
        for (pkg in knownPackages) {
            val label = safeGetLabel(pm, pkg)
            if (label != null) found += label
        }

        return found.toList().sorted()
    }

    private fun safeGetLabel(pm: PackageManager, pkg: String): String? {
        return try {
            val ai = pm.getApplicationInfo(pkg, 0)
            pm.getApplicationLabel(ai)?.toString()
        } catch (_: Throwable) { null }
    }
}