package com.example.trainingapp.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.data.entity.WorkoutPlan
import com.example.trainingapp.data.model.WorkoutPlanModel
import com.example.trainingapp.data.repository.PlanRepository
import kotlinx.coroutines.launch

/**
 * ViewModel obsługujący zarządzanie planami:
 * - obserwuje listę planów z Room
 * - mapuje je na WorkoutPlanModel
 * - udostępnia builder do tworzenia/edycji planu
 * - zapisuje do bazy (insert lub update)
 */
class PlanViewModel(
    private val repository: PlanRepository
) : ViewModel() {

    // --- ROOM DATA ---

    // lista wszystkich planów (UI-model)
    private val _plans = mutableStateListOf<WorkoutPlanModel>()
    val plans: List<WorkoutPlanModel> get() = _plans

    // ID aktualnie aktywnego planu
    var activePlanId by mutableStateOf<Long?>(null)
        private set

    init {
        // nasłuchuj LiveData z repozytorium
        repository.allPlans().observeForever { entities ->
            _plans.clear()
            _plans.addAll(entities.map { mapToModel(it) })
            activePlanId = entities.firstOrNull { it.isActive }?.planId
        }
    }

    // --- BUILDER STATE ---

    /** ID planu, który edytujemy; null = tworzymy nowy */
    private var editingPlanId: Long? = null

    /** Nazwa planu w builderze */
    var planName by mutableStateOf("")
        private set

    /** Wybrane dni tygodnia */
    private val _selectedDays = mutableStateListOf<Int>()
    val selectedDays: List<Int> get() = _selectedDays

    /** Mapa: dzień -> lista ID ćwiczeń */
    private val _exercisesByDay = mutableStateMapOf<Int, MutableList<Long>>()
    val exercisesByDay: Map<Int, List<Long>> get() = _exercisesByDay

    /** Pomocniczy getter listy ćwiczeń dla danego dnia */
    fun getSelectedExercisesForDay(day: Int): List<Long> =
        _exercisesByDay[day]?.toList() ?: emptyList()

    // --- CREATE / EDIT FLOW ---

    /** Przygotuj builder do nowego planu */
    fun startNew() {
        editingPlanId = null
        planName = ""
        _selectedDays.clear()
        _exercisesByDay.clear()
    }

    /** Rozpocznij edycję istniejącego planu (UI-model) */
    fun startEdit(ui: WorkoutPlanModel) {
        editingPlanId = ui.id
        planName = ui.name
        _selectedDays.clear().also { _selectedDays.addAll(ui.days) }
        _exercisesByDay.clear()
        ui.exercisesByDay.forEach { (day, list) ->
            _exercisesByDay[day] = list.toMutableList()
        }
    }

    /** Rozpocznij edycję po ID planu; fetch plan+i ćwiczenia z bazy */
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

    /** Zmień nazwę planu w builderze */
    fun updatePlanName(newName: String) {
        planName = newName
    }

    /** Przełącz zaznaczenie dnia */
    fun toggleDay(day: Int) {
        if (_selectedDays.contains(day)) _selectedDays.remove(day)
        else _selectedDays.add(day)
    }

    /** Ustaw listę ćwiczeń dla dnia (przy bezpośrednim nadpisaniu) */
    fun setExercisesForDay(day: Int, list: List<Long>) {
        _exercisesByDay[day] = list.toMutableList()
    }

    /** Przełącz zaznaczenie pojedynczego ćwiczenia */
    fun toggleExercise(day: Int, exerciseId: Long) {
        val list = _exercisesByDay.getOrPut(day) { mutableStateListOf() }
        if (list.contains(exerciseId)) list.remove(exerciseId)
        else list.add(exerciseId)
    }

    // --- SAVE & DELETE ---

    /** Zapisz plan do bazy: insert lub update; po zakończeniu wywołaj onComplete */
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

    /** Usuń plan */
    fun deletePlan(ui: WorkoutPlanModel) {
        viewModelScope.launch {
            repository.delete(ui)
            if (activePlanId == ui.id) activePlanId = null
        }
    }

    /** Usuń plan po ID */
    fun deletePlanById(planId: Long) {
        plans.firstOrNull { it.id == planId }?.let { deletePlan(it) }
    }

    /** Aktywuj plan: oznacz jako active, wydezaktywuj inne */
    fun activatePlan(ui: WorkoutPlanModel) {
        viewModelScope.launch {
            repository.createOrUpdate(ui.copy(exercisesByDay = ui.exercisesByDay))
        }
    }

    /** Aktywuj plan po ID */
    fun setActivePlanById(planId: Long) {
        plans.firstOrNull { it.id == planId }?.let { ui ->
            viewModelScope.launch {
                repository.createOrUpdate(ui)
                activePlanId = planId
            }
        }
    }

    // --- MAPPING ---

    private fun mapToModel(entity: WorkoutPlan): WorkoutPlanModel {
        return WorkoutPlanModel(
            id = entity.planId,
            name = entity.planName,
            days = (1..entity.daysPerWeek).toList(),
            exercisesByDay = emptyMap()
        )
    }
}