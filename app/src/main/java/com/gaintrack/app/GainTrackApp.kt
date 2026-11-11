package com.gaintrack.app

import android.app.Application
import com.gaintrack.app.data.local.GainTrackDatabase
import com.gaintrack.app.data.repository.UserRepository
import com.gaintrack.app.data.repository.WeightRepository
import com.gaintrack.app.data.repository.WorkoutRepository
import com.gaintrack.app.notification.NotificationHelper
import com.gaintrack.app.notification.ReminderScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class GainTrackApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val database by lazy { GainTrackDatabase.getDatabase(this, applicationScope) }
    val userRepository by lazy { UserRepository(database.userDao()) }
    val workoutRepository by lazy { WorkoutRepository(database.workoutDao()) }
    val weightRepository by lazy { WeightRepository(database.weightDao(), database.userDao()) }
    val notificationHelper by lazy { NotificationHelper(this) }
    val reminderScheduler by lazy { ReminderScheduler(this) }

    override fun onCreate() {
        super.onCreate()
        // V4 no longer uses AI. Remove the encrypted secret left by older releases.
        deleteSharedPreferences("gaintrack_gemini_secret")
        try {
            val store = java.security.KeyStore.getInstance("AndroidKeyStore")
            store.load(null)
            val alias = "gaintrack_gemini_local_aes_v1"
            if (store.containsAlias(alias)) store.deleteEntry(alias)
        } catch (_: Exception) {
            // Keystore may be unavailable on some devices; the unused secret is not accessed.
        }
        reminderScheduler.scheduleAllDefaultReminders()
    }
}
