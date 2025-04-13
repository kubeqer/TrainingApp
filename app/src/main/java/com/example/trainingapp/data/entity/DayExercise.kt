package com.example.trainingapp.data.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "day_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutDay::class,
            parentColumns = ["day_id"],
            childColumns = ["day_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("day_id"), Index("exercise_id")]
)
data class DayExercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "day_id")
    val dayId: Long,

    @ColumnInfo(name = "exercise_id")
    val exerciseId: Long,

    val sets: Int,
    val reps: Int,

    @ColumnInfo(name = "order_number")
    val orderNumber: Int
)