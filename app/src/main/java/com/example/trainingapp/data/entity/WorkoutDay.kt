package com.example.trainingapp.data.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_days",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlan::class,
            parentColumns = ["plan_id"],
            childColumns = ["plan_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plan_id")]
)
data class WorkoutDay(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "day_id")
    val dayId: Long = 0,

    @ColumnInfo(name = "plan_id")
    val planId: Long,

    @ColumnInfo(name = "day_number")
    val dayNumber: Int,

    @ColumnInfo(name = "day_name")
    val dayName: String
)