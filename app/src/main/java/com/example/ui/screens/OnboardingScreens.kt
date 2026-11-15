package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Strings
import com.example.model.Language
import com.example.sound.SoundManager
import com.example.ui.components.NeonButton
import com.example.ui.theme.*
import com.example.util.BidiHelper
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreens(
    lang: Language,
    onToggleLang: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = lang == Language.AR
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 })

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF491027),
                        Color(0xFF160A12),
                        CyberDarkBg
                    ),
                    radius = 1200f
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .testTag("onboarding_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Header: Brand Tag + Language Toggle Pill + Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Brand tag
                Text(
                    text = Strings.appName(lang),
                    color = CyberPink,
                    letterSpacing = if (isAr) 0.sp else 2.sp,
                    fontFamily = Strings.getFontFamily(lang),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Language Switcher Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CyberCard2)
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .clickable {
                                SoundManager.playButtonClick()
                                onToggleLang()
                            }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("onboarding_lang_toggle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Language,
                                contentDescription = "Language",
                                tint = CyberPink,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = if (isAr) "EN" else "عربي",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }


                }
            }

            Spacer(Modifier.height(8.dp))

            // 3-Page Horizontal Swipeable Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                when (page) {
                    0 -> OnboardingPageContent(
                        title = if (isAr) "افهم المخاطر قبل أن تتفاعل معها" else "Protect Your Digital Life with Smart Security",
                        subtitle = if (isAr)
                            "افحص الروابط، راجع قوة كلمات المرور ومؤشرات التسريب، وتعرّف على إعدادات أمان شبكات Wi-Fi — مع نتائج واضحة وحدود معروفة."
                        else
                            "Assess suspicious links, review password strength and breach indicators, and understand Wi-Fi security signals — with clear results and limitations.",
                        badgeText = if (isAr) "وعي أمني يبدأ بخطوة" else "Practical Digital Awareness",
                        icon = Icons.Filled.Shield,
                        accentColor = CyberPink,
                        features = listOf(
                            Pair(Icons.Filled.Link, if (isAr) "فحص الروابط المشبوهة والاحتيالية" else "Suspicious Link Assessment"),
                            Pair(Icons.Filled.VpnKey, if (isAr) "التحقق من التسريبات بطريقة k-Anonymity" else "Password Breach Lookup with k-Anonymity"),
                            Pair(Icons.Filled.Wifi, if (isAr) "مراجعة مؤشرات أمان Wi-Fi" else "Wi-Fi Security Assessment")
                        ),
                        lang = lang
                    )
                    1 -> OnboardingPageContent(
                        title = if (isAr) "التوعية بالأمن السيبراني" else "Cybersecurity Awareness",
                        subtitle = if (isAr)
                            "أكاديمية تفاعلية شاملة تضم 13 مساراً تعليمياً وتحديات يومية واقعية لتأهيلك لكشف أحدث أساليب الاحتيال والهندسة الاجتماعية."
                        else
                            "Explore guided learning tracks, practical articles, and quizzes that build awareness of phishing, scams, and social engineering.",
                        badgeText = if (isAr) "أكاديمية سايبر جارد" else "CyberGuard Academy",
                        icon = Icons.Filled.School,
                        accentColor = CyberGreen,
                        features = listOf(
                            Pair(Icons.Filled.MenuBook, if (isAr) "13 مساراً و130 مقالاً تدريبياً واقعياً" else "13 Tracks & 130 Real-World Lessons"),
                            Pair(Icons.Filled.Quiz, if (isAr) "اختبارات تفاعلية بنسبة اجتياز 75%" else "Interactive Quizzes with 75% Pass Mark"),
                            Pair(Icons.Filled.LocalFireDepartment, if (isAr) "تحديات يومية لتعزيز الوعي الأمني" else "Daily Security Learning Challenges")
                        ),
                        lang = lang
                    )
                    else -> OnboardingPageContent(
                        title = if (isAr) "استكشف إمكانيات سايبر جارد" else "Explore CyberGuard's Capabilities",
                        subtitle = if (isAr)
                            "افحص محتوى رموز QR، وراجع مؤشرات الأمان، وتابع تقدمك التعليمي عبر نقاط XP والإنجازات والملف التعليمي."
                        else
                            "Scan QR links, review security indicators, and follow your learning progress through XP, achievements, and the Learning Profile.",
                        badgeText = if (isAr) "تعلم وتابع تقدمك" else "Learn & Track Progress",
                        icon = Icons.Filled.AutoGraph,
                        accentColor = CyberBlue,
                        features = listOf(
                            Pair(Icons.Filled.Speed, if (isAr) "مراجعة مؤشرات الأمان المتاحة" else "Review Available Security Indicators"),
                            Pair(Icons.Filled.NotificationsActive, if (isAr) "تنبيهات وإشعارات حسب إعدادات التطبيق" else "App Alerts & Notifications"),
                            Pair(Icons.Filled.EmojiEvents, if (isAr) "أوسمة إنجاز ومكافآت XP تصاعدية" else "Achievement Badges & Level Milestones")
                        ),
                        lang = lang
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // 3-Dot Pagination Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 14.dp)
            ) {
                repeat(3) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .width(if (isSelected) 28.dp else 8.dp)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) CyberPink else CyberMuted.copy(alpha = 0.35f))
                            .clickable {
                                SoundManager.playButtonClick()
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }

            // Single, focused onboarding action inspired by the reference design.
            NeonButton(
                text = if (pagerState.currentPage < 2) Strings.next(lang) else Strings.getStarted(lang),
                onClick = {
                    SoundManager.playButtonClick()
                    if (pagerState.currentPage < 2) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onFinish()
                    }
                },
                icon = if (pagerState.currentPage < 2) Icons.AutoMirrored.Filled.ArrowForward else Icons.Filled.Shield,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
                    .height(54.dp),
                testTag = if (pagerState.currentPage < 2) "onboarding_next_button" else "onboarding_get_started_button"
            )
        }
    }
}

@Composable
private fun OnboardingPageContent(
    title: String,
    subtitle: String,
    badgeText: String,
    icon: ImageVector,
    accentColor: Color,
    features: List<Pair<ImageVector, String>>,
    lang: Language,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Glowing Icon Hero Visual
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.12f))
                .border(2.dp, accentColor.copy(alpha = 0.45f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Badge pill
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(accentColor.copy(alpha = 0.15f))
                .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            Text(
                text = BidiHelper.formatBiDi(badgeText, isAr = lang == Language.AR),
                color = accentColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Strings.getFontFamily(lang),
                style = LocalTextStyle.current.copy(
                    textDirection = if (lang == Language.AR) TextDirection.ContentOrRtl else TextDirection.ContentOrLtr
                )
            )
        }

        Spacer(Modifier.height(14.dp))

        // Title
        Text(
            text = BidiHelper.formatBiDi(title, isAr = lang == Language.AR),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = 30.sp,
            fontFamily = Strings.getFontFamily(lang),
            style = LocalTextStyle.current.copy(
                textDirection = if (lang == Language.AR) TextDirection.ContentOrRtl else TextDirection.ContentOrLtr
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(Modifier.height(8.dp))

        // Subtitle description
        Text(
            text = BidiHelper.formatBiDi(subtitle, isAr = lang == Language.AR),
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 13.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center,
            fontFamily = Strings.getFontFamily(lang),
            style = LocalTextStyle.current.copy(
                textDirection = if (lang == Language.AR) TextDirection.ContentOrRtl else TextDirection.ContentOrLtr
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(Modifier.height(20.dp))

        // Feature checklist pills
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            features.forEach { (featIcon, featText) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(CyberCard.copy(alpha = 0.78f), CyberCard.copy(alpha = 0.56f))
                            )
                        )
                        .border(
                            1.dp,
                            Brush.linearGradient(listOf(Color.White.copy(alpha = 0.24f), accentColor.copy(alpha = 0.16f), Color.White.copy(alpha = 0.06f))),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = featIcon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = BidiHelper.formatBiDi(featText, isAr = lang == Language.AR),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = Strings.getFontFamily(lang),
                        style = LocalTextStyle.current.copy(
                            textDirection = if (lang == Language.AR) TextDirection.ContentOrRtl else TextDirection.ContentOrLtr
                        )
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
    }
}
