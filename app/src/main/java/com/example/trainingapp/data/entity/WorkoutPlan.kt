package com.example.trainingapp.data.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_plans")
data class WorkoutPlan(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "plan_id")
    val planId: Long = 0,

    @ColumnInfo(name = "plan_name")
    val planName: String,

    @ColumnInfo(name = "days_per_week")
    val daysPerWeek: Int,

    @ColumnInfo(name = "date_created")
    val dateCreated: Long,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean

)

