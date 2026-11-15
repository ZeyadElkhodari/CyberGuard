package com.example.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {
    @Query("SELECT * FROM local_scan_history ORDER BY timestamp DESC LIMIT 30")
    fun getAllScans(): Flow<List<ScanHistoryEntity>>

    @Query("SELECT * FROM local_scan_history ORDER BY timestamp DESC LIMIT 30")
    suspend fun getAllScansList(): List<ScanHistoryEntity>

    @Query("SELECT COUNT(*) FROM local_scan_history")
    suspend fun countAll(): Int

    @Query("SELECT COUNT(*) FROM local_scan_history WHERE type = 'url'")
    suspend fun countUrlScans(): Int

    @Query("SELECT COUNT(*) FROM local_scan_history WHERE type = 'url' AND score < 60 AND overallVerdict != 'Invalid input - not assessed'")
    suspend fun countUrlThreats(): Int

    @Query("SELECT AVG(score) FROM local_scan_history WHERE type = 'url'")
    suspend fun getAverageUrlScore(): Double?

    @Query("SELECT COUNT(*) FROM local_scan_history WHERE type = 'qr'")
    suspend fun countQrScans(): Int

    @Query("SELECT COUNT(*) FROM local_scan_history WHERE type = 'qr' AND score < 60 AND overallVerdict != 'Invalid input - not assessed'")
    suspend fun countQrThreats(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scans: List<ScanHistoryEntity>)

    @Query("DELETE FROM local_scan_history WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM local_scan_history")
    suspend fun clearAll()
}

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM local_user_stats WHERE userId = :id LIMIT 1")
    suspend fun getStats(id: String = "local_user"): UserStatsEntity?

    @Query("SELECT * FROM local_user_stats WHERE userId = :id LIMIT 1")
    fun observeStats(id: String = "local_user"): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStats(stats: UserStatsEntity)

    @Query("DELETE FROM local_user_stats")
    suspend fun clearStats()
}

@Dao
interface LocalAlertDao {
    @Query("SELECT * FROM local_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<LocalAlertEntity>>

    @Query("SELECT * FROM local_alerts ORDER BY timestamp DESC")
    suspend fun getAlertsList(): List<LocalAlertEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: LocalAlertEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(alerts: List<LocalAlertEntity>)

    @Query("UPDATE local_alerts SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    @Query("UPDATE local_alerts SET isRead = 1")
    suspend fun markAllAsRead()

    @Query("DELETE FROM local_alerts WHERE id = :id")
    suspend fun deleteAlert(id: String)

    @Query("DELETE FROM local_alerts")
    suspend fun clearAlerts()
}

@Dao
interface LocalActivityDao {
    @Query("SELECT * FROM local_activities ORDER BY timestamp DESC LIMIT 30")
    suspend fun getAllActivities(): List<LocalActivityEntity>

    @Query("SELECT * FROM local_activities ORDER BY timestamp DESC LIMIT 30")
    fun observeAllActivities(): Flow<List<LocalActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: LocalActivityEntity)

    @Query("DELETE FROM local_activities WHERE id NOT IN (SELECT id FROM local_activities ORDER BY timestamp DESC LIMIT 30)")
    suspend fun keepLatestThirty()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(activities: List<LocalActivityEntity>)

    @Query("DELETE FROM local_activities WHERE id = :id")
    suspend fun deleteActivity(id: String)

    @Query("DELETE FROM local_activities")
    suspend fun clearActivities()
}

@Dao
interface CourseProgressDao {
    @Query("SELECT * FROM local_course_progress")
    suspend fun getAllProgress(): List<CourseProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: CourseProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(list: List<CourseProgressEntity>)

    @Query("DELETE FROM local_course_progress")
    suspend fun clearAll()
}

@Dao
interface WifiChecklistDao {
    @Query("SELECT * FROM local_wifi_checklist")
    suspend fun getAll(): List<WifiChecklistEntity>

    @Query("SELECT COUNT(*) FROM local_wifi_checklist WHERE checked = 1")
    suspend fun countChecked(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(item: WifiChecklistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(items: List<WifiChecklistEntity>)

    @Query("DELETE FROM local_wifi_checklist")
    suspend fun clearAll()
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM local_user_profile WHERE id = :id LIMIT 1")
    suspend fun getProfile(id: String = "primary_profile"): UserProfileEntity?

    @Query("SELECT * FROM local_user_profile WHERE id = :id LIMIT 1")
    fun observeProfile(id: String = "primary_profile"): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: UserProfileEntity)

    @Query("DELETE FROM local_user_profile WHERE id = :id")
    suspend fun deleteProfile(id: String = "primary_profile")
}
