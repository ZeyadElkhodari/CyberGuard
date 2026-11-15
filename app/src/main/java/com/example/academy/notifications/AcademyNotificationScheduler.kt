package com.example.academy.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.db.CyberGuardDatabase
import com.example.model.Language
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlin.random.Random

object AcademyNotificationScheduler {
    const val CHANNEL_ID = "cyberguard_academy_tips"
    private const val ALARM_REQUEST_BASE = 8801
    private const val TAG = "AcademyNotification"
    const val ACTION_POST_DAILY_TIP = "com.example.academy.ACTION_POST_DAILY_TIP"
    private const val EXTRA_SLOT = "notification_slot"
    private val DAILY_HOURS = intArrayOf(10, 13, 16, 19, 21)
    const val SLOT_DAILY_TIP_MORNING = 0
    const val SLOT_QUIZ_REMINDER = 1
    const val SLOT_DAILY_TIP_AFTERNOON = 2
    const val SLOT_TRACK_RECOMMENDATION = 3
    const val SLOT_DAILY_TIP_EVENING = 4
    private const val NOTIFICATION_ID_BASE = 8801
    private val deliveryLock = Any()

    private fun language(context: Context): Language =
        if (context.getSharedPreferences("cyberguard_settings_prefs", Context.MODE_PRIVATE)
                .getString("app_language", "ar") == "en") Language.EN else Language.AR

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val en = language(context) == Language.EN
            val channel = NotificationChannel(
                CHANNEL_ID,
                if (en) "CyberGuard security tips" else "نصائح سايبر جارد الأمنية",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = if (en) "Scheduled cybersecurity awareness tips" else "إشعارات توعوية دورية لتعزيز الحماية والوعي السيبراني"
                enableVibration(true)
            }
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    /** Schedules daily tips plus one quiz reminder and one learning-track recommendation. */
    fun scheduleDailyNotifications(
        context: Context,
        enabled: Boolean = true,
        intervalHours: Long = 12,
        forceReschedule: Boolean = false
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val prefs = context.getSharedPreferences("academy_notification_schedule", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("enabled", enabled).apply()
        if (!enabled) {
            DAILY_HOURS.indices.forEach { slot -> alarmManager.cancel(pendingIntent(context, slot)) }
            prefs.edit().putBoolean("scheduled", false).apply()
            return
        }
        createNotificationChannel(context)
        DAILY_HOURS.indices.forEach { slot -> scheduleSlot(context, slot) }
        prefs.edit().putBoolean("scheduled", true).putString("schedule", "10:00,13:00,16:00,19:00,21:00").apply()
    }

    private fun pendingIntent(context: Context, slot: Int): PendingIntent {
        val intent = Intent(context, AcademyNotificationReceiver::class.java).apply {
            action = ACTION_POST_DAILY_TIP
            putExtra(EXTRA_SLOT, slot)
        }
        return PendingIntent.getBroadcast(
            context, ALARM_REQUEST_BASE + slot, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun scheduleSlot(context: Context, slot: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, DAILY_HOURS[slot])
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= now.timeInMillis) add(Calendar.DAY_OF_YEAR, 1)
        }
        try {
            val pi = pendingIntent(context, slot)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, target.timeInMillis, pi)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android may defer inexact alarms under battery optimization; exact access
                // is used whenever the system permits it.
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, target.timeInMillis, pi)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, target.timeInMillis, pi)
            }
            Log.d(TAG, "Daily tip slot $slot scheduled at ${DAILY_HOURS[slot]}:00")
        } catch (e: SecurityException) {
            Log.e(TAG, "Unable to schedule slot $slot: ${e.message}")
        }
    }

    fun rescheduleSlotAfterDelivery(context: Context, slot: Int) {
        val enabled = context.getSharedPreferences("academy_notification_schedule", Context.MODE_PRIVATE)
            .getBoolean("enabled", true)
        if (enabled && slot in DAILY_HOURS.indices) scheduleSlot(context, slot)
    }

    fun cancelNotifications(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        DAILY_HOURS.indices.forEach { alarmManager.cancel(pendingIntent(context, it)) }
        context.getSharedPreferences("academy_notification_schedule", Context.MODE_PRIVATE)
            .edit().putBoolean("enabled", false).putBoolean("scheduled", false).apply()
    }

    fun postEducationalNotification(
        context: Context,
        id: Int = Random.nextInt(1000, 9999),
        title: String,
        message: String,
        trackId: String? = null,
        openQuiz: Boolean = false
    ): Boolean {
        createNotificationChannel(context)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            // Security notifications open the in-app security message card only.
            // Learning-track navigation is added solely for explicitly linked reminders.
            if (trackId != null || openQuiz) putExtra("navigate_to", "academy")
            putExtra("notification_title", title)
            putExtra("notification_message", message)
            trackId?.let { putExtra("track_id", it) }
            if (openQuiz) putExtra("open_quiz", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setColor(0xFFFF1744.toInt())
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        if (Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "POST_NOTIFICATIONS permission is not granted; notification was not displayed.")
            return false
        }
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return runCatching {
            // A stable notification ID replaces an already displayed notification for this slot.
            manager.notify(id, builder.build())
            true
        }.onFailure { Log.e(TAG, "Failed to display notification: ${it.message}") }
            .getOrDefault(false)
    }

    suspend fun postIncompleteQuizReminder(context: Context): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val db = CyberGuardDatabase.getInstance(context)
            val tracks = db.academyDao().getAllTracks()
            val pending = tracks.firstOrNull { track ->
                val quiz = db.academyDao().getQuizByTrack(track.id)
                quiz != null && !quiz.isPassed
            } ?: return@withContext false
            if (db.academyDao().getQuizByTrack(pending.id) == null) return@withContext false
            val en = language(context) == Language.EN
            val title = if (en) "Your learning quiz is waiting" else "اختبارك التعليمي ما زال بانتظارك"
            val trackTitle = if (en) pending.titleEn.ifBlank { "Security learning track" } else pending.titleAr
            val message = if (en) "Continue $trackTitle and complete its quiz to test what you learned." else "تابع مسار «$trackTitle» وأكمل الاختبار لقياس ما تعلمته."
            postEducationalNotification(context, id = NOTIFICATION_ID_BASE + SLOT_QUIZ_REMINDER, title = title, message = message, trackId = pending.id, openQuiz = true)
        }.getOrElse { Log.e(TAG, "Quiz reminder failed: ${it.message}"); false }
    }

    suspend fun postTrackRecommendation(context: Context): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val db = CyberGuardDatabase.getInstance(context)
            val dao = db.academyDao()
            val tracks = dao.getAllTracks()
            val recommended = tracks.firstOrNull { !it.trackCompleted && it.progressPercent > 0 }
                ?: tracks.firstOrNull { !it.trackCompleted }
                ?: return@withContext false
            val en = language(context) == Language.EN
            val title = if (en) "Recommended next step" else "خطوتك التعليمية التالية"
            val trackTitle = if (en) recommended.titleEn.ifBlank { "Cybersecurity basics" } else recommended.titleAr
            val message = if (en) "Continue learning with: $trackTitle" else "واصل التعلم في المسار المقترح: $trackTitle"
            postEducationalNotification(context, id = NOTIFICATION_ID_BASE + SLOT_TRACK_RECOMMENDATION, title = title, message = message, trackId = recommended.id)
        }.getOrElse { Log.e(TAG, "Track recommendation failed: ${it.message}"); false }
    }

    suspend fun postRandomTipFromCache(context: Context, slot: Int): Boolean = withContext(Dispatchers.IO) {
        if (slot !in DAILY_HOURS.indices) return@withContext false
        synchronized(deliveryLock) {
            val prefs = context.getSharedPreferences("academy_notification_schedule", Context.MODE_PRIVATE)
            val today = Calendar.getInstance().let { "${it.get(Calendar.YEAR)}-${it.get(Calendar.DAY_OF_YEAR)}" }
            val deliveryKey = "last_delivered_slot_$slot"
            if (prefs.getString(deliveryKey, null) == today) {
                Log.w(TAG, "Duplicate delivery suppressed for slot $slot on $today")
                return@synchronized false
            }
        try {
            val db = CyberGuardDatabase.getInstance(context)
            val lang = language(context)
            val recentIds = prefs.getStringSet("recent_tip_ids", emptySet()).orEmpty().toMutableSet()
            var title = if (lang == Language.EN) "CyberGuard Security Tip" else "نصيحة أمنية من سايبر جارد"
            var message = if (lang == Language.EN) "Review your account security and keep your passwords unique." else "راجع أمان حساباتك واستخدم كلمات مرور قوية ومختلفة."
            var trackId: String? = null
            // Always read both language columns, but never fall back to the other language.
            // A missing translation uses a complete, language-specific built-in message.
            val sql = "SELECT id, titleAr, messageAr, titleEn, messageEn, trackId FROM academy_notifications_cache ORDER BY RANDOM() LIMIT 100"
            val cursor = db.openHelper.readableDatabase.query(sql)
            var selectedId: String? = null
            cursor.use {
                while (it.moveToNext()) {
                    val candidateId = it.getString(0)
                    val candidateTitle = if (lang == Language.EN) it.getString(3) else it.getString(1)
                    val candidateMessage = if (lang == Language.EN) it.getString(4) else it.getString(2)
                    val hasRequestedLanguage = !candidateTitle.isNullOrBlank() && !candidateMessage.isNullOrBlank()
                    if (candidateId !in recentIds && hasRequestedLanguage) {
                        selectedId = candidateId
                        title = candidateTitle!!
                        message = candidateMessage!!
                        trackId = it.getString(5)
                        break
                    }
                }
            }
            val posted = postEducationalNotification(
                context,
                id = NOTIFICATION_ID_BASE + slot,
                title = title,
                message = message,
                trackId = trackId
            )
            if (posted) {
                selectedId?.let {
                    recentIds.add(it)
                    if (recentIds.size > 12) recentIds.remove(recentIds.first())
                    prefs.edit().putStringSet("recent_tip_ids", recentIds).apply()
                }
                // Persist only after Android accepted the notification, making delivery idempotent.
                val persisted = prefs.edit().putString(deliveryKey, today).commit()
                if (!persisted) {
                    Log.e(TAG, "Notification was posted, but its delivery marker could not be persisted for slot $slot")
                }
            }
            posted
        } catch (e: Exception) {
            Log.e(TAG, "Failed to post tip from cache: ${e.message}")
            false
        }
        }
    }
}

class AcademyNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != AcademyNotificationScheduler.ACTION_POST_DAILY_TIP) return
        val slot = intent.getIntExtra("notification_slot", -1)
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val academyPrefs = context.getSharedPreferences("cyberguard_academy_notif_prefs", Context.MODE_PRIVATE)
                val masterEnabled = academyPrefs.getBoolean("enabled", true)
                val tipsEnabled = academyPrefs.getBoolean("daily_tips", true)
                val quizEnabled = academyPrefs.getBoolean("quiz_reminders", true)
                val recommendationsEnabled = academyPrefs.getBoolean("track_recommendations", true)
                if (masterEnabled) {
                    when (slot) {
                        AcademyNotificationScheduler.SLOT_DAILY_TIP_MORNING,
                        AcademyNotificationScheduler.SLOT_DAILY_TIP_AFTERNOON,
                        AcademyNotificationScheduler.SLOT_DAILY_TIP_EVENING ->
                            if (tipsEnabled) AcademyNotificationScheduler.postRandomTipFromCache(context, slot)
                        AcademyNotificationScheduler.SLOT_QUIZ_REMINDER ->
                            if (quizEnabled) AcademyNotificationScheduler.postIncompleteQuizReminder(context)
                        AcademyNotificationScheduler.SLOT_TRACK_RECOMMENDATION ->
                            if (recommendationsEnabled) AcademyNotificationScheduler.postTrackRecommendation(context)
                    }
                }
            } catch (e: Exception) {
                Log.e("AcademyNotification", "Scheduled notification delivery failed: ${e.message}")
            } finally {
                AcademyNotificationScheduler.rescheduleSlotAfterDelivery(context, slot)
                pendingResult.finish()
            }
        }
    }
}
