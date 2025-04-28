package com.example.trainingapp.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Create
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingapp.MainViewModel
import com.example.trainingapp.navigation.AppDestinations
import com.example.trainingapp.screens.dashboard.components.ActionButton
import com.example.trainingapp.screens.dashboard.components.BodyPartButton
import com.example.trainingapp.screens.dashboard.components.ScheduleCard
import com.example.trainingapp.screens.dashboard.components.TodaysWorkoutCard
import com.example.trainingapp.ui.theme.BackgroundColor
import com.example.trainingapp.ui.theme.SportRed
import com.example.trainingapp.ui.theme.TextMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingDashboard(
    navController: NavController,
    mainViewModel: MainViewModel = viewModel()
) {
    // Fetch body parts from repository using ViewModel
    val bodyParts by mainViewModel.bodyParts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "FitStrong",
                        fontWeight = FontWeight.Bold,
                        color = SportRed,
                        fontSize = 24.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(BackgroundColor),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Body parts categories (horizontal scroll)
            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
            ) {
                Text(
                    text = "MUSCLE GROUPS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMedium,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 16.dp, bottom = 8.dp)
                ) {
                    items(bodyParts) { bodyPart ->
                        BodyPartButton(
                            bodyPart = bodyPart,
                            onClick = {
                                navController.navigate("exercises/${bodyPart.id}")
                            }
                        )
                    }
                }
            }

            // Workout actions section
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "QUICK ACTIONS",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMedium,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionButton(
                        title = "Create Plan",
                        icon = Icons.Rounded.Create,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(AppDestinations.CREATE_PLAN)
                        }
                    )

                    ActionButton(
                        title = "Add Exercise",
                        icon = Icons.Rounded.Add,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            // Navigate to add exercise screen
                            // For now, just go to exercise list with all exercises (body part id 0)
                            navController.navigate("exercises/0")
                        }
                    )
                }
            }

            // Schedule management
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "TRAINING SCHEDULE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMedium,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                )

                ScheduleCard(
                    onClick = {
                        navController.navigate(AppDestinations.CALENDAR)
                    }
                )
            }

            // Today's workout
            TodaysWorkoutCard(
                onClick = {
                    // For demo, navigate to a workout session with plan ID 1
                    navController.navigate("workout_session/1")
                },
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp)
            )
        }
    }
}