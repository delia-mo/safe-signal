package com.deliamo.safesignal.domain.model

data class AppCandidate(
    val packageName: String,
    val label: String,
    val score: Int,
    val isSystem: Boolean
)