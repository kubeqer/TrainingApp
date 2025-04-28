package com.example.trainingapp.data.repository

import com.example.trainingapp.data.dao.BodyPartDao
import com.example.trainingapp.data.entity.BodyPart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class BodyPartRepository(private val bodyPartDao: BodyPartDao) {

    fun getAllBodyParts() = bodyPartDao.getAllBodyParts()

    fun getBodyPartById(id: Long) = bodyPartDao.getBodyPartById(id)

    suspend fun getAllBodyPartsImmediate(): List<BodyPart> {
        return try {
            bodyPartDao.getAllBodyPartsImmediate()
        } catch (e: Exception) {
            getFallbackBodyParts()
        }
    }

    suspend fun insert(bodyPart: BodyPart): Long {
        return bodyPartDao.insert(bodyPart)
    }

    suspend fun update(bodyPart: BodyPart) {
        bodyPartDao.update(bodyPart)
    }

    suspend fun delete(bodyPart: BodyPart) {
        bodyPartDao.delete(bodyPart)
    }

    private fun getFallbackBodyParts(): List<BodyPart> {
        return listOf(
            BodyPart(1, "Back"),
            BodyPart(2, "Chest"),
            BodyPart(3, "Arms"),
            BodyPart(4, "Legs"),
            BodyPart(5, "Shoulders"),
            BodyPart(6, "Core"),
            BodyPart(7, "Cardio"),
            BodyPart(8, "Full Body")
        )
    }
}