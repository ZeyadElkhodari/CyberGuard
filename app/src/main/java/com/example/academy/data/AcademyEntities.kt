package com.example.academy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "academy_tracks_cache")
data class AcademyTrackEntity(
    @PrimaryKey val id: String,
    val slug: String,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String? = null,
    val iconName: String,
    val colorHex: String,
    val orderIndex: Int,
    val articlesCount: Int,
    val questionsCount: Int,
    // Cached user progress
    val articlesCompletedCount: Int = 0,
    val quizPassed: Boolean = false,
    val trackCompleted: Boolean = false,
    val progressPercent: Int = 0,
    val bestQuizScore: Double = 0.0,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "academy_articles_cache")
data class AcademyArticleEntity(
    @PrimaryKey val id: String,
    val trackId: String,
    val orderIndex: Int,
    val titleAr: String,
    val introAr: String,
    val simpleExplanationAr: String,
    val realisticScenarioAr: String,
    val warningSignsJson: String, // JSON array string
    val protectionStepsJson: String, // JSON array string
    val recoveryActionAr: String?,
    val summaryAr: String?,
    val titleEn: String? = null,
    val introEn: String? = null,
    val simpleExplanationEn: String? = null,
    val realisticScenarioEn: String? = null,
    val warningSignsJsonEn: String? = null,
    val protectionStepsJsonEn: String? = null,
    val recoveryActionEn: String? = null,
    val summaryEn: String? = null,
    val xpReward: Int = 20,
    val readTimeMinutes: Int = 3,
    val isCompleted: Boolean = false,
    val completedAt: String? = null,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "academy_quizzes_cache")
data class AcademyQuizEntity(
    @PrimaryKey val id: String,
    val trackId: String,
    val titleAr: String,
    val descriptionAr: String,
    val titleEn: String? = null,
    val descriptionEn: String? = null,
    val passingScorePercent: Int = 75,
    val xpReward: Int = 100,
    val questionsCount: Int = 20,
    val isPassed: Boolean = false,
    val bestScorePercent: Int = 0,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "academy_questions_cache")
data class AcademyQuestionEntity(
    @PrimaryKey val id: String,
    val quizId: String,
    val trackId: String,
    val orderIndex: Int,
    val questionTextAr: String,
    val situationAr: String?,
    val optionsJson: String, // JSON array of options
    val difficulty: String = "beginner",
    val correctOptionIndex: Int = 0,
    val explanationAr: String? = null,
    val warningLessonAr: String? = null,
    val questionTextEn: String? = null,
    val situationEn: String? = null,
    val optionsJsonEn: String? = null,
    val explanationEn: String? = null,
    val warningLessonEn: String? = null
)

@Entity(tableName = "academy_notifications_cache")
data class AcademyNotificationEntity(
    @PrimaryKey val id: String,
    val trackId: String,
    val titleAr: String,
    val messageAr: String,
    val titleEn: String? = null,
    val messageEn: String? = null,
    val importance: String = "normal",
    val actionType: String = "open_track",
    val actionTarget: String? = null,
    val isRead: Boolean = false,
    val createdAt: String = ""
)

@Entity(tableName = "academy_achievements_cache")
data class AcademyAchievementEntity(
    @PrimaryKey val id: String,
    val titleAr: String,
    val descriptionAr: String,
    val titleEn: String? = null,
    val descriptionEn: String? = null,
    val iconName: String,
    val xpReward: Int,
    val criteriaType: String,
    val criteriaThreshold: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: String? = null
)

/**
 * Local cache of User Academy Stats in local Room database.
 */
@Entity(tableName = "academy_user_stats_cache")
data class AcademyUserStatsEntity(
    @PrimaryKey val userId: String,
    val confirmedXp: Int = 0,
    val level: Int = 1,
    val xpToNextLevel: Int = 500,
    val rankTitle: String = "Security Cadet",
    val lastSyncedAt: Long = System.currentTimeMillis()
) {
    val totalDisplayXp: Int
        get() = confirmedXp
}
