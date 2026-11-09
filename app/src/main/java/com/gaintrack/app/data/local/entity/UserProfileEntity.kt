package com.gaintrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val currentWeightKg: Double = 47.0,
    val targetWeightKg: Double = 55.0,
    val heightCm: Double = 168.0,
    val calorieTarget: Int = 2100,
    val proteinTarget: Int = 85,
    val fitnessLevel: String = "Beginner",
    val pushupAbility: String = "5–10 reps",
    val mainGoal: String = "Healthy weight gain & muscle development",
    val trainingLocation: String = "Home (Backpack & Furniture)",
    val workoutFrequency: Int = 4,
    val preferredWorkoutTime: String = "19:30",
    val foodPreference: String = "Affordable Sri Lankan food",
    val darkTheme: Boolean = true,
    val workoutReminderEnabled: Boolean = true,
    val nutritionReminderEnabled: Boolean = true,
    val checkInReminderEnabled: Boolean = true,
    val waterReminderEnabled: Boolean = true,
    val sleepReminderEnabled: Boolean = true,
    val workoutReminderTime: String = "19:30",
    val checkInReminderTime: String = "19:00"
) {
    val bmi: Double
        get() {
            val heightM = heightCm / 100.0
            return if (heightM > 0) currentWeightKg / (heightM * heightM) else 0.0
        }
}
