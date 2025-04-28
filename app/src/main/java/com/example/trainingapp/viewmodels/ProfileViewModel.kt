package com.example.trainingapp.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val _userName = MutableStateFlow("User")
    val userName: StateFlow<String> = _userName

    private val _weight = MutableStateFlow(70.0f)
    val weight: StateFlow<Float> = _weight

    private val _height = MutableStateFlow(175)
    val height: StateFlow<Int> = _height

    private val _fitnessGoal = MutableStateFlow("Build Muscle")
    val fitnessGoal: StateFlow<String> = _fitnessGoal

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode

    // In a real app, these would be loaded from preferences or a user database
    init {
        loadUserData()
    }

    private fun loadUserData() {
        // Simulate loading from database or preferences
        // In a real app, this would be an async operation
    }

    fun updateUserName(name: String) {
        _userName.value = name
    }

    fun updateWeight(weight: Float) {
        _weight.value = weight
    }

    fun updateHeight(height: Int) {
        _height.value = height
    }

    fun updateFitnessGoal(goal: String) {
        _fitnessGoal.value = goal
    }

    fun toggleEditMode() {
        _isEditMode.value = !_isEditMode.value
    }

    fun calculateBMI(): Float {
        val heightInMeters = _height.value / 100f
        return _weight.value / (heightInMeters * heightInMeters)
    }

    fun getBMICategory(): String {
        val bmi = calculateBMI()
        return when {
            bmi < 18.5f -> "Underweight"
            bmi < 25f -> "Normal"
            bmi < 30f -> "Overweight"
            else -> "Obese"
        }
    }

    fun saveUserData() {
        // In a real app, this would save to a database or preferences
        viewModelScope.launch {
            // Simulate network or database operation
            // Then exit edit mode when complete
            _isEditMode.value = false
        }
    }
}