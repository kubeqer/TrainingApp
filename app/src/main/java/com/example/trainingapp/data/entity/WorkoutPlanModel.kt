package com.example.trainingapp.data.model

data class WorkoutPlanModel(
    val id: Long,
    val name: String,
    val days: List<Int>,                     // dni tygodnia: 1=Mon…7=Sun
    val exercisesByDay: Map<Int, List<Long>> // dzień → lista id ćwiczeń
)


