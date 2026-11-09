package com.gaintrack.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gaintrack.app.data.local.entity.WeeklyCheckInEntity
import com.gaintrack.app.data.local.entity.WeightLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Query("SELECT * FROM weight_logs ORDER BY dateString ASC, timestampEpochMs ASC")
    fun getAllWeightLogs(): Flow<List<WeightLogEntity>>

    @Query("SELECT * FROM weight_logs ORDER BY dateString DESC, timestampEpochMs DESC LIMIT 1")
    fun getLatestWeightLog(): Flow<WeightLogEntity?>

    @Query("SELECT * FROM weight_logs ORDER BY dateString DESC, timestampEpochMs DESC LIMIT 2")
    fun getRecentTwoWeightLogs(): Flow<List<WeightLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(log: WeightLogEntity): Long

    @Delete
    suspend fun deleteWeightLog(log: WeightLogEntity)

    @Query("SELECT * FROM weekly_checkins ORDER BY dateString DESC, timestampEpochMs DESC")
    fun getAllCheckIns(): Flow<List<WeeklyCheckInEntity>>

    @Query("SELECT * FROM weekly_checkins ORDER BY dateString DESC, timestampEpochMs DESC LIMIT 1")
    fun getLatestCheckIn(): Flow<WeeklyCheckInEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: WeeklyCheckInEntity): Long
}
