package com.example.trainingapp.data.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.trainingapp.data.entity.WorkoutDay

@Dao
interface WorkoutDayDao {
    @Query("SELECT * FROM workout_days WHERE plan_id = :planId ORDER BY day_number")
    fun getWorkoutDaysByPlan(planId: Long): LiveData<List<WorkoutDay>>

    @Query("SELECT * FROM workout_days WHERE day_id = :dayId")
    fun getWorkoutDayById(dayId: Long): LiveData<WorkoutDay?>

    @Insert
    suspend fun insert(workoutDay: WorkoutDay): Long

    @Insert
    suspend fun insertAll(workoutDays: List<WorkoutDay>): List<Long>

    @Update
    suspend fun update(workoutDay: WorkoutDay)

    @Delete
    suspend fun delete(workoutDay: WorkoutDay)
}