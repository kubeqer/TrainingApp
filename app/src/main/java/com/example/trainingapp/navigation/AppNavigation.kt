package com.example.trainingapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.trainingapp.screens.calendar.CalendarScreen
import com.example.trainingapp.screens.dashboard.TrainingDashboard
import com.example.trainingapp.screens.exercise.ExerciseDetailScreen
import com.example.trainingapp.screens.exercise.ExerciseListScreen
import com.example.trainingapp.screens.profile.ProfileScreen
import com.example.trainingapp.screens.progress.ProgressScreen
import com.example.trainingapp.screens.workout.CreatePlanScreen
import com.example.trainingapp.screens.workout.WorkoutSessionScreen

object AppDestinations {
    const val HOME = "home"
    const val EXERCISE_DETAIL = "exercise/{exerciseId}"
    const val EXERCISE_LIST = "exercises/{bodyPartId}"
    const val CREATE_PLAN = "create_plan"
    const val WORKOUT_SESSION = "workout_session/{planId}"
    const val PROGRESS = "progress"
    const val PROFILE = "profile"
    const val CALENDAR = "calendar"
}

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = AppDestinations.HOME) {
        composable(AppDestinations.HOME) {
            TrainingDashboard(navController = navController)
        }

        composable(
            route = AppDestinations.EXERCISE_DETAIL,
            arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: 0L
            ExerciseDetailScreen(exerciseId = exerciseId, navController = navController)
        }

        composable(
            route = AppDestinations.EXERCISE_LIST,
            arguments = listOf(navArgument("bodyPartId") { type = NavType.LongType })
        ) { backStackEntry ->
            val bodyPartId = backStackEntry.arguments?.getLong("bodyPartId") ?: 0L
            ExerciseListScreen(bodyPartId = bodyPartId, navController = navController)
        }

        composable(AppDestinations.CREATE_PLAN) {
            CreatePlanScreen(navController = navController)
        }

        composable(
            route = AppDestinations.WORKOUT_SESSION,
            arguments = listOf(navArgument("planId") { type = NavType.LongType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getLong("planId") ?: 0L
            WorkoutSessionScreen(planId = planId, navController = navController)
        }

        composable(AppDestinations.PROGRESS) {
            ProgressScreen(navController = navController)
        }

        composable(AppDestinations.PROFILE) {
            ProfileScreen(navController = navController)
        }

        composable(AppDestinations.CALENDAR) {
            CalendarScreen(navController = navController)
        }
    }
}