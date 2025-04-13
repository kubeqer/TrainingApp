package com.example.trainingapp.data.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.trainingapp.data.entity.DayExercise

@Dao
interface DayExerciseDao {
    @Query("SELECT * FROM day_exercises WHERE day_id = :dayId ORDER BY order_number")
    fun getExercisesForDay(dayId: Long): LiveData<List<DayExercise>>

    @Transaction
    @Query("SELECT * FROM day_exercises WHERE day_id = :dayId ORDER BY order_number")
    fun getDetailedExercisesForDay(dayId: Long): LiveData<List<DayExerciseWithExercise>>

    @Insert
    suspend fun insert(dayExercise: DayExercise): Long

    @Insert
    suspend fun insertAll(dayExercises: List<DayExercise>): List<Long>

    @Update
    suspend fun update(dayExercise: DayExercise)

    @Delete
    suspend fun delete(dayExercise: DayExercise)

    @Query("DELETE FROM day_exercises WHERE day_id = :dayId")
    suspend fun deleteAllForDay(dayId: Long)
}