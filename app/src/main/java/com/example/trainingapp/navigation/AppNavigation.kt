package com.example.trainingapp.navigation

import android.util.Log
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
import com.example.trainingapp.screens.workout.WorkoutSessionScreen
import com.example.trainingapp.screens.exercise.MuscleGroupsScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trainingapp.screens.plan.SelectDaysScreen
import com.example.trainingapp.screens.plan.SelectExercisesScreen
import com.example.trainingapp.viewmodels.PlanViewModel
import com.example.trainingapp.viewmodels.ExerciseViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.trainingapp.screens.plan.EditPlansScreen
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.trainingapp.data.database.WorkoutDatabase
import com.example.trainingapp.data.repository.PlanRepository
import com.example.trainingapp.viewmodels.PlanViewModelFactory
import com.example.trainingapp.data.repository.ExerciseRepository
import com.example.trainingapp.data.dao.ExerciseDao
import androidx.compose.runtime.rememberCoroutineScope
import com.example.trainingapp.viewmodels.CalendarViewModel
import com.example.trainingapp.screens.gallery.GalleryScreen
import android.net.Uri


private const val TAG = "AppNavigation"

object AppDestinations {
    const val HOME = "home"
    const val EXERCISE_DETAIL = "exercise/{exerciseId}"
    const val EXERCISE_LIST = "exercises/{bodyPartId}"
    const val WORKOUT_SESSION = "workout_session/{planId}"
    const val PROGRESS = "progress"
    const val PROFILE = "profile"
    const val CALENDAR = "calendar"
    const val CREATE_PLAN = "create_plan"
    const val SELECT_PLAN_EXERCISES = "select_plan_exercises/{day}"
    const val EDIT_PLANS = "edit_plans"
    const val EDIT_PLAN_ROUTE = "edit_plan/{planId}"
    const val GALLERY = "gallery"

    fun editPlanRoute(planId: Long)    = "edit_plan/$planId"
    fun selectExercisesRoute(day: Int) = "select_plan_exercises/$day"
}

@Composable
fun AppNavigation(
    navController: NavHostController
) {
    Log.d(TAG, "Setting up navigation")

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val database = WorkoutDatabase.getDatabase(context, scope)
    val planDao = database.planDao()
    val crossRefDao = database.planExerciseDao()
    val planRepo = remember { PlanRepository(planDao, crossRefDao) }
    val exerciseDao = database.exerciseDao()
    val exerciseRepo = remember { ExerciseRepository(exerciseDao) }
    val calendarVm: CalendarViewModel = viewModel()

    val planVm: PlanViewModel = viewModel(
        factory = PlanViewModelFactory(planRepo)
    )

    val exerciseVm: ExerciseViewModel = viewModel()

    NavHost(navController = navController, startDestination = AppDestinations.HOME) {
        composable(AppDestinations.HOME) {
            Log.d(TAG, "Navigating to Home")
            TrainingDashboard(navController = navController)
        }

        composable(
            route = AppDestinations.EXERCISE_DETAIL,
            arguments = listOf(navArgument("exerciseId") { type = NavType.LongType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: 0L
            Log.d(TAG, "Navigating to Exercise Detail: $exerciseId")
            ExerciseDetailScreen(exerciseId = exerciseId, navController = navController)
        }

        composable(
            route = AppDestinations.EXERCISE_LIST,
            arguments = listOf(navArgument("bodyPartId") { type = NavType.LongType })
        ) { backStackEntry ->
            val bodyPartId = backStackEntry.arguments?.getLong("bodyPartId") ?: 0L
            Log.d(TAG, "Navigating to Exercise List for body part: $bodyPartId")
            ExerciseListScreen(bodyPartId = bodyPartId, navController = navController)
        }

        composable(AppDestinations.CREATE_PLAN) {
            LaunchedEffect(Unit) { planVm.startNew() }
            val days = planVm.selectedDays
            SelectDaysScreen(
                planViewModel = planVm,
                onNext = {
                    val dayToEdit = days.firstOrNull() ?: 1
                    navController.navigate(AppDestinations.selectExercisesRoute(dayToEdit))
                }
            )
        }

        composable(AppDestinations.PROGRESS) {
            Log.d(TAG, "Navigating to Progress")
            ProgressScreen(navController = navController)
        }

        composable(AppDestinations.PROFILE) {
            Log.d(TAG, "Navigating to Profile")
            ProfileScreen(navController = navController)
        }

        composable(route = AppDestinations.CALENDAR) {
            val calendarVm: CalendarViewModel = viewModel()
            CalendarScreen(navController = navController, calendarVm = calendarVm)

        }

        composable("muscleGroups") {
            MuscleGroupsScreen(navController)
        }
        composable("exercises/{bodyPartId}") { backStackEntry ->
            val bodyPartId = backStackEntry.arguments?.getString("bodyPartId")?.toLongOrNull() ?: 0L
            ExerciseListScreen(bodyPartId = bodyPartId, navController = navController)
        }
        composable(
            route = AppDestinations.EDIT_PLAN_ROUTE,
            arguments = listOf(navArgument("planId") { type = NavType.LongType })
                    ) { back ->
            val planId = back.arguments!!.getLong("planId")
            LaunchedEffect(planId) { planVm.startEditById(planId) }
            val days = planVm.selectedDays
            SelectDaysScreen(
                planViewModel = planVm,
                onNext = {
                    val dayToEdit = days.firstOrNull() ?: 1
                     navController.navigate(AppDestinations.selectExercisesRoute(dayToEdit))
                }
            )
        }
        composable(
            route = AppDestinations.SELECT_PLAN_EXERCISES,
            arguments = listOf(navArgument("day") { type = NavType.IntType })
        ) { backStackEntry ->
            val day = backStackEntry.arguments!!.getInt("day")
            val allExercises by exerciseVm.exercises.collectAsState()
            SelectExercisesScreen(
                allExercises  = allExercises,
                planViewModel = planVm,
                day           = day,
                onSave        = {
                    planVm.savePlan()
                    navController.navigate(AppDestinations.EDIT_PLANS)
                }
            )
        }
        composable(AppDestinations.EDIT_PLANS) {
            EditPlansScreen(
                planList     = planVm.plans,                   // zamiast allPlans
                activePlanId = planVm.activePlanId,
                onSelect     = { planVm.setActivePlanById(it) },  // zamiast setActivePlan
                onDelete     = { planVm.deletePlanById(it) },     // zamiast deletePlan
                onEditClick  = { navController.navigate(AppDestinations.editPlanRoute(it)) },
                onBack       = { navController.popBackStack() }
            )
        }
        composable(
            route = AppDestinations.WORKOUT_SESSION,
            arguments = listOf(navArgument("planId") { type = NavType.LongType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getLong("planId") ?: 0L
            WorkoutSessionScreen(planId, navController)
        }

        composable(route = AppDestinations.GALLERY) {
            // lista przykładowych URL-i ― tu możesz wczytać z API albo z ViewModelu
            val sampleImages = listOf(
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ9VDAU5wKFTrI0EuIguwochhXrzU1NyYODKQ&s",
                "https://www.oxcloth.com/cdn/shop/articles/ab8622774dfd8bc6b2107656cc1d648ff48279b3-1200x600.webp?v=1712077173",
                "https://live-production.wcms.abc-cdn.net.au/774978bbad3c017a1796c989cf5e5508?src",
                "https://www.gymshark.com/_next/image?url=https%3A%2F%2Fimages.ctfassets.net%2F8urtyqugdt2l%2FfIOvOyiWNuyVnlhs7LnFt%2F9d2b2df51fdce86e93c0d1b0d28afc1e%2Fdesktop-cbum-back-workout.jpg&w=3840&q=85",
                "https://cdn.shopify.com/s/files/1/0133/8576/0826/files/i9gwlyq0s2c01_large.jpg?v=1566935718",
                "https://tnj.com/_next/image/?url=https%3A%2F%2Fcms.tnj.com%2Fwp-content%2Fuploads%2F2025%2F01%2Fblack-bodybuilders.jpg&w=1920&q=75",
                "https://i.ytimg.com/vi/h1rA2jMS-6I/hq720.jpg?sqp=-oaymwEXCK4FEIIDSFryq4qpAwkIARUAAIhCGAE=&rs=AOn4CLAD6U52LVuq545VY8wy9i991qlNtA",
                "https://i.etsystatic.com/17379512/r/il/64394f/4928338306/il_570xN.4928338306_8zlf.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQFMUhBEyj9JIiPDtOECGRtIXf8YuCpgB1x2w&s",
                "https://whatnext.pl/wp-content/uploads/2021/02/doom-slayer-min.jpg"
            )

            GalleryScreen(
                imageUrls = sampleImages,
                onImageClick = { imageUrl ->
                    // np. możesz nawigować do szczegółów
                    navController.navigate("imageDetail/${Uri.encode(imageUrl)}")
                }
            )
        }
    }
}