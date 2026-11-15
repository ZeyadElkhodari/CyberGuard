package com.example.repository

import android.content.Context
import com.example.db.*
import com.example.model.*
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserProgressRepository(context: Context) {

    private val db = CyberGuardDatabase.getInstance(context)
    private val statsDao = db.userStatsDao()
    private val courseDao = db.courseProgressDao()
    private val wifiDao = db.wifiChecklistDao()
    private val alertDao = db.alertDao()
    private val activityDao = db.activityDao()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val stringListAdapter = moshi.adapter<List<String>>(
        Types.newParameterizedType(List::class.java, String::class.java)
    )

    /**
     * Loads user statistics strictly from local Room DB.
     * No remote server synchronization or hardcoded demo values.
     */
    suspend fun loadStats(): UserStats = withContext(Dispatchers.IO) {
        val local = statsDao.getStats()
        if (local != null) {
            UserStats(
                score = local.score,
                threatsBlocked = local.threatsBlocked,
                scannedContentCount = local.scannedContentCount,
                scansDone = local.scansDone,
                level = local.level,
                xp = local.xp,
                xpToNextLevel = local.xpToNextLevel,
                rankTitle = local.rankTitle
            )
        } else {
            val initial = UserStats(
                score = 0,
                threatsBlocked = 0,
                scannedContentCount = 0,
                scansDone = 0,
                level = 1,
                xp = 0,
                xpToNextLevel = 100,
                rankTitle = "Security Explorer"
            )
            saveLocalStats(initial)
            initial
        }
    }

    suspend fun saveLocalStats(stats: UserStats) = withContext(Dispatchers.IO) {
        statsDao.saveStats(
            UserStatsEntity(
                userId = "local_user",
                score = stats.score,
                threatsBlocked = stats.threatsBlocked,
                scannedContentCount = stats.scannedContentCount,
                scansDone = stats.scansDone,
                level = stats.level,
                xp = stats.xp,
                xpToNextLevel = stats.xpToNextLevel,
                rankTitle = stats.rankTitle,
                soundEnabled = com.example.sound.SoundManager.isSoundEnabled
            )
        )
    }

    /**
     * Rewards XP, handles leveling, and persists directly in local Room DB.
     */
    suspend fun rewardXp(
        current: UserStats,
        amount: Int,
        reason: String,
        isAr: Boolean,
        onLevelUpAlert: (SecurityAlert) -> Unit
    ): UserStats = withContext(Dispatchers.IO) {
        var newXp = current.xp + amount
        var newLevel = current.level
        var newXpToNext = current.xpToNextLevel

        if (newXp >= newXpToNext) {
            newLevel += 1
            newXp -= newXpToNext
            newXpToNext = (newXpToNext * 1.25f).toInt()

            val levelUpAlert = SecurityAlert(
                id = "lvl-${System.currentTimeMillis()}",
                title = if (isAr) "ترقية المستوى! وصلت للمستوى $newLevel" else "Level Up! You reached Level $newLevel",
                detail = if (isAr) "واصل تطوير مهاراتك والوعي السيبراني. (+${amount} XP)" else "Keep honing your cyber awareness skills. (+${amount} XP)",
                time = "Just now",
                severity = AlertSeverity.SUCCESS,
                read = false,
                category = "learning"
            )
            onLevelUpAlert(levelUpAlert)
            saveLocalAlert(levelUpAlert)

            // Log activity for level up
            logActivity(
                SecurityActivity(
                    id = "act-${System.currentTimeMillis()}",
                    type = "lesson",
                    title = if (isAr) "ترقية للمستوى $newLevel" else "Promoted to Level $newLevel",
                    detail = "+$amount XP ($reason)",
                    time = "Just now",
                    status = "safe"
                )
            )
        }

        val updated = current.copy(
            xp = newXp,
            level = newLevel,
            xpToNextLevel = newXpToNext
        )

        saveLocalStats(updated)
        updated
    }

    /**
     * Completes a course lesson and saves progress in local Room database.
     */
    suspend fun completeLesson(
        courseId: String,
        lessonId: String,
        totalLessons: Int,
        xpGain: Int = 50
    ) = withContext(Dispatchers.IO) {
        val all = courseDao.getAllProgress()
        val existing = all.find { it.courseId == courseId }
        val completedList = if (existing != null) {
            try {
                stringListAdapter.fromJson(existing.completedLessonsJson)?.toMutableList() ?: mutableListOf()
            } catch (_: Exception) {
                mutableListOf()
            }
        } else {
            mutableListOf()
        }

        if (!completedList.contains(lessonId)) {
            completedList.add(lessonId)
        }

        val progress = (completedList.size.toFloat() / totalLessons.toFloat()).coerceIn(0f, 1f)
        courseDao.saveProgress(
            CourseProgressEntity(
                courseId = courseId,
                progress = progress,
                completedLessonsJson = stringListAdapter.toJson(completedList)
            )
        )

        // Log completion in local activities
        logActivity(
            SecurityActivity(
                id = "act-lesson-${System.currentTimeMillis()}",
                type = "lesson",
                title = "Lesson Completed",
                detail = "$courseId - $lessonId (+${xpGain} XP)",
                time = "Just now",
                status = "safe"
            )
        )
    }

    suspend fun loadCourseProgress(): Map<String, Pair<Float, List<String>>> = withContext(Dispatchers.IO) {
        val map = mutableMapOf<String, Pair<Float, List<String>>>()
        val local = courseDao.getAllProgress()
        local.forEach { entity ->
            val lessons = try {
                stringListAdapter.fromJson(entity.completedLessonsJson) ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            }
            map[entity.courseId] = Pair(entity.progress, lessons)
        }
        map
    }

    /**
     * Wi-Fi checklist persistence using local Room database.
     */
    suspend fun loadWifiChecklist(): Map<Int, Boolean> = withContext(Dispatchers.IO) {
        val map = mutableMapOf<Int, Boolean>()
        val local = wifiDao.getAll()
        local.forEach { map[it.itemId] = it.checked }
        map
    }

    suspend fun countCheckedWifi(): Int = withContext(Dispatchers.IO) {
        wifiDao.countChecked()
    }

    suspend fun updateWifiChecklist(itemId: Int, checked: Boolean) = withContext(Dispatchers.IO) {
        wifiDao.save(WifiChecklistEntity(itemId = itemId, checked = checked))
    }

    /**
     * Real local Alert persistence.
     */
    suspend fun saveLocalAlert(alert: SecurityAlert) = withContext(Dispatchers.IO) {
        alertDao.insertAlert(
            LocalAlertEntity(
                id = alert.id,
                title = alert.title,
                detail = alert.detail,
                time = alert.time,
                severity = alert.severity.name,
                isRead = alert.read,
                category = alert.category
            )
        )
    }

    suspend fun loadAlerts(): List<SecurityAlert> = withContext(Dispatchers.IO) {
        val local = alertDao.getAlertsList()
        local.map { entity ->
            val severity = try {
                AlertSeverity.valueOf(entity.severity)
            } catch (_: Exception) {
                AlertSeverity.INFO
            }
            SecurityAlert(
                id = entity.id,
                title = entity.title,
                detail = entity.detail,
                time = entity.time,
                severity = severity,
                read = entity.isRead,
                category = entity.category
            )
        }
    }

    suspend fun markAlertRead(id: String) = withContext(Dispatchers.IO) {
        alertDao.markAsRead(id)
    }

    suspend fun markAllAlertsRead() = withContext(Dispatchers.IO) {
        alertDao.markAllAsRead()
    }

    suspend fun deleteAlert(id: String) = withContext(Dispatchers.IO) {
        alertDao.deleteAlert(id)
    }

    /**
     * Real local Activity persistence.
     */
    suspend fun logActivity(activity: SecurityActivity) = withContext(Dispatchers.IO) {
        activityDao.insertActivity(
            LocalActivityEntity(
                id = activity.id,
                type = activity.type,
                title = activity.title,
                detail = activity.detail,
                time = activity.time,
                status = activity.status,
                timestamp = activity.timestamp
            )
        )
        activityDao.keepLatestThirty()
    }

    suspend fun trimActivityHistory() = withContext(Dispatchers.IO) {
        activityDao.keepLatestThirty()
    }

    /**
     * Observes the latest 30 security activities. Older entries are pruned on every insert.
     */
    fun observeActivities(): kotlinx.coroutines.flow.Flow<List<SecurityActivity>> {
        return activityDao.observeAllActivities().map { records ->
            records.map(::toSecurityActivity)
        }
    }

    private fun toSecurityActivity(entity: LocalActivityEntity): SecurityActivity =
        SecurityActivity(
            id = entity.id,
            type = entity.type,
            title = entity.title,
            detail = entity.detail,
            time = entity.time,
            status = entity.status,
            timestamp = entity.timestamp
        )

    suspend fun resetAllData() = withContext(Dispatchers.IO) {
        statsDao.clearStats()
        courseDao.clearAll()
        alertDao.clearAlerts()
        activityDao.clearActivities()
        wifiDao.clearAll()
    }
}
