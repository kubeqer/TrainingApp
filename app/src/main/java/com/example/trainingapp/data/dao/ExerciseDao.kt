package com.example.trainingapp.data.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.trainingapp.data.entity.Exercise

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises")
    fun getAllExercises(): LiveData<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getExerciseById(id: Long): LiveData<Exercise?>

    @Query("SELECT * FROM exercises WHERE body_part_id = :bodyPartId")
    fun getExercisesByBodyPart(bodyPartId: Long): LiveData<List<Exercise>>

    @Insert
    suspend fun insert(exercise: Exercise): Long

    @Update
    suspend fun update(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)
}