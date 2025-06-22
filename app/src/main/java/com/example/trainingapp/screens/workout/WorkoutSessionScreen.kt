package com.example.trainingapp.screens.workout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingapp.navigation.AppDestinations
import com.example.trainingapp.screens.calendar.components.WeekView
import com.example.trainingapp.viewmodels.CalendarViewModel
import com.example.trainingapp.viewmodels.ExerciseViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutSessionScreen(
    planId: Long,
    navController: NavController,
    calendarVm: CalendarViewModel = viewModel(),
    exerciseVm: ExerciseViewModel = viewModel()
) {
    // Ensure plan is active before loading
    LaunchedEffect(planId) {
        calendarVm.activatePlan(planId)
    }

    // Collect workout days and exercise map from calendar VM
    val workoutDays by calendarVm.workoutDays.collectAsStateWithLifecycle()
    val exerciseMap by calendarVm.exerciseMap.collectAsStateWithLifecycle()

    // Build current week schedule whenever underlying data changes
    val weekSchedule by remember(workoutDays, exerciseMap) {
        derivedStateOf { calendarVm.getCurrentWeekSchedule() }
    }

    // Collect full exercise list
    val allExercises by exerciseVm.exercises.collectAsStateWithLifecycle()

    // Track completed state per exercise
    val completedState = remember { mutableStateMapOf<Long, Boolean>() }

    // Determine today's entries
    val today = Calendar.getInstance().time
    val todayEntry = weekSchedule.firstOrNull { ds ->
        val c1 = Calendar.getInstance().apply { time = ds.date }
        val c2 = Calendar.getInstance().apply { time = today }
        c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }
    val todayIds = todayEntry?.exerciseIds.orEmpty()
    val todayExercises = allExercises.filter { it.id in todayIds }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout Session") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (todayExercises.isEmpty()) {
                Text("No exercises scheduled for today.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(todayExercises) { exercise ->
                        // initialize default state
                        val checked = completedState.getOrPut(exercise.id) { false }
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            colors = CardDefaults.cardColors()
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { completedState[exercise.id] = it },
                                    modifier = Modifier.padding(end = 16.dp)
                                )
                                Text(
                                    text = exercise.name,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        navController.navigate(
                                            AppDestinations.EXERCISE_DETAIL
                                                .replace("{exerciseId}", exercise.id.toString())
                                        )
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.MoreVert,
                                        contentDescription = "Details"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
