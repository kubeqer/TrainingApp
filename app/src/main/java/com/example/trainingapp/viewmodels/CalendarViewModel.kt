package com.example.trainingapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.data.database.WorkoutDatabase
import com.example.trainingapp.data.entity.WorkoutDay
import com.example.trainingapp.data.entity.WorkoutPlan
import com.example.trainingapp.data.repository.WorkoutPlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val database = WorkoutDatabase.getDatabase(application, viewModelScope)
    private val workoutPlanRepository = WorkoutPlanRepository(
        database.workoutPlanDao(),
        database.workoutDayDao()
    )

    private val _activePlan = MutableStateFlow<WorkoutPlan?>(null)
    val activePlan: StateFlow<WorkoutPlan?> = _activePlan

    private val _workoutDays = MutableStateFlow<List<WorkoutDay>>(emptyList())
    val workoutDays: StateFlow<List<WorkoutDay>> = _workoutDays

    init {
        loadActivePlan()
    }

    private fun loadActivePlan() {
        viewModelScope.launch(Dispatchers.IO) {
            workoutPlanRepository.getActiveWorkoutPlans().observeForever { plans ->
                val plan = plans.firstOrNull()
                _activePlan.value = plan

                plan?.let { loadWorkoutDays(it.planId) }
            }
        }
    }

    private fun loadWorkoutDays(planId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            workoutPlanRepository.getWorkoutDaysByPlan(planId).observeForever { days ->
                _workoutDays.value = days ?: emptyList()
            }
        }
    }

    fun getCurrentWeekSchedule(): List<DaySchedule> {
        val plan = _activePlan.value ?: return emptyList()
        val days = _workoutDays.value

        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY

        // Go to the beginning of the current week (Monday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val weekSchedule = mutableListOf<DaySchedule>()

        for (i in 0 until 7) {
            val dayOfWeek = i + 1 // 1 = Monday, 7 = Sunday
            val date = calendar.time

            // Check if this day has a workout in the plan
            val workoutDay = days.find { it.dayNumber == dayOfWeek }

            weekSchedule.add(
                DaySchedule(
                    date = date,
                    workoutDay = workoutDay
                )
            )

            // Move to next day
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return weekSchedule
    }

    fun activatePlan(planId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            workoutPlanRepository.activateWorkoutPlan(planId)
            loadActivePlan()
        }
    }
}

data class DaySchedule(
    val date: Date,
    val workoutDay: WorkoutDay?
)