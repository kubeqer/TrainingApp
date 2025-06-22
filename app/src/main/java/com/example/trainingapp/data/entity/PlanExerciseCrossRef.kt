package com.example.trainingapp.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "plan_exercise_cross_ref",
    primaryKeys = ["plan_id","day_of_week","exercise_id"]
)
data class PlanExerciseCrossRef(
    @ColumnInfo(name = "plan_id")     val planId: Long,
    @ColumnInfo(name = "day_of_week") val dayOfWeek: Int,
    @ColumnInfo(name = "exercise_id") val exerciseId: Long
)
