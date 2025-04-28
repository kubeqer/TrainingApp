package com.example.trainingapp.data.repository

import com.example.trainingapp.data.dao.ExerciseDao
import com.example.trainingapp.data.entity.Exercise

class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    fun getAllExercises() = exerciseDao.getAllExercises()

    fun getExerciseById(id: Long) = exerciseDao.getExerciseById(id)

    fun getExercisesByBodyPart(bodyPartId: Long) = exerciseDao.getExercisesByBodyPart(bodyPartId)

    suspend fun insert(exercise: Exercise): Long {
        return exerciseDao.insert(exercise)
    }

    suspend fun update(exercise: Exercise) {
        exerciseDao.update(exercise)
    }

    suspend fun delete(exercise: Exercise) {
        exerciseDao.delete(exercise)
    }
}