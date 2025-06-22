package com.example.trainingapp.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.trainingapp.TrainingApp
import com.example.trainingapp.data.entity.WorkoutDay
import com.example.trainingapp.data.entity.WorkoutPlan
import com.example.trainingapp.data.dao.PlanExerciseCrossRefDao
import com.example.trainingapp.data.repository.WorkoutPlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import androidx.lifecycle.Observer


class CalendarViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as TrainingApp
    private val workoutPlanRepo = WorkoutPlanRepository(
        app.database.workoutPlanDao(),
        app.database.workoutDayDao()
    )
    private val crossRefDao: PlanExerciseCrossRefDao = app.database.planExerciseDao()

    // --- StateFlows holding our UI state ---
    private val _activePlan   = MutableStateFlow<WorkoutPlan?>(null)
    val activePlan: StateFlow<WorkoutPlan?>            = _activePlan.asStateFlow()

    private val _workoutDays = MutableStateFlow<List<WorkoutDay>>(emptyList())
    val workoutDays: StateFlow<List<WorkoutDay>>       = _workoutDays.asStateFlow()

    private val _exerciseMap = MutableStateFlow<Map<Int,List<Long>>>(emptyMap())
    val exerciseMap: StateFlow<Map<Int,List<Long>>>   = _exerciseMap.asStateFlow()

    // --- LiveData observers so we can detach/reattach cleanly ---
    private var activePlansLD: LiveData<List<WorkoutPlan>>? = null
    private val activePlanObs = Observer<List<WorkoutPlan>> { plans ->
        val plan = plans.firstOrNull()
        _activePlan.value = plan
        if (plan != null) {
            observeWorkoutDays(plan.planId)
            loadExerciseMap(plan.planId)
        } else {
            _workoutDays.value = emptyList()
            _exerciseMap.value = emptyMap()
        }
    }

    private var workoutDaysLD: LiveData<List<WorkoutDay>>? = null
    private val workoutDaysObs = Observer<List<WorkoutDay>> { days ->
        _workoutDays.value = days ?: emptyList()
    }

    init {
        // 1) “Forever” attach our active-plan observer on the main thread
        activePlansLD?.removeObserver(activePlanObs)
        activePlansLD = workoutPlanRepo.getActiveWorkoutPlans().also { ld ->
            ld.observeForever(activePlanObs)
        }
    }

    /** Detach any old day-observer and attach a new one for this planId */
    private fun observeWorkoutDays(planId: Long) {
        workoutDaysLD?.removeObserver(workoutDaysObs)
        workoutDaysLD = workoutPlanRepo.getWorkoutDaysByPlan(planId).also { ld ->
            ld.observeForever(workoutDaysObs)
        }
    }

    /** Fetch the plan⇆exercise cross-refs off the main thread and push into a StateFlow */
    private fun loadExerciseMap(planId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val map = crossRefDao.getForPlan(planId)
            _exerciseMap.value = map
        }
    }

    /** Flip which plan is active in your DB; once that completes,
     *  your LiveData observer will automatically fire again */
    fun activatePlan(planId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            workoutPlanRepo.activateWorkoutPlan(planId)
        }
    }

    /** Build a 7-day strip (Mon→Sun), pairing each date with
     *  its optional WorkoutDay + the list of picked-exercise IDs. */
    fun getCurrentWeekSchedule(): List<DaySchedule> {
        val days = _workoutDays.value
        val exMap = _exerciseMap.value

        val cal = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
        }
        // rewind back to Monday
        val dowRaw = cal.get(Calendar.DAY_OF_WEEK)
        val diff   = if (dowRaw == Calendar.SUNDAY) 6 else dowRaw - Calendar.MONDAY
        cal.add(Calendar.DATE, -diff)

        return (1..7).map { dayNum ->
            val date       = cal.time
            val workoutDay = days.find { it.dayNumber == dayNum }
            val exerciseIds= exMap[dayNum] ?: emptyList()
            cal.add(Calendar.DATE, 1)
            DaySchedule(date, workoutDay, exerciseIds)
        }
    }
}

/** Carries one calendar date, an optional WorkoutDay row, plus that day’s exercise-IDs */
data class DaySchedule(
    val date: Date,
    val workoutDay: WorkoutDay?,
    val exerciseIds: List<Long> = emptyList()
)
