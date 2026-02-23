package com.deliamo.safesignal.domain.actions

sealed class NextBestAction(open val label: String) {
    data class OpenSettings(
        override val label: String,
        val kind: SettingsKind
    ): NextBestAction(label)

    data class OpenActionFlow(
        override val label: String,
        val flowId: String // same as findingId
    ): NextBestAction(label)

    data class OpenAppDetails(
        override val label: String,
        val packageName: String
    ): NextBestAction(label)

    data class ChooseApp(
        override val label: String,
        val flowId: String
    ): NextBestAction(label)
}