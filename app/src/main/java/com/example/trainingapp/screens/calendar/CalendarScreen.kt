package com.example.trainingapp.screens.calendar

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.trainingapp.navigation.AppDestinations
import com.example.trainingapp.screens.calendar.components.WeekView
import com.example.trainingapp.ui.theme.BackgroundColor
import com.example.trainingapp.ui.theme.SportRed
import com.example.trainingapp.ui.theme.TextMedium
import com.example.trainingapp.viewmodels.CalendarViewModel
import com.example.trainingapp.viewmodels.PlanViewModel
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "CalendarScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    calendarVm: CalendarViewModel,
    planVm:     PlanViewModel
) {
    // 1) Gdy zmieni się activePlanId w planVm, ładujemy dane do calendarVm
    LaunchedEffect(planVm.activePlanId) {
        planVm.activePlanId
            ?.takeIf { it > 0 }
            ?.let { calendarVm.activatePlan(it) }
    }

    // 2) Pobieramy dni i ćwiczenia jako StateFlow
    val workoutDays  by calendarVm.workoutDays.collectAsState()
    val weekSchedule by remember(workoutDays) { derivedStateOf { calendarVm.getCurrentWeekSchedule() } }

    // 3) Oba modele planu: in-memory UI i encja z Room
    val activePlanUI     = remember(planVm.plans, planVm.activePlanId) {
        planVm.plans.firstOrNull { it.id == planVm.activePlanId }
    }
    val activePlanEntity by calendarVm.activePlan.collectAsState()

    // 4) Obliczamy „dzisiaj” oraz jego day-of-week 1=Mon…7=Sun
    val todayCal = Calendar.getInstance()
    val rawDow   = todayCal.get(Calendar.DAY_OF_WEEK)        // 1=Sun…7=Sat
    val todayDow = if (rawDow == Calendar.SUNDAY) 7 else rawDow - 1
    val today    = todayCal.time

    val monthFmt = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }

    Log.d(TAG, "CalendarScreen: planDB=${activePlanEntity?.planName}, weekSize=${weekSchedule.size}")

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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(BackgroundColor)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Month + strip ---
            Text(
                text = monthFmt.format(today),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            WeekView(weekSchedule = weekSchedule, today = today)

            // --- Active Plan ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Active Plan", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    if (activePlanUI != null) {
                        Text(activePlanUI.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Text("${activePlanUI.days.size} days per week",
                            fontSize = 14.sp, color = TextMedium)
                    } else {
                        Text("No active plan selected",
                            fontSize = 16.sp, color = TextMedium)
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (activePlanUI == null)
                                navController.navigate(AppDestinations.CREATE_PLAN)
                            else
                                navController.navigate(AppDestinations.EDIT_PLANS)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors   = ButtonDefaults.buttonColors(containerColor = SportRed)
                    ) {
                        Text(if (activePlanUI == null) "Create New Plan" else "Change Plan")
                    }
                }
            }

            // --- Today's Training ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Today's Training", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    // szukamy wpis na dzisiaj
                    val todaySchedule = weekSchedule.find { it.workoutDay?.dayNumber == todayDow }
                    val count         = todaySchedule?.exerciseIds?.size ?: 0
                    val estimate      = count * 10

                    if (todaySchedule != null && count > 0) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.FitnessCenter, contentDescription = null,
                                tint = SportRed, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(todaySchedule.workoutDay!!.dayName,
                                    fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                Text("$count exercises • Est. $estimate min",
                                    fontSize = 14.sp, color = TextMedium)
                            }
                            Button(
                                onClick = {
                                    // przekazujemy planId pobrany z encji DB
                                    val planId = activePlanEntity?.planId ?: return@Button
                                    navController.navigate(
                                        AppDestinations.WORKOUT_SESSION
                                            .replace("{planId}", planId.toString())
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SportRed)
                            ) {
                                Text("Start")
                            }
                        }
                    } else {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Rest day – no workout scheduled", color = TextMedium)
                        }
                    }
                }
            }

            // --- Upcoming Workouts ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Upcoming Workouts", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))

                    if (activePlanUI == null) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No active plan selected", color = TextMedium)
                        }
                    } else {
                        // proste 3 najbliższe dni z ćwiczeniami
                        val sorted  = activePlanUI.days.sorted()
                        val upcoming = (sorted.dropWhile { it <= todayDow } + sorted.takeWhile { it <= todayDow })
                            .take(3)

                        if (upcoming.isEmpty()) {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No upcoming workouts this week", color = TextMedium)
                            }
                        } else {
                            val fmt = SimpleDateFormat("EEE", Locale.getDefault())
                            upcoming.forEach { dow ->
                                val cal = Calendar.getInstance().apply {
                                    while (let {
                                            val w = get(Calendar.DAY_OF_WEEK)
                                            if (w == Calendar.SUNDAY) 7 else w - 1
                                        } != dow) {
                                        add(Calendar.DAY_OF_YEAR, 1)
                                    }
                                }
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(fmt.format(cal.time),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(48.dp))
                                    Spacer(Modifier.width(16.dp))
                                    Text(dayName(dow),
                                        fontSize = 16.sp,
                                        modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// pomocnicza funkcja na koniec pliku
private fun dayName(num: Int) = when(num) {
    1 -> "Mon"; 2 -> "Tue"; 3 -> "Wed"; 4 -> "Thu"
    5 -> "Fri"; 6 -> "Sat"; 7 -> "Sun"
    else -> ""
}
