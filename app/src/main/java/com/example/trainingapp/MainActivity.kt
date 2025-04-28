package com.example.trainingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.trainingapp.navigation.AppNavigation
import com.example.trainingapp.ui.theme.TrainingAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrainingAppTheme {
                val navController = rememberNavController()
                var selectedRoute by remember { mutableStateOf("home") }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = selectedRoute == "home",
                                onClick = {
                                    selectedRoute = "home"
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                                label = { Text("Home") }
                            )
                            NavigationBarItem(
                                selected = selectedRoute == "calendar",
                                onClick = {
                                    selectedRoute = "calendar"
                                    navController.navigate("calendar") {
                                        popUpTo("home")
                                    }
                                },
                                icon = { Icon(Icons.Filled.DateRange, contentDescription = "Calendar") },
                                label = { Text("Calendar") }
                            )
                            NavigationBarItem(
                                selected = selectedRoute == "progress",
                                onClick = {
                                    selectedRoute = "progress"
                                    navController.navigate("progress") {
                                        popUpTo("home")
                                    }
                                },
                                icon = { Icon(Icons.Filled.BarChart, contentDescription = "Progress") },
                                label = { Text("Progress") }
                            )
                            NavigationBarItem(
                                selected = selectedRoute == "profile",
                                onClick = {
                                    selectedRoute = "profile"
                                    navController.navigate("profile") {
                                        popUpTo("home")
                                    }
                                },
                                icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
                                label = { Text("Profile") }
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        AppNavigation(navController = navController)
                    }
                }
            }
        }
    }
}