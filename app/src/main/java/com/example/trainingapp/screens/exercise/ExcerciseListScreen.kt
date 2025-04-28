package com.example.trainingapp.screens.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingapp.screens.exercise.components.ExerciseCard
import com.example.trainingapp.ui.theme.BackgroundColor
import com.example.trainingapp.ui.theme.TextDark
import com.example.trainingapp.viewmodels.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(
    bodyPartId: Long,
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel()
) {
    LaunchedEffect(bodyPartId) {
        viewModel.loadExercisesByBodyPart(bodyPartId)
    }

    val exercises = viewModel.getFilteredExercises()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val bodyParts by viewModel.bodyParts.collectAsState()

    val bodyPartName = if (bodyPartId == 0L) "All Exercises" else bodyParts[bodyPartId] ?: "Exercises"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(bodyPartName) },
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search exercises") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = "Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
            )

            if (exercises.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "No exercises found for this body part"
                        else "No exercises match your search",
                        color = TextDark,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(exercises) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            bodyPartName = viewModel.getBodyPartName(exercise.bodyPartId),
                            onClick = {
                                navController.navigate("exercise/${exercise.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}