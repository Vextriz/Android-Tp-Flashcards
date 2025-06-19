package com.example.tp_flashcard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tp_flashcard.ui.screens.FlashcardScreen
import com.example.tp_flashcard.ui.screens.HomeScreen
import com.example.tp_flashcard.viewmodel.FlashcardViewModel
import com.example.tp_flashcard.viewmodel.HomeViewModel

@Composable
fun FlashcardNavHost() {
    val navController = rememberNavController()
    // HomeViewModel lié à l'activité
    val homeViewModel: HomeViewModel = viewModel()

    NavHost(navController = navController, startDestination = "home") {
        // Écran d'accueil
        composable("home") {
            HomeScreen(
                homeViewModel = homeViewModel,
                onCategoryClick = { category ->
                    navController.navigate("flashcard/${category.id}")
                }
            )
        }

        // Écran de révision : on déclare l'argument categoryId comme Int
        composable(
            route = "flashcard/{categoryId}",
            arguments = listOf(navArgument("categoryId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            // Avec cette déclaration, categoryId ne peut plus être null
            val categoryId = backStackEntry.arguments!!.getInt("categoryId")
            // ViewModel scoped à ce NavBackStackEntry
            val flashcardViewModel: FlashcardViewModel = viewModel()

            // On démarre la session une seule fois à l'entrée de la route
            LaunchedEffect(categoryId) {
                flashcardViewModel.startSession(categoryId)
            }

            FlashcardScreen(
                viewModel = flashcardViewModel,
                navController = navController
            )
        }
    }
}
