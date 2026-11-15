package com.example.academy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.academy.data.*
import com.example.localization.Strings
import com.example.model.Language
import com.example.sound.SoundManager
import com.example.ui.theme.*

@Composable
fun AcademyLearningProfileModal(
    repository: AcademyRepository,
    onDismiss: () -> Unit,
    lang: Language = Language.AR
) {
    var historyResponse by remember { mutableStateOf<AcademyHistoryResponse?>(null) }
    var progressResponse by remember { mutableStateOf<AcademyProgressResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val userStats by repository.getUserStatsFlow().collectAsState(initial = null)

    LaunchedEffect(Unit) {
        isLoading = true
        val histResult = repository.getAcademyHistory()
        val progResult = repository.getAcademyProgress()
        if (histResult.isSuccess) {
            historyResponse = histResult.getOrNull()
        }
        if (progResult.isSuccess) {
            progressResponse = progResult.getOrNull()
        }
        isLoading = false
    }

    Dialog(
        onDismissRequest = {
            SoundManager.playButtonClick()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberDarkBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        SoundManager.playButtonClick()
                        onDismiss()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = AcademyTranslations.closeAction(lang),
                            tint = Color.White
                        )
                    }

                    Text(
                        text = if (lang == Language.AR) "الملف التعليمي وسجل النقاط" else "Learning Profile & XP Log",
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        fontSize = academyTextSize(lang, 18),
                        fontWeight = FontWeight.Bold,
                        fontFamily = Strings.getFontFamily(lang)
                    )

                    Spacer(modifier = Modifier.size(48.dp))
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = CyberGreen)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        // Level & XP Hero Card
                        item {
                            val level = userStats?.level ?: progressResponse?.userStats?.level ?: 1
                            val confirmedXp = userStats?.confirmedXp ?: progressResponse?.userStats?.xp ?: 0
                            val xpToNext = userStats?.xpToNextLevel ?: progressResponse?.userStats?.xpToNextLevel ?: 100
                            val rankTitle = AcademyTranslations.rankTitle(level, lang)

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.76f))
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
                                        .padding(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = rankTitle,
                                                color = Color.White,
                                                fontSize = academyTextSize(lang, 20),
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                            Text(
                                                text = if (lang == Language.AR)
                                                    "المستوى $level في أكاديمية سايبر جارد"
                                                else
                                                    "Level $level in CyberGuard Academy",
                                                color = CyberMuted,
                                                fontSize = academyTextSize(lang, 13),
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                        }

                                        AcademyLevelBadge(level = level, rankTitle = rankTitle, lang = lang)
                                    }

                                    HorizontalDivider(color = CyberBorder)

                                    // Confirmed vs Pending XP Display
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(
                                                text = if (lang == Language.AR) "النقاط المؤكدة" else "Confirmed XP",
                                                color = CyberMuted,
                                                fontSize = academyTextSize(lang, 12),
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                            Text(
                                                text = "$confirmedXp XP",
                                                color = CyberGreen,
                                                fontSize = academyTextSize(lang, 22),
                                                fontWeight = FontWeight.ExtraBold,
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                        }
                                    }

                                    // Next level progress
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = if (lang == Language.AR) "التقدم للمستوى التالي" else "Progress to next level",
                                                color = CyberMuted,
                                                fontSize = academyTextSize(lang, 11),
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                            Text(
                                                text = if (lang == Language.AR) "متبقي $xpToNext XP" else "$xpToNext XP remaining",
                                                color = CyberGreen,
                                                fontSize = academyTextSize(lang, 11),
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                        }
                                        LinearProgressIndicator(
                                            progress = {
                                                val totalForLevel = when (level) {
                                                    1 -> 100
                                                    2 -> 150
                                                    3 -> 250
                                                    4 -> 300
                                                    5 -> 400
                                                    else -> 500
                                                }
                                                val currentInLevel = totalForLevel - xpToNext
                                                (currentInLevel.toFloat() / totalForLevel.toFloat()).coerceIn(0.05f, 1f)
                                            },
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

                        // Learning Progress Metrics
                        item {
                            Text(
                                text = if (lang == Language.AR) "إحصائيات الإنجاز التعليمي" else "Learning Progress Overview",
                                color = Color.White,
                                fontSize = academyTextSize(lang, 16),
                                fontWeight = FontWeight.Bold,
                                fontFamily = Strings.getFontFamily(lang)
                            )
                        }

                        item {
                            val articlesDone = progressResponse?.articlesCompleted ?: 0
                            val quizzesDone = progressResponse?.quizzesPassed ?: 0
                            val tracksDone = progressResponse?.tracksCompleted ?: 0

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                AcademyStatCard(
                                    title = if (lang == Language.AR) "المقالات المقروءة" else "Articles Read",
                                    value = "$articlesDone/130",
                                    icon = Icons.Default.MenuBook,
                                    color = CyberGreen,
                                    modifier = Modifier.weight(1f),
                                    lang = lang
                                )
                                AcademyStatCard(
                                    title = if (lang == Language.AR) "الاختبارات المجتازة" else "Quizzes Passed",
                                    value = "$quizzesDone/13",
                                    icon = Icons.Default.Quiz,
                                    color = CyberBlue,
                                    modifier = Modifier.weight(1f),
                                    lang = lang
                                )
                                AcademyStatCard(
                                    title = if (lang == Language.AR) "المسارات المكتملة" else "Tracks Finished",
                                    value = "$tracksDone/13",
                                    icon = Icons.Default.CheckCircle,
                                    color = CyberPurple,
                                    modifier = Modifier.weight(1f),
                                    lang = lang
                                )
                            }
                        }

                        // Server XP Transactions History
                        val transactions = historyResponse?.xpTransactions ?: emptyList()
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (lang == Language.AR) "سجل معاملات النقاط" else "XP Rewards Log",
                                    color = Color.White,
                                    fontSize = academyTextSize(lang, 16),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                                Text(
                                    text = if (lang == Language.AR) "${transactions.size} معاملة" else "${transactions.size} entries",
                                    color = CyberMuted,
                                    fontSize = academyTextSize(lang, 12),
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }
                        }

                        if (transactions.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.76f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (lang == Language.AR)
                                                "لم يتم تسجيل أي مكافآت نقاط بعد. أكمل مقالاً أو اختباراً للبدء!"
                                            else
                                                "No XP rewards logged yet. Complete an article or quiz to earn XP!",
                                            color = CyberMuted,
                                            fontSize = academyTextSize(lang, 13),
                                            textAlign = TextAlign.Center,
                                            fontFamily = Strings.getFontFamily(lang)
                                        )
                                    }
                                }
                            }
                        } else {
                            items(transactions.take(15)) { tx ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.76f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(CyberGreen.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Stars,
                                                    contentDescription = null,
                                                    tint = CyberGreen,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = if (lang == Language.AR) tx.description else AcademyTranslations.translateSentence(tx.description),
                                                    color = Color.White,
                                                    fontSize = academyTextSize(lang, 13),
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontFamily = Strings.getFontFamily(lang)
                                                )
                                                Text(
                                                    text = tx.createdAt.take(16).replace("T", " "),
                                                    color = CyberMuted,
                                                    fontSize = academyTextSize(lang, 11)
                                                )
                                            }
                                        }

                                        Text(
                                            text = "+${tx.xpAmount} XP",
                                            color = CyberGreen,
                                            fontSize = academyTextSize(lang, 15),
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = Strings.getFontFamily(lang)
                                        )
                                    }
                                }
                            }
                        }

                        // Recent Quiz Attempts
                        val quizAttempts = historyResponse?.quizAttempts ?: emptyList()
                        if (quizAttempts.isNotEmpty()) {
                            item {
                                Text(
                                    text = if (lang == Language.AR) "محاولات الاختبارات الأخيرة" else "Recent Quiz Attempts",
                                    color = Color.White,
                                    fontSize = academyTextSize(lang, 16),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }

                            items(quizAttempts.take(5)) { attempt ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.76f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = if (lang == Language.AR) attempt.trackTitle else AcademyTranslations.translateSentence(attempt.trackTitle),
                                                color = Color.White,
                                                fontSize = academyTextSize(lang, 13),
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                            Text(
                                                text = attempt.attemptedAt.take(16).replace("T", " "),
                                                color = CyberMuted,
                                                fontSize = academyTextSize(lang, 11)
                                            )
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = "${attempt.scorePercent}%",
                                                color = if (attempt.passed == 1) CyberGreen else CyberRed,
                                                fontSize = academyTextSize(lang, 15),
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (attempt.passed == 1) {
                                                    if (lang == Language.AR) "ناجح" else "Passed"
                                                } else {
                                                    if (lang == Language.AR) "لم يجتز" else "Failed"
                                                },
                                                color = if (attempt.passed == 1) CyberGreen else CyberRed,
                                                fontSize = academyTextSize(lang, 12),
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
