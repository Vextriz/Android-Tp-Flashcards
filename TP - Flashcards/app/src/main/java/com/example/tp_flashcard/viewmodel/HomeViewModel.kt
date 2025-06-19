package com.example.tp_flashcard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tp_flashcard.model.FlashcardRepository
import com.example.tp_flashcard.model.FlashCardCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repository = FlashcardRepository.getInstance(app, viewModelScope)

    private val _categories = MutableStateFlow<List<FlashCardCategory>>(emptyList())
    val categories: StateFlow<List<FlashCardCategory>> = _categories.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getCategories().collect { list ->
                _categories.value = list
            }
        }
    }
}