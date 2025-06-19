package com.example.tp_flashcard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tp_flashcard.model.FlashCard
import com.example.tp_flashcard.model.FlashcardRepository
import com.example.tp_flashcard.model.FlashcardUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FlashcardViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = FlashcardRepository.getInstance(app, viewModelScope)

    private val _uiState = MutableStateFlow(FlashcardUiState())
    val uiState: StateFlow<FlashcardUiState> = _uiState.asStateFlow()

    fun startSession(categoryId: Int) {
        viewModelScope.launch {
            repository.getFlashcardsForCategory(categoryId).collect { cards ->
                _uiState.value = FlashcardUiState(
                    currentIndex = 0,
                    cards = cards,
                    isSessionFinished = cards.isEmpty()
                )
            }
        }
    }
    /**
     * Passe à la carte suivante. Met fin à la session si on dépasse la dernière.
     */
    fun goToNextCard() {
        val currentState = _uiState.value
        val nextIndex = currentState.currentIndex + 1

        if (nextIndex < currentState.cards.size) {
            _uiState.value = currentState.copy(currentIndex = nextIndex)
        } else {
            _uiState.value = currentState.copy(isSessionFinished = true)
        }
    }

    /**
     * Passe à la carte précedent. Met fin à la session si on dépasse la dernière.
     */
    fun goToPreviousCard() {
        val currentState = _uiState.value
        val previousIndex = currentState.currentIndex -1

        if (previousIndex >= 0) {
            _uiState.value = currentState.copy(currentIndex = previousIndex)
        } else {
            _uiState.value = currentState.copy(isSessionFinished = true)
        }
    }
}
