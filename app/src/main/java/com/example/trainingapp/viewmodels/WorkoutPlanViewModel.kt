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

class WorkoutPlanViewModel(application: Application) : AndroidViewModel(application) {
    private val database = WorkoutDatabase.getDatabase(application, viewModelScope)
    private val workoutPlanRepository = WorkoutPlanRepository(
        database.workoutPlanDao(),
        database.workoutDayDao()
    )

    private val _workoutPlans = MutableStateFlow<List<WorkoutPlan>>(emptyList())
    val workoutPlans: StateFlow<List<WorkoutPlan>> = _workoutPlans

    private val _activePlan = MutableStateFlow<WorkoutPlan?>(null)
    val activePlan: StateFlow<WorkoutPlan?> = _activePlan

    private val _planName = MutableStateFlow("")
    val planName: StateFlow<String> = _planName

    private val _daysPerWeek = MutableStateFlow(3)
    val daysPerWeek: StateFlow<Int> = _daysPerWeek

    init {
        loadWorkoutPlans()
        loadActivePlan()
    }

    private fun loadWorkoutPlans() {
        viewModelScope.launch(Dispatchers.IO) {
            workoutPlanRepository.getAllWorkoutPlans().observeForever { plans ->
                _workoutPlans.value = plans ?: emptyList()
            }
        }
    }

    private fun loadActivePlan() {
        viewModelScope.launch(Dispatchers.IO) {
            workoutPlanRepository.getActiveWorkoutPlans().observeForever { plans ->
                _activePlan.value = plans.firstOrNull()
            }
        }
    }

    fun setPlanName(name: String) {
        _planName.value = name
    }

    fun setDaysPerWeek(days: Int) {
        _daysPerWeek.value = days
    }

    fun createPlan() {
        viewModelScope.launch(Dispatchers.IO) {
            val planId = workoutPlanRepository.createWorkoutPlan(
                planName = _planName.value,
                daysPerWeek = _daysPerWeek.value
            )

            // Reset state
            _planName.value = ""
            _daysPerWeek.value = 3

            // Reload plans
            loadWorkoutPlans()
            loadActivePlan()
        }
    }

    fun activatePlan(planId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            workoutPlanRepository.activateWorkoutPlan(planId)
            loadActivePlan()
        }
    }
}