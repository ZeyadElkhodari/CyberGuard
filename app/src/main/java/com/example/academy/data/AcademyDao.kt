package com.example.academy.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AcademyDao {

    // --- Tracks ---
    @Query("SELECT * FROM academy_tracks_cache ORDER BY orderIndex ASC")
    fun getAllTracksFlow(): Flow<List<AcademyTrackEntity>>

    @Query("SELECT * FROM academy_tracks_cache ORDER BY orderIndex ASC")
    suspend fun getAllTracks(): List<AcademyTrackEntity>

    @Query("SELECT * FROM academy_tracks_cache WHERE id = :trackId LIMIT 1")
    fun getTrackFlow(trackId: String): Flow<AcademyTrackEntity?>

    @Query("SELECT * FROM academy_tracks_cache WHERE id = :trackId LIMIT 1")
    suspend fun getTrack(trackId: String): AcademyTrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTracks(tracks: List<AcademyTrackEntity>)

    @Query("""
        UPDATE academy_tracks_cache 
        SET articlesCompletedCount = :completedCount, 
            quizPassed = :quizPassed,
            trackCompleted = :trackCompleted,
            progressPercent = :progressPercent,
            bestQuizScore = MAX(bestQuizScore, :bestScore)
        WHERE id = :trackId
    """)
    suspend fun updateTrackProgress(
        trackId: String,
        completedCount: Int,
        quizPassed: Boolean,
        trackCompleted: Boolean,
        progressPercent: Int,
        bestScore: Double
    )

    // --- Articles ---
    @Query("SELECT * FROM academy_articles_cache WHERE trackId = :trackId ORDER BY orderIndex ASC")
    fun getArticlesByTrackFlow(trackId: String): Flow<List<AcademyArticleEntity>>

    @Query("SELECT * FROM academy_articles_cache WHERE trackId = :trackId ORDER BY orderIndex ASC")
    suspend fun getArticlesByTrack(trackId: String): List<AcademyArticleEntity>

    @Query("SELECT * FROM academy_articles_cache WHERE id = :articleId LIMIT 1")
    fun getArticleFlow(articleId: String): Flow<AcademyArticleEntity?>

    @Query("SELECT * FROM academy_articles_cache WHERE id = :articleId LIMIT 1")
    suspend fun getArticle(articleId: String): AcademyArticleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateArticles(articles: List<AcademyArticleEntity>)

    @Query("UPDATE academy_articles_cache SET isCompleted = 1, completedAt = :completedAt WHERE id = :articleId")
    suspend fun markArticleCompleted(articleId: String, completedAt: String)

    @Query("SELECT COUNT(*) FROM academy_articles_cache WHERE trackId = :trackId AND isCompleted = 1")
    suspend fun countCompletedArticlesInTrack(trackId: String): Int

    @Query("SELECT COUNT(*) FROM academy_articles_cache WHERE isCompleted = 1")
    suspend fun countAllCompletedArticles(): Int

    // --- Quizzes & Questions ---
    @Query("SELECT * FROM academy_quizzes_cache WHERE trackId = :trackId LIMIT 1")
    fun getQuizByTrackFlow(trackId: String): Flow<AcademyQuizEntity?>

    @Query("SELECT * FROM academy_quizzes_cache WHERE trackId = :trackId LIMIT 1")
    suspend fun getQuizByTrack(trackId: String): AcademyQuizEntity?

    @Query("SELECT * FROM academy_quizzes_cache WHERE id = :quizId LIMIT 1")
    suspend fun getQuiz(quizId: String): AcademyQuizEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateQuiz(quiz: AcademyQuizEntity)

    @Query("UPDATE academy_quizzes_cache SET isPassed = 1, bestScorePercent = MAX(bestScorePercent, :score) WHERE id = :quizId")
    suspend fun updateQuizPassed(quizId: String, score: Int)

    @Query("SELECT * FROM academy_questions_cache WHERE quizId = :quizId ORDER BY orderIndex ASC")
    suspend fun getQuestionsForQuiz(quizId: String): List<AcademyQuestionEntity>

    @Query("SELECT * FROM academy_questions_cache ORDER BY id ASC")
    suspend fun getAllQuestions(): List<AcademyQuestionEntity>

    @Query("SELECT * FROM academy_questions_cache WHERE id = :id LIMIT 1")
    suspend fun getQuestion(id: String): AcademyQuestionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateQuestions(questions: List<AcademyQuestionEntity>)

    // --- Notifications ---
    @Query("SELECT * FROM academy_notifications_cache WHERE trackId = :trackId ORDER BY createdAt DESC")
    fun getNotificationsForTrackFlow(trackId: String): Flow<List<AcademyNotificationEntity>>

    @Query("SELECT * FROM academy_notifications_cache ORDER BY createdAt DESC LIMIT 50")
    fun getAllNotificationsFlow(): Flow<List<AcademyNotificationEntity>>

    @Query("SELECT * FROM academy_notifications_cache")
    suspend fun getAllNotificationsForSeed(): List<AcademyNotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateNotifications(notifications: List<AcademyNotificationEntity>)

    @Query("UPDATE academy_notifications_cache SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: String)

    // --- Achievements ---
    @Query("SELECT * FROM academy_achievements_cache ORDER BY xpReward ASC")
    fun getAchievementsFlow(): Flow<List<AcademyAchievementEntity>>

    @Query("SELECT * FROM academy_achievements_cache ORDER BY xpReward ASC")
    suspend fun getAllAchievements(): List<AcademyAchievementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAchievements(achievements: List<AcademyAchievementEntity>)

    @Query("UPDATE academy_achievements_cache SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE id = :achievementId")
    suspend fun unlockAchievement(achievementId: String, unlockedAt: String)

    // --- User Stats & XP ---
    @Query("SELECT * FROM academy_user_stats_cache WHERE userId = :userId LIMIT 1")
    fun getUserStatsFlow(userId: String): Flow<AcademyUserStatsEntity?>

    @Query("SELECT * FROM academy_user_stats_cache WHERE userId = :userId LIMIT 1")
    suspend fun getUserStats(userId: String): AcademyUserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUserStats(stats: AcademyUserStatsEntity)

    @Query("""
        UPDATE academy_user_stats_cache 
        SET confirmedXp = :confirmedXp, 
            level = :level,
            xpToNextLevel = :xpToNextLevel,
            lastSyncedAt = :syncedAt
        WHERE userId = :userId
    """)
    suspend fun reconcileConfirmedXp(
        userId: String,
        confirmedXp: Int,
        level: Int,
        xpToNextLevel: Int,
        syncedAt: Long = System.currentTimeMillis()
    )
}
