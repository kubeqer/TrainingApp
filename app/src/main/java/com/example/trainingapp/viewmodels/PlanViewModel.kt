package com.example.trainingapp.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.data.entity.WorkoutPlan
import com.example.trainingapp.data.model.WorkoutPlanModel
import com.example.trainingapp.data.repository.PlanRepository
import kotlinx.coroutines.launch


class PlanViewModel(
    private val repository: PlanRepository
) : ViewModel() {


    private val _plans = mutableStateListOf<WorkoutPlanModel>()
    val plans: List<WorkoutPlanModel> get() = _plans

    var activePlanId by mutableStateOf<Long?>(null)
        private set

    init {

        repository.allPlans().observeForever { entities ->
            _plans.clear()
            _plans.addAll(entities.map { mapToModel(it) })
            activePlanId = entities.firstOrNull { it.isActive }?.planId
        }
    }


    
    private var editingPlanId: Long? = null

    
    var planName by mutableStateOf("")
        private set

    
    private val _selectedDays = mutableStateListOf<Int>()
    val selectedDays: List<Int> get() = _selectedDays

    
    private val _exercisesByDay = mutableStateMapOf<Int, MutableList<Long>>()
    val exercisesByDay: Map<Int, List<Long>> get() = _exercisesByDay

    
    fun getSelectedExercisesForDay(day: Int): List<Long> =
        _exercisesByDay[day]?.toList() ?: emptyList()


    
    fun startNew() {
        editingPlanId = null
        planName = ""
        _selectedDays.clear()
        _exercisesByDay.clear()
    }

    
    fun startEdit(ui: WorkoutPlanModel) {
        editingPlanId = ui.id
        planName = ui.name
        _selectedDays.clear().also { _selectedDays.addAll(ui.days) }
        _exercisesByDay.clear()
        ui.exercisesByDay.forEach { (day, list) ->
            _exercisesByDay[day] = list.toMutableList()
        }
    }

    
    fun startEditById(planId: Long) {
        editingPlanId = planId
        viewModelScope.launch {
            val ui = repository.loadPlanModel(planId)
            planName = ui.name
            _selectedDays.clear().also { _selectedDays.addAll(ui.days) }
            _exercisesByDay.clear()
            ui.exercisesByDay.forEach { (day, list) ->
                _exercisesByDay[day] = list.toMutableList()
            }
        }
    }

    
    fun updatePlanName(newName: String) {
        planName = newName
    }

    
    fun toggleDay(day: Int) {
        if (_selectedDays.contains(day)) _selectedDays.remove(day)
        else _selectedDays.add(day)
    }

    
    fun setExercisesForDay(day: Int, list: List<Long>) {
        _exercisesByDay[day] = list.toMutableList()
    }

    
    fun toggleExercise(day: Int, exerciseId: Long) {
        val list = _exercisesByDay.getOrPut(day) { mutableStateListOf() }
        if (list.contains(exerciseId)) list.remove(exerciseId)
        else list.add(exerciseId)
    }


    
    fun savePlan(onComplete: () -> Unit = {}) {
        val id = editingPlanId ?: 0L
        val model = WorkoutPlanModel(
            id = id,
            name = planName,
            days = _selectedDays.toList(),
            exercisesByDay = _exercisesByDay.mapValues { it.value.toList() }
        )
        viewModelScope.launch {
            repository.createOrUpdate(model)
            onComplete()
        }
        startNew()
    }

    
    fun deletePlan(ui: WorkoutPlanModel) {
        viewModelScope.launch {
            repository.delete(ui)
            if (activePlanId == ui.id) activePlanId = null
        }
    }

    
    fun deletePlanById(planId: Long) {
        plans.firstOrNull { it.id == planId }?.let { deletePlan(it) }
    }

    
    fun activatePlan(ui: WorkoutPlanModel) {
        viewModelScope.launch {
            repository.createOrUpdate(ui.copy(exercisesByDay = ui.exercisesByDay))
        }
    }

    
    fun setActivePlanById(planId: Long) {
        plans.firstOrNull { it.id == planId }?.let { ui ->
            viewModelScope.launch {
                repository.createOrUpdate(ui)
                activePlanId = planId
            }
        }
    }


    private fun mapToModel(entity: WorkoutPlan): WorkoutPlanModel {
        return WorkoutPlanModel(
            id = entity.planId,
            name = entity.planName,
            days = (1..entity.daysPerWeek).toList(),
            exercisesByDay = emptyMap()
        )
    }
}