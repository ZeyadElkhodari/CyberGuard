package com.example.academy.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.academy.data.AcademyArticleEntity
import com.example.academy.data.AcademyRepository
import com.example.academy.data.AcademyTranslations
import com.example.localization.Strings
import com.example.model.Language
import com.example.sound.SoundManager
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AcademyArticleReaderModal(
    isOpen: Boolean,
    articleId: String?,
    onClose: () -> Unit,
    onArticleCompleted: (articleId: String, xpGained: Int) -> Unit,
    lang: Language = Language.AR
) {
    if (!isOpen || articleId == null) return

    val context = LocalContext.current
    val repository = remember { AcademyRepository.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    var article by remember { mutableStateOf<AcademyArticleEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isCompleting by remember { mutableStateOf(false) }
    var completionMessage by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()

    // Has user reached near end of the list?
    val hasReachedEnd by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            if (totalItems <= 3) true
            else {
                val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisibleItemIndex >= totalItems - 2
            }
        }
    }

    LaunchedEffect(articleId) {
        isLoading = true
        var result = repository.refreshArticleDetail(articleId)
        if (result.isFailure || result.getOrNull() == null) {
            repository.seedFromAssetsIfEmpty(context)
            result = repository.refreshArticleDetail(articleId)
        }
        if (result.isSuccess) {
            article = result.getOrNull()
        }
        isLoading = false
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
                .testTag("article_reader_modal")
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = CyberPink)
                }
            } else if (article == null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = CyberRed,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (lang == Language.AR) "عذراً، تعذر تحميل المقال" else "Unable to load article content",
                        color = Color.White,
                        fontSize = academyTextSize(lang, 18),
                        fontWeight = FontWeight.Bold,
                        fontFamily = Strings.getFontFamily(lang)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            SoundManager.playButtonClick()
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCard2)
                    ) {
                        Text(
                            text = AcademyTranslations.closeAction(lang),
                            color = Color.White,
                            fontFamily = Strings.getFontFamily(lang)
                        )
                    }
                }
            } else {
                val art = article!!
                val content = remember(art, lang) {
                    AcademyTranslations.getArticleContent(art, lang)
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    // Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CyberCard)
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
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
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = CyberPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (lang == Language.AR) "${art.readTimeMinutes} دقائق قراءة" else "${art.readTimeMinutes} min read",
                                color = CyberPink,
                                fontSize = academyTextSize(lang, 12),
                                fontWeight = FontWeight.Bold,
                                fontFamily = Strings.getFontFamily(lang)
                            )
                        }

                        if (art.isCompleted) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = CyberGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = if (lang == Language.AR) "تمت القراءة" else "Completed",
                                    color = CyberGreen,
                                    fontSize = academyTextSize(lang, 11),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }
                        } else {
                            Box(modifier = Modifier.size(36.dp))
                        }
                    }

                    // Article Content
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 20.dp, bottom = 220.dp)
                    ) {
                        // Title
                        item {
                            Text(
                                text = content.title,
                                color = Color.White,
                                fontSize = academyTextSize(lang, 22),
                                fontWeight = FontWeight.ExtraBold,
                                lineHeight = academyTextSize(lang, 32),
                                fontFamily = Strings.getFontFamily(lang)
                            )
                        }

                        // Intro
                        if (content.intro.isNotBlank()) {
                            item {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.76f))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, CyberBorder, RoundedCornerShape(14.dp))
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Lightbulb,
                                            contentDescription = null,
                                            tint = CyberYellow,
                                            modifier = Modifier.size(22.dp)
                                        )
                                        Text(
                                            text = content.intro,
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontSize = academyTextSize(lang, 14),
                                            lineHeight = academyTextSize(lang, 22),
                                            fontFamily = Strings.getFontFamily(lang)
                                        )
                                    }
                                }
                            }
                        }

                        // Simple Explanation
                        if (content.simpleExplanation.isNotBlank()) {
                            item {
                                SectionHeader(
                                    title = if (lang == Language.AR) "الشرح المبسط" else "Simple Explanation",
                                    icon = Icons.Default.AutoStories,
                                    color = CyberBlue,
                                    lang = lang
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = content.simpleExplanation,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = academyTextSize(lang, 14),
                                    lineHeight = academyTextSize(lang, 24),
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }
                        }

                        // Realistic Scenario
                        if (content.realisticScenario.isNotBlank()) {
                            item {
                                SectionHeader(
                                    title = if (lang == Language.AR) "سيناريو من الواقع" else "Real-World Scenario",
                                    icon = Icons.Default.Visibility,
                                    color = CyberPurple,
                                    lang = lang
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = CyberCard2)
                                ) {
                                    Text(
                                        text = content.realisticScenario,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = academyTextSize(lang, 13),
                                        lineHeight = academyTextSize(lang, 22),
                                        modifier = Modifier.padding(14.dp),
                                        fontFamily = Strings.getFontFamily(lang)
                                    )
                                }
                            }
                        }

                        // Warning Signs
                        if (content.warningSigns.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = if (lang == Language.AR) "علامات الخطر (Red Flags)" else "Warning Signs (Red Flags)",
                                    icon = Icons.Default.Warning,
                                    color = CyberRed,
                                    lang = lang
                                )
                            }
                            itemsIndexed(content.warningSigns) { index, sign ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(CyberRed.copy(alpha = 0.08f))
                                        .border(1.dp, CyberRed.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(CyberRed),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}",
                                            color = Color.White,
                                            fontSize = academyTextSize(lang, 11),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = sign,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = academyTextSize(lang, 13),
                                        lineHeight = academyTextSize(lang, 20),
                                        fontFamily = Strings.getFontFamily(lang)
                                    )
                                }
                            }
                        }

                        // Protection Steps
                        if (content.protectionSteps.isNotEmpty()) {
                            item {
                                SectionHeader(
                                    title = if (lang == Language.AR) "خطوات الحماية والوقاية" else "Protection & Prevention Steps",
                                    icon = Icons.Default.Shield,
                                    color = CyberGreen,
                                    lang = lang
                                )
                            }
                            itemsIndexed(content.protectionSteps) { _, step ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(CyberGreen.copy(alpha = 0.08f))
                                        .border(1.dp, CyberGreen.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = CyberGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = step,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = academyTextSize(lang, 13),
                                        lineHeight = academyTextSize(lang, 20),
                                        fontFamily = Strings.getFontFamily(lang)
                                    )
                                }
                            }
                        }

                        // Recovery Advice
                        if (!content.recoveryAction.isNullOrBlank()) {
                            item {
                                SectionHeader(
                                    title = if (lang == Language.AR) "ماذا تفعل لو وقعت في الفخ؟" else "What If You Fell For The Scam?",
                                    icon = Icons.Default.HelpOutline,
                                    color = CyberYellow,
                                    lang = lang
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.76f))
                                ) {
                                    Text(
                                        text = content.recoveryAction,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = academyTextSize(lang, 13),
                                        lineHeight = academyTextSize(lang, 22),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, CyberYellow.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                            .padding(14.dp),
                                        fontFamily = Strings.getFontFamily(lang)
                                    )
                                }
                            }
                        }

                        // Summary
                        if (!content.summary.isNullOrBlank()) {
                            item {
                                SectionHeader(
                                    title = if (lang == Language.AR) "خلاصة الدرس" else "Lesson Key Takeaways",
                                    icon = Icons.Default.BookmarkBorder,
                                    color = CyberPink,
                                    lang = lang
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = content.summary,
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = academyTextSize(lang, 14),
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = academyTextSize(lang, 22),
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }
                        }
                    }
                }

                // Floating Bottom Completion Action
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(CyberCard.copy(alpha = 0.96f), CyberDarkBg.copy(alpha = 0.98f))
                            )
                        )
                        .border(
                            1.dp,
                            androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(Color.White.copy(alpha = 0.26f), CyberPink.copy(alpha = 0.28f), CyberBorder)
                            ),
                            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        )
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AnimatedVisibility(visible = completionMessage != null) {
                            Text(
                                text = completionMessage ?: "",
                                color = CyberGreen,
                                fontSize = academyTextSize(lang, 13),
                                fontWeight = FontWeight.Bold,
                                fontFamily = Strings.getFontFamily(lang)
                            )
                        }

                        if (art.isCompleted) {
                            Button(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    onClose()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCard2)
                            ) {
                                Text(
                                    text = if (lang == Language.AR) "تم إكمال هذا المقال سابقاً • إغلاق" else "Article Already Completed • Close",
                                    color = Color.White,
                                    fontSize = academyTextSize(lang, 14),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }
                        } else {
                            val buttonEnabled = hasReachedEnd && !isCompleting
                            Button(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    isCompleting = true
                                    coroutineScope.launch {
                                        val res = repository.completeArticle(art.id, art.trackId)
                                        if (res.isSuccess) {
                                            article = article?.copy(isCompleted = true)
                                            if (res.getOrNull() == true) {
                                                SoundManager.playArticleComplete()
                                                completionMessage = if (lang == Language.AR)
                                                    "أحسنت! تم إكمال المقال وإضافة ${art.xpReward} XP"
                                                else
                                                    "Well done! Article completed and ${art.xpReward} XP awarded"
                                                onArticleCompleted(art.id, art.xpReward)
                                            } else {
                                                completionMessage = if (lang == Language.AR)
                                                    "هذا المقال مكتمل بالفعل؛ لم تُضف نقاط مكررة."
                                                else
                                                    "This article is already completed; no duplicate XP was awarded."
                                            }
                                        } else {
                                            completionMessage = if (lang == Language.AR)
                                                "تم حفظ إكمال المقال محلياً وسيتم المزامنة تلقائياً."
                                            else
                                                "Saved completion offline; syncing in background."
                                        }
                                        isCompleting = false
                                    }
                                },
                                enabled = buttonEnabled,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("complete_article_button"),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.22f)),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CyberPink.copy(alpha = 0.88f),
                                    disabledContainerColor = CyberCard2.copy(alpha = 0.92f)
                                )
                            ) {
                                if (isCompleting) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                                        Text(
                                            text = if (hasReachedEnd) {
                                                if (lang == Language.AR) "فهمت الدرس وأكملت القراءة (+5 XP)" else "Completed & Understood (+5 XP)"
                                            } else {
                                                if (lang == Language.AR) "مرّر للأسفل لإكمال الدرس" else "Scroll down to finish reading"
                                            },
                                            color = if (hasReachedEnd) Color.White else CyberMuted,
                                            fontSize = academyTextSize(lang, 14),
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
}

@Composable
private fun SectionHeader(title: String, icon: ImageVector, color: Color, lang: Language) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Text(
            text = title,
            color = color,
            fontSize = academyTextSize(lang, 15),
            fontWeight = FontWeight.Bold,
            fontFamily = Strings.getFontFamily(lang)
        )
    }
}
