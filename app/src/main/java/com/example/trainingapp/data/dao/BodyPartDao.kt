package com.example.trainingapp.data.dao
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.trainingapp.data.entity.BodyPart

@Dao
interface BodyPartDao {
    @Query("SELECT * FROM body_parts")
    fun getAllBodyParts(): LiveData<List<BodyPart>>

    @Query("SELECT * FROM body_parts WHERE id = :id")
    fun getBodyPartById(id: Long): LiveData<BodyPart?>

    @Insert
    suspend fun insert(bodyPart: BodyPart): Long

    @Update
    suspend fun update(bodyPart: BodyPart)

    @Delete
    suspend fun delete(bodyPart: BodyPart)

    @Query("SELECT * FROM body_parts")
    suspend fun getAllBodyPartsImmediate(): List<BodyPart>
}