package com.gaintrack.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Workouts : Screen("workouts", "Workouts", Icons.Default.FitnessCenter)
    object Nutrition : Screen("nutrition", "Nutrition", Icons.Default.Restaurant)
    object Progress : Screen("progress", "Progress", Icons.Default.BarChart)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)

    object ActiveWorkout : Screen("active_workout/{planId}", "Active Workout") {
        fun createRoute(planId: Long) = "active_workout/$planId"
    }

    object WorkoutSummary : Screen("workout_summary/{sessionId}", "Workout Summary") {
        fun createRoute(sessionId: Long) = "workout_summary/$sessionId"
    }
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Workouts,
    Screen.Nutrition,
    Screen.Progress,
    Screen.Profile
)
