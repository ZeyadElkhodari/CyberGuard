package com.example.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_scan_history")
data class ScanHistoryEntity(
    @PrimaryKey val id: String,
    val target: String,
    val type: String,
    val score: Int,
    val riskLevel: String,
    val overallVerdict: String,
    val domain: String,
    val findingsJson: String,
    val recommendation: String,
    val providersJson: String,
    val analyzedAt: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPartialAssessment: Boolean = false
)

@Entity(tableName = "local_user_stats")
data class UserStatsEntity(
    @PrimaryKey val userId: String = "local_user",
    val score: Int = 0,
    val threatsBlocked: Int = 0,
    val scannedContentCount: Int = 0,
    val scansDone: Int = 0,
    val level: Int = 1,
    val xp: Int = 0,
    val xpToNextLevel: Int = 100,
    val rankTitle: String = "Security Explorer",
    val language: String = "AR",
    val soundEnabled: Boolean = true,
    val lastSyncTime: Long = System.currentTimeMillis()
)

@Entity(tableName = "local_alerts")
data class LocalAlertEntity(
    @PrimaryKey val id: String,
    val title: String,
    val detail: String,
    val time: String,
    val severity: String,
    val isRead: Boolean,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "local_activities")
data class LocalActivityEntity(
    @PrimaryKey val id: String,
    val type: String, // "password", "link", "qr", "lesson", "wifi", "system"
    val title: String,
    val detail: String,
    val time: String,
    val status: String, // "safe", "warning", "info", "critical"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "local_course_progress")
data class CourseProgressEntity(
    @PrimaryKey val courseId: String,
    val progress: Float,
    val completedLessonsJson: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "local_wifi_checklist")
data class WifiChecklistEntity(
    @PrimaryKey val itemId: Int,
    val checked: Boolean,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "local_user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "primary_profile",
    val name: String = "",
    val preferredAddress: String = "UNSPECIFIED", // MALE, FEMALE, UNSPECIFIED
    val profilePictureUri: String? = null,
    val avatarId: String? = null,
    val isCompleted: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
