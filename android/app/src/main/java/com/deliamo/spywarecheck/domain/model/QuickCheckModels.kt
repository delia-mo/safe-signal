package com.deliamo.spywarecheck.domain.model

enum class QuickAnswer { YES, NO, UNSURE }

data class QuickQuestion(
  val id: String,
  val text: String
)

enum class QuickRisk { LOW, MEDIUM, HIGH }

data class QuickCheckResult(
  val risk: QuickRisk,
  val summary: String,
  val top3: List<String>
)
