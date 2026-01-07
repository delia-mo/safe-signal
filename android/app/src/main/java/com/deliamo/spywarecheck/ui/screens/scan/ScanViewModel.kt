package com.deliamo.spywarecheck.ui.screens.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ScanUiState {
    data object Idle : ScanUiState
    data object Running : ScanUiState
    data class Done(val findingsCount: Int, val finishedAtMills: Long) : ScanUiState
    data class Error(val message: String) : ScanUiState
}

class ScanViewModel: ViewModel() {
    private val _state = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val state: StateFlow<ScanUiState> = _state.asStateFlow()

    fun startScan() {
        // Prevent double starts
        if (_state.value is ScanUiState.Running) return

        _state.value = ScanUiState.Running

        viewModelScope.launch {
            try {
                // TODO replace with real checks later
                delay(1200)

                // TODO compute from real checks
                val findings = 0

                _state.value = ScanUiState.Done(
                    findingsCount = findings,
                    finishedAtMills = System.currentTimeMillis()
                )
            } catch (t: Throwable) {
                _state.value = ScanUiState.Error(
                    message = t.message ?: "Scan konnte nicht ausgeführt werden."
                )
            }
        }
    }

    fun reset() {
        _state.value = ScanUiState.Idle
    }
}