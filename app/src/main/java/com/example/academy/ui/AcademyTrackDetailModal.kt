package com.example.academy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
fun AcademyTrackDetailModal(
    isOpen: Boolean,
    trackId: String?,
    onClose: () -> Unit,
    onOpenArticle: (articleId: String) -> Unit,
    onOpenQuiz: (trackId: String) -> Unit,
    lang: Language = Language.AR
) {
    if (!isOpen || trackId == null) return

    val context = LocalContext.current
    val repository = remember { AcademyRepository.getInstance(context) }

    val track by repository.getTrackFlow(trackId).collectAsState(initial = null)
    val articles by repository.getArticlesByTrackFlow(trackId).collectAsState(initial = emptyList())
    val quiz by repository.getQuizByTrackFlow(trackId).collectAsState(initial = null)

    LaunchedEffect(trackId) {
        if (articles.isEmpty()) {
            repository.seedFromAssetsIfEmpty(context)
        }
        repository.refreshTrackDetail(trackId)
    }

    Dialog(
        onDismissRequest = {
            SoundManager.playButtonClick()
            onClose()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberDarkBg)
                .testTag("track_detail_modal")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CyberCard)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            SoundManager.playButtonClick()
                            onClose()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = AcademyTranslations.closeAction(lang),
                            tint = Color.White
                        )
                    }

                    Text(
                        text = if (lang == Language.AR) "تفاصيل المسار التعليمي" else "Learning Track Details",
                        color = Color.White,
                        fontSize = academyTextSize(lang, 16),
                        fontWeight = FontWeight.Bold,
                        fontFamily = Strings.getFontFamily(lang)
                    )

                    Box(modifier = Modifier.size(36.dp))
                }

                // Content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
                ) {
                    // Track Hero Card
                    item {
                        val t = track
                        val color = try {
                            Color(android.graphics.Color.parseColor(t?.colorHex ?: "#FF0055"))
                        } catch (e: Exception) {
                            CyberPink
                        }

                        val trackTitle = t?.let { AcademyTranslations.getTrackTitle(it.id, it.titleAr, lang) }
                            ?: (if (lang == Language.AR) "جاري التحميل..." else "Loading...")
                        val trackDesc = t?.let { if (lang == Language.EN) AcademyTranslations.getTrackDescription(it.id, it.descriptionAr, lang) else it.descriptionAr } ?: ""

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.76f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(color.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = resolveAcademyIcon(t?.iconName ?: "School"),
                                            contentDescription = null,
                                            tint = color,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }

                                    val status = when {
                                        t?.trackCompleted == true -> "completed"
                                        (t?.articlesCompletedCount ?: 0) > 0 || (t?.quizPassed == true) -> "in_progress"
                                        else -> "not_started"
                                    }
                                    AcademyStatusChip(status = status, lang = lang)
                                }

                                Text(
                                    text = trackTitle,
                                    color = Color.White,
                                    fontSize = academyTextSize(lang, 20),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontFamily = Strings.getFontFamily(lang)
                                )

                                Text(
                                    text = trackDesc,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = academyTextSize(lang, 13),
                                    lineHeight = academyTextSize(lang, 20),
                                    fontFamily = Strings.getFontFamily(lang)
                                )

                                // Progress Info
                                val progressPercent = t?.progressPercent ?: 0
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (lang == Language.AR)
                                                "إنجاز المسار: ${t?.articlesCompletedCount ?: 0}/10 مقالات"
                                            else
                                                "Track Progress: ${t?.articlesCompletedCount ?: 0}/10 Articles",
                                            color = CyberMuted,
                                            fontSize = academyTextSize(lang, 12),
                                            fontFamily = Strings.getFontFamily(lang)
                                        )
                                        Text(
                                            text = "$progressPercent%",
                                            color = color,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = academyTextSize(lang, 12),
                                            fontFamily = Strings.getFontFamily(lang)
                                        )
                                    }

                                    LinearProgressIndicator(
                                        progress = { (progressPercent / 100f).coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = color,
                                        trackColor = CyberCard2
                                    )
                                }
                            }
                        }
                    }

                    // Quiz Banner Card
                    item {
                        val q = quiz
                        val quizPassed = track?.quizPassed == true
                        val quizColor = if (quizPassed) CyberGreen else CyberYellow

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable {
                                    SoundManager.playButtonClick()
                                    onOpenQuiz(trackId)
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.76f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, quizColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(quizColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (quizPassed) Icons.Default.CheckCircle else Icons.Default.Quiz,
                                        contentDescription = null,
                                        tint = quizColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (lang == Language.AR)
                                            (q?.titleAr ?: "اختبار المسار التفاعلي")
                                        else
                                            "Interactive Track Quiz",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = academyTextSize(lang, 15),
                                        fontFamily = Strings.getFontFamily(lang)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (quizPassed) {
                                            if (lang == Language.AR) "اجتزت الاختبار بنجاح (+100 XP)" else "Quiz passed successfully (+100 XP)"
                                        } else {
                                            if (lang == Language.AR) "20 سؤالاً واقعياً • نسبة النجاح 75% (+100 XP)" else "20 Questions • 75% Passing Mark (+100 XP)"
                                        },
                                        color = if (quizPassed) CyberGreen else CyberMuted,
                                        fontSize = academyTextSize(lang, 12),
                                        fontFamily = Strings.getFontFamily(lang)
                                    )
                                }

                                Icon(
                                    imageVector = if (lang == Language.AR) Icons.Default.ChevronLeft else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = quizColor
                                )
                            }
                        }
                    }

                    // Articles Section Header
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lang == Language.AR) "دروس ومقالات المسار" else "Track Lessons & Articles",
                                color = Color.White,
                                fontSize = academyTextSize(lang, 16),
                                fontWeight = FontWeight.Bold,
                                fontFamily = Strings.getFontFamily(lang)
                            )
                            Text(
                                text = if (lang == Language.AR) "${articles.size} مقالات" else "${articles.size} Articles",
                                color = CyberMuted,
                                fontSize = academyTextSize(lang, 13),
                                fontFamily = Strings.getFontFamily(lang)
                            )
                        }
                    }

                    // Articles List
                    itemsIndexed(articles) { index, art ->
                        val articleTitle = remember(art, lang) {
                            if (lang == Language.AR) art.titleAr else {
                                CuratedEnglishContent.getArticle(art.id)?.title
                                    ?: art.titleEn?.takeIf { it.isNotBlank() }
                                    ?: AcademyTranslations.translateSentence(art.titleAr)
                            }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    SoundManager.playButtonClick()
                                    onOpenArticle(art.id)
                                },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.76f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        1.dp,
                                        if (art.isCompleted) CyberGreen.copy(alpha = 0.3f) else CyberBorder,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (art.isCompleted) CyberGreen.copy(alpha = 0.15f) else CyberCard2),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (art.isCompleted) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(20.dp))
                                    } else {
                                        Text(
                                            text = "${index + 1}",
                                            color = CyberMuted,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = academyTextSize(lang, 13)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = articleTitle,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = academyTextSize(lang, 14),
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        fontFamily = Strings.getFontFamily(lang)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (lang == Language.AR) "${art.readTimeMinutes} دقائق" else "${art.readTimeMinutes} min",
                                            color = CyberMuted,
                                            fontSize = academyTextSize(lang, 11),
                                            fontFamily = Strings.getFontFamily(lang)
                                        )
                                        Text(text = "•", color = CyberMuted, fontSize = academyTextSize(lang, 10))
                                        Text(
                                            text = if (art.isCompleted) {
                                                if (lang == Language.AR) "مكتمل" else "Read"
                                            } else "+5 XP",
                                            color = if (art.isCompleted) CyberGreen else CyberPink,
                                            fontSize = academyTextSize(lang, 11),
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = Strings.getFontFamily(lang)
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = if (lang == Language.AR) Icons.Default.ChevronLeft else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = CyberMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
