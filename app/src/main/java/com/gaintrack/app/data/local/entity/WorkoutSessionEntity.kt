package com.gaintrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutPlanId: Long,
    val workoutTitle: String,
    val startTimeEpochMs: Long,
    val endTimeEpochMs: Long,
    val durationSeconds: Int,
    val completedSets: Int,
    val totalReps: Int,
    val isCompleted: Boolean = true,
    val notes: String = ""
)

@Entity(tableName = "exercise_set_records")
data class ExerciseSetRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutSessionId: Long,
    val exerciseName: String,
    val setNumber: Int,
    val targetReps: Int,
    val completedReps: Int,
    val weightKg: Double = 0.0,
    val isCompleted: Boolean = true,
    val timestampEpochMs: Long = System.currentTimeMillis()
)
