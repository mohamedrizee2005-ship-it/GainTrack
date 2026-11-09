package com.gaintrack.app.data.repository

import com.gaintrack.app.data.local.dao.WorkoutDao
import com.gaintrack.app.data.local.entity.ExerciseSetRecordEntity
import com.gaintrack.app.data.local.entity.WorkoutExerciseEntity
import com.gaintrack.app.data.local.entity.WorkoutPlanEntity
import com.gaintrack.app.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class WorkoutRepository(private val workoutDao: WorkoutDao) {

    val allPlans: Flow<List<WorkoutPlanEntity>> = workoutDao.getAllPlans()
    val allSessions: Flow<List<WorkoutSessionEntity>> = workoutDao.getAllSessions()
    val totalSessionsCount: Flow<Int> = workoutDao.getTotalCompletedSessionsCount()
    val totalSetsCount: Flow<Int> = workoutDao.getTotalCompletedSetsCount()
    val totalRepsCount: Flow<Int> = workoutDao.getTotalRepsCount()

    fun getExercisesForPlan(planId: Long): Flow<List<WorkoutExerciseEntity>> {
        return workoutDao.getExercisesForPlan(planId)
    }

    suspend fun getExercisesForPlanSync(planId: Long): List<WorkoutExerciseEntity> {
        return workoutDao.getExercisesForPlanSync(planId)
    }

    fun getPlanById(planId: Long): Flow<WorkoutPlanEntity?> {
        return workoutDao.getPlanById(planId)
    }

    fun getRecentSessions(limit: Int = 10): Flow<List<WorkoutSessionEntity>> {
        return workoutDao.getRecentSessions(limit)
    }

    fun getCompletedSessionsThisWeek(): Flow<Int> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return workoutDao.getSessionsCountSince(cal.timeInMillis)
    }

    suspend fun getLastSessionForPlan(planId: Long): WorkoutSessionEntity? {
        return workoutDao.getLastSessionForPlan(planId)
    }

    suspend fun getRecentRecordsForExercise(exerciseName: String, limit: Int = 5): List<ExerciseSetRecordEntity> {
        return workoutDao.getRecentRecordsForExercise(exerciseName, limit)
    }

    suspend fun saveCompletedWorkout(
        planId: Long,
        title: String,
        startTimeMs: Long,
        endTimeMs: Long,
        durationSeconds: Int,
        completedSets: Int,
        totalReps: Int,
        isCompleted: Boolean,
        notes: String,
        records: List<ExerciseSetRecordEntity>
    ): Long {
        val session = WorkoutSessionEntity(
            workoutPlanId = planId,
            workoutTitle = title,
            startTimeEpochMs = startTimeMs,
            endTimeEpochMs = endTimeMs,
            durationSeconds = durationSeconds,
            completedSets = completedSets,
            totalReps = totalReps,
            isCompleted = isCompleted,
            notes = notes
        )
        val sessionId = workoutDao.insertSession(session)
        val updatedRecords = records.map { it.copy(workoutSessionId = sessionId) }
        workoutDao.insertSetRecords(updatedRecords)
        return sessionId
    }

    suspend fun updateExercise(exercise: WorkoutExerciseEntity) {
        workoutDao.updateExercise(exercise)
    }

    /**
     * Checks if progressive overload should be suggested.
     * When completed reps reach maxReps in all sets, suggest adding backpack load or progression.
     */
    fun evaluateProgressiveOverload(
        exercise: WorkoutExerciseEntity,
        completedSetsList: List<Pair<Int, Double>> // Pair<Reps, WeightKg>
    ): String? {
        if (completedSetsList.isEmpty()) return null
        val allHitMax = completedSetsList.all { it.first >= exercise.maxReps }
        if (!allHitMax) return null

        val currentWeight = completedSetsList.firstOrNull()?.second ?: 0.0
        return if (currentWeight <= 0.0) {
            "Outstanding! You hit the top of the rep range (${exercise.maxReps} reps) across all sets. Suggestion: Next session, add 1-2 kg to your backpack or progress the elevation angle."
        } else {
            "Great strength gain! You completed all sets at ${currentWeight}kg with ${exercise.maxReps} reps. Suggestion: Add +0.5 to +1.0 kg to your backpack next session."
        }
    }
}
