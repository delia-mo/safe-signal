package com.deliamo.spywarecheck.domain.model

data class AppCandidate(
    val packageName: String,
    val label: String,
    val score: Int,
    val isSystem: Boolean
)

data class TutorialImg(
    val resId: Int,
    val desc: String
)

data class MatchResult(
    val expectedName: String,
    val candidates: List<AppCandidate>
)
