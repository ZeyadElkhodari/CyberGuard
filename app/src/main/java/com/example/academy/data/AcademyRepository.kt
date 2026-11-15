package com.example.academy.data

import android.content.Context
import android.util.Log
import com.example.db.CyberGuardDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class AcademyRepository(
    private val academyDao: AcademyDao,
    var appContext: Context? = null
) {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    companion object {
        private const val TAG = "AcademyRepository"

        @Volatile
        private var INSTANCE: AcademyRepository? = null

        fun getInstance(context: Context): AcademyRepository {
            return INSTANCE ?: synchronized(this) {
                val db = CyberGuardDatabase.getInstance(context)
                val instance = AcademyRepository(db.academyDao(), context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    // =========================================================================
    // 0. OFFLINE PRELOADER & SEEDING (All 13 Tracks, 130 Articles, 13 Quizzes, 260 Questions)
    // =========================================================================

    suspend fun seedFromAssetsIfEmpty(context: Context): Boolean = withContext(Dispatchers.IO + kotlinx.coroutines.NonCancellable) {
        val existingTracks = academyDao.getAllTracks()
        try {
            Log.i(TAG, "Seeding Academy content from assets/academy_seed.json...")
            val jsonStr = context.assets.open("academy_seed.json").bufferedReader().use { it.readText() }
            val root = JSONObject(jsonStr)

            // Existing progress map to prevent overwriting user progress on reseeding
            val existingProgressMap = existingTracks.associateBy { it.id }

            // 1. Tracks (13 Tracks)
            val tracksArr = root.getJSONArray("tracks")
            val trackEntities = mutableListOf<AcademyTrackEntity>()
            for (i in 0 until tracksArr.length()) {
                val obj = tracksArr.getJSONObject(i)
                val id = obj.getString("id")
                val prev = existingProgressMap[id]
                trackEntities.add(
                    AcademyTrackEntity(
                        id = id,
                        slug = obj.getString("slug"),
                        titleAr = obj.getString("title_ar"),
                        titleEn = obj.optString("title_en", ""),
                        descriptionAr = obj.getString("description_ar"),
                        descriptionEn = obj.optString("description_en").takeIf { it.isNotBlank() },
                        iconName = obj.getString("icon_name"),
                        colorHex = obj.getString("color_hex"),
                        orderIndex = obj.getInt("order_index"),
                        articlesCount = obj.optInt("articles_count", 10),
                        questionsCount = obj.optInt("questions_count", 20),
                        articlesCompletedCount = prev?.articlesCompletedCount ?: 0,
                        quizPassed = prev?.quizPassed ?: false,
                        trackCompleted = prev?.trackCompleted ?: false,
                        progressPercent = prev?.progressPercent ?: 0,
                        bestQuizScore = prev?.bestQuizScore ?: 0.0,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                )
            }
            academyDao.insertOrUpdateTracks(trackEntities)

            // 2. Articles (130 Articles)
            val articlesArr = root.getJSONArray("articles")
            val articleEntities = mutableListOf<AcademyArticleEntity>()
            for (i in 0 until articlesArr.length()) {
                val obj = articlesArr.getJSONObject(i)
                val id = obj.getString("id")
                val prevArticle = academyDao.getArticle(id)
                articleEntities.add(
                    AcademyArticleEntity(
                        id = id,
                        trackId = obj.getString("track_id"),
                        orderIndex = obj.getInt("order_index"),
                        titleAr = obj.getString("title_ar"),
                        introAr = obj.optString("intro_ar", ""),
                        simpleExplanationAr = obj.optString("simple_explanation_ar", ""),
                        realisticScenarioAr = obj.optString("realistic_scenario_ar", ""),
                        warningSignsJson = obj.optString("warning_signs_json", "[]"),
                        protectionStepsJson = obj.optString("protection_steps_json", "[]"),
                        recoveryActionAr = if (obj.isNull("recovery_action_ar")) null else obj.getString("recovery_action_ar"),
                        summaryAr = if (obj.isNull("summary_ar")) null else obj.getString("summary_ar"),
                        titleEn = obj.optString("title_en").takeIf { it.isNotBlank() },
                        introEn = obj.optString("intro_en").takeIf { it.isNotBlank() },
                        simpleExplanationEn = obj.optString("simple_explanation_en").takeIf { it.isNotBlank() },
                        realisticScenarioEn = obj.optString("realistic_scenario_en").takeIf { it.isNotBlank() },
                        warningSignsJsonEn = obj.optString("warning_signs_json_en").takeIf { it.isNotBlank() },
                        protectionStepsJsonEn = obj.optString("protection_steps_json_en").takeIf { it.isNotBlank() },
                        recoveryActionEn = obj.optString("recovery_action_en").takeIf { it.isNotBlank() },
                        summaryEn = obj.optString("summary_en").takeIf { it.isNotBlank() },
                        xpReward = obj.optInt("xp_reward", 20),
                        readTimeMinutes = obj.optInt("read_time_minutes", 3),
                        isCompleted = prevArticle?.isCompleted ?: false,
                        completedAt = prevArticle?.completedAt,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                )
            }
            academyDao.insertOrUpdateArticles(articleEntities)

            // 3. Quizzes (13 Quizzes)
            val quizzesArr = root.getJSONArray("quizzes")
            val quizEntities = mutableListOf<AcademyQuizEntity>()
            for (i in 0 until quizzesArr.length()) {
                val obj = quizzesArr.getJSONObject(i)
                val id = obj.getString("id")
                val prevQuiz = academyDao.getQuiz(id)
                quizEntities.add(
                    AcademyQuizEntity(
                        id = id,
                        trackId = obj.getString("track_id"),
                        titleAr = obj.getString("title_ar"),
                        descriptionAr = obj.getString("description_ar"),
                        titleEn = obj.optString("title_en").takeIf { it.isNotBlank() },
                        descriptionEn = obj.optString("description_en").takeIf { it.isNotBlank() },
                        passingScorePercent = obj.optInt("passing_score_percent", 75),
                        xpReward = obj.optInt("xp_reward", 10),
                        questionsCount = 20,
                        isPassed = prevQuiz?.isPassed ?: false,
                        bestScorePercent = prevQuiz?.bestScorePercent ?: 0,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                )
            }
            for (q in quizEntities) {
                academyDao.insertOrUpdateQuiz(q)
            }

            // 4. Questions (260 Questions with Answers & Explanations)
            val questionsArr = root.getJSONArray("questions")
            val questionEntities = mutableListOf<AcademyQuestionEntity>()
            for (i in 0 until questionsArr.length()) {
                val obj = questionsArr.getJSONObject(i)
                questionEntities.add(
                    AcademyQuestionEntity(
                        id = obj.getString("id"),
                        quizId = obj.getString("quiz_id"),
                        trackId = obj.getString("track_id"),
                        orderIndex = obj.getInt("order_index"),
                        questionTextAr = obj.getString("question_text_ar"),
                        situationAr = if (obj.isNull("situation_ar")) null else obj.getString("situation_ar"),
                        optionsJson = obj.getString("options_json"),
                        difficulty = obj.optString("difficulty", "beginner"),
                        correctOptionIndex = obj.optInt("correct_option_index", 0),
                        explanationAr = if (obj.isNull("explanation_ar")) null else obj.getString("explanation_ar"),
                        warningLessonAr = if (obj.isNull("warning_lesson_ar")) null else obj.getString("warning_lesson_ar"),
                        questionTextEn = obj.optString("question_text_en").takeIf { it.isNotBlank() },
                        situationEn = obj.optString("situation_en").takeIf { it.isNotBlank() },
                        optionsJsonEn = obj.optString("options_json_en").takeIf { it.isNotBlank() },
                        explanationEn = obj.optString("explanation_en").takeIf { it.isNotBlank() },
                        warningLessonEn = obj.optString("warning_lesson_en").takeIf { it.isNotBlank() }
                    )
                )
            }
            academyDao.insertOrUpdateQuestions(questionEntities)

            // 5. Notifications (195 Notifications)
            val notifArr = root.getJSONArray("notifications")
            val existingNotifications = academyDao.getAllNotificationsForSeed().associateBy { it.id }
            val notifEntities = mutableListOf<AcademyNotificationEntity>()
            for (i in 0 until notifArr.length()) {
                val obj = notifArr.getJSONObject(i)
                notifEntities.add(
                    AcademyNotificationEntity(
                        id = obj.getString("id"),
                        trackId = obj.getString("track_id"),
                        titleAr = obj.getString("title_ar"),
                        messageAr = obj.getString("message_ar"),
                        titleEn = obj.optString("title_en").takeIf { it.isNotBlank() },
                        messageEn = obj.optString("message_en").takeIf { it.isNotBlank() },
                        importance = obj.optString("importance", "normal"),
                        actionType = obj.optString("action_type", "open_track"),
                        actionTarget = if (obj.isNull("action_target")) null else obj.getString("action_target"),
                        isRead = existingNotifications[obj.getString("id")]?.isRead ?: false,
                        createdAt = obj.optString("created_at", "")
                    )
                )
            }
            academyDao.insertOrUpdateNotifications(notifEntities)

            // 6. Achievements (10 Achievements)
            val achArr = root.getJSONArray("achievements")
            val existingAchievements = academyDao.getAllAchievements().associateBy { it.id }
            val achEntities = mutableListOf<AcademyAchievementEntity>()
            for (i in 0 until achArr.length()) {
                val obj = achArr.getJSONObject(i)
                achEntities.add(
                    AcademyAchievementEntity(
                        id = obj.getString("id"),
                        titleAr = obj.getString("title_ar"),
                        descriptionAr = obj.getString("description_ar"),
                        titleEn = obj.optString("title_en").takeIf { it.isNotBlank() },
                        descriptionEn = obj.optString("description_en").takeIf { it.isNotBlank() },
                        iconName = obj.getString("icon_name"),
                        xpReward = obj.optInt("xp_reward", 50),
                        criteriaType = obj.getString("criteria_type"),
                        criteriaThreshold = obj.getInt("criteria_threshold"),
                        isUnlocked = existingAchievements[obj.getString("id")]?.isUnlocked ?: false,
                        unlockedAt = existingAchievements[obj.getString("id")]?.unlockedAt
                    )
                )
            }
            academyDao.insertOrUpdateAchievements(achEntities)

            // 7. Local User Stats
            if (academyDao.getUserStats("current_user") == null) {
                academyDao.insertOrUpdateUserStats(
                    AcademyUserStatsEntity(
                        userId = "current_user",
                        confirmedXp = 0,
                        level = 1,
                        xpToNextLevel = 100,
                        rankTitle = "مبتدئ أمني",
                        lastSyncedAt = System.currentTimeMillis()
                    )
                )
            }

            Log.i(TAG, "Academy offline content successfully seeded.")
            true
        } catch (e: Exception) {
            if (e is kotlinx.coroutines.CancellationException) throw e
            Log.e(TAG, "Failed to seed Academy content from assets", e)
            false
        }
    }

    // =========================================================================
    // 1. TRACKS
    // =========================================================================

    fun getTracksFlow(): Flow<List<AcademyTrackEntity>> = academyDao.getAllTracksFlow()

    fun getTrackFlow(trackId: String): Flow<AcademyTrackEntity?> = academyDao.getTrackFlow(trackId)

    suspend fun refreshTracks(): Result<List<AcademyTrackEntity>> = withContext(Dispatchers.IO) {
        Result.success(academyDao.getAllTracks())
    }

    suspend fun refreshTrackDetail(trackId: String): Result<Unit> = withContext(Dispatchers.IO) {
        Result.success(Unit)
    }

    // =========================================================================
    // 2. ARTICLES
    // =========================================================================

    fun getArticlesByTrackFlow(trackId: String): Flow<List<AcademyArticleEntity>> =
        academyDao.getArticlesByTrackFlow(trackId)

    fun getArticleFlow(articleId: String): Flow<AcademyArticleEntity?> =
        academyDao.getArticleFlow(articleId)

    suspend fun refreshArticleDetail(articleId: String): Result<AcademyArticleEntity> = withContext(Dispatchers.IO) {
        val article = academyDao.getArticle(articleId)
        if (article != null) {
            Result.success(article)
        } else {
            Result.failure(Exception("Article not found: $articleId"))
        }
    }

    suspend fun completeArticle(
        articleId: String,
        trackId: String,
        userId: String = "current_user"
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        val completedAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())

        val article = academyDao.getArticle(articleId)
            ?: return@withContext Result.failure(Exception("Article not found: $articleId"))
        val firstCompletion = !article.isCompleted
        if (firstCompletion) {
            academyDao.markArticleCompleted(articleId, completedAt)
            appendArticleHistory(article, completedAt)
        }

        val completedCount = academyDao.countCompletedArticlesInTrack(trackId)
        val track = academyDao.getTrack(trackId)
        val totalArticles = (track?.articlesCount ?: 10).coerceAtLeast(1)
        val quizPassed = track?.quizPassed == true
        val wasTrackCompleted = track?.trackCompleted == true
        val trackCompleted = completedCount >= totalArticles && quizPassed
        val progress = (((completedCount + if (quizPassed) 1 else 0).toDouble() / (totalArticles + 1)) * 100).toInt().coerceIn(0, 100)
        academyDao.updateTrackProgress(trackId, completedCount, quizPassed, trackCompleted, progress, track?.bestQuizScore ?: 0.0)

        if (firstCompletion) {
            awardXpLocally(userId, article.xpReward.coerceAtLeast(0))
            appendXpHistory("article", articleId, article.xpReward, "Article completed", completedAt)
            if (trackCompleted && !wasTrackCompleted) {
                awardXpLocally(userId, 100)
                appendXpHistory("track", trackId, 100, "Track completed", completedAt)
            }
            checkAndUnlockAchievements(userId)
        }
        Result.success(firstCompletion)
    }

    // =========================================================================
    // 3. QUIZZES & QUESTIONS
    // =========================================================================

    fun getQuizByTrackFlow(trackId: String): Flow<AcademyQuizEntity?> =
        academyDao.getQuizByTrackFlow(trackId)

    suspend fun refreshQuiz(trackId: String): Result<AcademyQuizResponse> = withContext(Dispatchers.IO) {
        loadQuizFromLocal(trackId)
    }

    suspend fun loadQuizFromLocal(trackId: String): Result<AcademyQuizResponse> = withContext(Dispatchers.IO) {
        val quizEntity = academyDao.getQuizByTrack(trackId)
            ?: return@withContext Result.failure(Exception("Quiz not found locally for track $trackId"))
        val questionEntities = academyDao.getQuestionsForQuiz(quizEntity.id)
        if (questionEntities.isEmpty()) {
            return@withContext Result.failure(Exception("No questions found locally for quiz ${quizEntity.id}"))
        }

        val questionDtos = questionEntities.map { q ->
            var optionsList: List<String> = emptyList()
            try {
                val jsonArr = JSONArray(q.optionsJson)
                optionsList = (0 until jsonArr.length()).map { jsonArr.getString(it) }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse optionsJson for question ${q.id}", e)
            }
            AcademyQuestionDto(
                id = q.id,
                quizId = q.quizId,
                trackId = q.trackId,
                orderIndex = q.orderIndex,
                questionTextAr = q.questionTextAr,
                situationAr = q.situationAr,
                options = optionsList,
                difficulty = q.difficulty,
                questionTextEn = q.questionTextEn,
                situationEn = q.situationEn,
                optionsEn = q.optionsJsonEn?.let { json -> runCatching { JSONArray(json).let { arr -> (0 until arr.length()).map { arr.getString(it) } } }.getOrNull() },
                explanationEn = q.explanationEn,
                warningLessonEn = q.warningLessonEn
            )
        }

        val quizDto = AcademyQuizDto(
            id = quizEntity.id,
            trackId = quizEntity.trackId,
            titleAr = quizEntity.titleAr,
            descriptionAr = quizEntity.descriptionAr,
            titleEn = quizEntity.titleEn,
            descriptionEn = quizEntity.descriptionEn,
            passingScorePercent = quizEntity.passingScorePercent,
            xpReward = quizEntity.xpReward,
            questionsCount = questionDtos.size
        )

        Result.success(
            AcademyQuizResponse(
                quiz = quizDto,
                questions = questionDtos
            )
        )
    }

    suspend fun getQuestionsForQuiz(quizId: String): List<AcademyQuestionEntity> = withContext(Dispatchers.IO) {
        academyDao.getQuestionsForQuiz(quizId)
    }

    /**
     * Submit quiz answers locally.
     * Enforces strict 75% passing mark and awards XP and unlocks achievements.
     */
    suspend fun submitQuiz(
        quizId: String,
        trackId: String,
        answers: List<AcademyQuizSubmitAnswerItem>,
        userId: String = "current_user"
    ): Result<AcademyQuizSubmitResponse> = withContext(Dispatchers.IO) {
        evaluateQuizLocally(quizId, trackId, answers, userId)
    }

    private suspend fun evaluateQuizLocally(
        quizId: String,
        trackId: String,
        answers: List<AcademyQuizSubmitAnswerItem>,
        userId: String
    ): Result<AcademyQuizSubmitResponse> {
        val localQuestions = academyDao.getQuestionsForQuiz(quizId)
        val answersMap = answers.associate { it.questionId to it.selectedOptionIndex }
        var correctCount = 0
        val questionResults = localQuestions.map { q ->
            var optionsList: List<String> = emptyList()
            try {
                val jsonArr = JSONArray(q.optionsJson)
                optionsList = (0 until jsonArr.length()).map { jsonArr.getString(it) }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse options for result ${q.id}", e)
            }
            val selectedIdx = answersMap[q.id] ?: -1
            val isCorrect = selectedIdx == q.correctOptionIndex
            if (isCorrect) correctCount++
            AcademyQuizQuestionResultDto(
                questionId = q.id,
                orderIndex = q.orderIndex,
                questionTextAr = q.questionTextAr,
                situationAr = q.situationAr,
                options = optionsList,
                selectedOptionIndex = selectedIdx,
                correctOptionIndex = q.correctOptionIndex,
                isCorrect = isCorrect,
                explanationAr = q.explanationAr ?: "",
                warningLessonAr = q.warningLessonAr
            )
        }

        val totalQuestions = if (localQuestions.isNotEmpty()) localQuestions.size else 1
        val scorePercent = ((correctCount * 100) / totalQuestions)
        val passed = scorePercent >= 75
        val quiz = academyDao.getQuiz(quizId)
        val firstPass = passed && quiz?.isPassed != true
        val xpReward = if (firstPass) (quiz?.xpReward ?: 100).coerceAtLeast(0) else 0
        var trackBonusXp = 0

        if (passed) {
            academyDao.updateQuizPassed(quizId, scorePercent)
            val completedArticles = academyDao.countCompletedArticlesInTrack(trackId)
            val track = academyDao.getTrack(trackId)
            val totalArticles = (track?.articlesCount ?: 10).coerceAtLeast(1)
            val wasTrackCompleted = track?.trackCompleted == true
            val isTrackComplete = completedArticles >= totalArticles
            val progress = (((completedArticles + 1.0) / (totalArticles + 1.0)) * 100).toInt().coerceIn(0, 100)
            academyDao.updateTrackProgress(trackId, completedArticles, true, isTrackComplete, progress, scorePercent.toDouble())
            if (firstPass) {
                val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())
                if (xpReward > 0) {
                    awardXpLocally(userId, xpReward)
                    appendXpHistory("quiz", quizId, xpReward, "Quiz passed", now)
                }
                if (isTrackComplete && !wasTrackCompleted) {
                    trackBonusXp = 100
                    awardXpLocally(userId, trackBonusXp)
                    appendXpHistory("track", trackId, trackBonusXp, "Track completed", now)
                }
                checkAndUnlockAchievements(userId)
            }
        }
        val attemptAt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())
        appendQuizHistory(quizId, trackId, scorePercent, passed, attemptAt)

        val currentStats = academyDao.getUserStats(userId)
        val localUserStats = if (currentStats != null) {
            AcademyUserStatsDto(
                xp = currentStats.confirmedXp,
                level = currentStats.level,
                xpToNextLevel = currentStats.xpToNextLevel,
                rankTitle = currentStats.rankTitle
            )
        } else null

        val submitResponse = AcademyQuizSubmitResponse(
            attemptId = UUID.randomUUID().toString(),
            quizId = quizId,
            trackId = trackId,
            scorePercent = scorePercent,
            correctCount = correctCount,
            totalQuestions = totalQuestions,
            passingThreshold = 75,
            passed = passed,
            xpAwarded = (xpReward + trackBonusXp) > 0,
            xpGained = xpReward,
            trackBonusXp = trackBonusXp,
            totalXpGained = xpReward + trackBonusXp,
            questionResults = questionResults,
            userStats = localUserStats,
            unlockedAchievements = emptyList()
        )
        return Result.success(submitResponse)
    }

    // =========================================================================
    // 4. ACHIEVEMENTS & NOTIFICATIONS
    // =========================================================================

    fun getAchievementsFlow(): Flow<List<AcademyAchievementEntity>> =
        academyDao.getAchievementsFlow()

    suspend fun refreshAchievements(): Result<List<AcademyAchievementEntity>> = withContext(Dispatchers.IO) {
        Result.success(academyDao.getAllAchievements())
    }

    fun getNotificationsFlow(trackId: String? = null): Flow<List<AcademyNotificationEntity>> =
        if (trackId != null) academyDao.getNotificationsForTrackFlow(trackId)
        else academyDao.getAllNotificationsFlow()

    suspend fun refreshNotifications(trackId: String? = null): Result<Unit> = withContext(Dispatchers.IO) {
        Result.success(Unit)
    }


    // =========================================================================
    // 5. USER STATS & LOCAL XP REWARDING
    // =========================================================================

    fun getUserStatsFlow(userId: String = "current_user"): Flow<AcademyUserStatsEntity?> =
        academyDao.getUserStatsFlow(userId)

    suspend fun initOrUpdateUserStats(userId: String = "current_user", confirmedXp: Int, level: Int, xpToNextLevel: Int) = withContext(Dispatchers.IO) {
        val existing = academyDao.getUserStats(userId)
        if (existing == null) {
            academyDao.insertOrUpdateUserStats(
                AcademyUserStatsEntity(
                    userId = userId,
                    confirmedXp = confirmedXp,
                    level = level,
                    xpToNextLevel = xpToNextLevel
                )
            )
        } else {
            academyDao.reconcileConfirmedXp(
                userId = userId,
                confirmedXp = confirmedXp,
                level = level,
                xpToNextLevel = xpToNextLevel
            )
        }
    }

    private suspend fun awardXpLocally(userId: String, xpGain: Int) {
        val current = academyDao.getUserStats(userId)
        val currentXp = (current?.confirmedXp ?: 0) + xpGain
        var level = current?.level ?: 1
        var xpToNext = current?.xpToNextLevel ?: 100

        while (currentXp >= xpToNext) {
            level += 1
            xpToNext = (xpToNext * 1.35f).toInt()
        }

        val rankTitle = when {
            level >= 8 -> "حارس سيبراني أسطوري"
            level >= 5 -> "خبير حماية متقدم"
            level >= 3 -> "مستكشف سيبراني"
            level >= 2 -> "متدرب أمني"
            else -> "مبتدئ أمني"
        }

        academyDao.insertOrUpdateUserStats(
            AcademyUserStatsEntity(
                userId = userId,
                confirmedXp = currentXp,
                level = level,
                xpToNextLevel = xpToNext,
                rankTitle = rankTitle,
                lastSyncedAt = System.currentTimeMillis()
            )
        )
    }

    private suspend fun checkAndUnlockAchievements(userId: String) {
        val achievements = academyDao.getAllAchievements()
        val allCompletedArticles = academyDao.countAllCompletedArticles()
        val tracks = academyDao.getAllTracks()
        val passedQuizzes = tracks.count { it.quizPassed }
        val completedTracks = tracks.count { it.trackCompleted }
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())

        for (ach in achievements) {
            if (ach.isUnlocked) continue
            var shouldUnlock = false
            when (ach.criteriaType) {
                "articles_count" -> {
                    if (allCompletedArticles >= ach.criteriaThreshold) shouldUnlock = true
                }
                "quizzes_passed" -> {
                    if (passedQuizzes >= ach.criteriaThreshold) shouldUnlock = true
                }
                "tracks_completed" -> {
                    if (completedTracks >= ach.criteriaThreshold) shouldUnlock = true
                }
                "specific_track" -> {
                    val trackId = "track-${ach.criteriaThreshold}"
                    val t = tracks.find { it.id == trackId }
                    if (t != null && t.trackCompleted) shouldUnlock = true
                }
            }

            if (shouldUnlock) {
                academyDao.unlockAchievement(ach.id, now)
                awardXpLocally(userId, ach.xpReward)
                appendXpHistory("achievement", ach.id, ach.xpReward, "Achievement unlocked: ${ach.titleAr}", now)
            }
        }
    }

    // =========================================================================
    // 6. DAILY CHALLENGE & LEARNING PROFILE HISTORY
    // =========================================================================

    suspend fun getDailyChallenge(): Result<AcademyDailyChallengeResponse> = withContext(Dispatchers.IO) {
        try {
            var questions = academyDao.getAllQuestions()
            if (questions.isEmpty()) {
                appContext?.let { seedFromAssetsIfEmpty(it) }
                questions = academyDao.getAllQuestions()
            }

            if (questions.isEmpty()) {
                return@withContext Result.failure(Exception("No questions available for daily challenge"))
            }

            val utcCalendar = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())
            val dayIndex = utcCalendar.timeInMillis / 86_400_000L
            val selectedQ = questions[Math.floorMod(dayIndex, questions.size.toLong()).toInt()]

            val prefs = appContext?.getSharedPreferences("cyberguard_daily_challenge_prefs", Context.MODE_PRIVATE)
            val completedDate = prefs?.getString("completed_date", null)
            val isCompleted = (completedDate == today)
            val completedAt = if (isCompleted) prefs?.getString("completed_at", null) else null

            val track = academyDao.getTrack(selectedQ.trackId)
            val trackTitleAr = track?.titleAr ?: "الأمن العام"

            val optionsList = mutableListOf<String>()
            try {
                val jsonArr = JSONArray(selectedQ.optionsJson)
                for (i in 0 until jsonArr.length()) {
                    optionsList.add(jsonArr.getString(i))
                }
            } catch (_: Exception) {
                optionsList.add("نعم")
                optionsList.add("لا")
            }

            val challengeResponse = AcademyDailyChallengeResponse(
                date = today,
                titleAr = "تحدي الأمان اليومي",
                descriptionAr = "سؤال يومي سريع لاختبار ذكائك وحماية نفسك من أحدث طرق الاحتيال.",
                trackTitleAr = trackTitleAr,
                xpReward = 15,
                isCompleted = isCompleted,
                completedAt = completedAt,
                question = AcademyQuestionDto(
                    id = selectedQ.id,
                    quizId = selectedQ.quizId,
                    trackId = selectedQ.trackId,
                    orderIndex = selectedQ.orderIndex,
                    questionTextAr = selectedQ.questionTextAr,
                    situationAr = selectedQ.situationAr,
                    options = optionsList,
                    difficulty = selectedQ.difficulty
                )
            )
            Result.success(challengeResponse)
        } catch (e: Exception) {
            Log.e(TAG, "Error building offline daily challenge", e)
            Result.failure(e)
        }
    }

    suspend fun submitDailyChallenge(
        questionId: String,
        selectedOptionIndex: Int,
        userId: String = "current_user"
    ): Result<AcademyDailyChallengeSubmitResponse> = withContext(Dispatchers.IO) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())
        val prefs = appContext?.getSharedPreferences("cyberguard_daily_challenge_prefs", Context.MODE_PRIVATE)
        val alreadyCompleted = (prefs?.getString("completed_date", null) == today)

        val q = academyDao.getQuestion(questionId)
        val isCorrect = (q != null && selectedOptionIndex == q.correctOptionIndex)
        val willAwardXp = (isCorrect && !alreadyCompleted)
        val xpGained = if (willAwardXp) 15 else 0

        if (willAwardXp) {
            prefs?.edit()
                ?.putString("completed_date", today)
                ?.putString("completed_at", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date()))
                ?.apply()

            awardXpLocally(userId, 15)
            val completedAt = prefs?.getString("completed_at", today) ?: today
            appendXpHistory("daily_challenge", questionId, 15, "Daily security challenge completed", completedAt)
            checkAndUnlockAchievements(userId)
        }

        val stats = academyDao.getUserStats(userId)
        val userStatsDto = stats?.let {
            AcademyUserStatsDto(
                xp = it.totalDisplayXp,
                level = it.level,
                xpToNextLevel = it.xpToNextLevel,
                rankTitle = it.rankTitle
            )
        }

        Result.success(
            AcademyDailyChallengeSubmitResponse(
                success = true,
                isCorrect = isCorrect,
                xpAwarded = willAwardXp,
                xpGained = xpGained,
                correctOptionIndex = q?.correctOptionIndex ?: 0,
                explanationAr = q?.explanationAr ?: "",
                warningLessonAr = q?.warningLessonAr,
                userStats = userStatsDto
            )
        )
    }

    suspend fun getAcademyProgress(): Result<AcademyProgressResponse> = withContext(Dispatchers.IO) {
        val tracks = academyDao.getAllTracks()
        val articlesCompleted = tracks.sumOf { it.articlesCompletedCount }
        val quizzesPassed = tracks.count { it.quizPassed }
        val tracksCompleted = tracks.count { it.trackCompleted }
        val totalLearningUnits = tracks.sumOf { it.articlesCount + 1 }.coerceAtLeast(1)
        val overallPercent = if (tracks.isNotEmpty()) {
            (((articlesCompleted + quizzesPassed).toFloat() / totalLearningUnits) * 100).toInt().coerceIn(0, 100)
        } else 0
        val uStats = academyDao.getUserStats("current_user")
        val progress = AcademyProgressResponse(
            articlesCompleted = articlesCompleted,
            totalArticles = 130,
            quizzesPassed = quizzesPassed,
            tracksCompleted = tracksCompleted,
            totalTracks = 13,
            overallCompletionPercent = overallPercent,
            userStats = AcademyUserStatsDto(
                xp = uStats?.confirmedXp ?: 0,
                level = uStats?.level ?: 1,
                xpToNextLevel = uStats?.xpToNextLevel ?: 100,
                rankTitle = uStats?.rankTitle ?: "مبتدئ أمني"
            ),
            academyXpEarned = uStats?.confirmedXp ?: 0
        )
        Result.success(progress)
    }

    suspend fun getAcademyHistory(): Result<AcademyHistoryResponse> = withContext(Dispatchers.IO) {
        val prefs = appContext?.getSharedPreferences("cyberguard_academy_history", Context.MODE_PRIVATE)
        fun readArray(key: String) = runCatching { JSONArray(prefs?.getString(key, "[]") ?: "[]") }.getOrDefault(JSONArray())
        val articleItems = readArray("articles").let { arr -> (0 until arr.length()).mapNotNull { i -> runCatching { val o = arr.getJSONObject(i); AcademyHistoryArticleCompletionDto(o.getString("articleId"), o.getString("trackId"), o.getString("completedAt"), o.getString("articleTitle"), o.getString("trackTitle")) }.getOrNull() } }
        val quizItems = readArray("quizzes").let { arr -> (0 until arr.length()).mapNotNull { i -> runCatching { val o = arr.getJSONObject(i); AcademyHistoryQuizAttemptDto(o.getString("id"), o.getString("quizId"), o.getString("trackId"), o.getInt("scorePercent"), if (o.getBoolean("passed")) 1 else 0, o.getString("attemptedAt"), o.getString("trackTitle")) }.getOrNull() } }
        val xpItems = readArray("xp").let { arr -> (0 until arr.length()).mapNotNull { i -> runCatching { val o = arr.getJSONObject(i); AcademyHistoryXpTransactionDto(o.getString("id"), o.getString("sourceType"), o.getString("sourceId"), o.getInt("xpAmount"), o.getString("description"), o.getString("createdAt")) }.getOrNull() } }
        Result.success(AcademyHistoryResponse(articleItems.reversed(), quizItems.reversed(), xpItems.reversed()))
    }

    private fun appendHistory(key: String, item: JSONObject) {
        val prefs = appContext?.getSharedPreferences("cyberguard_academy_history", Context.MODE_PRIVATE) ?: return
        val arr = runCatching { JSONArray(prefs.getString(key, "[]") ?: "[]") }.getOrDefault(JSONArray())
        arr.put(item)
        while (arr.length() > 500) arr.remove(0)
        prefs.edit().putString(key, arr.toString()).apply()
    }

    private suspend fun appendArticleHistory(article: AcademyArticleEntity, at: String) {
        val track = academyDao.getTrack(article.trackId)
        appendHistory("articles", JSONObject().put("articleId", article.id).put("trackId", article.trackId).put("completedAt", at).put("articleTitle", article.titleAr).put("trackTitle", track?.titleAr ?: article.trackId))
    }

    private suspend fun appendQuizHistory(quizId: String, trackId: String, score: Int, passed: Boolean, at: String) {
        val track = academyDao.getTrack(trackId)
        appendHistory("quizzes", JSONObject().put("id", UUID.randomUUID().toString()).put("quizId", quizId).put("trackId", trackId).put("scorePercent", score).put("passed", passed).put("attemptedAt", at).put("trackTitle", track?.titleAr ?: trackId))
    }

    private fun appendXpHistory(type: String, sourceId: String, amount: Int, description: String, at: String) {
        if (amount <= 0) return
        appendHistory("xp", JSONObject().put("id", UUID.randomUUID().toString()).put("sourceType", type).put("sourceId", sourceId).put("xpAmount", amount).put("description", description).put("createdAt", at))
    }

    suspend fun getNotificationPreferences(): Result<AcademyNotificationPreferencesDto> = withContext(Dispatchers.IO) {
        val prefs = appContext?.getSharedPreferences("cyberguard_academy_notif_prefs", Context.MODE_PRIVATE)
        val enabled = prefs?.getBoolean("enabled", true) ?: true
        val dailyTips = prefs?.getBoolean("daily_tips", true) ?: true
        val quizReminders = prefs?.getBoolean("quiz_reminders", true) ?: true
        val trackRecs = prefs?.getBoolean("track_recommendations", true) ?: true
        Result.success(
            AcademyNotificationPreferencesDto(
                enabled = enabled,
                dailyTips = dailyTips,
                quizReminders = quizReminders,
                trackRecommendations = trackRecs
            )
        )
    }

    suspend fun updateNotificationPreferences(preferences: AcademyNotificationPreferencesDto): Result<AcademyNotificationPreferencesDto> = withContext(Dispatchers.IO) {
        val prefs = appContext?.getSharedPreferences("cyberguard_academy_notif_prefs", Context.MODE_PRIVATE)
        prefs?.edit()
            ?.putBoolean("enabled", preferences.enabled)
            ?.putBoolean("daily_tips", preferences.dailyTips)
            ?.putBoolean("quiz_reminders", preferences.quizReminders)
            ?.putBoolean("track_recommendations", preferences.trackRecommendations)
            ?.apply()
        Result.success(preferences)
    }
}
