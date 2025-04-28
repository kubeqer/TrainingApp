// WorkoutHistoryRepository.kt
package com.example.trainingapp.data.repository

import com.example.trainingapp.data.dao.DayExerciseDao
import com.example.trainingapp.viewmodels.WorkoutSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WorkoutHistoryRepository(
    private val dayExerciseDao: DayExerciseDao
) {
    private val _recentWorkouts = MutableStateFlow<List<WorkoutSummary>>(emptyList())
    val recentWorkouts: Flow<List<WorkoutSummary>> = _recentWorkouts

    // Mock data for demo
    private var weeklyWorkoutCount = 0
    private var totalExercisesCompleted = 0
    private var totalWorkoutTimeSeconds = 0L

    init {
        // Load mock data for demo
        loadMockData()
    }

    // Change return type to Flow<Int>
    fun getWeeklyWorkoutCount(): Flow<Int> = flow {
        emit(weeklyWorkoutCount)
    }

    // Change return type to Flow<Int>
    fun getTotalExercisesCompleted(): Flow<Int> = flow {
        emit(totalExercisesCompleted)
    }

    // Change return type to Flow<Long>
    fun getTotalWorkoutTimeSeconds(): Flow<Long> = flow {
        emit(totalWorkoutTimeSeconds)
    }

    // Record a completed workout
    suspend fun recordWorkout(
        dayId: Long,
        exercisesCompleted: Int,
        durationSeconds: Long
    ) {
        // In a real app, save to database
        // For demo, just update the mock data
        weeklyWorkoutCount++
        totalExercisesCompleted += exercisesCompleted
        totalWorkoutTimeSeconds += durationSeconds

        // Add to recent workouts
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

        val newWorkout = WorkoutSummary(
            id = System.currentTimeMillis(),
            date = dateFormat.format(calendar.time),
            exercises = exercisesCompleted,
            duration = durationSeconds
        )

        val currentList = _recentWorkouts.value.toMutableList()
        currentList.add(0, newWorkout) // Add at the beginning
        _recentWorkouts.value = currentList.take(10) // Keep only the last 10
    }

    // In a real app, this would load from the database
    private fun loadMockData() {
        weeklyWorkoutCount = 3
        totalExercisesCompleted = 45
        totalWorkoutTimeSeconds = 9000 // 2.5 hours

        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val mockWorkouts = mutableListOf<WorkoutSummary>()

        // Generate some mock workout history
        for (i in 0 until 5) {
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            mockWorkouts.add(
                WorkoutSummary(
                    id = i.toLong(),
                    date = dateFormat.format(calendar.time),
                    exercises = (4..8).random(),
                    duration = (1800..3600).random().toLong() // 30-60 minutes
                )
            )
            calendar.add(Calendar.DAY_OF_YEAR, i) // Reset for next iteration
        }

        _recentWorkouts.value = mockWorkouts
    }
}