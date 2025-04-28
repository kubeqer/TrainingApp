package com.example.trainingapp.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainingapp.TrainingApp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as TrainingApp
    private val sharedPreferences = app.getSharedPreferences("user_profile", Context.MODE_PRIVATE)

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _weight = MutableStateFlow(70.0f)
    val weight: StateFlow<Float> = _weight

    private val _height = MutableStateFlow(175)
    val height: StateFlow<Int> = _height

    private val _fitnessGoal = MutableStateFlow("Build Muscle")
    val fitnessGoal: StateFlow<String> = _fitnessGoal

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode

    init {
        loadUserData()
    }

    private fun loadUserData() {
        _userName.value = sharedPreferences.getString("user_name", "User") ?: "User"
        _weight.value = sharedPreferences.getFloat("weight", 70.0f)
        _height.value = sharedPreferences.getInt("height", 175)
        _fitnessGoal.value = sharedPreferences.getString("fitness_goal", "Build Muscle") ?: "Build Muscle"
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
        viewModelScope.launch {
            with(sharedPreferences.edit()) {
                putString("user_name", _userName.value)
                putFloat("weight", _weight.value)
                putInt("height", _height.value)
                putString("fitness_goal", _fitnessGoal.value)
                apply()
            }
            _isEditMode.value = false
        }
    }
}