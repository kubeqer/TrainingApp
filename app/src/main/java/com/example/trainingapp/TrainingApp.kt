package com.example.trainingapp

import android.app.Application
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import com.example.trainingapp.data.database.WorkoutDatabase
import com.example.trainingapp.data.database.DatabaseInitializer
import kotlinx.coroutines.Dispatchers


class TrainingApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy {
        WorkoutDatabase.getDatabase(this, applicationScope)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("TrainingApp", "Application onCreate called")
        Log.d("TrainingApp", "Database initialized: $database")
    }
}