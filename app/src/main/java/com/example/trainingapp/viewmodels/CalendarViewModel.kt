package com.example.trainingapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.TrainingApp
import com.example.trainingapp.data.entity.WorkoutDay
import com.example.trainingapp.data.entity.WorkoutPlan
import com.example.trainingapp.data.repository.WorkoutPlanRepository
import com.example.trainingapp.data.dao.PlanExerciseCrossRefDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

/**
 * ViewModel for the Calendar screen, exposing the active plan,
 * its workout days, and exercises per day.
 */
class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val tag = "CalendarViewModel"

    // Application and repository setup
    private val app = application as TrainingApp
    private val workoutPlanRepo: WorkoutPlanRepository by lazy {
        WorkoutPlanRepository(
            app.database.workoutPlanDao(),
            app.database.workoutDayDao()
        )
    }
    private val crossRefDao: PlanExerciseCrossRefDao = app.database.planExerciseDao()

    // --- StateFlows exposed to UI ---
    private val _activePlan = MutableStateFlow<WorkoutPlan?>(null)
    val activePlan: StateFlow<WorkoutPlan?> = _activePlan

    private val _workoutDays = MutableStateFlow<List<WorkoutDay>>(emptyList())
    val workoutDays: StateFlow<List<WorkoutDay>> = _workoutDays

    private val _exerciseMap = MutableStateFlow<Map<Int, List<Long>>>(emptyMap())
    val exerciseMap: StateFlow<Map<Int, List<Long>>> = _exerciseMap

    // LiveData observers for Room
    private var activePlansLD: LiveData<List<WorkoutPlan>>? = null
    private var workoutDaysLD: LiveData<List<WorkoutDay>>?   = null

    private val activePlanObserver = Observer<List<WorkoutPlan>> { plans ->
        val plan = plans.firstOrNull()
        _activePlan.value = plan
        if (plan != null) {
            loadWorkoutDays(plan.planId)
            loadExerciseMap(plan.planId)
        } else {
            _workoutDays.value = emptyList()
            _exerciseMap.value = emptyMap()
        }
    }

    private val workoutDaysObserver = Observer<List<WorkoutDay>> { days ->
        _workoutDays.value = days ?: emptyList()
    }

    init {
        Log.d(tag, "init – observing active plan on main thread")
        attachActivePlanObserver()
    }

    /**
     * (Re)attach observer to active-workout-plans LiveData.
     */
    private fun attachActivePlanObserver() {
        activePlansLD?.removeObserver(activePlanObserver)
        activePlansLD = workoutPlanRepo.getActiveWorkoutPlans()
        activePlansLD?.observeForever(activePlanObserver)
    }

    /**
     * Observe workout days for a given plan.
     */
    private fun loadWorkoutDays(planId: Long) {
        workoutDaysLD?.removeObserver(workoutDaysObserver)
        workoutDaysLD = workoutPlanRepo.getWorkoutDaysByPlan(planId)
        workoutDaysLD?.observeForever(workoutDaysObserver)
    }

    /**
     * Load exercise-day cross references on IO thread.
     */
    private fun loadExerciseMap(planId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val map = crossRefDao.getForPlan(planId)
            _exerciseMap.value = map
        }
    }

    /**
     * Activate a plan: flip isActive flags and reattach observers.
     */
    fun activatePlan(planId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            workoutPlanRepo.activateWorkoutPlan(planId)
        }
        attachActivePlanObserver()
    }

    /**
     * Build a 7-day schedule (Mon→Sun) pairing WorkoutDay & exercise IDs.
     */
    fun getCurrentWeekSchedule(): List<DaySchedule> {
        val days = _workoutDays.value
        val exMap = _exerciseMap.value
        val cal = Calendar.getInstance().apply { firstDayOfWeek = Calendar.MONDAY }
        // roll back to Monday
        val dowRaw = cal.get(Calendar.DAY_OF_WEEK)
        val shift = if (dowRaw == Calendar.SUNDAY) 6 else dowRaw - Calendar.MONDAY
        cal.add(Calendar.DATE, -shift)
        return (1..7).map { dayNum ->
            val date = cal.time
            val workoutDay = days.find { it.dayNumber == dayNum }
            val exerciseIds = exMap[dayNum] ?: emptyList()
            cal.add(Calendar.DATE, 1)
            DaySchedule(date, workoutDay, exerciseIds)
        }
    }
}

/**
 * Data carrier for each day in the week strip.
 */
data class DaySchedule(
    val date: Date,
    val workoutDay: WorkoutDay?,
    val exerciseIds: List<Long> = emptyList()
)
