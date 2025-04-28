package com.example.trainingapp.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.TrainingApp
import com.example.trainingapp.data.entity.WorkoutPlan
import com.example.trainingapp.data.repository.WorkoutPlanRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkoutPlanViewModel(application: Application) : AndroidViewModel(application) {
    private val tag = "WorkoutPlanViewModel"

    private val app = application as TrainingApp
    private val database = app.database
    private val workoutPlanRepository by lazy {
        WorkoutPlanRepository(
            database.workoutPlanDao(),
            database.workoutDayDao()
        )
    }

    private val _workoutPlans = MutableStateFlow<List<WorkoutPlan>>(emptyList())
    val workoutPlans: StateFlow<List<WorkoutPlan>> = _workoutPlans

    private val _activePlan = MutableStateFlow<WorkoutPlan?>(null)
    val activePlan: StateFlow<WorkoutPlan?> = _activePlan

    private val _planName = MutableStateFlow("My Workout Plan")
    val planName: StateFlow<String> = _planName

    private val _daysPerWeek = MutableStateFlow(3)
    val daysPerWeek: StateFlow<Int> = _daysPerWeek

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var plansObserver: LiveData<List<WorkoutPlan>>? = null
    private var activePlansObserver: LiveData<List<WorkoutPlan>>? = null

    init {
        Log.d(tag, "WorkoutPlanViewModel initialized")
        loadWorkoutPlans()
        loadActivePlan()
    }

    private fun loadWorkoutPlans() {
        viewModelScope.launch {
            try {
                Log.d(tag, "Loading workout plans")
                _isLoading.value = true

                withContext(Dispatchers.IO) {
                    plansObserver?.let { observer ->
                        workoutPlanRepository.getAllWorkoutPlans().removeObserver { observer }
                    }
                    plansObserver = workoutPlanRepository.getAllWorkoutPlans()
                    plansObserver?.observeForever { plans ->
                        Log.d(tag, "Plans updated: ${plans?.size ?: 0}")
                        _workoutPlans.value = plans ?: emptyList()
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Error loading workout plans", e)
                _errorMessage.value = "Error loading workout plans: ${e.message}"
                _isLoading.value = false
                _workoutPlans.value = emptyList()
            }
        }
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
                        _activePlan.value = plans?.firstOrNull()
                    }
                }
            } catch (e: Exception) {
                Log.e(tag, "Error loading active plan", e)
                _errorMessage.value = "Error loading active plan: ${e.message}"
                _activePlan.value = null
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
        viewModelScope.launch {
            try {
                Log.d(tag, "Creating workout plan: ${_planName.value} with ${_daysPerWeek.value} days")
                _isLoading.value = true
                _errorMessage.value = null

                withContext(Dispatchers.IO) {
                    val planId = workoutPlanRepository.createWorkoutPlan(
                        planName = _planName.value.takeIf { it.isNotBlank() } ?: "My Workout Plan",
                        daysPerWeek = _daysPerWeek.value.coerceIn(1, 7)
                    )

                    Log.d(tag, "Plan created with ID: $planId")
                }
                _planName.value = "My Workout Plan"
                _daysPerWeek.value = 3
                loadWorkoutPlans()
                loadActivePlan()

                _isLoading.value = false
            } catch (e: Exception) {
                Log.e(tag, "Error creating workout plan", e)
                _errorMessage.value = "Error creating plan: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun activatePlan(planId: Long) {
        viewModelScope.launch {
            try {
                Log.d(tag, "Activating plan $planId")
                _isLoading.value = true
                _errorMessage.value = null

                withContext(Dispatchers.IO) {
                    workoutPlanRepository.activateWorkoutPlan(planId)
                }

                loadActivePlan()
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e(tag, "Error activating plan", e)
                _errorMessage.value = "Error activating plan: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        plansObserver?.let { observer ->
            workoutPlanRepository.getAllWorkoutPlans().removeObserver { observer }
        }
        activePlansObserver?.let { observer ->
            workoutPlanRepository.getActiveWorkoutPlans().removeObserver { observer }
        }
    }
}