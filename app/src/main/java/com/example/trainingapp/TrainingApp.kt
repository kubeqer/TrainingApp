package com.example.trainingapp

import android.app.Application
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import com.example.trainingapp.data.database.WorkoutDatabase

class TrainingApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy {
        Log.d("TrainingApp", "Initializing database")
        WorkoutDatabase.getDatabase(this, applicationScope)
    }
    override fun onCreate() {
        super.onCreate()
        Log.d("TrainingApp", "Application onCreate called")
        val db = database
        Log.d("TrainingApp", "Database initialized: $db")
    }
}