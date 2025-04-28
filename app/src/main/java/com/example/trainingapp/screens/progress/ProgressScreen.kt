package com.example.trainingapp.screens.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingapp.screens.progress.components.ProgressOverviewTab
import com.example.trainingapp.screens.progress.components.StatsTab
import com.example.trainingapp.screens.progress.components.WorkoutHistoryTab
import com.example.trainingapp.ui.theme.BackgroundColor
import com.example.trainingapp.ui.theme.SportRed
import com.example.trainingapp.viewmodels.ProgressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    navController: NavController,
    viewModel: ProgressViewModel = viewModel()
) {
    val weeklyWorkouts by viewModel.weeklyWorkoutCount.collectAsState()
    val completedExercises by viewModel.exercisesCompleted.collectAsState()
    val totalWorkoutTime by viewModel.totalWorkoutTime.collectAsState()
    val recentWorkouts by viewModel.recentWorkouts.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf("Overview", "History", "Stats")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Progress") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            // Tab row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = SportRed
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> ProgressOverviewTab(
                    weeklyWorkouts = weeklyWorkouts,
                    completedExercises = completedExercises,
                    totalWorkoutTime = totalWorkoutTime
                )
                1 -> WorkoutHistoryTab(
                    recentWorkouts = recentWorkouts,
                    onWorkoutClick = { workoutId ->
                        // In a real app, navigate to workout details
                    }
                )
                2 -> StatsTab()
            }
        }
    }
}