package com.example.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.localization.Strings
import com.example.model.Course
import com.example.model.Language
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.*
import com.example.util.BidiHelper

@Composable
fun CourseLessonModal(
    course: Course?,
    isOpen: Boolean,
    onClose: () -> Unit,
    lang: Language,
    onCompleteLesson: (courseId: String, lessonId: String, xp: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen || course == null) return

    val isAr = lang == Language.AR
    var activeLessonIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }
    var isCompleted by remember { mutableStateOf(false) }

    val lesson = course.lessons.getOrNull(activeLessonIndex) ?: course.lessons[0]

    Dialog(onDismissRequest = onClose) {
        GlassCard(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("course_lesson_modal"),
            borderColor = course.color.copy(alpha = 0.35f)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(course.color.copy(alpha = 0.18f))
                            .border(1.dp, course.color.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = course.color, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = course.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1
                        )
                        Text(
                            text = course.category,
                            color = CyberMuted,
                            fontSize = 10.sp
                        )
                    }
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = CyberMuted)
                }
            }

            Spacer(Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Lesson Title & Duration Pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lesson.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(lesson.duration, color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
                    }
                }

                // Lesson Content Body
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberCard2.copy(alpha = 0.7f))
                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = lesson.content,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }

                // Knowledge Check Quiz Question
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF09090C))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.EmojiEvents, null, tint = CyberPink, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (isAr) BidiHelper.formatBiDi("سؤال التحقق المعرفي (+40 XP):", isAr = true) else "Knowledge Check (+40 XP):",
                            color = CyberPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = lesson.quizQuestion.question,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    // Options
                    lesson.quizQuestion.options.forEachIndexed { idx, option ->
                        val isSelected = selectedOption == idx
                        val isCorrect = idx == lesson.quizQuestion.correctIndex
                        val (bgColor, borderColor, textColor) = when {
                            hasAnswered && isCorrect -> Triple(CyberGreen.copy(alpha = 0.2f), CyberGreen, CyberGreen)
                            hasAnswered && isSelected -> Triple(CyberRed.copy(alpha = 0.2f), CyberRed, CyberRed)
                            isSelected -> Triple(CyberPink.copy(alpha = 0.2f), CyberPink, Color.White)
                            else -> Triple(CyberCard2, Color.White.copy(alpha = 0.08f), Color.White)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(bgColor)
                                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                .clickable(enabled = !hasAnswered) {
                                    selectedOption = idx
                                    hasAnswered = true
                                    if (idx == lesson.quizQuestion.correctIndex) {
                                        com.example.sound.SoundManager.playSuccess()
                                    } else {
                                        com.example.sound.SoundManager.playAlert()
                                    }
                                }
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option,
                                color = textColor,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected || (hasAnswered && isCorrect)) FontWeight.Bold else FontWeight.Normal
                            )
                            if (hasAnswered && isCorrect) {
                                Icon(Icons.Filled.Check, null, tint = CyberGreen, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    if (hasAnswered) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = if (isAr) "الشرح والتوضيح:" else "Explanation:",
                                    color = CyberGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = lesson.quizQuestion.explanation,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }

                // Completion Button
                if (hasAnswered && !isCompleted) {
                    NeonButton(
                        text = if (isAr) BidiHelper.formatBiDi("إكمال الدرس والحصول على 40 XP", isAr = true) else "Complete Lesson & Claim 40 XP",
                        onClick = {
                            isCompleted = true
                            com.example.sound.SoundManager.playSuccess()
                            onCompleteLesson(course.id, lesson.id, 40)
                        },
                        testTag = "complete_lesson_button"
                    )
                }

                if (isCompleted) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CyberGreen.copy(alpha = 0.15f))
                            .border(1.dp, CyberGreen.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, null, tint = CyberGreen, modifier = Modifier.size(36.dp))
                        Text(
                            text = if (isAr) BidiHelper.formatBiDi("تم إكمال الدرس بنجاح! تم إضافة +40 XP", isAr = true) else "Lesson Completed! +40 XP Added",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Button(
                            onClick = onClose,
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (isAr) "إغلاق" else "Close", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
