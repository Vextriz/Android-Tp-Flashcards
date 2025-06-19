package com.example.tp_flashcard.model

data class FlashCard(
    val id: Int,
    val categoryId: Int,
    val question: String,
    val answer: String
)

data class FlashCardCategory(
    val id: Int,
    val name: String
)