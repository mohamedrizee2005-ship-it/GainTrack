package com.gaintrack.app.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

class ReminderScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    fun scheduleWorkoutReminder(hour: Int = 19, minute: Int = 30) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ReminderBroadcastReceiver.ACTION_WORKOUT_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun scheduleSundayCheckInReminder(hour: Int = 19, minute: Int = 0) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ReminderBroadcastReceiver.ACTION_CHECKIN_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            102,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
    }

    fun scheduleNutritionReminder(hour: Int = 20, minute: Int = 0) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            action = ReminderBroadcastReceiver.ACTION_NUTRITION_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            103,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun scheduleAllDefaultReminders() {
        scheduleWorkoutReminder(19, 30)
        scheduleSundayCheckInReminder(19, 0)
        // Workout-only v4: stop old nutrition reminders when upgrading.
        cancelReminder(ReminderBroadcastReceiver.ACTION_NUTRITION_REMINDER, 103)
    }

    fun cancelReminder(action: String, requestCode: Int) {
        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            this.action = action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager?.cancel(pendingIntent)
    }
}
