package com.example.tp_flashcard.model

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FlashcardRepository private constructor(private val db: AppDatabase) {

    fun getCategories(): Flow<List<FlashCardCategory>> =
        db.categoryDao()
            .getAllCategories()
            .map { it.map { e -> FlashCardCategory(e.id, e.name) } }

    fun getFlashcardsForCategory(catId: Int): Flow<List<FlashCard>> =
        db.flashCardDao()
            .getCardsForCategory(catId)
            .map { it.map { e -> FlashCard(e.id, e.categoryId, e.question, e.answer) } }

    companion object {
        @Volatile
        private var INSTANCE: FlashcardRepository? = null

        fun getInstance(context: Context, scope: CoroutineScope): FlashcardRepository {
            return INSTANCE ?: synchronized(this) {
                val db = AppDatabase.getInstance(context, scope)
                val instance = FlashcardRepository(db)
                INSTANCE = instance
                instance
            }
        }
    }
}