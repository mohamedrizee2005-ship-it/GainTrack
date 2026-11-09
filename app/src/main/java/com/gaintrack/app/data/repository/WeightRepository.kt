package com.gaintrack.app.data.repository

import com.gaintrack.app.data.local.dao.UserDao
import com.gaintrack.app.data.local.dao.WeightDao
import com.gaintrack.app.data.local.entity.WeeklyCheckInEntity
import com.gaintrack.app.data.local.entity.WeightLogEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeightRepository(
    private val weightDao: WeightDao,
    private val userDao: UserDao
) {

    val allWeightLogs: Flow<List<WeightLogEntity>> = weightDao.getAllWeightLogs()
    val latestWeightLog: Flow<WeightLogEntity?> = weightDao.getLatestWeightLog()
    val allCheckIns: Flow<List<WeeklyCheckInEntity>> = weightDao.getAllCheckIns()
    val latestCheckIn: Flow<WeeklyCheckInEntity?> = weightDao.getLatestCheckIn()

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    suspend fun logWeight(weightKg: Double, notes: String = ""): Long {
        val log = WeightLogEntity(
            dateString = getTodayDateString(),
            timestampEpochMs = System.currentTimeMillis(),
            weightKg = weightKg,
            notes = notes
        )
        val id = weightDao.insertWeightLog(log)

        // Also update current weight in user profile
        val profile = userDao.getUserProfileSync()
        if (profile != null) {
            userDao.insertOrUpdateProfile(profile.copy(currentWeightKg = weightKg))
        }
        return id
    }

    suspend fun deleteWeightLog(log: WeightLogEntity) {
        weightDao.deleteWeightLog(log)
    }

    suspend fun logWeeklyCheckIn(
        weightKg: Double,
        workoutsCompleted: Int,
        energyLevel: String,
        appetite: String,
        averageSleepHours: Double,
        muscleSoreness: String,
        notes: String
    ): Long {
        val checkIn = WeeklyCheckInEntity(
            dateString = getTodayDateString(),
            timestampEpochMs = System.currentTimeMillis(),
            weightKg = weightKg,
            workoutsCompleted = workoutsCompleted,
            energyLevel = energyLevel,
            appetite = appetite,
            averageSleepHours = averageSleepHours,
            muscleSoreness = muscleSoreness,
            notes = notes
        )
        val id = weightDao.insertCheckIn(checkIn)
        // Also log this as a weight entry
        logWeight(weightKg, "Sunday Check-in: $notes")
        return id
    }
}
