package com.example.tp_flashcard.model

data class FlashcardUiState(
    val currentIndex: Int = 0,
    val cards: List<FlashCard> = emptyList(),
    val isSessionFinished: Boolean = false
)