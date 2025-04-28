package com.example.trainingapp.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
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
import com.example.trainingapp.screens.profile.components.MeasurementItem
import com.example.trainingapp.screens.profile.components.SettingsItem
import com.example.trainingapp.ui.theme.BackgroundColor
import com.example.trainingapp.ui.theme.CardColor
import com.example.trainingapp.ui.theme.SportRed
import com.example.trainingapp.ui.theme.TextDark
import com.example.trainingapp.ui.theme.TextMedium
import com.example.trainingapp.viewmodels.ProfileViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val userName by viewModel.userName.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val height by viewModel.height.collectAsState()
    val fitnessGoal by viewModel.fitnessGoal.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()

    var editedName by remember { mutableStateOf(userName) }
    var editedWeight by remember { mutableStateOf(weight.toString()) }
    var editedHeight by remember { mutableStateOf(height.toString()) }
    var editedGoal by remember { mutableStateOf(fitnessGoal) }

    LaunchedEffect(userName, weight, height, fitnessGoal) {
        editedName = userName
        editedWeight = weight.toString()
        editedHeight = height.toString()
        editedGoal = fitnessGoal
    }

    val bmi = viewModel.calculateBMI()
    val bmiCategory = viewModel.getBMICategory()
    val df = DecimalFormat("#.#")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (isEditMode) {
                                viewModel.updateUserName(editedName)
                                viewModel.updateWeight(editedWeight.toFloatOrNull() ?: weight)
                                viewModel.updateHeight(editedHeight.toIntOrNull() ?: height)
                                viewModel.updateFitnessGoal(editedGoal)
                                viewModel.saveUserData()
                            }
                            viewModel.toggleEditMode()
                        }
                    ) {
                        Icon(
                            if (isEditMode) Icons.Rounded.Check else Icons.Rounded.Edit,
                            contentDescription = if (isEditMode) "Save" else "Edit"
                        )
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(SportRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userName.firstOrNull()?.toString() ?: "U",
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isEditMode) {
                        OutlinedTextField(
                            value = editedName,
                            onValueChange = { editedName = it },
                            label = { Text("Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = userName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Body Measurements",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (isEditMode) {
                        OutlinedTextField(
                            value = editedWeight,
                            onValueChange = { editedWeight = it },
                            label = { Text("Weight (kg)") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = editedHeight,
                            onValueChange = { editedHeight = it },
                            label = { Text("Height (cm)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MeasurementItem(
                                label = "Weight",
                                value = "$weight kg",
                                modifier = Modifier.weight(1f)
                            )

                            MeasurementItem(
                                label = "Height",
                                value = "$height cm",
                                modifier = Modifier.weight(1f)
                            )

                            MeasurementItem(
                                label = "BMI",
                                value = "${df.format(bmi)}\n$bmiCategory",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Fitness Goal",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (isEditMode) {
                        val goals = listOf("Build Muscle", "Lose Weight", "Improve Fitness", "Increase Strength")

                        goals.forEach { goal ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = goal == editedGoal,
                                    onClick = { editedGoal = goal }
                                )
                                Text(goal)
                            }
                        }
                    } else {
                        Text(
                            text = fitnessGoal,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}