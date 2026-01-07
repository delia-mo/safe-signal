package com.deliamo.spywarecheck.ui.screens.quickcheck

import androidx.lifecycle.ViewModel
import com.deliamo.spywarecheck.domain.model.*
import com.deliamo.spywarecheck.domain.quickcheck.QuickCheckQuestions
import com.deliamo.spywarecheck.domain.quickcheck.QuickCheckScoring
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

// QuickCheckUiState, answerCurrent, ruft evaluate aus domain auf

data class QuickCheckUiState(
    val questions: List<QuickQuestion> = emptyList(),
    val index: Int = 0,
    val answers: Map<String, QuickAnswer> = emptyMap()
) {
    val total: Int get() = questions.size
    val currentQuestion: QuickQuestion? get() = questions.getOrNull(index)
    val progressLabel: String get() = "${index + 1}/$total"
    val isLast: Boolean get() = index == total - 1
    val canGoBack: Boolean get() = index > 0
}

class QuickCheckViewModel: ViewModel() {
    private val _ui = MutableStateFlow(
        QuickCheckUiState(questions = QuickCheckQuestions.defaultQuestions())
    )

    val ui: StateFlow<QuickCheckUiState> = _ui

    fun answerCurrent(answer: QuickAnswer) {
        _ui.update { state ->
            val q = state.currentQuestion ?: return
            state.copy(answers = state.answers + (q.id to answer))
        }
    }

    fun next(): Boolean {
        var moved = false
        _ui.update { state ->
            val nextIndex = (state.index + 1).coerceAtMost(state.total - 1)
            moved = nextIndex != state.index
            state.copy(index = nextIndex)
        }
        return moved
    }

    fun previous(): Boolean {
        var moved = false
        _ui.update { state ->
            val prevIndex = (state.index - 1).coerceAtLeast(0)
            moved = prevIndex != state.index
            state.copy(index = prevIndex)
        }
        return moved
    }

    fun buildResult() = QuickCheckScoring.evaluate(_ui.value.answers)
}