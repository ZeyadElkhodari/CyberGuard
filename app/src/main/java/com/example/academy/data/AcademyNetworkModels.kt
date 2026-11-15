package com.example.academy.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AcademyTracksResponse(
    @Json(name = "tracks") val tracks: List<AcademyTrackDto>
)

@JsonClass(generateAdapter = true)
data class AcademyTrackDto(
    @Json(name = "id") val id: String,
    @Json(name = "slug") val slug: String,
    @Json(name = "title_ar") val titleAr: String,
    @Json(name = "title_en") val titleEn: String?,
    @Json(name = "description_ar") val descriptionAr: String,
    @Json(name = "icon_name") val iconName: String,
    @Json(name = "color_hex") val colorHex: String,
    @Json(name = "order_index") val orderIndex: Int,
    @Json(name = "articles_count") val articlesCount: Int = 10,
    @Json(name = "questions_count") val questionsCount: Int = 20,
    @Json(name = "user_progress") val userProgress: AcademyTrackUserProgressDto? = null
)

@JsonClass(generateAdapter = true)
data class AcademyTrackUserProgressDto(
    @Json(name = "articles_completed_count") val articlesCompletedCount: Int = 0,
    @Json(name = "quiz_passed") val quizPassed: Boolean = false,
    @Json(name = "track_completed") val trackCompleted: Boolean = false,
    @Json(name = "progress_percent") val progressPercent: Int = 0,
    @Json(name = "best_quiz_score") val bestQuizScore: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class AcademyTrackDetailResponse(
    @Json(name = "track") val track: AcademyTrackDto,
    @Json(name = "articles") val articles: List<AcademyArticleSummaryDto>,
    @Json(name = "quiz") val quiz: AcademyQuizDto?,
    @Json(name = "user_progress") val userProgress: AcademyTrackUserProgressDto?
)

@JsonClass(generateAdapter = true)
data class AcademyArticleSummaryDto(
    @Json(name = "id") val id: String,
    @Json(name = "track_id") val trackId: String,
    @Json(name = "order_index") val orderIndex: Int,
    @Json(name = "title_ar") val titleAr: String,
    @Json(name = "intro_ar") val introAr: String,
    @Json(name = "xp_reward") val xpReward: Int = 20,
    @Json(name = "read_time_minutes") val readTimeMinutes: Int = 3,
    @Json(name = "is_completed") val isCompleted: Boolean = false
)

@JsonClass(generateAdapter = true)
data class AcademyArticleDetailDto(
    @Json(name = "id") val id: String,
    @Json(name = "track_id") val trackId: String,
    @Json(name = "order_index") val orderIndex: Int,
    @Json(name = "title_ar") val titleAr: String,
    @Json(name = "intro_ar") val introAr: String,
    @Json(name = "simple_explanation_ar") val simpleExplanationAr: String,
    @Json(name = "realistic_scenario_ar") val realisticScenarioAr: String,
    @Json(name = "warning_signs") val warningSigns: List<String> = emptyList(),
    @Json(name = "protection_steps") val protectionSteps: List<String> = emptyList(),
    @Json(name = "recovery_action_ar") val recoveryActionAr: String?,
    @Json(name = "summary_ar") val summaryAr: String?,
    @Json(name = "xp_reward") val xpReward: Int = 20,
    @Json(name = "read_time_minutes") val readTimeMinutes: Int = 3,
    @Json(name = "is_completed") val isCompleted: Boolean = false,
    @Json(name = "completed_at") val completedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class AcademyArticleCompleteRequest(
    @Json(name = "idempotency_key") val idempotencyKey: String
)

@JsonClass(generateAdapter = true)
data class AcademyArticleCompleteResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "article_id") val articleId: String,
    @Json(name = "track_id") val trackId: String,
    @Json(name = "completed") val completed: Boolean,
    @Json(name = "xp_awarded") val xpAwarded: Boolean,
    @Json(name = "xp_gained") val xpGained: Int,
    @Json(name = "track_bonus_xp") val trackBonusXp: Int = 0,
    @Json(name = "total_xp_gained") val totalXpGained: Int = 0,
    @Json(name = "user_stats") val userStats: AcademyUserStatsDto?,
    @Json(name = "unlocked_achievements") val unlockedAchievements: List<AcademyAchievementDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AcademyQuizDto(
    @Json(name = "id") val id: String,
    @Json(name = "track_id") val trackId: String,
    @Json(name = "title_ar") val titleAr: String,
    @Json(name = "description_ar") val descriptionAr: String,
    @Json(name = "title_en") val titleEn: String? = null,
    @Json(name = "description_en") val descriptionEn: String? = null,
    @Json(name = "passing_score_percent") val passingScorePercent: Int = 75,
    @Json(name = "xp_reward") val xpReward: Int = 100,
    @Json(name = "questions_count") val questionsCount: Int = 20
)

@JsonClass(generateAdapter = true)
data class AcademyQuizResponse(
    @Json(name = "quiz") val quiz: AcademyQuizDto,
    @Json(name = "questions") val questions: List<AcademyQuestionDto>
)

@JsonClass(generateAdapter = true)
data class AcademyQuestionDto(
    @Json(name = "id") val id: String,
    @Json(name = "quiz_id") val quizId: String,
    @Json(name = "track_id") val trackId: String,
    @Json(name = "order_index") val orderIndex: Int,
    @Json(name = "question_text_ar") val questionTextAr: String,
    @Json(name = "situation_ar") val situationAr: String?,
    @Json(name = "options") val options: List<String>,
    @Json(name = "difficulty") val difficulty: String = "beginner",
    @Json(name = "question_text_en") val questionTextEn: String? = null,
    @Json(name = "situation_en") val situationEn: String? = null,
    @Json(name = "options_en") val optionsEn: List<String>? = null,
    @Json(name = "explanation_en") val explanationEn: String? = null,
    @Json(name = "warning_lesson_en") val warningLessonEn: String? = null
)

@JsonClass(generateAdapter = true)
data class AcademyQuizSubmitAnswerItem(
    @Json(name = "question_id") val questionId: String,
    @Json(name = "selected_option_index") val selectedOptionIndex: Int
)

@JsonClass(generateAdapter = true)
data class AcademyQuizSubmitRequest(
    @Json(name = "answers") val answers: List<AcademyQuizSubmitAnswerItem>,
    @Json(name = "idempotency_key") val idempotencyKey: String
)

@JsonClass(generateAdapter = true)
data class AcademyQuizQuestionResultDto(
    @Json(name = "question_id") val questionId: String,
    @Json(name = "order_index") val orderIndex: Int,
    @Json(name = "question_text_ar") val questionTextAr: String,
    @Json(name = "situation_ar") val situationAr: String?,
    @Json(name = "options") val options: List<String>,
    @Json(name = "selected_option_index") val selectedOptionIndex: Int,
    @Json(name = "correct_option_index") val correctOptionIndex: Int,
    @Json(name = "is_correct") val isCorrect: Boolean,
    @Json(name = "explanation_ar") val explanationAr: String,
    @Json(name = "warning_lesson_ar") val warningLessonAr: String?
)

@JsonClass(generateAdapter = true)
data class AcademyQuizSubmitResponse(
    @Json(name = "attempt_id") val attemptId: String,
    @Json(name = "quiz_id") val quizId: String,
    @Json(name = "track_id") val trackId: String,
    @Json(name = "score_percent") val scorePercent: Int,
    @Json(name = "correct_count") val correctCount: Int,
    @Json(name = "total_questions") val totalQuestions: Int,
    @Json(name = "passing_threshold") val passingThreshold: Int = 75,
    @Json(name = "passed") val passed: Boolean,
    @Json(name = "xp_awarded") val xpAwarded: Boolean,
    @Json(name = "xp_gained") val xpGained: Int,
    @Json(name = "track_bonus_xp") val trackBonusXp: Int = 0,
    @Json(name = "total_xp_gained") val totalXpGained: Int = 0,
    @Json(name = "question_results") val questionResults: List<AcademyQuizQuestionResultDto> = emptyList(),
    @Json(name = "user_stats") val userStats: AcademyUserStatsDto?,
    @Json(name = "unlocked_achievements") val unlockedAchievements: List<AcademyAchievementDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AcademyUserStatsDto(
    @Json(name = "xp") val xp: Int = 0,
    @Json(name = "level") val level: Int = 1,
    @Json(name = "xp_to_next_level") val xpToNextLevel: Int = 500,
    @Json(name = "rank_title") val rankTitle: String? = null
)

@JsonClass(generateAdapter = true)
data class AcademyProgressResponse(
    @Json(name = "articles_completed") val articlesCompleted: Int,
    @Json(name = "total_articles") val totalArticles: Int = 130,
    @Json(name = "quizzes_passed") val quizzesPassed: Int,
    @Json(name = "tracks_completed") val tracksCompleted: Int,
    @Json(name = "total_tracks") val totalTracks: Int = 13,
    @Json(name = "overall_completion_percent") val overallCompletionPercent: Int,
    @Json(name = "user_stats") val userStats: AcademyUserStatsDto?,
    @Json(name = "academy_xp_earned") val academyXpEarned: Int
)

@JsonClass(generateAdapter = true)
data class AcademyAchievementsResponse(
    @Json(name = "achievements") val achievements: List<AcademyAchievementDto>
)

@JsonClass(generateAdapter = true)
data class AcademyAchievementDto(
    @Json(name = "id") val id: String,
    @Json(name = "title_ar") val titleAr: String,
    @Json(name = "description_ar") val descriptionAr: String,
    @Json(name = "icon_name") val iconName: String,
    @Json(name = "xp_reward") val xpReward: Int,
    @Json(name = "criteria_type") val criteriaType: String,
    @Json(name = "criteria_threshold") val criteriaThreshold: Int,
    @Json(name = "is_unlocked") val isUnlocked: Boolean = false,
    @Json(name = "unlocked_at") val unlockedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class AcademyNotificationsResponse(
    @Json(name = "notifications") val notifications: List<AcademyNotificationDto>
)

@JsonClass(generateAdapter = true)
data class AcademyNotificationDto(
    @Json(name = "id") val id: String,
    @Json(name = "track_id") val trackId: String,
    @Json(name = "title_ar") val titleAr: String,
    @Json(name = "message_ar") val messageAr: String,
    @Json(name = "importance") val importance: String = "normal",
    @Json(name = "action_type") val actionType: String = "open_track",
    @Json(name = "action_target") val actionTarget: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class AcademyNotificationPreferencesDto(
    @Json(name = "enabled") val enabled: Boolean = true,
    @Json(name = "daily_tips") val dailyTips: Boolean = true,
    @Json(name = "quiz_reminders") val quizReminders: Boolean = true,
    @Json(name = "track_recommendations") val trackRecommendations: Boolean = true
)

@JsonClass(generateAdapter = true)
data class AcademySyncArticleItem(
    @Json(name = "article_id") val articleId: String,
    @Json(name = "idempotency_key") val idempotencyKey: String,
    @Json(name = "completed_at") val completedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class AcademySyncQuizItem(
    @Json(name = "quiz_id") val quizId: String,
    @Json(name = "answers") val answers: List<AcademyQuizSubmitAnswerItem>,
    @Json(name = "idempotency_key") val idempotencyKey: String,
    @Json(name = "completed_at") val completedAt: String? = null
)

@JsonClass(generateAdapter = true)
data class AcademySyncRequest(
    @Json(name = "article_completions") val articleCompletions: List<AcademySyncArticleItem> = emptyList(),
    @Json(name = "quiz_attempts") val quizAttempts: List<AcademySyncQuizItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AcademySyncResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "user_stats") val userStats: AcademyUserStatsDto?
)

@JsonClass(generateAdapter = true)
data class AcademyDailyChallengeResponse(
    @Json(name = "date") val date: String,
    @Json(name = "title_ar") val titleAr: String,
    @Json(name = "description_ar") val descriptionAr: String,
    @Json(name = "track_title_ar") val trackTitleAr: String?,
    @Json(name = "xp_reward") val xpReward: Int = 15,
    @Json(name = "is_completed") val isCompleted: Boolean = false,
    @Json(name = "completed_at") val completedAt: String? = null,
    @Json(name = "question") val question: AcademyQuestionDto
)

@JsonClass(generateAdapter = true)
data class AcademyDailyChallengeSubmitRequest(
    @Json(name = "question_id") val questionId: String,
    @Json(name = "selected_option_index") val selectedOptionIndex: Int
)

@JsonClass(generateAdapter = true)
data class AcademyDailyChallengeSubmitResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "is_correct") val isCorrect: Boolean,
    @Json(name = "xp_awarded") val xpAwarded: Boolean,
    @Json(name = "xp_gained") val xpGained: Int,
    @Json(name = "correct_option_index") val correctOptionIndex: Int,
    @Json(name = "explanation_ar") val explanationAr: String,
    @Json(name = "warning_lesson_ar") val warningLessonAr: String?,
    @Json(name = "user_stats") val userStats: AcademyUserStatsDto?
)

@JsonClass(generateAdapter = true)
data class AcademyHistoryArticleCompletionDto(
    @Json(name = "article_id") val articleId: String,
    @Json(name = "track_id") val trackId: String,
    @Json(name = "completed_at") val completedAt: String,
    @Json(name = "article_title") val articleTitle: String,
    @Json(name = "track_title") val trackTitle: String
)

@JsonClass(generateAdapter = true)
data class AcademyHistoryQuizAttemptDto(
    @Json(name = "id") val id: String,
    @Json(name = "quiz_id") val quizId: String,
    @Json(name = "track_id") val trackId: String,
    @Json(name = "score_percent") val scorePercent: Int,
    @Json(name = "passed") val passed: Int,
    @Json(name = "attempted_at") val attemptedAt: String,
    @Json(name = "track_title") val trackTitle: String
)

@JsonClass(generateAdapter = true)
data class AcademyHistoryXpTransactionDto(
    @Json(name = "id") val id: String,
    @Json(name = "source_type") val sourceType: String,
    @Json(name = "source_id") val sourceId: String,
    @Json(name = "xp_amount") val xpAmount: Int,
    @Json(name = "description") val description: String,
    @Json(name = "created_at") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class AcademyHistoryResponse(
    @Json(name = "article_completions") val articleCompletions: List<AcademyHistoryArticleCompletionDto> = emptyList(),
    @Json(name = "quiz_attempts") val quizAttempts: List<AcademyHistoryQuizAttemptDto> = emptyList(),
    @Json(name = "xp_transactions") val xpTransactions: List<AcademyHistoryXpTransactionDto> = emptyList()
)
