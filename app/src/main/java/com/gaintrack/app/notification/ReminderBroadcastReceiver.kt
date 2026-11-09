package com.gaintrack.app.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_WORKOUT_REMINDER = "com.gaintrack.app.ACTION_WORKOUT_REMINDER"
        const val ACTION_CHECKIN_REMINDER = "com.gaintrack.app.ACTION_CHECKIN_REMINDER"
        const val ACTION_NUTRITION_REMINDER = "com.gaintrack.app.ACTION_NUTRITION_REMINDER"
        const val ACTION_WATER_REMINDER = "com.gaintrack.app.ACTION_WATER_REMINDER"
        const val ACTION_SLEEP_REMINDER = "com.gaintrack.app.ACTION_SLEEP_REMINDER"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val helper = NotificationHelper(context)

        when (intent.action) {
            ACTION_WORKOUT_REMINDER -> {
                helper.showWorkoutReminder(
                    title = "GainTrack: Evening Workout Ready",
                    message = "Time for your 35-min session! Prepare your backpack and hydrate. Let's make progress!"
                )
            }
            ACTION_CHECKIN_REMINDER -> {
                helper.showSundayCheckInReminder()
            }
            ACTION_NUTRITION_REMINDER -> {
                helper.showNutritionReminder(
                    title = "GainTrack: Nutrition Check",
                    message = "Have you logged your meals and protein today? Don't forget your weight-gain shake!"
                )
            }
            ACTION_WATER_REMINDER -> {
                helper.showWaterReminder()
            }
            ACTION_SLEEP_REMINDER -> {
                helper.showSleepReminder()
            }
            Intent.ACTION_BOOT_COMPLETED -> {
                // Reschedule default alarms after phone reboot
                val scheduler = ReminderScheduler(context)
                scheduler.scheduleAllDefaultReminders()
            }
        }
    }
}
