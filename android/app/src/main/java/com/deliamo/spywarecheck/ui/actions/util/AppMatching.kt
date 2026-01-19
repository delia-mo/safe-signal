package com.deliamo.spywarecheck.ui.actions.util

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.deliamo.spywarecheck.domain.model.AppCandidate

fun normalizeLabelForMatch(raw: String): String {
    val noParens = raw.replace(Regex("\\s*\\(.*?\\)\\s*"), " ")
    return noParens.trim()
}

private fun norm(s: String): String =
    s.lowercase()
        .replace(Regex("[^a-z0-9äöüß ]"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()

private fun tokens(s: String): Set<String> =
    norm(s).split(" ").filter { it.length >= 2 }.toSet()

private fun safeLabelMatch(appLabel: String, expectedName: String): Boolean {
    val a = norm(appLabel)
    val e = norm(expectedName)
    if (a == e) return true

    val at = tokens(a)
    val et = tokens(e)
    if (at.size < 2) return false
    return et.containsAll(at)
}

fun resolveTopPackagesForFinding(
    pm: PackageManager,
    expectedNameRaw: String,
    pkgFromScan: String,
    max: Int = 2
): List<AppCandidate> {
    val expectedName = normalizeLabelForMatch(expectedNameRaw)
    val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

    fun isSystem(ai: ApplicationInfo): Boolean =
        (ai.flags and ApplicationInfo.FLAG_SYSTEM) != 0

    fun score(label: String, pkg: String, ai: ApplicationInfo): Int {
        if (pkg == "com.google.android.gms") return -1000

        val exact = norm(label) == norm(expectedName)
        val subset = safeLabelMatch(label, expectedName)
        if (!exact && !subset) return -1000

        var s = 0
        if (exact) s += 200 else s += 120
        if (!isSystem(ai)) s += 20

        val overlap = tokens(label).intersect(tokens(expectedName)).size
        s += overlap * 3
        return s
    }

    val candidates = apps.mapNotNull { ai ->
        val label = runCatching { pm.getApplicationLabel(ai).toString() }.getOrNull() ?: return@mapNotNull null
        val sc = score(label, ai.packageName, ai)
        if (sc < 0) null else AppCandidate(ai.packageName, label, sc, isSystem(ai))
    }.sortedByDescending { it.score }

    val scanCandidate = pkgFromScan.takeIf { it.isNotBlank() && it != "com.google.android.gms" }
    val scanLabel = scanCandidate?.let {
        runCatching {
            val ai = pm.getApplicationInfo(it, 0)
            pm.getApplicationLabel(ai).toString()
        }.getOrNull()
    }
    val scanIsSafe = scanCandidate != null && scanLabel != null && safeLabelMatch(scanLabel, expectedName)

    return if (scanIsSafe) {
        val scanApp = AppCandidate(scanCandidate, scanLabel, 999, isSystem = false)
        listOf(scanApp) + candidates.filterNot { it.packageName == scanCandidate }.take(max - 1)
    } else {
        candidates.take(max)
    }
}

