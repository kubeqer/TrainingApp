package com.example.trainingapp.screens.calendar

import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trainingapp.TrainingApp
import com.example.trainingapp.data.repository.PlanRepository
import com.example.trainingapp.navigation.AppDestinations
import com.example.trainingapp.screens.calendar.components.WeekView
import com.example.trainingapp.ui.theme.BackgroundColor
import com.example.trainingapp.ui.theme.TextMedium
import com.example.trainingapp.ui.theme.SportRed
import com.example.trainingapp.viewmodels.CalendarViewModel
import com.example.trainingapp.viewmodels.PlanViewModel
import com.example.trainingapp.viewmodels.PlanViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CalendarScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    calendarVm: CalendarViewModel = viewModel()
) {

    val app = LocalContext.current.applicationContext as TrainingApp
    val planRepo = remember { PlanRepository(app.database.planDao(), app.database.planExerciseDao()) }
    val planVm: PlanViewModel = viewModel(factory = PlanViewModelFactory(planRepo))

    LaunchedEffect(planVm.activePlanId) {
        planVm.activePlanId?.takeIf { it > 0L }?.let { calendarVm.activatePlan(it) }
    }

    val activePlan by calendarVm.activePlan.collectAsStateWithLifecycle()
    val workoutDays by calendarVm.workoutDays.collectAsStateWithLifecycle()
    val exerciseMap by calendarVm.exerciseMap.collectAsStateWithLifecycle()

    val weekSchedule by remember(workoutDays, exerciseMap) {
        derivedStateOf { calendarVm.getCurrentWeekSchedule() }
    }

    val today = remember { Calendar.getInstance().time }
    val monthFmt = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }

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
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(BackgroundColor)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = monthFmt.format(today),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            WeekView(weekSchedule = weekSchedule, today = today)

            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Active Plan", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    activePlan?.let { plan ->
                        Text(plan.planName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text(
                            text = "${plan.daysPerWeek} days per week",
                            fontSize = 14.sp,
                            color = TextMedium
                        )
                    } ?: Text("No active plan selected", fontSize = 16.sp, color = TextMedium)

                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (activePlan == null) navController.navigate(AppDestinations.CREATE_PLAN)
                            else navController.navigate(AppDestinations.EDIT_PLANS)
                        },
                        Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SportRed)
                    ) {
                        Text(if (activePlan == null) "Create New Plan" else "Change Plan")
                    }
                }
            }

            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Today's Training", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    val entry = weekSchedule.firstOrNull { isSameDay(it.date, today) }
                    val exercisesToday = entry?.exerciseIds.orEmpty()

                    if (entry != null && exercisesToday.isNotEmpty()) {

                        val dayName = entry.workoutDay?.dayName ?: dowName(weekday(today))
                        val count = exercisesToday.size
                        val estimate = count * 10

                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.FitnessCenter, contentDescription = null, tint = SportRed)
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(dayName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Text("$count exercises • Est. $estimate min", fontSize = 14.sp, color = TextMedium)
                            }
                            Button(
                                onClick = {
                                    activePlan?.let { plan ->
                                        navController.navigate(
                                            AppDestinations.WORKOUT_SESSION.replace("{planId}", plan.planId.toString())
                                        )
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SportRed)
                            ) {
                                Text("Start Workout")
                            }
                        }
                    } else {
                        Box(
                            Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Rest day – no workout scheduled", color = TextMedium)
                        }
                    }
                }
            }

            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Upcoming Workouts", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    val upcoming = weekSchedule
                        .filter { !isSameDay(it.date, today) && it.exerciseIds.isNotEmpty() }
                        .take(3)

                    if (upcoming.isEmpty()) {
                        Box(
                            Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No upcoming workouts this week", color = TextMedium)
                        }
                    } else {
                        val fmt = SimpleDateFormat("EEE", Locale.getDefault())
                        upcoming.forEach { ds ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(fmt.format(ds.date), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(48.dp))
                                Spacer(Modifier.width(16.dp))
                                Text(ds.workoutDay?.dayName ?: dowName(weekday(ds.date)), fontSize = 16.sp, modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

fun isSameDay(d1: Date, d2: Date): Boolean {
    val c1 = Calendar.getInstance().apply { time = d1 }
    val c2 = Calendar.getInstance().apply { time = d2 }
    return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
}

private fun weekday(date: Date): Int {
    val cal = Calendar.getInstance().apply { time = date }
    return if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 7 else cal.get(Calendar.DAY_OF_WEEK) - 1
}

private fun dowName(dow: Int): String = when (dow) {
    1 -> "Mon"; 2 -> "Tue"; 3 -> "Wed"; 4 -> "Thu"
    5 -> "Fri"; 6 -> "Sat"; 7 -> "Sun"
    else -> ""
}
