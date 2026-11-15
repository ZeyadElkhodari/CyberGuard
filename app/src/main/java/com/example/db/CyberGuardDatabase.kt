package com.example.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.academy.data.*

@Database(
    entities = [
        ScanHistoryEntity::class,
        UserStatsEntity::class,
        LocalAlertEntity::class,
        LocalActivityEntity::class,
        CourseProgressEntity::class,
        WifiChecklistEntity::class,
        UserProfileEntity::class,
        AcademyTrackEntity::class,
        AcademyArticleEntity::class,
        AcademyQuizEntity::class,
        AcademyQuestionEntity::class,
        AcademyNotificationEntity::class,
        AcademyAchievementEntity::class,
        AcademyUserStatsEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class CyberGuardDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun alertDao(): LocalAlertDao
    abstract fun activityDao(): LocalActivityDao
    abstract fun courseProgressDao(): CourseProgressDao
    abstract fun wifiChecklistDao(): WifiChecklistDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun academyDao(): AcademyDao

    companion object {
        @Volatile
        private var INSTANCE: CyberGuardDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `local_user_profile` (
                        `id` TEXT NOT NULL, 
                        `name` TEXT NOT NULL, 
                        `preferredAddress` TEXT NOT NULL, 
                        `profilePictureUri` TEXT, 
                        `avatarId` TEXT, 
                        `isCompleted` INTEGER NOT NULL, 
                        `updatedAt` INTEGER NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `academy_tracks_cache` (
                        `id` TEXT NOT NULL, 
                        `slug` TEXT NOT NULL, 
                        `titleAr` TEXT NOT NULL, 
                        `titleEn` TEXT NOT NULL, 
                        `descriptionAr` TEXT NOT NULL, 
                        `iconName` TEXT NOT NULL, 
                        `colorHex` TEXT NOT NULL, 
                        `orderIndex` INTEGER NOT NULL, 
                        `articlesCount` INTEGER NOT NULL, 
                        `questionsCount` INTEGER NOT NULL, 
                        `articlesCompletedCount` INTEGER NOT NULL, 
                        `quizPassed` INTEGER NOT NULL, 
                        `trackCompleted` INTEGER NOT NULL, 
                        `progressPercent` INTEGER NOT NULL, 
                        `bestQuizScore` REAL NOT NULL, 
                        `lastSyncedAt` INTEGER NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `academy_articles_cache` (
                        `id` TEXT NOT NULL, 
                        `trackId` TEXT NOT NULL, 
                        `orderIndex` INTEGER NOT NULL, 
                        `titleAr` TEXT NOT NULL, 
                        `introAr` TEXT NOT NULL, 
                        `simpleExplanationAr` TEXT NOT NULL, 
                        `realisticScenarioAr` TEXT NOT NULL, 
                        `warningSignsJson` TEXT NOT NULL, 
                        `protectionStepsJson` TEXT NOT NULL, 
                        `recoveryActionAr` TEXT, 
                        `summaryAr` TEXT, 
                        `xpReward` INTEGER NOT NULL, 
                        `readTimeMinutes` INTEGER NOT NULL, 
                        `isCompleted` INTEGER NOT NULL, 
                        `completedAt` TEXT, 
                        `lastSyncedAt` INTEGER NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `academy_quizzes_cache` (
                        `id` TEXT NOT NULL, 
                        `trackId` TEXT NOT NULL, 
                        `titleAr` TEXT NOT NULL, 
                        `descriptionAr` TEXT NOT NULL, 
                        `passingScorePercent` INTEGER NOT NULL, 
                        `xpReward` INTEGER NOT NULL, 
                        `questionsCount` INTEGER NOT NULL, 
                        `isPassed` INTEGER NOT NULL, 
                        `bestScorePercent` INTEGER NOT NULL, 
                        `lastSyncedAt` INTEGER NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())

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
                        `correctOptionIndex` INTEGER NOT NULL DEFAULT 0,
                        `explanationAr` TEXT,
                        `warningLessonAr` TEXT,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `academy_notifications_cache` (
                        `id` TEXT NOT NULL, 
                        `trackId` TEXT NOT NULL, 
                        `titleAr` TEXT NOT NULL, 
                        `messageAr` TEXT NOT NULL, 
                        `importance` TEXT NOT NULL, 
                        `actionType` TEXT NOT NULL, 
                        `actionTarget` TEXT, 
                        `isRead` INTEGER NOT NULL, 
                        `createdAt` TEXT NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `academy_achievements_cache` (
                        `id` TEXT NOT NULL, 
                        `titleAr` TEXT NOT NULL, 
                        `descriptionAr` TEXT NOT NULL, 
                        `iconName` TEXT NOT NULL, 
                        `xpReward` INTEGER NOT NULL, 
                        `criteriaType` TEXT NOT NULL, 
                        `criteriaThreshold` INTEGER NOT NULL, 
                        `isUnlocked` INTEGER NOT NULL, 
                        `unlockedAt` TEXT, 
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `academy_outbox` (
                        `id` TEXT NOT NULL, 
                        `operationType` TEXT NOT NULL, 
                        `payloadJson` TEXT NOT NULL, 
                        `idempotencyKey` TEXT NOT NULL, 
                        `createdAt` INTEGER NOT NULL, 
                        `retryCount` INTEGER NOT NULL, 
                        `status` TEXT NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `academy_user_stats_cache` (
                        `userId` TEXT NOT NULL, 
                        `confirmedXp` INTEGER NOT NULL, 
                        `pendingXp` INTEGER NOT NULL, 
                        `level` INTEGER NOT NULL, 
                        `xpToNextLevel` INTEGER NOT NULL, 
                        `rankTitle` TEXT NOT NULL, 
                        `lastSyncedAt` INTEGER NOT NULL, 
                        PRIMARY KEY(`userId`)
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Ensure table exists
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
                        `correctOptionIndex` INTEGER NOT NULL DEFAULT 0,
                        `explanationAr` TEXT,
                        `warningLessonAr` TEXT,
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())

                val cursor = db.query("PRAGMA table_info(`academy_questions_cache`)")
                val existingColumns = mutableSetOf<String>()
                cursor.use {
                    val nameIndex = it.getColumnIndex("name")
                    while (it.moveToNext()) {
                        if (nameIndex != -1) {
                            existingColumns.add(it.getString(nameIndex))
                        }
                    }
                }
                if (!existingColumns.contains("correctOptionIndex")) {
                    db.execSQL("ALTER TABLE `academy_questions_cache` ADD COLUMN `correctOptionIndex` INTEGER NOT NULL DEFAULT 0")
                }
                if (!existingColumns.contains("explanationAr")) {
                    db.execSQL("ALTER TABLE `academy_questions_cache` ADD COLUMN `explanationAr` TEXT")
                }
                if (!existingColumns.contains("warningLessonAr")) {
                    db.execSQL("ALTER TABLE `academy_questions_cache` ADD COLUMN `warningLessonAr` TEXT")
                }
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `local_activities` (
                        `id` TEXT NOT NULL, 
                        `type` TEXT NOT NULL, 
                        `title` TEXT NOT NULL, 
                        `detail` TEXT NOT NULL, 
                        `time` TEXT NOT NULL, 
                        `status` TEXT NOT NULL, 
                        `timestamp` INTEGER NOT NULL, 
                        PRIMARY KEY(`id`)
                    )
                """.trimIndent())
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS `academy_outbox`")
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `academy_user_stats_cache_new` (
                        `userId` TEXT NOT NULL, 
                        `confirmedXp` INTEGER NOT NULL, 
                        `level` INTEGER NOT NULL, 
                        `xpToNextLevel` INTEGER NOT NULL, 
                        `rankTitle` TEXT NOT NULL, 
                        `lastSyncedAt` INTEGER NOT NULL, 
                        PRIMARY KEY(`userId`)
                    )
                """.trimIndent())
                try {
                    db.execSQL("""
                        INSERT INTO `academy_user_stats_cache_new` (`userId`, `confirmedXp`, `level`, `xpToNextLevel`, `rankTitle`, `lastSyncedAt`)
                        SELECT `userId`, (`confirmedXp` + COALESCE(`pendingXp`, 0)), `level`, `xpToNextLevel`, `rankTitle`, `lastSyncedAt`
                        FROM `academy_user_stats_cache`
                    """.trimIndent())
                    db.execSQL("DROP TABLE `academy_user_stats_cache`")
                } catch (_: Exception) {
                    // Ignore if source table didn't exist
                }
                db.execSQL("ALTER TABLE `academy_user_stats_cache_new` RENAME TO `academy_user_stats_cache`")
            }
        }

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                fun addColumn(table: String, column: String, type: String = "TEXT") {
                    val cursor = db.query("PRAGMA table_info(`$table`)")
                    val exists = cursor.use {
                        val index = it.getColumnIndex("name")
                        var found = false
                        while (it.moveToNext()) if (index >= 0 && it.getString(index) == column) found = true
                        found
                    }
                    if (!exists) db.execSQL("ALTER TABLE `$table` ADD COLUMN `$column` $type")
                }
                addColumn("academy_tracks_cache", "descriptionEn")
                listOf("titleEn", "introEn", "simpleExplanationEn", "realisticScenarioEn", "warningSignsJsonEn", "protectionStepsJsonEn", "recoveryActionEn", "summaryEn").forEach { addColumn("academy_articles_cache", it) }
                listOf("titleEn", "descriptionEn").forEach { addColumn("academy_quizzes_cache", it) }
                listOf("questionTextEn", "situationEn", "optionsJsonEn", "explanationEn", "warningLessonEn").forEach { addColumn("academy_questions_cache", it) }
                listOf("titleEn", "messageEn").forEach { addColumn("academy_notifications_cache", it) }
                listOf("titleEn", "descriptionEn").forEach { addColumn("academy_achievements_cache", it) }
            }
        }

        val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val cursor = db.query("PRAGMA table_info(`local_scan_history`)")
                val hasColumn = cursor.use {
                    val index = it.getColumnIndex("name")
                    var found = false
                    while (it.moveToNext()) if (index >= 0 && it.getString(index) == "isPartialAssessment") found = true
                    found
                }
                if (!hasColumn) {
                    db.execSQL("ALTER TABLE `local_scan_history` ADD COLUMN `isPartialAssessment` INTEGER NOT NULL DEFAULT 0")
                }
            }
        }

        fun getInstance(context: Context): CyberGuardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CyberGuardDatabase::class.java,
                    "cyberguard_local.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8)
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
