package com.deliamo.spywarecheck.ui.screens.report

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliamo.spywarecheck.data.session.SessionStore
import com.deliamo.spywarecheck.domain.report.ActionUiState
import com.deliamo.spywarecheck.domain.report.ReportUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.jvm.Throws

class ReportViewModel : ViewModel() {

  private val _ui = MutableStateFlow(ReportUiState())
  val ui: StateFlow<ReportUiState> = _ui.asStateFlow()

  fun load(context: Context) {

    if (!_ui.value.isLoading && _ui.value.error == null) return

    viewModelScope.launch {
      try {
          _ui.value = _ui.value.copy(isLoading = true, error = null)

        val store = SessionStore(context.applicationContext)
        val session = store.load()

        val baseline = session.baseline
        val statusMap = session.actionStatus.mapValues { (_, v) ->
          when(v) {
            "DONE" -> ActionUiState.DONE
            "IN_PROGRESS" -> ActionUiState.IN_PROGRESS
            "SKIPPED" -> ActionUiState.SKIPPED
            else -> ActionUiState.NOT_STARTED
          }
        }
        _ui.value = ReportUiState(
          isLoading = false,
          hasBaseLine = baseline != null,
          baslineAtMillis = baseline?.createdAdMills,
          findingsBaseline = baseline?.findings ?: emptyList(),
          actionStatusByFindingId = statusMap
        )
      } catch (t: Throwable) {
        _ui.value = ReportUiState(
          isLoading = false,
          hasBaseLine = false,
          error = t.message ?: "Report konnte nicht geladen werden."
        )
      }
    }

  }
}
