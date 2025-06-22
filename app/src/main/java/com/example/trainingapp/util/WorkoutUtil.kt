package com.example.trainingapp.util

object WorkoutUtil {
    fun calculateOneRepMax(weight: Float, reps: Int): Float {

        return if (reps < 1) weight else weight * (36f / (37f - reps))
    }

    fun calculateVolume(weight: Float, sets: Int, reps: Int): Float {
        return weight * sets * reps
    }

    fun suggestNextWorkoutWeight(currentWeight: Float, repsCompleted: Int, targetReps: Int): Float {
        return when {
            repsCompleted >= targetReps + 2 -> currentWeight * 1.05f // Increase by 5%
            repsCompleted >= targetReps -> currentWeight * 1.025f // Increase by 2.5%
            repsCompleted <= targetReps - 2 -> currentWeight * 0.95f // Decrease by 5%
            else -> currentWeight
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
}