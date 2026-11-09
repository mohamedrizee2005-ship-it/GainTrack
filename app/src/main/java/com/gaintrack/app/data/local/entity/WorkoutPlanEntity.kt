package com.gaintrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_plans")
data class WorkoutPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dayOfWeek: String, // MONDAY, TUESDAY, THURSDAY, SATURDAY
    val title: String,
    val description: String,
    val estimatedDurationMinutes: Int = 35,
    val targetMuscleGroups: String
)

@Entity(tableName = "workout_exercises")
data class WorkoutExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutPlanId: Long,
    val orderIndex: Int,
    val name: String,
    val defaultSets: Int,
    val minReps: Int,
    val maxReps: Int,
    val defaultRestSeconds: Int = 60,
    val defaultWeightKg: Double = 0.0,
    val formInstructions: String,
    val safetyTips: String,
    val targetMuscles: String
)
