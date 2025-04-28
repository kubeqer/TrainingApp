package com.example.trainingapp.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FitnessCenter
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
import com.example.trainingapp.screens.calendar.components.WeekView
import com.example.trainingapp.ui.theme.BackgroundColor
import com.example.trainingapp.ui.theme.CardColor
import com.example.trainingapp.ui.theme.SportRed
import com.example.trainingapp.ui.theme.TextDark
import com.example.trainingapp.ui.theme.TextMedium
import com.example.trainingapp.viewmodels.CalendarViewModel
import com.example.trainingapp.viewmodels.DaySchedule
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: CalendarViewModel = viewModel()
) {
    val activePlan by viewModel.activePlan.collectAsState()
    val weekSchedule = viewModel.getCurrentWeekSchedule()
    val today = Calendar.getInstance().time
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Training Schedule") },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Month header
            Text(
                text = monthFormat.format(today),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // Week view
            WeekView(
                weekSchedule = weekSchedule,
                today = today
            )

            // Active plan
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Active Plan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        if (activePlan != null) {
                            OutlinedButton(
                                onClick = { /* Navigate to change active plan */ },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = SportRed
                                )
                            ) {
                                Text("Change")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (activePlan != null) {
                        Text(
                            text = activePlan!!.planName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "${activePlan!!.daysPerWeek} days per week",
                            fontSize = 14.sp,
                            color = TextMedium
                        )
                    } else {
                        Text(
                            text = "No active plan selected",
                            fontSize = 16.sp,
                            color = TextMedium
                        )

                        Button(
                            onClick = { navController.navigate("create_plan") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SportRed
                            )
                        ) {
                            Text("Create New Plan")
                        }
                    }
                }
            }

            // Today's workout
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
                        text = "Today's Training",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val todayWorkout = weekSchedule.find { isSameDay(it.date, today) }?.workoutDay

                    if (todayWorkout != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FitnessCenter,
                                contentDescription = null,
                                tint = SportRed,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = todayWorkout.dayName,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp
                                )

                                Text(
                                    text = "5 exercises • Est. 45 min",
                                    color = TextMedium,
                                    fontSize = 14.sp
                                )
                            }

                            Button(
                                onClick = { navController.navigate("workout_session/${activePlan?.planId ?: 0}") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SportRed
                                )
                            ) {
                                Text("Start")
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Rest day - no workout scheduled",
                                color = TextMedium
                            )
                        }
                    }
                }
            }

            // Upcoming workouts
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
                        text = "Upcoming Workouts",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val upcomingWorkouts = weekSchedule
                        .filter { !isSameDay(it.date, today) && it.workoutDay != null }
                        .take(3)

                    if (upcomingWorkouts.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No upcoming workouts this week",
                                color = TextMedium
                            )
                        }
                    } else {
                        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

                        upcomingWorkouts.forEach { daySchedule ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dayFormat.format(daySchedule.date),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(48.dp)
                                )

                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = daySchedule.workoutDay?.dayName ?: "",
                                    fontSize = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}