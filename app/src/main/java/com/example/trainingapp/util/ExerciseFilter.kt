package com.example.trainingapp.util

import com.example.trainingapp.data.entity.Exercise

object ExerciseFilter {
    fun filterBySearchQuery(exercises: List<Exercise>, query: String): List<Exercise> {
        if (query.isBlank()) return exercises
        val lowercaseQuery = query.lowercase()

        return exercises.filter {
            it.name.lowercase().contains(lowercaseQuery) ||
                    it.description.lowercase().contains(lowercaseQuery)
        }
    }

    fun filterByBodyPart(exercises: List<Exercise>, bodyPartId: Long?): List<Exercise> {
        if (bodyPartId == null || bodyPartId == 0L) return exercises
        return exercises.filter { it.bodyPartId == bodyPartId }
    }
}