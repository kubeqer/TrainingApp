package com.example.trainingapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.data.database.WorkoutDatabase
import com.example.trainingapp.data.entity.DayExercise
import com.example.trainingapp.data.entity.Exercise
import com.example.trainingapp.data.repository.WorkoutHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar

class WorkoutSessionViewModel(application: Application) : AndroidViewModel(application) {
    private val database = WorkoutDatabase.getDatabase(application, viewModelScope)
    private val dayExerciseDao = database.dayExerciseDao()
    private val workoutDayDao = database.workoutDayDao()
    private val workoutHistoryRepository = WorkoutHistoryRepository(dayExerciseDao)

    // Exercise data for current workout
    private val _currentExercises = MutableStateFlow<List<ExerciseWithSets>>(emptyList())
    val currentExercises: StateFlow<List<ExerciseWithSets>> = _currentExercises

    // Workout timer
    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning

    // Current workout data
    private var currentWorkoutId: Long? = null
    private var currentPlanId: Long? = null
    private var exercisesCompleted = 0

    fun startWorkout(planId: Long) {
        currentPlanId = planId

        // Get today's day of week (1-7, where 1 is Monday)
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val adjustedDayOfWeek = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1

        viewModelScope.launch(Dispatchers.IO) {
            // Find the workout day for this plan matching today's day of week
            workoutDayDao.getWorkoutDaysByPlan(planId).observeForever { days ->
                val todayWorkout = days.find { it.dayNumber == adjustedDayOfWeek }
                todayWorkout?.let { day ->
                    // Load exercises for this day
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
                    // If no workout for today, just show an empty list
                    _currentExercises.value = emptyList()
                }
            }
        }

        // Start workout timer
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

                // Check if all sets for this exercise are completed
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

        // Save workout data to history
        viewModelScope.launch(Dispatchers.IO) {
            currentPlanId?.let { planId ->
                workoutHistoryRepository.recordWorkout(
                    dayId = planId, // Using planId here, but in a real app would use the dayId
                    exercisesCompleted = exercisesCompleted,
                    durationSeconds = _elapsedTime.value
                )
            }
        }

        // Reset states
        _elapsedTime.value = 0
        _currentExercises.value = emptyList()
        exercisesCompleted = 0
        currentWorkoutId = null
        currentPlanId = null
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