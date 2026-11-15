package com.example.academy.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AcademyBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        Log.d("AcademyBootReceiver", "Received broadcast: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            // Reschedule daily tips, quiz reminders and track recommendations
            val prefs = context.getSharedPreferences("academy_notification_schedule", Context.MODE_PRIVATE)
            AcademyNotificationScheduler.scheduleDailyNotifications(
                context = context,
                enabled = prefs.getBoolean("enabled", false),
                intervalHours = prefs.getLong("interval_hours", 12L),
                forceReschedule = true
            )
            Log.d("AcademyBootReceiver", "Rescheduled Academy periodic notifications successfully.")
        }
    }
}
