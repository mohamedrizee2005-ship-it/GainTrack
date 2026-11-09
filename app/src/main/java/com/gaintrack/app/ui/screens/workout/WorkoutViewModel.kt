package com.gaintrack.app.ui.screens.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gaintrack.app.data.local.entity.ExerciseSetRecordEntity
import com.gaintrack.app.data.local.entity.WorkoutExerciseEntity
import com.gaintrack.app.data.local.entity.WorkoutPlanEntity
import com.gaintrack.app.data.local.entity.WorkoutSessionEntity
import com.gaintrack.app.data.repository.WorkoutRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ActiveWorkoutUiState(
    val plan: WorkoutPlanEntity? = null,
    val exercises: List<WorkoutExerciseEntity> = emptyList(),
    val currentExerciseIndex: Int = 0,
    val completedSetRecords: List<ExerciseSetRecordEntity> = emptyList(),
    val currentSetNumber: Int = 1,
    val inputReps: String = "10",
    val inputWeightKg: String = "0.0",
    val sessionDurationSeconds: Int = 0,
    val isPaused: Boolean = false,
    val showRestTimer: Boolean = false,
    val restTimerDuration: Int = 60,
    val previousExercisePerformance: String = "",
    val progressiveOverloadAlert: String? = null,
    val isSessionFinished: Boolean = false,
    val savedSessionId: Long? = null
)

class WorkoutViewModel(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    val allPlans: StateFlow<List<WorkoutPlanEntity>> = workoutRepository.allPlans.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val recentSessions: StateFlow<List<WorkoutSessionEntity>> = workoutRepository.getRecentSessions(10).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _activeState = MutableStateFlow(ActiveWorkoutUiState())
    val activeState: StateFlow<ActiveWorkoutUiState> = _activeState.asStateFlow()

    private var timerJob: Job? = null
    private var sessionStartTimeMs: Long = 0L

    fun startWorkoutSession(planId: Long) {
        viewModelScope.launch {
            val plans = workoutRepository.allPlans
            val exercises = workoutRepository.getExercisesForPlanSync(planId)
            sessionStartTimeMs = System.currentTimeMillis()

            val firstEx = exercises.firstOrNull()
            val initialReps = firstEx?.minReps?.toString() ?: "10"
            val initialWeight = firstEx?.defaultWeightKg?.toString() ?: "0.0"

            val pastPerformance = if (firstEx != null) {
                fetchPastPerformanceText(firstEx.name)
            } else ""

            _activeState.value = ActiveWorkoutUiState(
                exercises = exercises,
                currentExerciseIndex = 0,
                currentSetNumber = 1,
                inputReps = initialReps,
                inputWeightKg = initialWeight,
                sessionDurationSeconds = 0,
                isPaused = false,
                previousExercisePerformance = pastPerformance
            )

            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                if (!_activeState.value.isPaused) {
                    _activeState.update { it.copy(sessionDurationSeconds = it.sessionDurationSeconds + 1) }
                }
            }
        }
    }

    fun togglePauseTimer() {
        _activeState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun updateInputReps(reps: String) {
        _activeState.update { it.copy(inputReps = reps) }
    }

    fun updateInputWeight(weight: String) {
        _activeState.update { it.copy(inputWeightKg = weight) }
    }

    fun completeCurrentSet() {
        val state = _activeState.value
        val currentEx = state.exercises.getOrNull(state.currentExerciseIndex) ?: return

        val reps = state.inputReps.toIntOrNull() ?: currentEx.minReps
        val weight = state.inputWeightKg.toDoubleOrNull() ?: currentEx.defaultWeightKg

        val record = ExerciseSetRecordEntity(
            workoutSessionId = 0L,
            exerciseName = currentEx.name,
            setNumber = state.currentSetNumber,
            targetReps = currentEx.maxReps,
            completedReps = reps,
            weightKg = weight,
            isCompleted = true
        )

        val updatedRecords = state.completedSetRecords + record

        // Check if exercise has more sets
        if (state.currentSetNumber < currentEx.defaultSets) {
            _activeState.update {
                it.copy(
                    completedSetRecords = updatedRecords,
                    currentSetNumber = it.currentSetNumber + 1,
                    showRestTimer = true,
                    restTimerDuration = currentEx.defaultRestSeconds
                )
            }
        } else {
            // Exercise completed - evaluate progressive overload
            val exerciseSets = updatedRecords.filter { it.exerciseName == currentEx.name }
                .map { Pair(it.completedReps, it.weightKg) }
            val overloadHint = workoutRepository.evaluateProgressiveOverload(currentEx, exerciseSets)

            // Check if there is a next exercise
            if (state.currentExerciseIndex < state.exercises.size - 1) {
                val nextIdx = state.currentExerciseIndex + 1
                val nextEx = state.exercises[nextIdx]
                viewModelScope.launch {
                    val pastPerf = fetchPastPerformanceText(nextEx.name)
                    _activeState.update {
                        it.copy(
                            completedSetRecords = updatedRecords,
                            currentExerciseIndex = nextIdx,
                            currentSetNumber = 1,
                            inputReps = nextEx.minReps.toString(),
                            inputWeightKg = nextEx.defaultWeightKg.toString(),
                            showRestTimer = true,
                            restTimerDuration = currentEx.defaultRestSeconds,
                            previousExercisePerformance = pastPerf,
                            progressiveOverloadAlert = overloadHint
                        )
                    }
                }
            } else {
                // Completed all exercises in workout
                _activeState.update {
                    it.copy(
                        completedSetRecords = updatedRecords,
                        progressiveOverloadAlert = overloadHint
                    )
                }
                finishAndSaveWorkout(isComplete = true)
            }
        }
    }

    fun goToNextExercise() {
        val state = _activeState.value
        if (state.currentExerciseIndex < state.exercises.size - 1) {
            val nextIdx = state.currentExerciseIndex + 1
            val nextEx = state.exercises[nextIdx]
            viewModelScope.launch {
                val pastPerf = fetchPastPerformanceText(nextEx.name)
                _activeState.update {
                    it.copy(
                        currentExerciseIndex = nextIdx,
                        currentSetNumber = 1,
                        inputReps = nextEx.minReps.toString(),
                        inputWeightKg = nextEx.defaultWeightKg.toString(),
                        previousExercisePerformance = pastPerf
                    )
                }
            }
        }
    }

    fun goToPreviousExercise() {
        val state = _activeState.value
        if (state.currentExerciseIndex > 0) {
            val prevIdx = state.currentExerciseIndex - 1
            val prevEx = state.exercises[prevIdx]
            viewModelScope.launch {
                val pastPerf = fetchPastPerformanceText(prevEx.name)
                _activeState.update {
                    it.copy(
                        currentExerciseIndex = prevIdx,
                        currentSetNumber = 1,
                        inputReps = prevEx.minReps.toString(),
                        inputWeightKg = prevEx.defaultWeightKg.toString(),
                        previousExercisePerformance = pastPerf
                    )
                }
            }
        }
    }

    fun dismissRestTimer() {
        _activeState.update { it.copy(showRestTimer = false) }
    }

    fun finishAndSaveWorkout(isComplete: Boolean) {
        timerJob?.cancel()
        val state = _activeState.value
        val endTime = System.currentTimeMillis()
        val plan = state.plan
        val records = state.completedSetRecords

        val completedSets = records.size
        val totalReps = records.sumOf { it.completedReps }

        viewModelScope.launch {
            val sessionId = workoutRepository.saveCompletedWorkout(
                planId = plan?.id ?: 1L,
                title = plan?.title ?: "Home Workout Session",
                startTimeMs = sessionStartTimeMs,
                endTimeMs = endTime,
                durationSeconds = state.sessionDurationSeconds,
                completedSets = completedSets,
                totalReps = totalReps,
                isCompleted = isComplete,
                notes = if (isComplete) "Full session completed" else "Early finish - partial progress saved",
                records = records
            )

            _activeState.update {
                it.copy(
                    isSessionFinished = true,
                    savedSessionId = sessionId
                )
            }
        }
    }

    private suspend fun fetchPastPerformanceText(exerciseName: String): String {
        val past = workoutRepository.getRecentRecordsForExercise(exerciseName, 3)
        return if (past.isNotEmpty()) {
            val setsText = past.joinToString(", ") { "${it.completedReps} reps" + if (it.weightKg > 0) " @ ${it.weightKg}kg" else "" }
            "Last session: $setsText"
        } else {
            "First time logging this exercise. Focus on strict form!"
        }
    }
}
