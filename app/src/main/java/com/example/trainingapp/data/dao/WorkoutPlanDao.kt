package com.example.trainingapp.data.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.trainingapp.data.entity.WorkoutPlan

@Dao
interface WorkoutPlanDao {
    @Query("SELECT * FROM workout_plans")
    fun getAllWorkoutPlans(): LiveData<List<WorkoutPlan>>

    @Query("SELECT * FROM workout_plans WHERE plan_id = :planId")
    fun getWorkoutPlanById(planId: Long): LiveData<WorkoutPlan?>

    @Query("SELECT * FROM workout_plans WHERE is_active = 1")
    fun getActiveWorkoutPlans(): LiveData<List<WorkoutPlan>>

    @Insert
    suspend fun insert(workoutPlan: WorkoutPlan): Long

    @Update
    suspend fun update(workoutPlan: WorkoutPlan)

    @Delete
    suspend fun delete(workoutPlan: WorkoutPlan)
}