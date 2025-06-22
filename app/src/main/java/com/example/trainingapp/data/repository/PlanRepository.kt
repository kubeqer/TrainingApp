package com.example.trainingapp.data.repository

import androidx.lifecycle.LiveData
import com.example.trainingapp.data.dao.PlanDao
import com.example.trainingapp.data.dao.PlanExerciseCrossRefDao
import com.example.trainingapp.data.entity.PlanExerciseCrossRef
import com.example.trainingapp.data.entity.WorkoutPlan
import com.example.trainingapp.data.model.WorkoutPlanModel

class PlanRepository(
    private val planDao: PlanDao,
    private val crossRefDao: PlanExerciseCrossRefDao
) {
    fun allPlans(): LiveData<List<WorkoutPlan>> = planDao.getAllPlans()
    fun planById(id: Long): LiveData<WorkoutPlan> = planDao.getPlanById(id)

    suspend fun getExercisesForPlan(planId: Long): Map<Int,List<Long>> =
        crossRefDao.getForPlan(planId)

    suspend fun createOrUpdate(ui: WorkoutPlanModel) {

        val entity = WorkoutPlan(
            planId      = if (ui.id > 0) ui.id else 0L,
            planName    = ui.name,
            daysPerWeek = ui.days.size,
            dateCreated = System.currentTimeMillis(),
            isActive    = (ui.id > 0)
        )

        val newId = if (ui.id == 0L) {
            planDao.insertPlan(entity)
        } else {
            planDao.updatePlan(entity)
            ui.id
        }

        crossRefDao.deleteForPlan(newId)
        val refs = ui.exercisesByDay.flatMap { (day, list) ->
            list.map { exId ->
                PlanExerciseCrossRef(planId = newId, dayOfWeek = day, exerciseId = exId)
            }
        }
        crossRefDao.insertAll(refs)
    }

    suspend fun delete(ui: WorkoutPlanModel) {

        planDao.deletePlan(
            WorkoutPlan(
                planId      = ui.id,
                planName    = ui.name,
                daysPerWeek = ui.days.size,
                dateCreated = 0L,
                isActive    = false
            )
        )
        crossRefDao.deleteForPlan(ui.id)
    }
    suspend fun loadPlanModel(planId: Long): WorkoutPlanModel {
        val entity = planDao.getPlanByIdImmediate(planId)
            ?: throw IllegalArgumentException("Plan $planId not found")
        val exercisesMap = crossRefDao.getForPlan(planId)
        val days = exercisesMap.keys.sorted()
        return WorkoutPlanModel(
            id             = entity.planId,
            name           = entity.planName,
            days           = days,
            exercisesByDay = exercisesMap
        )
    }
}
