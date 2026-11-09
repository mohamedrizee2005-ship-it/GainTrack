package com.gaintrack.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.gaintrack.app.MainActivity

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_WORKOUTS = "channel_gaintrack_workouts"
        const val CHANNEL_CHECKINS = "channel_gaintrack_checkins"
        const val CHANNEL_NUTRITION = "channel_gaintrack_nutrition"
        const val CHANNEL_WELLNESS = "channel_gaintrack_wellness"

        const val NOTIF_ID_WORKOUT = 1001
        const val NOTIF_ID_CHECKIN = 1002
        const val NOTIF_ID_NUTRITION = 1003
        const val NOTIF_ID_WATER = 1004
        const val NOTIF_ID_SLEEP = 1005
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val workoutChannel = NotificationChannel(
                CHANNEL_WORKOUTS,
                "Workout Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for scheduled evening workout sessions"
                enableVibration(true)
            }

            val checkinChannel = NotificationChannel(
                CHANNEL_CHECKINS,
                "Sunday Check-in",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weekly progress review and Sunday weight check-ins"
                enableVibration(true)
            }

            val nutritionChannel = NotificationChannel(
                CHANNEL_NUTRITION,
                "Nutrition & Meals",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to log calories and protein"
            }

            val wellnessChannel = NotificationChannel(
                CHANNEL_WELLNESS,
                "Recovery & Hydration",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Reminders for water hydration and restful sleep"
            }

            notificationManager.createNotificationChannels(
                listOf(workoutChannel, checkinChannel, nutritionChannel, wellnessChannel)
            )
        }
    }

    fun showWorkoutReminder(title: String, message: String) {
        showNotification(
            channelId = CHANNEL_WORKOUTS,
            notifId = NOTIF_ID_WORKOUT,
            title = title,
            message = message
        )
    }

    fun showSundayCheckInReminder() {
        showNotification(
            channelId = CHANNEL_CHECKINS,
            notifId = NOTIF_ID_CHECKIN,
            title = "GainTrack: Sunday Weekly Check-in",
            message = "Time for your weekly check-in! Record your weight, energy, and review your weekly consistency."
        )
    }

    fun showNutritionReminder(title: String, message: String) {
        showNotification(
            channelId = CHANNEL_NUTRITION,
            notifId = NOTIF_ID_NUTRITION,
            title = title,
            message = message
        )
    }

    fun showWaterReminder() {
        showNotification(
            channelId = CHANNEL_WELLNESS,
            notifId = NOTIF_ID_WATER,
            title = "GainTrack Hydration Check",
            message = "Stay hydrated! Drink a fresh glass of water to support digestion, muscle fullness, and recovery."
        )
    }

    fun showSleepReminder() {
        showNotification(
            channelId = CHANNEL_WELLNESS,
            notifId = NOTIF_ID_SLEEP,
            title = "GainTrack Sleep & Recovery",
            message = "Muscles grow during deep rest. Aim for 7–8 hours of solid sleep tonight to fuel tomorrow's gains."
        )
    }

    private fun showNotification(
        channelId: String,
        notifId: Int,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notifId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(notifId, builder.build())
        } catch (_: SecurityException) {
            // Permission not granted by user yet
        }
    }
}
