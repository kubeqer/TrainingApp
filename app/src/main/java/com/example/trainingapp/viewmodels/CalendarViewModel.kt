package com.example.trainingapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.TrainingApp
import com.example.trainingapp.data.entity.WorkoutDay
import com.example.trainingapp.data.entity.WorkoutPlan
import com.example.trainingapp.data.repository.WorkoutPlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date

class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val tag = "CalendarViewModel"

    private val app = application as TrainingApp
    private val database = app.database
    private val workoutPlanRepository by lazy {
        WorkoutPlanRepository(
            database.workoutPlanDao(),
            database.workoutDayDao()
        )
    }

    private val _activePlan = MutableStateFlow<WorkoutPlan?>(null)
    val activePlan: StateFlow<WorkoutPlan?> = _activePlan

    private val _workoutDays = MutableStateFlow<List<WorkoutDay>>(emptyList())
    val workoutDays: StateFlow<List<WorkoutDay>> = _workoutDays

    private var activePlansObserver: LiveData<List<WorkoutPlan>>? = null
    private var workoutDaysObserver: LiveData<List<WorkoutDay>>? = null

    init {
        Log.d(tag, "CalendarViewModel initialized")
        loadActivePlan()
    }

    private fun loadActivePlan() {
        viewModelScope.launch {
            try {
                Log.d(tag, "Loading active plan")
                withContext(Dispatchers.IO) {
                    activePlansObserver?.let { observer ->
                        workoutPlanRepository.getActiveWorkoutPlans().removeObserver { observer }
                    }
                    activePlansObserver = workoutPlanRepository.getActiveWorkoutPlans()
                    activePlansObserver?.observeForever { plans ->
                        Log.d(tag, "Active plans updated: ${plans?.size ?: 0}")
                        val plan = plans?.firstOrNull()
                        _activePlan.value = plan

                        plan?.let {
                            loadWorkoutDays(it.planId)
                        } ?: run {
                            _workoutDays.value = emptyList()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Error loading active plan", e)
                _activePlan.value = null
                _workoutDays.value = emptyList()
            }
        }
    }

    private fun loadWorkoutDays(planId: Long) {
        viewModelScope.launch {
            try {
                Log.d(tag, "Loading workout days for plan $planId")
                withContext(Dispatchers.IO) {
                    workoutDaysObserver?.let { observer ->
                        workoutPlanRepository.getWorkoutDaysByPlan(planId).removeObserver { observer }
                    }

                    // Get workout days and observe changes
                    workoutDaysObserver = workoutPlanRepository.getWorkoutDaysByPlan(planId)
                    workoutDaysObserver?.observeForever { days ->
                        Log.d(tag, "Workout days updated: ${days?.size ?: 0}")
                        _workoutDays.value = days ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Error loading workout days", e)
                _workoutDays.value = emptyList()
            }
        }
    }

    fun getCurrentWeekSchedule(): List<DaySchedule> {
        val days = _workoutDays.value
        Log.d(tag, "Getting week schedule with ${days.size} workout days")
        val calendar = Calendar.getInstance()
        calendar.firstDayOfWeek = Calendar.MONDAY
        val today = calendar.get(Calendar.DAY_OF_WEEK)
        val diff = if (today == Calendar.SUNDAY) 6 else today - Calendar.MONDAY
        calendar.add(Calendar.DATE, -diff)

        val weekSchedule = mutableListOf<DaySchedule>()

        for (i in 0 until 7) {
            val dayOfWeek = i + 1
            val date = calendar.time
            val workoutDay = days.find { it.dayNumber == dayOfWeek }
            Log.d(tag, "Day $dayOfWeek has workout: ${workoutDay != null}")

            weekSchedule.add(
                DaySchedule(
                    date = date,
                    workoutDay = workoutDay
                )
            )
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return weekSchedule
    }

    fun activatePlan(planId: Long) {
        viewModelScope.launch {
            try {
                Log.d(tag, "Activating plan $planId")
                withContext(Dispatchers.IO) {
                    workoutPlanRepository.activateWorkoutPlan(planId)
                }
                loadActivePlan()
            } catch (e: Exception) {
                Log.e(tag, "Error activating plan", e)
            }
        }
    }
}

data class DaySchedule(
    val date: Date,
    val workoutDay: WorkoutDay?
)