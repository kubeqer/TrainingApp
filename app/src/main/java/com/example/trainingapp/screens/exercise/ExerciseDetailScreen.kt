package com.example.trainingapp.screens.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingapp.screens.exercise.components.ExerciseDetailCard
import com.example.trainingapp.ui.theme.BackgroundColor
import com.example.trainingapp.ui.theme.SportRed
import com.example.trainingapp.viewmodels.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailScreen(
    exerciseId: Long,
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel()
) {
    // Load the exercise details
    LaunchedEffect(exerciseId) {
        viewModel.getExerciseById(exerciseId)
    }

    val exercise by viewModel.exercise.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exercise?.name ?: "Exercise Details") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            exercise?.let { ex ->
                // Exercise image placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Exercise Demonstration", color = Color.White)
                }

                // Exercise details
                ExerciseDetailCard(
                    title = "Description",
                    content = ex.description
                )

                // Target muscles
                ExerciseDetailCard(
                    title = "Target Muscles",
                    content = "Primary: ${viewModel.getBodyPartName(ex.bodyPartId)}"
                )

                // Suggested sets/reps
                ExerciseDetailCard(
                    title = "Suggested Training",
                    content = """
                        Beginner: 3 sets of 8-10 reps
                        Intermediate: 4 sets of 10-12 reps
                        Advanced: 5 sets of 12-15 reps
                    """.trimIndent()
                )

                // Add to workout button
                Button(
                    onClick = { /* TODO: Implement add to workout */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SportRed)
                ) {
                    Text(
                        text = "Add to Workout",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } ?: run {
                // Show loading state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}