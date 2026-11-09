package com.gaintrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gaintrack.app.data.local.entity.ExerciseSetRecordEntity
import com.gaintrack.app.data.local.entity.WorkoutExerciseEntity
import com.gaintrack.app.data.local.entity.WorkoutPlanEntity
import com.gaintrack.app.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_plans ORDER BY id ASC")
    fun getAllPlans(): Flow<List<WorkoutPlanEntity>>

    @Query("SELECT * FROM workout_plans WHERE dayOfWeek = :day LIMIT 1")
    fun getPlanByDay(day: String): Flow<WorkoutPlanEntity?>

    @Query("SELECT * FROM workout_plans WHERE id = :id LIMIT 1")
    fun getPlanById(id: Long): Flow<WorkoutPlanEntity?>

    @Query("SELECT * FROM workout_exercises WHERE workoutPlanId = :planId ORDER BY orderIndex ASC")
    fun getExercisesForPlan(planId: Long): Flow<List<WorkoutExerciseEntity>>

    @Query("SELECT * FROM workout_exercises WHERE workoutPlanId = :planId ORDER BY orderIndex ASC")
    suspend fun getExercisesForPlanSync(planId: Long): List<WorkoutExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<WorkoutPlanEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<WorkoutExerciseEntity>)

    @Update
    suspend fun updateExercise(exercise: WorkoutExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity): Long

    @Update
    suspend fun updateSession(session: WorkoutSessionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetRecords(records: List<ExerciseSetRecordEntity>)

    @Query("SELECT * FROM workout_sessions ORDER BY startTimeEpochMs DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE isCompleted = 1 ORDER BY startTimeEpochMs DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions WHERE workoutPlanId = :planId AND isCompleted = 1 ORDER BY startTimeEpochMs DESC LIMIT 1")
    suspend fun getLastSessionForPlan(planId: Long): WorkoutSessionEntity?

    @Query("SELECT * FROM exercise_set_records WHERE exerciseName = :exerciseName ORDER BY timestampEpochMs DESC LIMIT :limit")
    suspend fun getRecentRecordsForExercise(exerciseName: String, limit: Int = 10): List<ExerciseSetRecordEntity>

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE isCompleted = 1")
    fun getTotalCompletedSessionsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM workout_sessions WHERE isCompleted = 1 AND startTimeEpochMs >= :startOfWeekMs")
    fun getSessionsCountSince(startOfWeekMs: Long): Flow<Int>

    @Query("SELECT COALESCE(SUM(completedSets), 0) FROM workout_sessions WHERE isCompleted = 1")
    fun getTotalCompletedSetsCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(totalReps), 0) FROM workout_sessions WHERE isCompleted = 1")
    fun getTotalRepsCount(): Flow<Int>
}
