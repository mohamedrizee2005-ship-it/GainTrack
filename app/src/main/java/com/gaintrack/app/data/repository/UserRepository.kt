package com.gaintrack.app.data.repository

import com.gaintrack.app.data.local.dao.UserDao
import com.gaintrack.app.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    val userProfile: Flow<UserProfileEntity?> = userDao.getUserProfile()

    suspend fun getUserProfileSync(): UserProfileEntity? {
        return userDao.getUserProfileSync()
    }

    suspend fun updateProfile(profile: UserProfileEntity) {
        userDao.insertOrUpdateProfile(profile)
    }

    fun calculateBmiCategory(bmi: Double): String {
        return when {
            bmi < 18.5 -> "Underweight (Health assessment advised)"
            bmi < 24.9 -> "Normal / Healthy Weight"
            bmi < 29.9 -> "Overweight"
            else -> "Obese"
        }
    }

    fun getRecommendedCalorieSurplus(currentWeightKg: Double, targetWeightKg: Double): Int {
        // Safe gradual surplus: 250 - 350 kcal above maintenance (approx 35-40 kcal/kg for active young adult)
        val estimatedMaintenance = (currentWeightKg * 36).toInt()
        val safeSurplus = 300
        return estimatedMaintenance + safeSurplus
    }

    fun getRecommendedProteinRange(currentWeightKg: Double): Pair<Int, Int> {
        val minProtein = (currentWeightKg * 1.6).toInt()
        val maxProtein = (currentWeightKg * 2.0).toInt()
        return Pair(minProtein, maxProtein)
    }
}
