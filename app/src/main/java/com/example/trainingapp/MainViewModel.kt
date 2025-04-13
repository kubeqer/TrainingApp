package com.example.trainingapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.data.database.WorkoutDatabase
import com.example.trainingapp.data.entity.BodyPart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the main dashboard that handles data operations
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = WorkoutDatabase.getDatabase(application, viewModelScope)
    private val bodyPartDao = database.bodyPartDao()
    private val _bodyParts = MutableStateFlow<List<BodyPart>>(emptyList())
    val bodyParts: StateFlow<List<BodyPart>> = _bodyParts

    fun loadBodyParts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val parts = bodyPartDao.getAllBodyPartsImmediate()
                _bodyParts.value = parts
            } catch (e: Exception) {
                _bodyParts.value = getFallbackBodyParts()
            }
        }
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