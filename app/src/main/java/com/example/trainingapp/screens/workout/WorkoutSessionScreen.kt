// WorkoutSessionScreen.kt
package com.example.trainingapp.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingapp.screens.workout.components.WorkoutExerciseCard
import com.example.trainingapp.ui.theme.BackgroundColor
import com.example.trainingapp.ui.theme.SportRed
import com.example.trainingapp.viewmodels.WorkoutSessionViewModel
import com.example.trainingapp.viewmodels.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionScreen(
    planId: Long,
    navController: NavController,
    viewModel: WorkoutSessionViewModel = viewModel()
) {
    val exercises by viewModel.currentExercises.collectAsState()
    val elapsedTime by viewModel.elapsedTime.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()

    LaunchedEffect(planId) {
        viewModel.startWorkout(planId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.pauseTimer()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout in Progress") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleTimer() }) {
                        Icon(
                            if (isTimerRunning) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (isTimerRunning) "Pause" else "Resume"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = SportRed,
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Time: ${formatTime(elapsedTime)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Button(
                        onClick = {
                            viewModel.completeWorkout()
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = SportRed
                        )
                    ) {
                        Text("Finish Workout")
                    }
                }
            }
        }
    ) { paddingValues ->
        if (exercises.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "No exercises planned for today",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Add exercises to your workout or choose a rest day",
                        color = Color.Gray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = { /* Add exercise */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SportRed
                        )
                    ) {
                        Icon(Icons.Filled.AddCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Exercise")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(BackgroundColor)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(exercises) { exerciseWithSets ->
                    WorkoutExerciseCard(
                        exerciseName = exerciseWithSets.exercise.name,
                        sets = exerciseWithSets.sets,
                        onSetComplete = { setIndex, reps, weight ->
                            viewModel.recordSet(exerciseWithSets.exercise.id, setIndex, reps, weight)
                        }
                    )
                }

                item {
                    Button(
                        onClick = { /* Add exercise */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SportRed.copy(alpha = 0.1f),
                            contentColor = SportRed
                        )
                    ) {
                        Icon(Icons.Filled.AddCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Exercise")
                    }
                }
            }
        }
    }
}