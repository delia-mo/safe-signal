package com.deliamo.safesignal.domain.report

import com.deliamo.safesignal.domain.session.StoredFinding

enum class ActionUiState { NOT_STARTED, IN_PROGRESS, DONE, SKIPPED }

data class ReportUiState(
  val isLoading: Boolean = true,
  val hasBaseline: Boolean = false,
  val baslineAtMillis: Long? = null,
  val lastScanAtMillis: Long? = null,
  val findingsBaseline: List<StoredFinding> = emptyList(),
  val actionStatusByFindingId: Map<String, ActionUiState> = emptyMap(),
  val error: String? = null
)
