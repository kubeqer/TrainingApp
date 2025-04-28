package com.example.trainingapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.data.database.WorkoutDatabase
import com.example.trainingapp.data.entity.BodyPart
import com.example.trainingapp.data.repository.BodyPartRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the main dashboard that handles data operations
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = WorkoutDatabase.getDatabase(application, viewModelScope)
    private val bodyPartRepository = BodyPartRepository(database.bodyPartDao())

    private val _bodyParts = MutableStateFlow<List<BodyPart>>(emptyList())
    val bodyParts: StateFlow<List<BodyPart>> = _bodyParts

    init {
        loadBodyParts()
    }

    fun loadBodyParts() {
        viewModelScope.launch(Dispatchers.IO) {
            _bodyParts.value = bodyPartRepository.getAllBodyPartsImmediate()
        }
    }
}