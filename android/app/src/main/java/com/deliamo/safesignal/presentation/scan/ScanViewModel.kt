package com.deliamo.safesignal.presentation.scan

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliamo.safesignal.data.scanner.AndroidScanner
import com.deliamo.safesignal.data.session.SessionService
import com.deliamo.safesignal.data.session.SessionStore
import com.deliamo.safesignal.domain.model.ScanResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ScanUiState {
    data object Idle : ScanUiState
    data object Running : ScanUiState
    data class Done(val result: ScanResult) : ScanUiState
    data class Error(val message: String) : ScanUiState
}

class ScanViewModel: ViewModel() {
    private val _state = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
    val state: StateFlow<ScanUiState> = _state.asStateFlow()

    fun startScan(context: Context) {
        // Prevent double starts
        if (_state.value is ScanUiState.Running) return

        _state.value = ScanUiState.Running

        viewModelScope.launch {
            try {
                val appContext = context.applicationContext
                val scanner = AndroidScanner(context.applicationContext)
                val result = scanner.run()

                val sessionService = SessionService(SessionStore(appContext))
                sessionService.onScanCompleted(result) // saves baseline if none
                if(sessionService.hasBaseline()) {
                    sessionService.updateResolvedFromRescan(result)
                }
                _state.value = ScanUiState.Done(result)
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