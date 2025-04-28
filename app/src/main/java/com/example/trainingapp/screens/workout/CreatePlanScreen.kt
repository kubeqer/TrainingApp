package com.example.trainingapp.screens.workout

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import com.example.trainingapp.screens.workout.components.DaySelectionItem
import com.example.trainingapp.ui.theme.BackgroundColor
import com.example.trainingapp.ui.theme.SportRed
import com.example.trainingapp.ui.theme.TextMedium
import com.example.trainingapp.viewmodels.WorkoutPlanViewModel

private const val TAG = "CreatePlanScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlanScreen(
    navController: NavController,
    viewModel: WorkoutPlanViewModel = viewModel()
) {
    val planName by viewModel.planName.collectAsState()
    val daysPerWeek by viewModel.daysPerWeek.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Log.d(TAG, "CreatePlanScreen composable called")
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Log.e(TAG, "Error in CreatePlanScreen: $it")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Workout Plan") },
                navigationIcon = {
                    IconButton(onClick = {
                        Log.d(TAG, "Navigate back from CreatePlanScreen")
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (!isLoading) {
                        Log.d(TAG, "Creating plan with name: $planName and days: $daysPerWeek")
                        viewModel.createPlan()
                        navController.popBackStack()
                    }
                },
                containerColor = SportRed
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Rounded.Check, contentDescription = "Save Plan")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(BackgroundColor)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = SportRed
                )
            }

            errorMessage?.let {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(it)
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = planName,
                    onValueChange = {
                        if (!isLoading) {
                            viewModel.setPlanName(it)
                        }
                    },
                    label = { Text("Plan Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Text(
                    text = "DAYS PER WEEK",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMedium,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items((1..7).toList()) { day ->
                        DaySelectionItem(
                            day = day,
                            isSelected = day <= daysPerWeek,
                            onSelect = { selected ->
                                if (!isLoading) {
                                    viewModel.setDaysPerWeek(if (selected) day else day - 1)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}