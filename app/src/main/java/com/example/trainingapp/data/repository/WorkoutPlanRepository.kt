package com.example.trainingapp.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.Transaction
import com.example.trainingapp.data.dao.WorkoutPlanDao
import com.example.trainingapp.data.dao.WorkoutDayDao
import com.example.trainingapp.data.entity.WorkoutPlan
import com.example.trainingapp.data.entity.WorkoutDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WorkoutPlanRepository(
    private val workoutPlanDao: WorkoutPlanDao,
    private val workoutDayDao: WorkoutDayDao
) {
    private val tag = "WorkoutPlanRepository"

    fun getAllWorkoutPlans(): LiveData<List<WorkoutPlan>> {
        Log.d(tag, "Getting all workout plans")
        return workoutPlanDao.getAllWorkoutPlans()
    }

    fun getWorkoutPlanById(planId: Long): LiveData<WorkoutPlan?> {
        Log.d(tag, "Getting workout plan by ID: $planId")
        return workoutPlanDao.getWorkoutPlanById(planId)
    }

    fun getActiveWorkoutPlans(): LiveData<List<WorkoutPlan>> {
        Log.d(tag, "Getting active workout plans")
        return workoutPlanDao.getActiveWorkoutPlans()
    }

    fun getWorkoutDaysByPlan(planId: Long): LiveData<List<WorkoutDay>> {
        Log.d(tag, "Getting workout days for plan: $planId")
        return workoutDayDao.getWorkoutDaysByPlan(planId)
    }

    suspend fun insertWorkoutPlan(workoutPlan: WorkoutPlan): Long {
        Log.d(tag, "Inserting workout plan: ${workoutPlan.planName}")
        return withContext(Dispatchers.IO) {
            workoutPlanDao.insert(workoutPlan)
        }
    }

    suspend fun updateWorkoutPlan(workoutPlan: WorkoutPlan) {
        Log.d(tag, "Updating workout plan: ${workoutPlan.planId}")
        withContext(Dispatchers.IO) {
            workoutPlanDao.update(workoutPlan)
        }
    }

    suspend fun deleteWorkoutPlan(workoutPlan: WorkoutPlan) {
        Log.d(tag, "Deleting workout plan: ${workoutPlan.planId}")
        withContext(Dispatchers.IO) {
            workoutPlanDao.delete(workoutPlan)
        }
    }

    suspend fun insertWorkoutDay(workoutDay: WorkoutDay): Long {
        Log.d(tag, "Inserting workout day for plan: ${workoutDay.planId}")
        return withContext(Dispatchers.IO) {
            workoutDayDao.insert(workoutDay)
        }
    }

    suspend fun insertWorkoutDays(workoutDays: List<WorkoutDay>): List<Long> {
        Log.d(tag, "Inserting ${workoutDays.size} workout days")
        return withContext(Dispatchers.IO) {
            workoutDayDao.insertAll(workoutDays)
        }
    }

    suspend fun activateWorkoutPlan(planId: Long) {
        Log.d(tag, "Activating workout plan: $planId")
        withContext(Dispatchers.IO) {
            try {
                val plans = workoutPlanDao.getAllWorkoutPlansImmediate()

                plans.forEach { plan ->
                    if (plan.isActive) {
                        Log.d(tag, "Deactivating plan: ${plan.planId}")
                        workoutPlanDao.update(plan.copy(isActive = false))
                    }
                }

                val planToActivate = workoutPlanDao.getWorkoutPlanByIdImmediate(planId)
                planToActivate?.let { plan ->
                    Log.d(tag, "Activating plan: ${plan.planId}")
                    workoutPlanDao.update(plan.copy(isActive = true))
                } ?: Log.w(tag, "Plan not found: $planId")
            } catch (e: Exception) {
                Log.e(tag, "Error activating workout plan", e)
                throw e
            }
        }
    }

    suspend fun createWorkoutPlan(planName: String, daysPerWeek: Int): Long {
        Log.d(tag, "Creating workout plan: $planName with $daysPerWeek days")
        return withContext(Dispatchers.IO) {
            try {
                val plans = workoutPlanDao.getAllWorkoutPlansImmediate()
                plans.forEach { plan ->
                    if (plan.isActive) {
                        Log.d(tag, "Deactivating plan: ${plan.planId}")
                        workoutPlanDao.update(plan.copy(isActive = false))
                    }
                }

                val plan = WorkoutPlan(
                    planName = planName,
                    daysPerWeek = daysPerWeek,
                    dateCreated = System.currentTimeMillis(),
                    isActive = true
                )

                val planId = workoutPlanDao.insert(plan)
                Log.d(tag, "Created plan with ID: $planId")

                val days = (1..daysPerWeek).map { dayNumber ->
                    WorkoutDay(
                        planId = planId,
                        dayNumber = dayNumber,
                        dayName = getDayName(dayNumber)
                    )
                }

                workoutDayDao.insertAll(days)
                Log.d(tag, "Created ${days.size} workout days")

                planId
            } catch (e: Exception) {
                Log.e(tag, "Error creating workout plan", e)
                throw e
            }
        }
    }

    private fun getDayName(dayNumber: Int): String {
        return when (dayNumber) {
            1 -> "Day 1 - Push"
            2 -> "Day 2 - Pull"
            3 -> "Day 3 - Legs"
            4 -> "Day 4 - Upper Body"
            5 -> "Day 5 - Lower Body"
            6 -> "Day 6 - Core"
            7 -> "Day 7 - Recovery"
            else -> "Day $dayNumber"
        }
    }
}