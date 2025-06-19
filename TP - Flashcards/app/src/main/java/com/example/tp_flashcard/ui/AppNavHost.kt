package com.example.tp_flashcard.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tp_flashcard.ui.screens.FlashcardScreen
import com.example.tp_flashcard.ui.screens.HomeScreen
import com.example.tp_flashcard.viewmodel.FlashcardViewModel
import com.example.tp_flashcard.viewmodel.HomeViewModel

@Composable
fun AppNavHost(homeViewModel: HomeViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                homeViewModel = homeViewModel,
                onCategoryClick = { category ->
                    navController.navigate("flashcard/${category.id}")
                }
            )
        }

        composable("flashcard/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull()
            if (categoryId != null) {
                val flashcardViewModel: FlashcardViewModel = viewModel()
                // Initialise la session avec la catégorie donnée
                LaunchedEffect(Unit) {
                    flashcardViewModel.startSession(categoryId)
                }

                FlashcardScreen(
                    viewModel = flashcardViewModel,
                    navController = navController
                )
            }
        }
    }
}
