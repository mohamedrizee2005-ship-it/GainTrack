package com.gaintrack.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaintrack.app.data.local.entity.MealLogEntity
import com.gaintrack.app.data.local.entity.UserProfileEntity
import com.gaintrack.app.data.local.entity.WeightLogEntity
import com.gaintrack.app.data.local.entity.WorkoutPlanEntity
import com.gaintrack.app.data.repository.NutritionRepository
import com.gaintrack.app.data.repository.UserRepository
import com.gaintrack.app.data.repository.WeightRepository
import com.gaintrack.app.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class HomeUiState(
    val userProfile: UserProfileEntity = UserProfileEntity(),
    val todayWorkout: WorkoutPlanEntity? = null,
    val nextScheduledWorkout: WorkoutPlanEntity? = null,
    val nextWorkoutDayText: String = "",
    val completedWorkoutsCountThisWeek: Int = 0,
    val weeklyCompletionPercentage: Int = 0,
    val todayCalories: Int = 0,
    val todayProtein: Double = 0.0,
    val weightChangeSinceLastWeek: Double = 0.0,
    val nextCheckInDateText: String = "",
    val motivationalQuote: String = "Small consistent meals + progressive bodyweight training = steady muscle gain."
)

class HomeViewModel(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val nutritionRepository: NutritionRepository,
    private val weightRepository: WeightRepository
) : ViewModel() {

    private val motivationalQuotes = listOf(
        "Safe, consistent nutrition fuels muscle growth. Aim for gradual 0.1–0.25 kg progress per week.",
        "Your bodyweight and backpack training builds genuine tendon and muscle strength without gym equipment.",
        "Consistency over intensity: 4 solid 35-minute home sessions will transform your frame over 12 weeks.",
        "Eat your dhal, eggs, milk, and rice steadily. Muscle is constructed set by set, meal by meal."
    )

    private val todayDateString = nutritionRepository.getTodayDateString()

    val uiState: StateFlow<HomeUiState> = combine(
        userRepository.userProfile,
        workoutRepository.allPlans,
        workoutRepository.getCompletedSessionsThisWeek(),
        nutritionRepository.getMealsForDate(todayDateString),
        weightRepository.allWeightLogs
    ) { profile, plans, completedCount, meals, weightLogs ->
        val currentProfile = profile ?: UserProfileEntity()
        val currentDayOfWeek = getCurrentDayOfWeek()

        val todayPlan = plans.firstOrNull { it.dayOfWeek.equals(currentDayOfWeek, ignoreCase = true) }
        val nextPlan = findNextScheduledWorkout(plans, currentDayOfWeek)

        val totalCalories = meals.sumOf { it.calories }
        val totalProtein = meals.sumOf { it.proteinGrams }

        val weeklyCompletion = (completedCount.toFloat() / currentProfile.workoutFrequency.coerceAtLeast(1) * 100).toInt().coerceAtMost(100)

        val weightDelta = calculateWeeklyWeightDelta(weightLogs)
        val quoteIndex = ((System.currentTimeMillis() / (1000L * 60 * 60 * 6)) % motivationalQuotes.size.toLong()).toInt().coerceIn(0, motivationalQuotes.size - 1)

        HomeUiState(
            userProfile = currentProfile,
            todayWorkout = todayPlan,
            nextScheduledWorkout = nextPlan.first,
            nextWorkoutDayText = nextPlan.second,
            completedWorkoutsCountThisWeek = completedCount,
            weeklyCompletionPercentage = weeklyCompletion,
            todayCalories = totalCalories,
            todayProtein = totalProtein,
            weightChangeSinceLastWeek = weightDelta,
            nextCheckInDateText = getNextSundayDateString(),
            motivationalQuote = motivationalQuotes[quoteIndex]
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    private fun getCurrentDayOfWeek(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "MONDAY"
            Calendar.TUESDAY -> "TUESDAY"
            Calendar.WEDNESDAY -> "WEDNESDAY"
            Calendar.THURSDAY -> "THURSDAY"
            Calendar.FRIDAY -> "FRIDAY"
            Calendar.SATURDAY -> "SATURDAY"
            Calendar.SUNDAY -> "SUNDAY"
            else -> "MONDAY"
        }
    }

    private fun findNextScheduledWorkout(plans: List<WorkoutPlanEntity>, currentDay: String): Pair<WorkoutPlanEntity?, String> {
        val scheduledOrder = listOf("MONDAY", "TUESDAY", "THURSDAY", "SATURDAY")
        val currentIndex = scheduledOrder.indexOf(currentDay)

        if (currentIndex != -1 && plans.any { it.dayOfWeek == currentDay }) {
            return Pair(plans.first { it.dayOfWeek == currentDay }, "Today")
        }

        // Search for next day
        val dayIndexMap = mapOf(
            "SUNDAY" to 0,
            "MONDAY" to 1,
            "TUESDAY" to 2,
            "WEDNESDAY" to 3,
            "THURSDAY" to 4,
            "FRIDAY" to 5,
            "SATURDAY" to 6
        )

        val curIdx = dayIndexMap[currentDay] ?: 0
        val targetDayOrder = listOf("MONDAY", "TUESDAY", "THURSDAY", "SATURDAY")

        for (target in targetDayOrder) {
            val targetIdx = dayIndexMap[target] ?: 0
            if (targetIdx > curIdx) {
                val plan = plans.firstOrNull { it.dayOfWeek == target }
                return Pair(plan, target.lowercase().replaceFirstChar { it.titlecase(Locale.getDefault()) })
            }
        }

        val firstPlan = plans.firstOrNull { it.dayOfWeek == "MONDAY" }
        return Pair(firstPlan, "Monday")
    }

    private fun calculateWeeklyWeightDelta(logs: List<WeightLogEntity>): Double {
        if (logs.size < 2) return 0.0
        val latest = logs.last().weightKg
        val prev = logs[logs.size - 2].weightKg
        return (latest - prev)
    }

    private fun getNextSundayDateString(): String {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_WEEK)
        val daysUntilSunday = (Calendar.SUNDAY - currentDay + 7) % 7
        cal.add(Calendar.DAY_OF_YEAR, if (daysUntilSunday == 0) 7 else daysUntilSunday)
        val sdf = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
        return sdf.format(cal.time)
    }
}
