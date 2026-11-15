package com.example.academy.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.academy.data.AcademyDailyChallengeResponse
import com.example.academy.data.AcademyDailyChallengeSubmitResponse
import com.example.academy.data.AcademyRepository
import com.example.academy.data.AcademyTranslations
import com.example.localization.Strings
import com.example.model.Language
import com.example.sound.SoundManager
import com.example.util.BidiHelper
import com.example.ui.theme.*
import kotlinx.coroutines.launch


/**
 * Keeps each challenge field in the direction of its first written language.
 * Arabic-led text uses the same per-field BiDi treatment as the Privacy & Terms page;
 * English-led text remains LTR while embedded Arabic runs are isolated as RTL.
 */
private fun dailyChallengeDirection(text: String): TextDirection {
    val firstStrong = text.firstOrNull { ch ->
        ch in 'A'..'Z' || ch in 'a'..'z' ||
            ch in '\u0600'..'\u06FF' || ch in '\u0750'..'\u077F' ||
            ch in '\u08A0'..'\u08FF'
    } ?: return TextDirection.ContentOrLtr
    return if (firstStrong in '\u0600'..'\u06FF' ||
        firstStrong in '\u0750'..'\u077F' || firstStrong in '\u08A0'..'\u08FF'
    ) TextDirection.ContentOrRtl else TextDirection.ContentOrLtr
}

private val ARABIC_RUN_REGEX = Regex("""[\u0600-\u06FF\u0750-\u077F\u08A0-\u08FF]+(?:[ ،؛؟.!،:：]*)""")

private fun formatDailyChallengeText(text: String): String {
    if (text.isBlank()) return text
    return if (dailyChallengeDirection(text) == TextDirection.ContentOrRtl) {
        text.lines().joinToString("\n") { line -> BidiHelper.formatBiDi(line, isAr = true) }
    } else {
        ARABIC_RUN_REGEX.replace(text) { match -> BidiHelper.isolateRtl(match.value) }
    }
}

@Composable
fun AcademyDailyChallengeModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    onChallengeCompleted: (xpGained: Int) -> Unit,
    lang: Language = Language.AR
) {
    if (!isOpen) return

    val context = LocalContext.current
    val repository = remember { AcademyRepository.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    var challenge by remember { mutableStateOf<AcademyDailyChallengeResponse?>(null) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var submitResult by remember { mutableStateOf<AcademyDailyChallengeSubmitResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoading = true
        val res = repository.getDailyChallenge()
        if (res.isSuccess) {
            challenge = res.getOrNull()
        }
        isLoading = false
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberDarkBg)
                .testTag("daily_challenge_modal"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 680.dp)
            ) {
                // Header Bar
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
                        text = AcademyTranslations.dailyChallengeHeader(lang),
                        color = Color.White,
                        fontSize = academyTextSize(lang, 16),
                        fontWeight = FontWeight.Bold,
                        fontFamily = Strings.getFontFamily(lang)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberPink.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = AcademyTranslations.dailyChallengeReward(lang, 15),
                            color = CyberPink,
                            fontSize = academyTextSize(lang, 12),
                            fontWeight = FontWeight.Bold,
                            fontFamily = Strings.getFontFamily(lang)
                        )
                    }
                }

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = CyberPink)
                    }
                } else if (challenge == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = CyberPink, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (lang == Language.AR) "تعذر تحميل التحدي اليومي" else "Unable to load daily challenge",
                            color = Color.White,
                            fontFamily = Strings.getFontFamily(lang)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                SoundManager.playButtonClick()
                                coroutineScope.launch {
                                    isLoading = true
                                    val r = repository.getDailyChallenge()
                                    if (r.isSuccess) challenge = r.getOrNull()
                                    isLoading = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberPink)
                        ) {
                            Text(if (lang == Language.AR) "إعادة المحاولة" else "Retry", fontFamily = Strings.getFontFamily(lang))
                        }
                    }
                } else {
                    val ch = challenge!!
                    val q = ch.question

                    val displayQuestionText = if (lang == Language.AR) q.questionTextAr else AcademyTranslations.translateSentence(q.questionTextAr)
                    val displaySituation = q.situationAr?.let { if (lang == Language.AR) it else AcademyTranslations.translateSentence(it) }
                    val displayOptions = q.options.map { if (lang == Language.AR) it else AcademyTranslations.translateSentence(it) }
                    val displayTrackTitle = if (lang == Language.AR) (ch.trackTitleAr ?: "الأمان الرقمي اليومي") else "Daily Cybersecurity"

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.76f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = CyberYellow)
                                        Text(
                                            text = formatDailyChallengeText(displayTrackTitle),
                                            color = CyberYellow,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = academyTextSize(lang, 13),
                                            fontFamily = Strings.getFontFamily(lang),
                                            style = LocalTextStyle.current.copy(textDirection = dailyChallengeDirection(displayTrackTitle))
                                        )
                                    }

                                    if (!displaySituation.isNullOrBlank()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(CyberCard2)
                                                .padding(12.dp)
                                        ) {
                                            Text(
                                                text = formatDailyChallengeText(displaySituation),
                                                color = Color.White.copy(alpha = 0.9f),
                                                fontSize = academyTextSize(lang, 13),
                                                lineHeight = academyTextSize(lang, 20),
                                                fontFamily = Strings.getFontFamily(lang),
                                                style = LocalTextStyle.current.copy(textDirection = dailyChallengeDirection(displaySituation))
                                            )
                                        }
                                    }

                                    Text(
                                        text = formatDailyChallengeText(displayQuestionText),
                                        color = Color.White,
                                        fontSize = academyTextSize(lang, 16),
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = academyTextSize(lang, 24),
                                        fontFamily = Strings.getFontFamily(lang),
                                        style = LocalTextStyle.current.copy(textDirection = dailyChallengeDirection(displayQuestionText))
                                    )
                                }
                            }
                        }

                        // Answer choices
                        itemsIndexed(displayOptions) { index, option ->
                            val isSelected = selectedOption == index
                            val isSubmitted = submitResult != null
                            val isCorrectOption = submitResult?.correctOptionIndex == index

                            val cardBg = when {
                                isSubmitted && isCorrectOption -> CyberGreen.copy(alpha = 0.2f)
                                isSubmitted && isSelected && !isCorrectOption -> CyberPink.copy(alpha = 0.2f)
                                isSelected -> CyberPink.copy(alpha = 0.15f)
                                else -> CyberCard
                            }

                            val borderStroke = when {
                                isSubmitted && isCorrectOption -> CyberGreen
                                isSubmitted && isSelected && !isCorrectOption -> CyberPink
                                isSelected -> CyberPink
                                else -> CyberBorder
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(cardBg)
                                    .border(1.dp, borderStroke, RoundedCornerShape(12.dp))
                                    .clickable(enabled = !isSubmitted) {
                                        SoundManager.playButtonClick()
                                        selectedOption = index
                                    }
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected || (isSubmitted && isCorrectOption)) borderStroke else Color.White.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isSubmitted && isCorrectOption) {
                                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                        } else if (isSubmitted && isSelected && !isCorrectOption) {
                                            Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        } else if (isSelected) {
                                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color.White))
                                        }
                                    }

                                    Text(
                                        text = formatDailyChallengeText(option),
                                        color = Color.White,
                                        fontSize = academyTextSize(lang, 14),
                                        lineHeight = academyTextSize(lang, 20),
                                        fontFamily = Strings.getFontFamily(lang),
                                        style = LocalTextStyle.current.copy(textDirection = dailyChallengeDirection(option)),
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }

                        // Explanation and Warning Feedback
                        if (submitResult != null) {
                            item {
                                AnimatedVisibility(visible = true) {
                                    val result = submitResult!!
                                    val isCorrect = result.isCorrect
                                    val explanation = if (lang == Language.AR) result.explanationAr else AcademyTranslations.translateSentence(result.explanationAr)
                                    val warning = result.warningLessonAr?.let { if (lang == Language.AR) it else AcademyTranslations.translateSentence(it) }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(if (isCorrect) CyberGreen.copy(alpha = 0.1f) else CyberPink.copy(alpha = 0.1f))
                                            .border(1.dp, if (isCorrect) CyberGreen else CyberPink, RoundedCornerShape(16.dp))
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                                contentDescription = null,
                                                tint = if (isCorrect) CyberGreen else CyberPink
                                            )
                                            Text(
                                                text = if (isCorrect) {
                                                    if (result.xpAwarded) (if (lang == Language.AR) "إجابة صحيحة! تم كسب +15 XP" else "Correct Answer! Earned +15 XP")
                                                    else (if (lang == Language.AR) "إجابة صحيحة! (تم منح XP سابقاً)" else "Correct! (XP already claimed today)")
                                                } else {
                                                    if (lang == Language.AR) "إجابة خاطئة! احذر من هذا الفخ" else "Incorrect! Watch out for this trap"
                                                },
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = academyTextSize(lang, 15),
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                        }

                                        if (explanation.isNotBlank()) {
                                            Text(
                                                text = formatDailyChallengeText(explanation),
                                                color = Color.White.copy(alpha = 0.9f),
                                                fontSize = academyTextSize(lang, 13),
                                                lineHeight = academyTextSize(lang, 20),
                                                fontFamily = Strings.getFontFamily(lang),
                                                style = LocalTextStyle.current.copy(textDirection = dailyChallengeDirection(explanation))
                                            )
                                        }

                                        if (!warning.isNullOrBlank()) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color.White.copy(alpha = 0.05f))
                                                    .padding(10.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.Top,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = CyberYellow, modifier = Modifier.size(16.dp))
                                                    Text(
                                                        text = formatDailyChallengeText(warning),
                                                        color = CyberYellow,
                                                        fontSize = academyTextSize(lang, 12),
                                                        lineHeight = academyTextSize(lang, 18),
                                                        fontFamily = Strings.getFontFamily(lang),
                                                        style = LocalTextStyle.current.copy(textDirection = dailyChallengeDirection(warning))
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Action Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberCard)
                            .padding(16.dp)
                    ) {
                        if (ch.isCompleted && submitResult == null) {
                            Button(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    onClose()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCard2)
                            ) {
                                Text(
                                    text = if (lang == Language.AR) "تم إكمال تحدي اليوم سابقاً • إغلاق" else "Today's challenge completed • Close",
                                    color = Color.White,
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }
                        } else if (submitResult != null) {
                            Button(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    onClose()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
                            ) {
                                Text(
                                    text = AcademyTranslations.closeAction(lang),
                                    color = Color.White,
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (selectedOption != null) {
                                        SoundManager.playButtonClick()
                                        isSubmitting = true
                                        coroutineScope.launch {
                                            val res = repository.submitDailyChallenge(q.id, selectedOption!!)
                                            if (res.isSuccess) {
                                                val body = res.getOrNull()
                                                submitResult = body
                                                if (body?.isCorrect == true) {
                                                    SoundManager.playCorrectAnswer()
                                                    SoundManager.playDailyChallengeComplete()
                                                    if (body.xpAwarded) {
                                                        onChallengeCompleted(body.xpGained)
                                                    }
                                                } else {
                                                    SoundManager.playIncorrectAnswer()
                                                }
                                            }
                                            isSubmitting = false
                                        }
                                    }
                                },
                                enabled = selectedOption != null && !isSubmitting,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("submit_daily_challenge"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberPink)
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Text(
                                        text = AcademyTranslations.submitAnswerAction(lang),
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
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
