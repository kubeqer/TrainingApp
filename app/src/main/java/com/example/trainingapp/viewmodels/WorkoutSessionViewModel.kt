package com.example.trainingapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.TrainingApp
import com.example.trainingapp.data.entity.Exercise
import com.example.trainingapp.data.repository.WorkoutHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap

class WorkoutSessionViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as TrainingApp
    private val database = app.database
    private val dayExerciseDao = database.dayExerciseDao()
    private val workoutDayDao = database.workoutDayDao()
    private val workoutHistoryRepository = WorkoutHistoryRepository(dayExerciseDao)
    private val _currentExercises = MutableStateFlow<List<ExerciseWithSets>>(emptyList())
    val currentExercises: StateFlow<List<ExerciseWithSets>> = _currentExercises
    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning
    private var currentWorkoutId: Long? = null
    private var currentPlanId: Long? = null
    private var exercisesCompleted = 0
    private val _completed = mutableStateMapOf<Long, Boolean>()
    val completed: SnapshotStateMap<Long, Boolean> = _completed


    fun startWorkout(planId: Long) {
        currentPlanId = planId
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val adjustedDayOfWeek = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1

        viewModelScope.launch(Dispatchers.IO) {
            workoutDayDao.getWorkoutDaysByPlan(planId).observeForever { days ->
                val todayWorkout = days.find { it.dayNumber == adjustedDayOfWeek }
                todayWorkout?.let { day ->
                    dayExerciseDao.getDetailedExercisesForDay(day.dayId).observeForever { exercises ->
                        val exerciseWithSets = exercises.map { dayExerciseWithExercise ->
                            ExerciseWithSets(
                                exercise = dayExerciseWithExercise.exercise,
                                sets = generateDefaultSets(dayExerciseWithExercise.dayExercise.sets)
                            )
                        }
                        _currentExercises.value = exerciseWithSets
                    }
                } ?: run {
                    _currentExercises.value = emptyList()
                }
            }
        }
        startTimer()
    }

    private fun generateDefaultSets(setCount: Int): List<WorkoutSet> {
        return List(setCount) { WorkoutSet(12, null, null, false) }
    }

    fun recordSet(exerciseId: Long, setIndex: Int, reps: Int, weight: Float) {
        val currentList = _currentExercises.value.toMutableList()
        val exerciseIndex = currentList.indexOfFirst { it.exercise.id == exerciseId }

        if (exerciseIndex != -1) {
            val exercise = currentList[exerciseIndex]
            val updatedSets = exercise.sets.toMutableList()

            if (setIndex < updatedSets.size) {
                updatedSets[setIndex] = WorkoutSet(
                    targetReps = exercise.sets[setIndex].targetReps,
                    completedReps = reps,
                    weight = weight,
                    isCompleted = true
                )

                val updatedExercise = exercise.copy(sets = updatedSets)
                currentList[exerciseIndex] = updatedExercise
                _currentExercises.value = currentList
                if (updatedSets.all { it.isCompleted }) {
                    exercisesCompleted++
                }
            }
        }
    }

    fun startTimer() {
        if (!_isTimerRunning.value) {
            _isTimerRunning.value = true
            viewModelScope.launch(Dispatchers.Default) {
                while (isActive && _isTimerRunning.value) {
                    delay(1000)
                    _elapsedTime.value += 1
                }
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
    }

    fun toggleTimer() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    fun completeWorkout() {
        pauseTimer()
        viewModelScope.launch(Dispatchers.IO) {
            currentPlanId?.let { planId ->
                workoutHistoryRepository.recordWorkout(
                    dayId = planId,
                    exercisesCompleted = exercisesCompleted,
                    durationSeconds = _elapsedTime.value
                )
            }
        }
        _elapsedTime.value = 0
        _currentExercises.value = emptyList()
        exercisesCompleted = 0
        currentWorkoutId = null
        currentPlanId = null
    }
    fun setCompleted(exerciseId: Long, done: Boolean) {
        _completed[exerciseId] = done
    }
}

data class ExerciseWithSets(
    val exercise: Exercise,
    val sets: List<WorkoutSet>
)

data class WorkoutSet(
    val targetReps: Int,
    val completedReps: Int?,
    val weight: Float?,
    val isCompleted: Boolean
)