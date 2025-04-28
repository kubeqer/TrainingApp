package com.example.trainingapp.data.repository

import com.example.trainingapp.data.dao.WorkoutPlanDao
import com.example.trainingapp.data.dao.WorkoutDayDao
import com.example.trainingapp.data.entity.WorkoutPlan
import com.example.trainingapp.data.entity.WorkoutDay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkoutPlanRepository(
    private val workoutPlanDao: WorkoutPlanDao,
    private val workoutDayDao: WorkoutDayDao
) {

    fun getAllWorkoutPlans() = workoutPlanDao.getAllWorkoutPlans()

    fun getWorkoutPlanById(planId: Long) = workoutPlanDao.getWorkoutPlanById(planId)

    fun getActiveWorkoutPlans() = workoutPlanDao.getActiveWorkoutPlans()

    fun getWorkoutDaysByPlan(planId: Long) = workoutDayDao.getWorkoutDaysByPlan(planId)

    suspend fun insertWorkoutPlan(workoutPlan: WorkoutPlan): Long {
        return workoutPlanDao.insert(workoutPlan)
    }

    suspend fun updateWorkoutPlan(workoutPlan: WorkoutPlan) {
        workoutPlanDao.update(workoutPlan)
    }

    suspend fun deleteWorkoutPlan(workoutPlan: WorkoutPlan) {
        workoutPlanDao.delete(workoutPlan)
    }

    suspend fun insertWorkoutDay(workoutDay: WorkoutDay): Long {
        return workoutDayDao.insert(workoutDay)
    }

    suspend fun insertWorkoutDays(workoutDays: List<WorkoutDay>): List<Long> {
        return workoutDayDao.insertAll(workoutDays)
    }

    suspend fun activateWorkoutPlan(planId: Long) {
        // Deactivate all plans first
        getAllWorkoutPlans().value?.forEach { plan ->
            if (plan.isActive) {
                workoutPlanDao.update(plan.copy(isActive = false))
            }
        }

        // Activate the selected plan
        getWorkoutPlanById(planId).value?.let { plan ->
            workoutPlanDao.update(plan.copy(isActive = true))
        }
    }

    suspend fun createWorkoutPlan(planName: String, daysPerWeek: Int): Long {
        val plan = WorkoutPlan(
            planName = planName,
            daysPerWeek = daysPerWeek,
            dateCreated = System.currentTimeMillis(),
            isActive = true
        )

        val planId = insertWorkoutPlan(plan)

        // Create workout days for this plan
        val days = (1..daysPerWeek).map { dayNumber ->
            WorkoutDay(
                planId = planId,
                dayNumber = dayNumber,
                dayName = getDayName(dayNumber)
            )
        }

        insertWorkoutDays(days)

        return planId
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