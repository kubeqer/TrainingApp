
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

    private var weeklyWorkoutCount = 0
    private var totalExercisesCompleted = 0
    private var totalWorkoutTimeSeconds = 0L

    init {
        loadMockData()
    }

    fun getWeeklyWorkoutCount(): Flow<Int> = flow {
        emit(weeklyWorkoutCount)
    }

    fun getTotalExercisesCompleted(): Flow<Int> = flow {
        emit(totalExercisesCompleted)
    }

    fun getTotalWorkoutTimeSeconds(): Flow<Long> = flow {
        emit(totalWorkoutTimeSeconds)
    }

    suspend fun recordWorkout(
        dayId: Long,
        exercisesCompleted: Int,
        durationSeconds: Long
    ) {
        weeklyWorkoutCount++
        totalExercisesCompleted += exercisesCompleted
        totalWorkoutTimeSeconds += durationSeconds

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

        val newWorkout = WorkoutSummary(
            id = System.currentTimeMillis(),
            date = dateFormat.format(calendar.time),
            exercises = exercisesCompleted,
            duration = durationSeconds
        )

        val currentList = _recentWorkouts.value.toMutableList()
        currentList.add(0, newWorkout)
        _recentWorkouts.value = currentList.take(10)
    }

    private fun loadMockData() {
        weeklyWorkoutCount = 3
        totalExercisesCompleted = 45
        totalWorkoutTimeSeconds = 9000

        val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val mockWorkouts = mutableListOf<WorkoutSummary>()

        for (i in 0 until 5) {
            calendar.add(Calendar.DAY_OF_YEAR, -i)
            mockWorkouts.add(
                WorkoutSummary(
                    id = i.toLong(),
                    date = dateFormat.format(calendar.time),
                    exercises = (4..8).random(),
                    duration = (1800..3600).random().toLong()
                )
            )
            calendar.add(Calendar.DAY_OF_YEAR, i)
        }

        _recentWorkouts.value = mockWorkouts
    }
}