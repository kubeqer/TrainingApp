package com.example.trainingapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.trainingapp.data.entity.PlanExerciseCrossRef

@Dao
interface PlanExerciseCrossRefDao {

    @Query("""
    SELECT day_of_week        AS `key`,
           GROUP_CONCAT(exercise_id, ',') AS concatenated
      FROM plan_exercise_cross_ref
     WHERE plan_id = :planId
  GROUP BY day_of_week
  """)
    suspend fun _getRaw(planId: Long): List<DayGroup>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(refs: List<PlanExerciseCrossRef>)

    @Query("DELETE FROM plan_exercise_cross_ref WHERE plan_id = :planId")
    suspend fun deleteForPlan(planId: Long)

    data class DayGroup(val key: Int, val concatenated: String)

    suspend fun getForPlan(planId: Long): Map<Int,List<Long>> {
        return _getRaw(planId)
            .associate { dg ->
                dg.key to dg.concatenated
                    .split(',')
                    .filter(String::isNotBlank)
                    .map(String::toLong)
            }
    }
}

