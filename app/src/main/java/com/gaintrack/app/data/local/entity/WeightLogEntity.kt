package com.gaintrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_logs")
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String, // "YYYY-MM-DD"
    val timestampEpochMs: Long = System.currentTimeMillis(),
    val weightKg: Double,
    val notes: String = ""
)

@Entity(tableName = "weekly_checkins")
data class WeeklyCheckInEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateString: String,
    val timestampEpochMs: Long = System.currentTimeMillis(),
    val weightKg: Double,
    val workoutsCompleted: Int, // out of 4
    val energyLevel: String, // "Low", "Moderate", "High", "Very High"
    val appetite: String, // "Low", "Normal", "High"
    val averageSleepHours: Double,
    val muscleSoreness: String, // "None", "Mild", "Moderate", "High"
    val notes: String = ""
)
