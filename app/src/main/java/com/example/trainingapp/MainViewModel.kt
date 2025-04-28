package com.example.trainingapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.data.entity.BodyPart
import com.example.trainingapp.data.repository.BodyPartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val tag = "MainViewModel"

    private val app = application as TrainingApp
    private val database = app.database
    private val bodyPartRepository by lazy {
        try {
            Log.d(tag, "Initializing bodyPartRepository")
            BodyPartRepository(database.bodyPartDao())
        } catch (e: Exception) {
            Log.e(tag, "Error initializing bodyPartRepository", e)
            null
        }
    }

    private val _bodyParts = MutableStateFlow<List<BodyPart>>(emptyList())
    val bodyParts: StateFlow<List<BodyPart>> = _bodyParts

    init {
        Log.d(tag, "MainViewModel initialized")
        loadBodyParts()
    }

    fun loadBodyParts() {
        viewModelScope.launch {
            try {
                Log.d(tag, "Loading body parts")
                val repo = bodyPartRepository ?: return@launch

                withContext(Dispatchers.IO) {
                    val parts = repo.getAllBodyPartsImmediate()
                    Log.d(tag, "Loaded ${parts.size} body parts")
                    _bodyParts.value = parts
                }
            } catch (e: Exception) {
                Log.e(tag, "Error loading body parts", e)
                _bodyParts.value = getFallbackBodyParts()
            }
        }
    }

    private fun getFallbackBodyParts(): List<BodyPart> {
        return listOf(
            BodyPart(1, "Chest"),
            BodyPart(2, "Back"),
            BodyPart(3, "Arms"),
            BodyPart(4, "Legs"),
            BodyPart(5, "Shoulders"),
            BodyPart(6, "Core"),
            BodyPart(7, "Cardio"),
            BodyPart(8, "Full Body")
        )
    }
}