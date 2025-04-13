package com.example.trainingapp.data.dao
import androidx.room.Embedded
import androidx.room.Relation
import com.example.trainingapp.data.entity.DayExercise
import com.example.trainingapp.data.entity.Exercise
import com.example.trainingapp.data.entity.WorkoutDay
import com.example.trainingapp.data.entity.WorkoutPlan

data class DayExerciseWithExercise(
    @Embedded val dayExercise: DayExercise,
    @Relation(
        parentColumn = "exercise_id",
        entityColumn = "id"
    )
    val exercise: Exercise
)

data class WorkoutDayWithExercises(
    @Embedded val workoutDay: WorkoutDay,
    @Relation(
        entity = DayExercise::class,
        parentColumn = "day_id",
        entityColumn = "day_id"
    )
    val dayExercises: List<DayExerciseWithExercise>
)

data class WorkoutPlanWithDays(
    @Embedded val workoutPlan: WorkoutPlan,
    @Relation(
        entity = WorkoutDay::class,
        parentColumn = "plan_id",
        entityColumn = "plan_id"
    )
    val workoutDays: List<WorkoutDay>
)