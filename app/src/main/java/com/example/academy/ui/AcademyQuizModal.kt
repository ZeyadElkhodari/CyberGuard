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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import kotlinx.coroutines.launch

@Composable
fun AcademyQuizModal(
    isOpen: Boolean,
    trackId: String?,
    onClose: () -> Unit,
    onQuizSubmitted: (scorePercent: Int, passed: Boolean, xpGained: Int) -> Unit,
    lang: Language = Language.AR
) {
    if (!isOpen || trackId == null) return

    val context = LocalContext.current
    val repository = remember { AcademyRepository.getInstance(context) }
    val coroutineScope = rememberCoroutineScope()

    var quizDto by remember { mutableStateOf<AcademyQuizDto?>(null) }
    var questions by remember { mutableStateOf<List<AcademyQuestionDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Quiz taking state
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val selectedAnswers = remember { mutableStateMapOf<String, Int>() } // questionId -> optionIndex

    // Submission result state
    var isSubmitting by remember { mutableStateOf(false) }
    var submitResult by remember { mutableStateOf<AcademyQuizSubmitResponse?>(null) }
    var isReviewingFeedback by remember { mutableStateOf(false) }

    LaunchedEffect(trackId) {
        isLoading = true
        errorMessage = null
        var result = repository.refreshQuiz(trackId)
        if (result.isFailure || result.getOrNull()?.questions.isNullOrEmpty()) {
            repository.seedFromAssetsIfEmpty(context)
            result = repository.refreshQuiz(trackId)
        }
        if (result.isSuccess && !result.getOrNull()?.questions.isNullOrEmpty()) {
            val body = result.getOrNull()
            quizDto = body?.quiz
            questions = body?.questions ?: emptyList()
        } else {
            errorMessage = if (lang == Language.AR)
                "تعذر تحميل أسئلة الاختبار، يرجى المحاولة لاحقاً."
            else
                "Unable to load quiz questions, please try again."
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
                .testTag("academy_quiz_modal")
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = CyberPink)
                }
            } else if (errorMessage != null || questions.isEmpty()) {
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
                        text = errorMessage ?: (if (lang == Language.AR) "لا توجد أسئلة متاحة لهذا المسار حالياً" else "No questions available for this track"),
                        color = Color.White,
                        fontSize = academyTextSize(lang, 16),
                        textAlign = TextAlign.Center,
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
                        Text(AcademyTranslations.closeAction(lang), color = Color.White, fontFamily = Strings.getFontFamily(lang))
                    }
                }
            } else if (submitResult != null) {
                // Quiz Results & Feedback Screen
                val res = submitResult!!
                val isPassed = res.passed

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isReviewingFeedback) {
                                if (lang == Language.AR) "مراجعة إجابات الاختبار" else "Review Quiz Answers"
                            } else {
                                if (lang == Language.AR) "نتيجة الاختبار النهائي" else "Final Quiz Result"
                            },
                            color = Color.White,
                            fontSize = academyTextSize(lang, 18),
                            fontWeight = FontWeight.Bold,
                            fontFamily = Strings.getFontFamily(lang)
                        )

                        IconButton(
                            onClick = {
                                SoundManager.playButtonClick()
                                if (isReviewingFeedback) {
                                    isReviewingFeedback = false
                                } else {
                                    onClose()
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.08f))
                        ) {
                            Icon(
                                imageVector = if (isReviewingFeedback) Icons.AutoMirrored.Filled.ArrowBack else Icons.Default.Close,
                                contentDescription = AcademyTranslations.closeAction(lang),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isReviewingFeedback) {
                        // Main Score Summary Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.76f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        2.dp,
                                        if (isPassed) CyberGreen else CyberRed,
                                        RoundedCornerShape(20.dp)
                                    )
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (isPassed) CyberGreen else CyberRed,
                                    modifier = Modifier.size(64.dp)
                                )

                                Text(
                                    text = if (isPassed) {
                                        if (lang == Language.AR) "مبارك! لقد اجتزت الاختبار بنجاح" else "Congratulations! You Passed!"
                                    } else {
                                        if (lang == Language.AR) "للأسف، لم تحقق نسبة الاجتياز المطلوبة" else "You did not reach the passing score"
                                    },
                                    color = Color.White,
                                    fontSize = academyTextSize(lang, 18),
                                    fontWeight = FontWeight.ExtraBold,
                                    textAlign = TextAlign.Center,
                                    fontFamily = Strings.getFontFamily(lang)
                                )

                                Text(
                                    text = if (lang == Language.AR)
                                        "نسبة النجاح المطلوبة: 75% • درجتك: ${res.scorePercent}%"
                                    else
                                        "Passing Score: 75% • Your Score: ${res.scorePercent}%",
                                    color = if (isPassed) CyberGreen else CyberRed,
                                    fontSize = academyTextSize(lang, 16),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Strings.getFontFamily(lang)
                                )

                                Text(
                                    text = if (lang == Language.AR)
                                        "أجبت بشكل صحيح على ${res.correctCount} من أصل ${res.totalQuestions} سؤالاً"
                                    else
                                        "You answered ${res.correctCount} of ${res.totalQuestions} questions correctly",
                                    color = CyberMuted,
                                    fontSize = academyTextSize(lang, 13),
                                    fontFamily = Strings.getFontFamily(lang)
                                )

                                if (res.totalXpGained > 0) {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(CyberPink.copy(alpha = 0.15f))
                                            .border(1.dp, CyberPink.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                            .padding(horizontal = 14.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = CyberPink)
                                        Text(
                                            text = if (lang == Language.AR) "مكافأة مؤكدة: +${res.totalXpGained} XP" else "Confirmed Reward: +${res.totalXpGained} XP",
                                            color = CyberPink,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = Strings.getFontFamily(lang)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Actions
                        Button(
                            onClick = {
                                SoundManager.playButtonClick()
                                isReviewingFeedback = true
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberBlue)
                        ) {
                            Text(
                                text = if (lang == Language.AR) "مراجعة الإجابات والشروحات" else "Review Answers & Explanations",
                                fontWeight = FontWeight.Bold,
                                fontFamily = Strings.getFontFamily(lang)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (!isPassed) {
                            Button(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    submitResult = null
                                    currentQuestionIndex = 0
                                    selectedAnswers.clear()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCard2)
                            ) {
                                Text(
                                    text = if (lang == Language.AR) "إعادة المحاولة الآن" else "Try Again Now",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    onClose()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberGreen)
                            ) {
                                Text(
                                    text = if (lang == Language.AR) "إتمام وإنهاء" else "Finish & Return",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }
                        }
                    } else {
                        // Detailed question review list
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            itemsIndexed(res.questionResults) { index, qr ->
                                val questionText = if (lang == Language.AR) qr.questionTextAr else AcademyTranslations.translateSentence(qr.questionTextAr)
                                val explanation = if (lang == Language.AR) qr.explanationAr else AcademyTranslations.translateSentence(qr.explanationAr)
                                val options = qr.options.map { if (lang == Language.AR) it else AcademyTranslations.translateSentence(it) }

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.76f))
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(
                                                1.dp,
                                                if (qr.isCorrect) CyberGreen.copy(alpha = 0.4f) else CyberRed.copy(alpha = 0.4f),
                                                RoundedCornerShape(14.dp)
                                            )
                                            .padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = if (lang == Language.AR) "سؤال ${index + 1}" else "Question ${index + 1}",
                                                color = if (qr.isCorrect) CyberGreen else CyberRed,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = academyTextSize(lang, 12),
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                            Text(
                                                text = if (qr.isCorrect) {
                                                    if (lang == Language.AR) "إجابة صحيحة ✓" else "Correct ✓"
                                                } else {
                                                    if (lang == Language.AR) "إجابة خاطئة ✗" else "Incorrect ✗"
                                                },
                                                color = if (qr.isCorrect) CyberGreen else CyberRed,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = academyTextSize(lang, 12),
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                        }

                                        Text(
                                            text = questionText,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = academyTextSize(lang, 14),
                                            fontFamily = Strings.getFontFamily(lang)
                                        )

                                        if (qr.selectedOptionIndex >= 0 && qr.selectedOptionIndex < options.size) {
                                            Text(
                                                text = if (lang == Language.AR) "إجابتك: ${options[qr.selectedOptionIndex]}" else "Your answer: ${options[qr.selectedOptionIndex]}",
                                                color = if (qr.isCorrect) CyberGreen else CyberRed,
                                                fontSize = academyTextSize(lang, 13),
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                        }

                                        if (!qr.isCorrect && qr.correctOptionIndex >= 0 && qr.correctOptionIndex < options.size) {
                                            Text(
                                                text = if (lang == Language.AR) "الإجابة الصحيحة: ${options[qr.correctOptionIndex]}" else "Correct answer: ${options[qr.correctOptionIndex]}",
                                                color = CyberGreen,
                                                fontSize = academyTextSize(lang, 13),
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                        }

                                        if (explanation.isNotBlank()) {
                                            Text(
                                                text = if (lang == Language.AR) "الشرح: $explanation" else "Explanation: $explanation",
                                                color = Color.White.copy(alpha = 0.8f),
                                                fontSize = academyTextSize(lang, 12),
                                                lineHeight = academyTextSize(lang, 18),
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                SoundManager.playButtonClick()
                                isReviewingFeedback = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCard2)
                        ) {
                            Text(if (lang == Language.AR) "العودة للنتيجة" else "Back to Results", color = Color.White, fontFamily = Strings.getFontFamily(lang))
                        }
                    }
                }
            } else {
                // Active Quiz Taking Screen
                val currentQ = questions.getOrNull(currentQuestionIndex) ?: questions.first()
                val totalQuestions = questions.size
                val selectedOption = selectedAnswers[currentQ.id]

                val localizedQuestion = remember(currentQ, lang) {
                    val englishQ = CuratedEnglishContent.getQuestion(currentQ.id)
                    if (lang == Language.EN && currentQ.questionTextEn?.isNotBlank() == true) {
                        currentQ.questionTextEn!! to (currentQ.situationEn to (currentQ.optionsEn ?: currentQ.options))
                    } else if (lang == Language.EN && englishQ != null) {
                        englishQ.questionText to (englishQ.situation to englishQ.options)
                    } else {
                        val qText = if (lang == Language.AR) currentQ.questionTextAr else AcademyTranslations.translateSentence(currentQ.questionTextAr)
                        val sText = currentQ.situationAr?.let { if (lang == Language.AR) it else AcademyTranslations.translateSentence(it) }
                        val opts = currentQ.options.map { if (lang == Language.AR) it else AcademyTranslations.translateSentence(it) }
                        qText to (sText to opts)
                    }
                }

                val displayQuestionText = localizedQuestion.first
                val displaySituation = localizedQuestion.second.first
                val displayOptions = localizedQuestion.second.second

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // Header Bar with Progress Indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (lang == Language.AR)
                                    "السؤال ${currentQuestionIndex + 1} من $totalQuestions"
                                else
                                    "Question ${currentQuestionIndex + 1} of $totalQuestions",
                                color = Color.White,
                                fontSize = academyTextSize(lang, 14),
                                fontWeight = FontWeight.Bold,
                                fontFamily = Strings.getFontFamily(lang)
                            )
                            Text(
                                text = if (lang == Language.AR) "نسبة الاجتياز: 75% (15/20)" else "Pass mark: 75% (15/20)",
                                color = CyberMuted,
                                fontSize = academyTextSize(lang, 11),
                                fontFamily = Strings.getFontFamily(lang)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberPink.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "+100 XP",
                                color = CyberPink,
                                fontSize = academyTextSize(lang, 12),
                                fontWeight = FontWeight.Bold,
                                fontFamily = Strings.getFontFamily(lang)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Linear progress indicator
                    LinearProgressIndicator(
                        progress = { (currentQuestionIndex + 1).toFloat() / totalQuestions.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CyberPink,
                        trackColor = Color.White.copy(alpha = 0.08f)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Question Card
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
                            if (!displaySituation.isNullOrBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(CyberCard2)
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = displaySituation,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = academyTextSize(lang, 13),
                                        lineHeight = academyTextSize(lang, 20),
                                        fontFamily = Strings.getFontFamily(lang)
                                    )
                                }
                            }

                            Text(
                                text = displayQuestionText,
                                color = Color.White,
                                fontSize = academyTextSize(lang, 16),
                                fontWeight = FontWeight.Bold,
                                lineHeight = academyTextSize(lang, 24),
                                fontFamily = Strings.getFontFamily(lang)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Answer Choices
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(displayOptions) { optionIndex, optionText ->
                            val isSelected = selectedOption == optionIndex
                            val borderColor = if (isSelected) CyberPink else CyberBorder
                            val containerColor = if (isSelected) CyberPink.copy(alpha = 0.15f) else CyberCard

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(containerColor)
                                    .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                                    .clickable {
                                        SoundManager.playButtonClick()
                                        selectedAnswers[currentQ.id] = optionIndex
                                    }
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        SoundManager.playButtonClick()
                                        selectedAnswers[currentQ.id] = optionIndex
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = CyberPink,
                                        unselectedColor = CyberMuted
                                    )
                                )
                                Text(
                                    text = optionText,
                                    color = Color.White,
                                    fontSize = academyTextSize(lang, 14),
                                    lineHeight = academyTextSize(lang, 20),
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }
                        }
                    }

                    // Navigation Actions (Previous / Next / Submit)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (currentQuestionIndex > 0) {
                            OutlinedButton(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    currentQuestionIndex--
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(CyberMuted))
                            ) {
                                Text(
                                    text = if (lang == Language.AR) "السابق" else "Previous",
                                    color = Color.White,
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }
                        }

                        val isLast = currentQuestionIndex == totalQuestions - 1
                        val canProceed = selectedOption != null

                        Button(
                            onClick = {
                                SoundManager.playButtonClick()
                                if (isLast) {
                                    // Submit Quiz
                                    isSubmitting = true
                                    val quizId = quizDto?.id ?: "quiz-$trackId"
                                    val answerItems = questions.map { q ->
                                        AcademyQuizSubmitAnswerItem(
                                            questionId = q.id,
                                            selectedOptionIndex = selectedAnswers[q.id] ?: -1
                                        )
                                    }

                                    coroutineScope.launch {
                                        val res = repository.submitQuiz(quizId, trackId, answerItems)
                                        if (res.isSuccess) {
                                            val body = res.getOrNull()
                                            submitResult = body
                                            if (body != null) {
                                                if (body.passed) {
                                                    SoundManager.playCorrectAnswer()
                                                    SoundManager.playQuizComplete()
                                                } else {
                                                    SoundManager.playIncorrectAnswer()
                                                }
                                                onQuizSubmitted(body.scorePercent, body.passed, body.totalXpGained)
                                            }
                                        } else {
                                            errorMessage = if (lang == Language.AR)
                                                "تم حفظ الإجابات محلياً وسيتم تقييمها وتأكيد النقاط فور استعادة الاتصال."
                                            else
                                                "Answers saved locally; results will be synchronized."
                                        }
                                        isSubmitting = false
                                    }
                                } else {
                                    currentQuestionIndex++
                                }
                            },
                            enabled = canProceed && !isSubmitting,
                            modifier = Modifier
                                .weight(if (currentQuestionIndex > 0) 2f else 1f)
                                .height(48.dp)
                                .testTag("quiz_action_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLast) CyberGreen else CyberPink,
                                disabledContainerColor = CyberCard2
                            )
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text(
                                    text = if (isLast) {
                                        if (lang == Language.AR) "إرسال الإجابات وإنهاء الاختبار" else "Submit Quiz & Finish"
                                    } else {
                                        if (lang == Language.AR) "التالي" else "Next"
                                    },
                                    color = if (canProceed) Color.White else CyberMuted,
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
