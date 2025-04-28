package com.example.trainingapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.TrainingApp
import com.example.trainingapp.data.entity.Exercise
import com.example.trainingapp.data.repository.BodyPartRepository
import com.example.trainingapp.data.repository.ExerciseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as TrainingApp
    private val database = app.database
    private val exerciseRepository = ExerciseRepository(database.exerciseDao())
    private val bodyPartRepository = BodyPartRepository(database.bodyPartDao())

    private val _exercise = MutableStateFlow<Exercise?>(null)
    val exercise: StateFlow<Exercise?> = _exercise

    private val _bodyParts = MutableStateFlow<Map<Long, String>>(emptyMap())
    val bodyParts: StateFlow<Map<Long, String>> = _bodyParts

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedBodyPart = MutableStateFlow<Long?>(null)
    val selectedBodyPart: StateFlow<Long?> = _selectedBodyPart

    init {
        loadBodyParts()
    }

    fun getExerciseById(exerciseId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            exerciseRepository.getExerciseById(exerciseId).observeForever { exercise ->
                _exercise.value = exercise
            }
        }
    }

    fun loadExercisesByBodyPart(bodyPartId: Long) {
        _selectedBodyPart.value = bodyPartId

        viewModelScope.launch(Dispatchers.IO) {
            if (bodyPartId == 0L) {
                exerciseRepository.getAllExercises().observeForever { exercises ->
                    _exercises.value = exercises ?: emptyList()
                }
            } else {
                exerciseRepository.getExercisesByBodyPart(bodyPartId).observeForever { exercises ->
                    _exercises.value = exercises ?: emptyList()
                }
            }
        }
    }

    private fun loadBodyParts() {
        viewModelScope.launch(Dispatchers.IO) {
            val parts = bodyPartRepository.getAllBodyPartsImmediate()
            val partsMap = parts.associateBy({ it.id }, { it.type })
            _bodyParts.value = partsMap
        }
    }

    fun getBodyPartName(bodyPartId: Long): String {
        return _bodyParts.value[bodyPartId] ?: "Unknown"
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun getFilteredExercises(): List<Exercise> {
        val query = _searchQuery.value.lowercase()

        return if (query.isBlank()) {
            _exercises.value
        } else {
            _exercises.value.filter {
                it.name.lowercase().contains(query) ||
                        it.description.lowercase().contains(query)
            }
        }
    }
}