package com.example.trainingapp.screens.workout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlanScreen(
    navController: NavController,
    viewModel: WorkoutPlanViewModel = viewModel()
) {
    val planName by viewModel.planName.collectAsState()
    val daysPerWeek by viewModel.daysPerWeek.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Workout Plan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.createPlan()
                    navController.popBackStack()
                },
                containerColor = SportRed
            ) {
                Icon(Icons.Rounded.Check, contentDescription = "Save Plan")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Plan name input
            OutlinedTextField(
                value = planName,
                onValueChange = { viewModel.setPlanName(it) },
                label = { Text("Plan Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Days per week selection
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
                            viewModel.setDaysPerWeek(if (selected) day else day - 1)
                        }
                    )
                }
            }
        }
    }
}
