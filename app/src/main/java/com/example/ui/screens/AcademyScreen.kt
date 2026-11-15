package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.academy.data.*
import com.example.academy.ui.*
import com.example.localization.Strings
import com.example.model.Language
import com.example.model.UserStats
import com.example.sound.SoundManager
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AcademyScreen(
    stats: UserStats,
    lang: Language,
    modifier: Modifier = Modifier,
    notificationTrackId: String? = null,
    notificationOpenQuiz: Boolean = false,
    onNotificationNavigationConsumed: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { AcademyRepository.getInstance(context) }

    // Live reactive state from Room DB
    val tracks by repository.getTracksFlow().collectAsState(initial = emptyList())
    val userStatsEntity by repository.getUserStatsFlow().collectAsState(initial = null)

    // Progress and daily challenge states
    var progressData by remember { mutableStateOf<AcademyProgressResponse?>(null) }
    var dailyChallengeData by remember { mutableStateOf<AcademyDailyChallengeResponse?>(null) }
    var isRefreshing by remember { mutableStateOf(false) }

    // Modal navigation states
    var selectedTrackId by remember { mutableStateOf<String?>(null) }
    var selectedArticleId by remember { mutableStateOf<String?>(null) }
    var selectedQuizTrackId by remember { mutableStateOf<String?>(null) }
    var showAchievementsModal by remember { mutableStateOf(false) }
    var showDailyChallengeModal by remember { mutableStateOf(false) }
    var showProfileModal by remember { mutableStateOf(false) }
    var showNotificationSettingsModal by remember { mutableStateOf(false) }

    // Open the exact learning destination when the user taps an Academy notification.
    LaunchedEffect(notificationTrackId, notificationOpenQuiz) {
        val targetTrackId = notificationTrackId
        if (!targetTrackId.isNullOrBlank()) {
            if (notificationOpenQuiz) selectedQuizTrackId = targetTrackId
            else selectedTrackId = targetTrackId
            onNotificationNavigationConsumed()
        }
    }

    // Initial Data Refresh
    LaunchedEffect(Unit) {
        isRefreshing = true
        // 1. Seed all 13 tracks, 130 articles, 13 quizzes, 260 questions offline from local assets
        repository.seedFromAssetsIfEmpty(context)

        // 2. Fetch tracks and achievements (uses local Room cache immediately)
        repository.refreshTracks()
        repository.refreshAchievements()
        val pResult = repository.getAcademyProgress()
        if (pResult.isSuccess) {
            progressData = pResult.getOrNull()
        }
        val dResult = repository.getDailyChallenge()
        if (dResult.isSuccess) {
            dailyChallengeData = dResult.getOrNull()
        }
        isRefreshing = false
    }

    // Refresh callback
    val refreshAll: () -> Unit = {
        coroutineScope.launch {
            repository.seedFromAssetsIfEmpty(context)
            repository.refreshTracks()
            repository.refreshAchievements()
            val pRes = repository.getAcademyProgress()
            if (pRes.isSuccess) progressData = pRes.getOrNull()
            val dRes = repository.getDailyChallenge()
            if (dRes.isSuccess) dailyChallengeData = dRes.getOrNull()
        }
    }

    // Derived statistics across all 13 tracks (130 articles + 13 quizzes = 143 items)
    val totalArticlesCompleted = tracks.sumOf { it.articlesCompletedCount }
    val totalQuizzesPassed = tracks.count { it.quizPassed }
    val totalTracksCompleted = tracks.count { it.trackCompleted }
    val totalPossibleItems = 130 + 13 // 143
    val totalCompletedItems = totalArticlesCompleted + totalQuizzesPassed
    val overallProgressPercent = if (totalPossibleItems > 0) {
        ((totalCompletedItems.toFloat() / totalPossibleItems.toFloat()) * 100).toInt().coerceIn(0, 100)
    } else 0

    val currentLevel = userStatsEntity?.level ?: progressData?.userStats?.level ?: 1
    val confirmedXp = userStatsEntity?.confirmedXp ?: progressData?.userStats?.xp ?: stats.xp
    // Derive the rank label from the active language so API/cache values cannot
    // leave Arabic text visible after switching the app to English (or vice versa).
    val rankTitle = AcademyTranslations.rankTitle(currentLevel, lang)

    CompositionLocalProvider(
        LocalLayoutDirection provides LayoutDirection.Ltr
    ) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("academy_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
        ) {
            // Screen Header with Language & Sound Controls
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AcademyTranslations.academyTitle(lang),
                            color = Color.White,
                            fontSize = academyTextSize(lang, 24),
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = Strings.getFontFamily(lang)
                        )
                        Text(
                            text = AcademyTranslations.academySubtitle(lang),
                            color = CyberMuted,
                            fontSize = academyTextSize(lang, 13),
                            fontFamily = Strings.getFontFamily(lang)
                        )
                    }

                    // Notification Settings Button
                    IconButton(
                        onClick = {
                            SoundManager.playButtonClick()
                            showNotificationSettingsModal = true
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(CyberCard)
                            .border(1.dp, CyberBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = CyberGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Hero Learning Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        CyberGreen.copy(alpha = 0.12f),
                                        CyberCard
                                    )
                                )
                            )
                            .border(1.dp, CyberBorder, RoundedCornerShape(20.dp))
                            .padding(horizontal = 20.dp, vertical = 19.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lang == Language.AR) "مرحباً بك، $rankTitle" else "Welcome, $rankTitle",
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                                color = Color.White,
                                fontSize = academyTextSize(lang, if (lang == Language.EN) 16 else 18),
                                fontWeight = FontWeight.Bold,
                                fontFamily = Strings.getFontFamily(lang),
                                maxLines = 2,
                                lineHeight = academyTextSize(lang, 21),
                                overflow = TextOverflow.Ellipsis
                            )
                            AcademyLevelBadge(level = currentLevel, rankTitle = rankTitle, lang = lang)
                        }
                        Text(
                            text = if (lang == Language.AR)
                                "إجمالي النقاط المؤكدة: $confirmedXp XP"
                            else
                                "Total Confirmed XP: $confirmedXp XP",
                            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                            color = CyberGreen,
                            fontSize = academyTextSize(lang, if (lang == Language.EN) 12 else 13),
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = Strings.getFontFamily(lang),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        // Progress Bar
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (lang == Language.AR)
                                        "التقدم العام في المحتوى (143 وحدة تعليمية)"
                                    else
                                        "Overall Curriculum Progress (143 Lessons)",
                                    color = CyberMuted,
                                    fontSize = academyTextSize(lang, 12),
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                                Text(
                                    text = "$overallProgressPercent%",
                                    color = CyberGreen,
                                    fontSize = academyTextSize(lang, 12),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            LinearProgressIndicator(
                                progress = { (overallProgressPercent.toFloat() / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = CyberGreen,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AcademyStatCard(
                        title = if (lang == Language.AR) "المقالات المقروءة" else "Articles Read",
                        value = "$totalArticlesCompleted/130",
                        icon = Icons.Default.MenuBook,
                        color = CyberGreen,
                        modifier = Modifier.weight(1f),
                        lang = lang
                    )
                    AcademyStatCard(
                        title = if (lang == Language.AR) "الاختبارات المجتازة" else "Quizzes Passed",
                        value = "$totalQuizzesPassed/13",
                        icon = Icons.Default.Quiz,
                        color = CyberBlue,
                        modifier = Modifier.weight(1f),
                        lang = lang,
                        titleFontSize = if (lang == Language.AR) 10 else null
                    )
                    AcademyStatCard(
                        title = if (lang == Language.AR) "المسارات المكتملة" else "Tracks Finished",
                        value = "$totalTracksCompleted/13",
                        icon = Icons.Default.CheckCircle,
                        color = CyberPurple,
                        modifier = Modifier.weight(1f),
                        lang = lang
                    )
                }
            }

            // Quick Navigation Hub (Achievements & Learning Profile)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            SoundManager.playButtonClick()
                            showAchievementsModal = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.24f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = CyberYellow.copy(alpha = 0.12f),
                            contentColor = CyberYellow
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == Language.AR) "الأوسمة والإنجازات" else "Badges & Ranks",
                            fontSize = academyTextSize(lang, 13),
                            fontWeight = FontWeight.Bold,
                            fontFamily = Strings.getFontFamily(lang)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            SoundManager.playButtonClick()
                            showProfileModal = true
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.24f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = CyberBlue.copy(alpha = 0.12f),
                            contentColor = CyberBlue
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == Language.AR) "الملف التعليمي" else "Learning Profile",
                            fontSize = academyTextSize(lang, 13),
                            fontWeight = FontWeight.Bold,
                            fontFamily = Strings.getFontFamily(lang)
                        )
                    }
                }
            }

            // Daily Challenge Card
            item {
                val challenge = dailyChallengeData
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            SoundManager.playButtonClick()
                            showDailyChallengeModal = true
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberCard)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        CyberPink.copy(alpha = 0.12f),
                                        CyberCard
                                    )
                                )
                            )
                            .border(1.dp, CyberPink.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(CyberPink.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = CyberPink,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = AcademyTranslations.dailyChallengeHeader(lang),
                                        color = CyberPink,
                                        fontSize = academyTextSize(lang, 12),
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = Strings.getFontFamily(lang)
                                    )
                                    Text(
                                        text = "+15 XP",
                                        color = CyberGreen,
                                        fontSize = academyTextSize(lang, 11),
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Text(
                                    text = if (lang == Language.AR)
                                        (challenge?.titleAr ?: "سؤال أمني سريع لتثبيت المعلومات")
                                    else
                                        "Daily Real-World Cyber Trap",
                                    color = Color.White,
                                    fontSize = academyTextSize(lang, 14),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Strings.getFontFamily(lang),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = if (challenge?.isCompleted == true) {
                                        AcademyTranslations.dailyChallengeCompleted(lang)
                                    } else {
                                        if (lang == Language.AR) "اضغط هنا لبدء التحدي واكتساب النقاط" else "Tap here to take today's challenge"
                                    },
                                    color = if (challenge?.isCompleted == true) CyberGreen else CyberMuted,
                                    fontSize = academyTextSize(lang, 11),
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }
                        }

                        Icon(
                            imageVector = if (challenge?.isCompleted == true) {
                                Icons.Default.CheckCircle
                            } else {
                                if (lang == Language.AR) Icons.Default.ChevronLeft else Icons.AutoMirrored.Filled.KeyboardArrowRight
                            },
                            contentDescription = null,
                            tint = if (challenge?.isCompleted == true) CyberGreen else CyberPink,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Section Title for Tracks
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AcademyTranslations.learningTracksHeader(lang),
                        color = Color.White,
                        fontSize = academyTextSize(lang, 18),
                        fontWeight = FontWeight.Bold,
                        fontFamily = Strings.getFontFamily(lang)
                    )
                    Text(
                        text = if (lang == Language.AR) "${tracks.size} مسارات متاحة" else "${tracks.size} Available",
                        color = CyberMuted,
                        fontSize = academyTextSize(lang, 12),
                        fontFamily = Strings.getFontFamily(lang)
                    )
                }
            }

            // Educational Tracks List (all 13 tracks)
            items(tracks, key = { it.id }) { track ->
                val trackColor = try {
                    Color(android.graphics.Color.parseColor(track.colorHex))
                } catch (_: Exception) {
                    CyberGreen
                }
                val icon = resolveAcademyIcon(track.iconName)

                val status = when {
                    track.trackCompleted -> "completed"
                    track.articlesCompletedCount > 0 || track.quizPassed -> "in_progress"
                    else -> "not_started"
                }

                val localizedTrackTitle = AcademyTranslations.getTrackTitle(track.id, track.titleAr, lang)
                val localizedTrackDesc = AcademyTranslations.getTrackDescription(track.slug, track.descriptionAr, lang)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            SoundManager.playButtonClick()
                            selectedTrackId = track.id
                        }
                        .testTag("academy_track_${track.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, trackColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(trackColor.copy(alpha = 0.16f))
                                        .border(1.dp, trackColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = trackColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = localizedTrackTitle,
                                        color = Color.White,
                                        fontSize = academyTextSize(lang, 15),
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = Strings.getFontFamily(lang)
                                    )
                                    Text(
                                        text = localizedTrackDesc,
                                        color = CyberMuted,
                                        fontSize = academyTextSize(lang, 11),
                                        fontFamily = Strings.getFontFamily(lang),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            AcademyStatusChip(status = status, lang = lang)
                        }

                        // Progress metrics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lang == Language.AR)
                                    "${track.articlesCompletedCount}/${track.articlesCount} مقالات • ${if (track.quizPassed) "الاختبار مُجتاز" else "الاختبار معلق"}"
                                else
                                    "${track.articlesCompletedCount}/${track.articlesCount} Articles • ${if (track.quizPassed) "Quiz Passed" else "Quiz Pending"}",
                                color = CyberMuted,
                                fontSize = academyTextSize(lang, 11),
                                fontFamily = Strings.getFontFamily(lang)
                            )
                            Text(
                                text = "${track.progressPercent}%",
                                color = trackColor,
                                fontSize = academyTextSize(lang, 11),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        LinearProgressIndicator(
                            progress = { (track.progressPercent.toFloat() / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = trackColor,
                            trackColor = Color.White.copy(alpha = 0.08f)
                        )
                    }
                }
            }
        }
    }

    // Modal Overlays
    AcademyTrackDetailModal(
        isOpen = selectedTrackId != null,
        trackId = selectedTrackId,
        onClose = {
            selectedTrackId = null
            refreshAll()
        },
        onOpenArticle = { articleId ->
            selectedArticleId = articleId
        },
        onOpenQuiz = { trackId ->
            selectedQuizTrackId = trackId
        },
        lang = lang
    )

    AcademyArticleReaderModal(
        isOpen = selectedArticleId != null,
        articleId = selectedArticleId,
        onClose = {
            selectedArticleId = null
            refreshAll()
        },
        onArticleCompleted = { _, _ ->
            refreshAll()
        },
        lang = lang
    )

    AcademyQuizModal(
        isOpen = selectedQuizTrackId != null,
        trackId = selectedQuizTrackId,
        onClose = {
            selectedQuizTrackId = null
            refreshAll()
        },
        onQuizSubmitted = { _, _, _ ->
            refreshAll()
        },
        lang = lang
    )

    AcademyAchievementsModal(
        isOpen = showAchievementsModal,
        onClose = {
            showAchievementsModal = false
            refreshAll()
        },
        lang = lang
    )

    AcademyDailyChallengeModal(
        isOpen = showDailyChallengeModal,
        onClose = {
            showDailyChallengeModal = false
            refreshAll()
        },
        onChallengeCompleted = { _ ->
            refreshAll()
        },
        lang = lang
    )

    if (showProfileModal) {
        AcademyLearningProfileModal(
            repository = repository,
            onDismiss = {
                showProfileModal = false
                refreshAll()
            },
            lang = lang
        )
    }

    if (showNotificationSettingsModal) {
        AcademyNotificationSettingsModal(
            repository = repository,
            lang = lang,
            onDismiss = {
                showNotificationSettingsModal = false
                refreshAll()
            }
        )
    }
    }
}
