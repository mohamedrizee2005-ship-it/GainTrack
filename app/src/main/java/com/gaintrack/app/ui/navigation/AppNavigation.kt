package com.gaintrack.app.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gaintrack.app.GainTrackApp
import com.gaintrack.app.ui.screens.home.HomeScreen
import com.gaintrack.app.ui.screens.home.HomeViewModel
import com.gaintrack.app.ui.screens.nutrition.NutritionScreen
import com.gaintrack.app.ui.screens.nutrition.NutritionViewModel
import com.gaintrack.app.ui.screens.profile.ProfileScreen
import com.gaintrack.app.ui.screens.profile.ProfileViewModel
import com.gaintrack.app.ui.screens.progress.ProgressScreen
import com.gaintrack.app.ui.screens.progress.ProgressViewModel
import com.gaintrack.app.ui.screens.workout.ActiveWorkoutScreen
import com.gaintrack.app.ui.screens.workout.WorkoutSummaryScreen
import com.gaintrack.app.ui.screens.workout.WorkoutViewModel
import com.gaintrack.app.ui.screens.workout.WorkoutsScreen
import com.gaintrack.app.ui.theme.EmeraldPrimary
import com.gaintrack.app.ui.theme.NavyBackground
import com.gaintrack.app.ui.theme.NavyDarkest
import com.gaintrack.app.ui.theme.NavySurface
import com.gaintrack.app.ui.theme.TextMuted
import com.gaintrack.app.ui.theme.TextWhite

@Composable
fun AppNavigation(
    app: GainTrackApp,
    navController: NavHostController = rememberNavController()
) {
    // ViewModels with real repository singletons
    val homeViewModel = rememberViewModel {
        HomeViewModel(app.userRepository, app.workoutRepository, app.nutritionRepository, app.weightRepository)
    }
    val workoutViewModel = rememberViewModel {
        WorkoutViewModel(app.workoutRepository)
    }
    val nutritionViewModel = rememberViewModel {
        NutritionViewModel(app.nutritionRepository, app.userRepository)
    }
    val progressViewModel = rememberViewModel {
        ProgressViewModel(app.weightRepository, app.workoutRepository, app.userRepository)
    }
    val profileViewModel = rememberViewModel {
        ProfileViewModel(app.userRepository, app.workoutRepository, app.nutritionRepository, app.weightRepository, app.database, app.reminderScheduler)
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                NavigationBar(
                    containerColor = NavyDarkest,
                    tonalElevation = 8.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = {
                                screen.icon?.let {
                                    Icon(it, contentDescription = screen.title)
                                }
                            },
                            label = { Text(screen.title, fontSize = 11.sp) },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = EmeraldPrimary,
                                selectedTextColor = EmeraldPrimary,
                                unselectedIconColor = TextMuted,
                                unselectedTextColor = TextMuted,
                                indicatorColor = NavySurface
                            ),
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        },
        containerColor = NavyBackground
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onStartWorkout = { planId ->
                        navController.navigate(Screen.ActiveWorkout.createRoute(planId))
                    },
                    onNavigateToWorkouts = {
                        navController.navigate(Screen.Workouts.route)
                    },
                    onNavigateToNutrition = {
                        navController.navigate(Screen.Nutrition.route)
                    },
                    onNavigateToProgress = {
                        navController.navigate(Screen.Progress.route)
                    }
                )
            }

            composable(Screen.Workouts.route) {
                WorkoutsScreen(
                    viewModel = workoutViewModel,
                    onStartWorkout = { planId ->
                        navController.navigate(Screen.ActiveWorkout.createRoute(planId))
                    }
                )
            }

            composable(Screen.Nutrition.route) {
                NutritionScreen(viewModel = nutritionViewModel)
            }

            composable(Screen.Progress.route) {
                ProgressScreen(viewModel = progressViewModel)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(viewModel = profileViewModel)
            }

            composable(
                route = Screen.ActiveWorkout.route,
                arguments = listOf(navArgument("planId") { type = NavType.LongType })
            ) { backStackEntry ->
                val planId = backStackEntry.arguments?.getLong("planId") ?: 1L
                ActiveWorkoutScreen(
                    planId = planId,
                    viewModel = workoutViewModel,
                    onWorkoutFinished = { sessionId ->
                        navController.navigate(Screen.WorkoutSummary.createRoute(sessionId)) {
                            popUpTo(Screen.Workouts.route)
                        }
                    },
                    onCancel = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.WorkoutSummary.route,
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
                WorkoutSummaryScreen(
                    sessionId = sessionId,
                    viewModel = workoutViewModel,
                    onReturnHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
inline fun <reified VM : androidx.lifecycle.ViewModel> rememberViewModel(
    crossinline creator: () -> VM
): VM {
    return androidx.lifecycle.viewmodel.compose.viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return creator() as T
            }
        }
    )
}
