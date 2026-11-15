package com.example.model

import androidx.compose.ui.graphics.Color

enum class TabType {
    HOME,
    SCAN,
    ACADEMY,
    ALERTS,
    MORE
}

enum class Language {
    EN,
    AR
}

enum class AlertSeverity {
    CRITICAL,
    WARNING,
    INFO,
    SUCCESS
}

data class Developer(
    val name: String
)

data class SecurityActivity(
    val id: String,
    val type: String, // "password", "link", "qr", "wifi", "phishing-message", "lesson", "system"
    val title: String,
    val detail: String,
    val time: String,
    val status: String, // "safe", "warning", "info", "critical"
    val timestamp: Long = System.currentTimeMillis()
)

data class SecurityAlert(
    val id: String,
    val title: String,
    val detail: String,
    val time: String,
    val severity: AlertSeverity,
    val read: Boolean,
    val category: String // "threat", "system", "learning", "auth"
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class Lesson(
    val id: String,
    val title: String,
    val duration: String,
    val completed: Boolean,
    val content: String,
    val quizQuestion: QuizQuestion
)

data class Course(
    val id: String,
    val title: String,
    val category: String,
    val lessonsCount: Int,
    val progress: Float, // 0f to 1f
    val color: Color,
    val iconName: String, // "Shield", "Network", "Terminal", "Globe"
    val description: String,
    val lessons: List<Lesson>
)

data class UserStats(
    val score: Int = 0,
    val threatsBlocked: Int = 0,
    val scannedContentCount: Int = 0,
    val scansDone: Int = 0,
    val level: Int = 1,
    val xp: Int = 0,
    val xpToNextLevel: Int = 100,
    val rankTitle: String = "Security Explorer"
)

data class ScanFinding(
    val title: String,
    val description: String,
    val passed: Boolean
)

data class ThreatProviderInfo(
    val name: String,
    val status: String, // clean, malicious, suspicious, rate_limited, not_indexed, submitted, unavailable, unconfigured, configuration_error
    val verdict: String,
    val details: String,
    val tags: List<String> = emptyList()
)

data class ScanResult(
    val target: String,
    val type: String = "url",
    val score: Int, // 0 - 100
    val riskLevel: String, // "Safe", "Low Risk", "Medium Risk", "Critical Danger"
    val overallVerdict: String = "Safe",
    val domain: String = "",
    val findings: List<ScanFinding> = emptyList(),
    val recommendation: String = "",
    val analyzedAt: String = "",
    val isPartialAssessment: Boolean = false,
    val providers: List<ThreatProviderInfo> = emptyList()
)

data class PasswordCheckItem(
    val label: String,
    val passed: Boolean
)

data class PasswordEvaluation(
    val score: Int,
    val entropy: Int,
    val crackTimeEstimate: String,
    val checks: List<PasswordCheckItem>
)

data class PasswordBreachResult(
    val checked: Boolean,
    val breached: Boolean,
    val count: Int,
    val prefixSent: String,
    val checkedAt: String,
    val error: String? = null
)

data class ScorePillarContribution(
    val id: String,
    val title: String,
    val status: String, // "assessed", "pending"
    val score: Int,
    val maxScore: Int,
    val deductions: List<String>,
    val summary: String
)

data class DynamicSecurityAssessment(
    val compositeScore: Int,
    val status: String, // "not_started", "partial", "complete"
    val assessedPillarsCount: Int,
    val totalPillarsCount: Int,
    val ratingText: String,
    val ratingColor: Color,
    val pillars: List<ScorePillarContribution>,
    val threatsBlockedCount: Int,
    val scansDoneCount: Int,
    val scannedContentCount: Int,
    val earnedPoints: Int,
    val maxAssessedPoints: Int
)

data class SecurityAuditState(
    val passwordStrength: Int? = null,
    val passwordEntropy: Int? = null,
    val passwordBreachStatus: String = "untested", // "clean", "breached", "untested"
    val passwordBreachCount: Int = 0,
    val urlScansCount: Int = 0,
    val urlThreatsCount: Int = 0,
    val urlAverageScore: Int = 0,
    val qrScansCount: Int = 0,
    val qrThreatsCount: Int = 0,
    val wifiPassed: Int = 0,
    val wifiTotal: Int = 5,
    val wifiScore: Int = 0
)

data class WifiCheckItem(
    val id: Int,
    val titleEn: String,
    val titleAr: String,
    val descEn: String,
    val descAr: String,
    val checked: Boolean
)

enum class PreferredAddress {
    MALE,
    FEMALE,
    UNSPECIFIED
}

data class UserProfile(
    val name: String = "",
    val preferredAddress: PreferredAddress = PreferredAddress.UNSPECIFIED,
    val profilePictureUri: String? = null,
    val avatarId: String? = null,
    val isCompleted: Boolean = false
)
