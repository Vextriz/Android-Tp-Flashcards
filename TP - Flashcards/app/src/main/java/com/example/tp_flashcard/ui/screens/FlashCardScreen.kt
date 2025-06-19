@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.tp_flashcard.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tp_flashcard.ui.components.ProgressIndicator
import com.example.tp_flashcard.viewmodel.FlashcardViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    viewModel: FlashcardViewModel = viewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()
    // Dès que la session est finie, on revient
    if (uiState.isSessionFinished) {
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    val currentCard = uiState.cards.getOrNull(uiState.currentIndex) ?: return

    // Flip 3D
    val rotationAnim = remember { Animatable(0f) }
    var isFlipped by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current.density

    LaunchedEffect(uiState.currentIndex) {
        isFlipped = false
        rotationAnim.snapTo(0f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Révision") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Retour à l’accueil"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progression
            ProgressIndicator(
                current = uiState.currentIndex + 1,
                total = uiState.cards.size
            )

            AnimatedContent(
                targetState = currentCard,
                transitionSpec = { fadeIn(tween(300)) with fadeOut(tween(300)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) { card ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            rotationY = rotationAnim.value
                            cameraDistance = 8 * density
                        }
                        .clickable {
                            scope.launch {
                                val target = if (!isFlipped) 180f else 0f
                                rotationAnim.animateTo(target, tween(400))
                                isFlipped = !isFlipped
                            }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                // Redresse le verso pour ne pas l'afficher en miroir
                                rotationY = if (rotationAnim.value > 90f) 180f else 0f
                            }
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val text =
                            if (rotationAnim.value <= 90f) card.question else card.answer
                        Text(
                            text = text,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Boutons Précédent + Suivant + Accueil
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.goToPreviousCard() },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text("Précédent", style = MaterialTheme.typography.bodyLarge)
                }
                Button(
                    onClick = { viewModel.goToNextCard() },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text("Suivant", style = MaterialTheme.typography.bodyLarge)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedButton(
                onClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Retour à l'accueil", style = MaterialTheme.typography.bodyLarge)
            }

        }
    }
}
