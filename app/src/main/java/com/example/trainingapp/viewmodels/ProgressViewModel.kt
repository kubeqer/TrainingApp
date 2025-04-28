package com.example.trainingapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.TrainingApp
import com.example.trainingapp.data.repository.WorkoutHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProgressViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as TrainingApp
    private val database = app.database
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
        }
    }
}

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