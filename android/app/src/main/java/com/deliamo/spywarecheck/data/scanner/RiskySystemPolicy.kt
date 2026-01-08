package com.deliamo.spywarecheck.data.scanner

object RiskySystemPolicy {

    val allowListedLableKeywords: List<String> = listOf(
        "google play",
        "family link",
        "find phone",
        "find device",
        "gerät finden",
        "geräte finden",
        "kindersicherung",
        "parental",
        "verwaltung",
        "device policy"
    )

    fun shouldShowSystemApp(packageName: String, appLabel: String?): Boolean {
        val label = appLabel?.lowercase() ?: return false
        return allowListedLableKeywords.any {kw -> label.contains(kw)}
    }
}