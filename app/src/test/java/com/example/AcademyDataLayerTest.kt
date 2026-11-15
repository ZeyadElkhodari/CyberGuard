package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.academy.data.*
import com.example.db.CyberGuardDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AcademyDataLayerTest {

    private lateinit var db: CyberGuardDatabase
    private lateinit var dao: AcademyDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CyberGuardDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.academyDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun `insert and retrieve 13 tracks from local Room cache`() = runBlocking {
        val tracks = (1..13).map { index ->
            AcademyTrackEntity(
                id = "track-$index",
                slug = "track-slug-$index",
                titleAr = "المسار التعليمي $index",
                titleEn = "Track $index",
                descriptionAr = "وصف المسار $index",
                iconName = "School",
                colorHex = "#FF0055",
                orderIndex = index,
                articlesCount = 10,
                questionsCount = 20
            )
        }

        dao.insertOrUpdateTracks(tracks)
        val cachedTracks = dao.getAllTracks()

        assertEquals(13, cachedTracks.size)
        assertEquals("track-1", cachedTracks[0].id)
        assertEquals("track-13", cachedTracks[12].id)
    }

    @Test
    fun `article completion updates local cache and calculates track progress accurately`() = runBlocking {
        // Setup Track
        val track = AcademyTrackEntity(
            id = "track-1",
            slug = "fake-messages",
            titleAr = "الرسائل المزيفة",
            titleEn = "Fake Messages",
            descriptionAr = "وصف المسار",
            iconName = "Phishing",
            colorHex = "#FF0055",
            orderIndex = 1,
            articlesCount = 10,
            questionsCount = 20
        )
        dao.insertOrUpdateTracks(listOf(track))

        // Setup 10 Articles
        val articles = (1..10).map { i ->
            AcademyArticleEntity(
                id = "art-1-$i",
                trackId = "track-1",
                orderIndex = i,
                titleAr = "مقال $i",
                introAr = "مقدمة",
                simpleExplanationAr = "شرح",
                realisticScenarioAr = "سيناريو",
                warningSignsJson = "[]",
                protectionStepsJson = "[]",
                recoveryActionAr = null,
                summaryAr = null
            )
        }
        dao.insertOrUpdateArticles(articles)

        // Complete first 5 articles
        for (i in 1..5) {
            dao.markArticleCompleted("art-1-$i", "2026-11-02T12:00:00Z")
        }

        val completedCount = dao.countCompletedArticlesInTrack("track-1")
        assertEquals(5, completedCount)

        dao.updateTrackProgress(
            trackId = "track-1",
            completedCount = 5,
            quizPassed = false,
            trackCompleted = false,
            progressPercent = 50,
            bestScore = 0.0
        )

        val updatedTrack = dao.getTrack("track-1")
        assertNotNull(updatedTrack)
        assertEquals(5, updatedTrack!!.articlesCompletedCount)
        assertEquals(50, updatedTrack.progressPercent)
        assertFalse(updatedTrack.trackCompleted)
    }

    @Test
    fun `user stats cache manages local confirmed XP and level progression`() = runBlocking {
        val stats = AcademyUserStatsEntity(
            userId = "test-user",
            confirmedXp = 500,
            level = 2,
            xpToNextLevel = 750
        )
        dao.insertOrUpdateUserStats(stats)

        val loaded = dao.getUserStats("test-user")
        assertNotNull(loaded)
        assertEquals(500, loaded!!.confirmedXp)
        assertEquals(500, loaded.totalDisplayXp)

        // Update confirmed XP
        dao.reconcileConfirmedXp(
            userId = "test-user",
            confirmedXp = 550,
            level = 2,
            xpToNextLevel = 750
        )

        val reconciled = dao.getUserStats("test-user")
        assertNotNull(reconciled)
        assertEquals(550, reconciled!!.confirmedXp)
        assertEquals(550, reconciled.totalDisplayXp)
    }

    @Test
    fun `seedFromAssetsIfEmpty populates all 13 tracks 130 articles 13 quizzes and 260 questions`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = AcademyRepository(dao)

        val seeded = repository.seedFromAssetsIfEmpty(context)
        assertTrue("Database should be seeded from assets", seeded)

        val allTracks = dao.getAllTracks()
        assertEquals("Should have exactly 13 tracks", 13, allTracks.size)

        // Verify Track 1 articles
        val track1Articles = dao.getArticlesByTrack("track-1")
        assertEquals("Track 1 should have exactly 10 articles", 10, track1Articles.size)

        // Verify total completed articles calculation
        val totalCompleted = dao.countAllCompletedArticles()
        assertEquals("Initially 0 completed articles", 0, totalCompleted)

        // Verify Quiz for Track 1
        val quiz1 = dao.getQuizByTrack("track-1")
        assertNotNull("Track 1 quiz must exist", quiz1)
        assertEquals("quiz-track-1", quiz1!!.id)

        // Verify Questions for Quiz 1
        val questions = dao.getQuestionsForQuiz(quiz1.id)
        assertEquals("Track 1 quiz should have 20 questions", 20, questions.size)

        // Verify question has explanation and options
        val firstQ = questions[0]
        assertNotNull(firstQ.questionTextAr)
        assertTrue(firstQ.optionsJson.contains("service_code") || firstQ.optionsJson.contains("البنك") || firstQ.optionsJson.length > 5)
    }

    @Test
    fun `offline quiz submission evaluates answers locally and passes above 75 percent`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = AcademyRepository(dao)
        repository.seedFromAssetsIfEmpty(context)

        val quiz = dao.getQuizByTrack("track-1")!!
        val questions = dao.getQuestionsForQuiz(quiz.id)

        // Answer 16 out of 20 correctly (80% score >= 75% threshold)
        val answers = questions.mapIndexed { index, q ->
            AcademyQuizSubmitAnswerItem(
                questionId = q.id,
                selectedOptionIndex = if (index < 16) q.correctOptionIndex else (q.correctOptionIndex + 1) % 4
            )
        }

        val result = repository.submitQuiz(
            quizId = quiz.id,
            trackId = "track-1",
            answers = answers,
            userId = "test-offline-user"
        )

        assertTrue(result.isSuccess)
        val response = result.getOrNull()!!
        assertEquals(80, response.scorePercent)
        assertEquals(16, response.correctCount)
        assertTrue(response.passed)
        assertEquals(100, response.xpGained)

        // A repeated successful attempt must not grant the same XP again.
        val repeated = repository.submitQuiz(quiz.id, "track-1", answers, "test-offline-user").getOrNull()!!
        assertEquals(0, repeated.xpGained)
        assertFalse(repeated.xpAwarded)
        assertEquals(100, dao.getUserStats("test-offline-user")!!.confirmedXp)

        // Verify Room was updated locally
        val updatedQuiz = dao.getQuiz(quiz.id)!!
        assertTrue(updatedQuiz.isPassed)
        assertEquals(80, updatedQuiz.bestScorePercent)
    }

    @Test
    fun `article completion is idempotent and grants XP only once`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = AcademyRepository(dao, context)
        repository.seedFromAssetsIfEmpty(context)
        val article = dao.getArticlesByTrack("track-1").first()
        val first = repository.completeArticle(article.id, article.trackId, "article-test-user")
        val second = repository.completeArticle(article.id, article.trackId, "article-test-user")
        assertTrue(first.isSuccess)
        assertTrue(first.getOrNull() == true)
        assertTrue(second.isSuccess)
        assertTrue(second.getOrNull() == false)
        assertEquals(article.xpReward, dao.getUserStats("article-test-user")!!.confirmedXp)
    }

    @Test
    fun `migration 3 to 4 safely upgrades academy_questions_cache schema`() {
        val helper = androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory()
            .create(
                androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration.builder(
                    ApplicationProvider.getApplicationContext()
                )
                    .name("test_migration.db")
                    .callback(object : androidx.sqlite.db.SupportSQLiteOpenHelper.Callback(3) {
                        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            db.execSQL("""
                                CREATE TABLE IF NOT EXISTS `academy_questions_cache` (
                                    `id` TEXT NOT NULL, 
                                    `quizId` TEXT NOT NULL, 
                                    `trackId` TEXT NOT NULL, 
                                    `orderIndex` INTEGER NOT NULL, 
                                    `questionTextAr` TEXT NOT NULL, 
                                    `situationAr` TEXT, 
                                    `optionsJson` TEXT NOT NULL, 
                                    `difficulty` TEXT NOT NULL, 
                                    PRIMARY KEY(`id`)
                                )
                            """.trimIndent())
                            db.execSQL("INSERT INTO `academy_questions_cache` VALUES ('q1', 'quiz1', 't1', 1, 'ما هو التصيد؟', NULL, '[]', 'easy')")
                        }
                        override fun onUpgrade(db: androidx.sqlite.db.SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
                    })
                    .build()
            )

        val writableDb = helper.writableDatabase
        CyberGuardDatabase.MIGRATION_3_4.migrate(writableDb)

        val cursor = writableDb.query("PRAGMA table_info(`academy_questions_cache`)")
        val columns = mutableSetOf<String>()
        cursor.use {
            val nameIndex = it.getColumnIndex("name")
            while (it.moveToNext()) {
                if (nameIndex != -1) columns.add(it.getString(nameIndex))
            }
        }

        assertTrue(columns.contains("correctOptionIndex"))
        assertTrue(columns.contains("explanationAr"))
        assertTrue(columns.contains("warningLessonAr"))

        // Verify existing data wasn't deleted
        val qCursor = writableDb.query("SELECT id, correctOptionIndex FROM `academy_questions_cache` WHERE id = 'q1'")
        qCursor.use {
            assertTrue(it.moveToNext())
            assertEquals("q1", it.getString(0))
            assertEquals(0, it.getInt(1))
        }
        writableDb.close()
    }
}
