package com.josiah.streaker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val type = intent.getStringExtra("type") ?: "daily"

        when (type) {
            "daily" -> {
                StreakerNotificationService.showNotification(
                    context = context,
                    title   = "🔥 Keep your streak alive!",
                    message = "Don't forget to complete your habits today."
                )
            }
            "streak_risk" -> {
                StreakerNotificationService.showNotification(
                    context = context,
                    title   = "⚠️ Streak at risk!",
                    message = "You haven't completed your habits yet. Don't break your streak!"
                )
            }
        }
    }
}

object NotificationScheduler {

    // Schedule daily reminder at 9 AM
    fun scheduleDailyReminder(context: Context, enabled: Boolean) {
        val alarmManager  = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent        = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("type", "daily")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 1001, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (!enabled) {
            alarmManager.cancel(pendingIntent)
            return
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE,      0)
            set(Calendar.SECOND,      0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    // Schedule streak risk alert at 8 PM
    fun scheduleStreakAlert(context: Context, enabled: Boolean) {
        val alarmManager  = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent        = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("type", "streak_risk")
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, 1002, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        if (!enabled) {
            alarmManager.cancel(pendingIntent)
            return
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20)
            set(Calendar.MINUTE,      0)
            set(Calendar.SECOND,      0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}