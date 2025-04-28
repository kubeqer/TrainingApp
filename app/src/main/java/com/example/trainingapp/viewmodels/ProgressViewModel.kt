package com.example.trainingapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.data.database.WorkoutDatabase
import com.example.trainingapp.data.repository.WorkoutHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProgressViewModel(application: Application) : AndroidViewModel(application) {
    private val database = WorkoutDatabase.getDatabase(application, viewModelScope)
    private val workoutHistoryRepository = WorkoutHistoryRepository(database.dayExerciseDao())

    val weeklyWorkoutCount: StateFlow<Int> = workoutHistoryRepository.getWeeklyWorkoutCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = 0
        )

    val exercisesCompleted: StateFlow<Int> = workoutHistoryRepository.getTotalExercisesCompleted()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = 0
        )

    val totalWorkoutTime: StateFlow<Long> = workoutHistoryRepository.getTotalWorkoutTimeSeconds()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = 0L
        )

    val recentWorkouts = workoutHistoryRepository.recentWorkouts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    init {
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            // This would trigger data reload in a real app
            // For the mock implementation, it's already being loaded in the repository
        }
    }
}

// Utility function for formatting time
fun formatTime(timeInSeconds: Long): String {
    val hours = timeInSeconds / 3600
    val minutes = (timeInSeconds % 3600) / 60
    val seconds = timeInSeconds % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}

data class WorkoutSummary(
    val id: Long,
    val date: String,
    val exercises: Int,
    val duration: Long
)