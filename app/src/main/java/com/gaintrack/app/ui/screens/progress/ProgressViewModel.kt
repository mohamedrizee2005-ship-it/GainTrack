package com.gaintrack.app.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaintrack.app.data.local.entity.UserProfileEntity
import com.gaintrack.app.data.local.entity.WeeklyCheckInEntity
import com.gaintrack.app.data.local.entity.WeightLogEntity
import com.gaintrack.app.data.repository.UserRepository
import com.gaintrack.app.data.repository.WeightRepository
import com.gaintrack.app.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProgressUiState(
    val userProfile: UserProfileEntity = UserProfileEntity(),
    val weightLogs: List<WeightLogEntity> = emptyList(),
    val filteredWeightLogs: List<WeightLogEntity> = emptyList(),
    val selectedTimeframeDays: Int = 30, // 7, 30, 90, 365
    val totalWorkoutsCompleted: Int = 0,
    val totalSetsCompleted: Int = 0,
    val totalRepsCompleted: Int = 0,
    val weeklyWorkoutsDoneThisWeek: Int = 0,
    val recentCheckIns: List<WeeklyCheckInEntity> = emptyList(),
    val latestCheckIn: WeeklyCheckInEntity? = null,
    val currentPhase: String = "Month 1: Foundation & Technique (Weeks 1–4)"
)

private data class TrainingStats(
    val totalSessions: Int = 0,
    val totalSets: Int = 0,
    val totalReps: Int = 0,
    val weeklyWorkouts: Int = 0
)

class ProgressViewModel(
    private val weightRepository: WeightRepository,
    private val workoutRepository: WorkoutRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _selectedTimeframeDays = MutableStateFlow(30)
    val selectedTimeframeDays: StateFlow<Int> = _selectedTimeframeDays.asStateFlow()

    private val statsFlow = combine(
        workoutRepository.totalSessionsCount,
        workoutRepository.totalSetsCount,
        workoutRepository.totalRepsCount,
        workoutRepository.getCompletedSessionsThisWeek()
    ) { sessions, sets, reps, weekly ->
        TrainingStats(sessions, sets, reps, weekly)
    }

    val uiState: StateFlow<ProgressUiState> = combine(
        userRepository.userProfile,
        weightRepository.allWeightLogs,
        weightRepository.allCheckIns,
        statsFlow,
        _selectedTimeframeDays
    ) { profile, allLogs, checkIns, stats, timeframe ->
        val userProf = profile ?: UserProfileEntity()
        val cutoffMs = System.currentTimeMillis() - (timeframe.toLong() * 24L * 60L * 60L * 1000L)
        val filteredLogs = if (timeframe >= 365) allLogs else allLogs.filter { it.timestampEpochMs >= cutoffMs }

        val phase = when {
            stats.totalSessions < 16 -> "Month 1: Foundation & Technique (Weeks 1–4)"
            stats.totalSessions < 32 -> "Month 2: Strength & Progressive Overload (Weeks 5–8)"
            else -> "Month 3: Muscle Development & Consistency (Weeks 9–12)"
        }

        ProgressUiState(
            userProfile = userProf,
            weightLogs = allLogs,
            filteredWeightLogs = filteredLogs,
            selectedTimeframeDays = timeframe,
            totalWorkoutsCompleted = stats.totalSessions,
            totalSetsCompleted = stats.totalSets,
            totalRepsCompleted = stats.totalReps,
            weeklyWorkoutsDoneThisWeek = stats.weeklyWorkouts,
            recentCheckIns = checkIns,
            latestCheckIn = checkIns.firstOrNull(),
            currentPhase = phase
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProgressUiState()
    )

    fun setTimeframe(days: Int) {
        _selectedTimeframeDays.value = days
    }

    fun logNewWeight(weightKg: Double, notes: String) {
        viewModelScope.launch {
            weightRepository.logWeight(weightKg, notes)
        }
    }

    fun submitSundayCheckIn(
        weightKg: Double,
        workoutsCompleted: Int,
        energyLevel: String,
        appetite: String,
        sleepHours: Double,
        soreness: String,
        notes: String
    ) {
        viewModelScope.launch {
            weightRepository.logWeeklyCheckIn(
                weightKg = weightKg,
                workoutsCompleted = workoutsCompleted,
                energyLevel = energyLevel,
                appetite = appetite,
                averageSleepHours = sleepHours,
                muscleSoreness = soreness,
                notes = notes
            )
        }
    }
}
