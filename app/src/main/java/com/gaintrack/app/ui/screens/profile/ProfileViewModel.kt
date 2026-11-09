package com.gaintrack.app.ui.screens.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaintrack.app.data.local.GainTrackDatabase
import com.gaintrack.app.data.local.PrepopulatedData
import com.gaintrack.app.data.local.entity.UserProfileEntity
import com.gaintrack.app.data.repository.NutritionRepository
import com.gaintrack.app.data.repository.UserRepository
import com.gaintrack.app.data.repository.WeightRepository
import com.gaintrack.app.data.repository.WorkoutRepository
import com.gaintrack.app.notification.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter

data class ProfileUiState(
    val profile: UserProfileEntity = UserProfileEntity(),
    val exportSuccessMessage: String? = null,
    val resetSuccessMessage: String? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository,
    private val workoutRepository: WorkoutRepository,
    private val nutritionRepository: NutritionRepository,
    private val weightRepository: WeightRepository,
    private val database: GainTrackDatabase,
    private val reminderScheduler: ReminderScheduler
) : ViewModel() {

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    val uiState: StateFlow<ProfileUiState> = userRepository.userProfile.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserProfileEntity()
    ).let { flow ->
        MutableStateFlow(ProfileUiState()).apply {
            viewModelScope.launch {
                flow.collect { p ->
                    value = ProfileUiState(profile = p ?: UserProfileEntity())
                }
            }
        }
    }

    fun updateProfile(
        currentWeight: Double,
        targetWeight: Double,
        height: Double,
        calorieTarget: Int,
        proteinTarget: Int,
        workoutTime: String,
        workoutReminder: Boolean,
        checkInReminder: Boolean,
        nutritionReminder: Boolean,
        waterReminder: Boolean,
        sleepReminder: Boolean
    ) {
        viewModelScope.launch {
            val current = uiState.value.profile
            val updated = current.copy(
                currentWeightKg = currentWeight,
                targetWeightKg = targetWeight,
                heightCm = height,
                calorieTarget = calorieTarget,
                proteinTarget = proteinTarget,
                preferredWorkoutTime = workoutTime,
                workoutReminderEnabled = workoutReminder,
                checkInReminderEnabled = checkInReminder,
                nutritionReminderEnabled = nutritionReminder,
                waterReminderEnabled = waterReminder,
                sleepReminderEnabled = sleepReminder
            )
            userRepository.updateProfile(updated)

            if (workoutReminder) {
                reminderScheduler.scheduleWorkoutReminder(19, 30)
            }
            if (checkInReminder) {
                reminderScheduler.scheduleSundayCheckInReminder(19, 0)
            }
            if (nutritionReminder) {
                reminderScheduler.scheduleNutritionReminder(20, 0)
            }
            _message.value = "Profile & reminders updated successfully!"
        }
    }

    fun exportDataToCsv(context: Context) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val weightLogs = weightRepository.allWeightLogs.firstOrNull() ?: emptyList()
                    val checkIns = weightRepository.allCheckIns.firstOrNull() ?: emptyList()

                    val exportFile = File(context.getExternalFilesDir(null), "GainTrack_Export.csv")
                    FileWriter(exportFile).use { writer ->
                        writer.append("Type,Date,WeightKg,ExtraInfo,Notes\n")
                        for (w in weightLogs) {
                            writer.append("WeightLog,${w.dateString},${w.weightKg},-,${w.notes.replace(",", ";")}\n")
                        }
                        for (c in checkIns) {
                            writer.append("SundayCheckIn,${c.dateString},${c.weightKg},Workouts:${c.workoutsCompleted}/4;Energy:${c.energyLevel};Sleep:${c.averageSleepHours}h,${c.notes.replace(",", ";")}\n")
                        }
                    }
                    _message.value = "Data exported to ${exportFile.name} successfully!"
                } catch (e: Exception) {
                    _message.value = "Export error: ${e.localizedMessage}"
                }
            }
        }
    }

    fun resetData() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                database.clearAllTables()
                userRepository.updateProfile(PrepopulatedData.defaultUserProfile)
                database.workoutDao().insertPlans(PrepopulatedData.defaultWorkoutPlans)
                for (plan in PrepopulatedData.defaultWorkoutPlans) {
                    val exercises = PrepopulatedData.getExercisesForPlan(plan.id)
                    database.workoutDao().insertExercises(exercises)
                }
                database.nutritionDao().insertFoodItems(PrepopulatedData.defaultFoodItems)
                for (log in PrepopulatedData.getInitialWeightLogs()) {
                    database.weightDao().insertWeightLog(log)
                }
            }
            _message.value = "GainTrack reset to clean initial state."
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
