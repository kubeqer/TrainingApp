package com.example.trainingapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.trainingapp.data.entity.WorkoutPlan

@Dao
interface PlanDao {
    @Query("SELECT * FROM workout_plans")
    fun getAllPlans(): LiveData<List<WorkoutPlan>>

    @Query("SELECT * FROM workout_plans WHERE plan_id = :id")
    fun getPlanById(id: Long): LiveData<WorkoutPlan>

    @Query("SELECT * FROM workout_plans WHERE plan_id = :id")
    suspend fun getPlanByIdImmediate(id: Long): WorkoutPlan?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: WorkoutPlan): Long

    @Update
    suspend fun updatePlan(plan: WorkoutPlan)

    @Delete
    suspend fun deletePlan(plan: WorkoutPlan)
}
