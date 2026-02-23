package com.deliamo.safesignal.data.scanner

object RiskySystemPolicy {

    val allowListedLableKeywords: List<String> = listOf(
        "family link",
        "find",
        "find device",
        "gerät finden",
        "geräte finden",
        "kindersicherung",
        "parental",
        "device policy",
        "maps"
    )

    fun shouldShowSystemApp(packageName: String, appLabel: String?): Boolean {
        val label = appLabel?.lowercase() ?: return false
        return allowListedLableKeywords.any {kw -> label.contains(kw)}
    }
}