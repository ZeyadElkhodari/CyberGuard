package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Strings
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    stats: UserStats,
    activities: List<SecurityActivity>,
    dynamicAssessment: DynamicSecurityAssessment,
    lang: Language,
    onOpenTool: (String) -> Unit,
    userProfile: UserProfile = UserProfile(),
    modifier: Modifier = Modifier
) {
    var showBreakdown by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Top Bar: User Profile & Actions
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            com.example.sound.SoundManager.playButtonClick()
                            onOpenTool("edit_profile")
                        }
                        .testTag("dashboard_profile_avatar"),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    UserProfileAvatar(
                        profile = userProfile,
                        size = 46.dp,
                        borderColor = CyberPink
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = Strings.greetingPrefixOnly(userProfile.preferredAddress, lang),
                        color = CyberMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = userProfile.name.ifBlank { Strings.userName(lang) },
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                RoundIconButton(
                    icon = Icons.Filled.QrCodeScanner,
                    onClick = { onOpenTool("qr") },
                    contentDescription = "Scan QR",
                    testTag = "dashboard_qr_button"
                )
                Spacer(Modifier.width(8.dp))
                RoundIconButton(
                    icon = Icons.Filled.NotificationsNone,
                    onClick = { onOpenTool("alerts") },
                    contentDescription = "Notifications",
                    hasBadge = true,
                    testTag = "dashboard_notifications_button"
                )
            }
        }

        // 2. ScoreCard: Real Data Assessment Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF421321),
                                Color(0xFF19131C),
                                Color(0xFF101017)
                            )
                        )
                    )
                    .border(1.dp, CyberPink.copy(alpha = 0.25f), RoundedCornerShape(26.dp))
                    .padding(20.dp)
                    .testTag("score_card")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.securityScore(lang),
                        color = CyberMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = if (lang == Language.AR) 0.sp else 0.5.sp
                    )
                    CyberTag(
                        text = "${dynamicAssessment.assessedPillarsCount}/${dynamicAssessment.totalPillarsCount} Checks",
                        color = dynamicAssessment.ratingColor
                    )
                }

                Spacer(Modifier.height(14.dp))

                // Score + Gauge Row: Gauge on Left, [Score % -> Rating -> Points] on Right
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Left: Circular Gauge
                    ScoreCircularGauge(
                        score = dynamicAssessment.compositeScore,
                        ratingColor = dynamicAssessment.ratingColor
                    )

                    Spacer(Modifier.width(18.dp))

                    // Right Side: Clear Visual Hierarchy [Percentage -> Rating -> Points]
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .testTag("security_indicator_group"),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // 1. Percentage (%) Number on the RIGHT
                        Text(
                            text = "${dynamicAssessment.compositeScore}%",
                            color = Color.White,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = Strings.getFontFamily(lang),
                            lineHeight = 46.sp,
                            modifier = Modifier.testTag("security_indicator_percentage")
                        )

                        Spacer(Modifier.height(4.dp))

                        // 2. Verbal Security Rating directly BELOW percentage
                        val verbalRating = when {
                            dynamicAssessment.compositeScore >= 90 -> Strings.ratingExcellent(lang)
                            dynamicAssessment.compositeScore >= 75 -> Strings.ratingGood(lang)
                            dynamicAssessment.compositeScore >= 55 -> Strings.ratingModerate(lang)
                            dynamicAssessment.compositeScore >= 35 -> Strings.ratingWeak(lang)
                            else -> Strings.ratingCritical(lang)
                        }
                        Text(
                            text = verbalRating,
                            color = dynamicAssessment.ratingColor,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Strings.getFontFamily(lang),
                            lineHeight = 22.sp,
                            modifier = Modifier.testTag("security_indicator_rating")
                        )

                        Spacer(Modifier.height(4.dp))

                        // 3. Points Description (النقاط) clearly BELOW rating
                        Text(
                            text = Strings.pointsEarned(lang, dynamicAssessment.earnedPoints, dynamicAssessment.maxAssessedPoints),
                            color = CyberMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Strings.getFontFamily(lang),
                            lineHeight = 16.sp,
                            modifier = Modifier.testTag("security_indicator_points")
                        )
                    }
                }


                Spacer(Modifier.height(16.dp))

                // Meaningful Assessment Summary Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .border(1.dp, dynamicAssessment.ratingColor.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                tint = dynamicAssessment.ratingColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = Strings.assessmentCardTitle(lang),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        val adviceText = when {
                            dynamicAssessment.compositeScore >= 90 -> Strings.assessmentAdviceExcellent(lang)
                            dynamicAssessment.compositeScore >= 75 -> Strings.assessmentAdviceGood(lang)
                            dynamicAssessment.compositeScore >= 55 -> Strings.assessmentAdviceModerate(lang)
                            dynamicAssessment.compositeScore >= 35 -> Strings.assessmentAdviceWeak(lang)
                            else -> Strings.assessmentAdviceCritical(lang)
                        }
                        Text(
                            text = adviceText,
                            color = CyberMuted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // 3 Stats (Threats Blocked, Scanned Content, Scans Done)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatItemCard(
                        label = Strings.threatsBlocked(lang),
                        value = "${dynamicAssessment.threatsBlockedCount}",
                        subText = Strings.activeIntercepts(lang),
                        modifier = Modifier.weight(1f),
                        accentColor = CyberRed
                    )
                    StatItemCard(
                        label = Strings.scannedContent(lang),
                        value = "${dynamicAssessment.scannedContentCount}",
                        subText = Strings.itemsAudited(lang),
                        modifier = Modifier.weight(1f),
                        accentColor = CyberBlue
                    )
                    StatItemCard(
                        label = Strings.scansDone(lang),
                        value = "${dynamicAssessment.scansDoneCount}",
                        subText = Strings.realChecks(lang),
                        modifier = Modifier.weight(1f),
                        accentColor = CyberPink
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Breakdown Accordion Toggle
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .clickable { showBreakdown = !showBreakdown }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = CyberPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (lang == Language.AR) "تفاصيل نقاط ومحاور الفحص" else "View Score Breakdown",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Icon(
                            imageVector = if (showBreakdown) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = CyberMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                AnimatedVisibility(visible = showBreakdown) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        dynamicAssessment.pillars.forEach { pillar ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = if (pillar.status == "assessed") Icons.Filled.CheckCircle else Icons.Filled.HourglassEmpty,
                                        contentDescription = null,
                                        tint = if (pillar.status == "assessed") CyberGreen else CyberYellow,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = pillar.title,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = pillar.summary,
                                            color = CyberMuted,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                                Text(
                                    text = "${pillar.score}/${pillar.maxScore} pts",
                                    color = if (pillar.score > 0) CyberGreen else CyberMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Quick Access: 2x2 Grid of Feature Cards
        item {
            SectionTitleBar(
                title = Strings.quickAccess(lang),
                action = Strings.seeAll(lang),
                onActionClick = { onOpenTool("scan") }
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Swapped visual positions: Password Check first, Link Scanner second.
                    QuickAccessCard(
                        title = Strings.passwordCheck(lang),
                        sub = Strings.passwordCheckDesc(lang),
                        icon = Icons.Filled.Lock,
                        color = CyberBlue,
                        onClick = { onOpenTool("password") },
                        modifier = Modifier.weight(1f),
                        testTag = "quick_card_password"
                    )
                    QuickAccessCard(
                        title = Strings.linkScanner(lang),
                        sub = Strings.linkScannerDesc(lang),
                        icon = Icons.Filled.Link,
                        color = CyberRed,
                        onClick = { onOpenTool("link") },
                        modifier = Modifier.weight(1f),
                        testTag = "quick_card_link"
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    QuickAccessCard(
                        title = Strings.phishingMessageCheck(lang),
                        sub = Strings.phishingMessageDesc(lang),
                        icon = Icons.Filled.Mail,
                        color = CyberPurple,
                        onClick = { onOpenTool("phishing-msg") },
                        modifier = Modifier.weight(1f),
                        testTag = "quick_card_phishing_message"
                    )
                    QuickAccessCard(
                        title = Strings.wifiSafety(lang),
                        sub = Strings.wifiSafetyDesc(lang),
                        icon = Icons.Filled.Wifi,
                        color = CyberGreen,
                        onClick = { onOpenTool("wifi") },
                        modifier = Modifier.weight(1f),
                        testTag = "quick_card_wifi_safety"
                    )
                }
            }
        }

        // 4. Your Security Activity
        item {
            SectionTitleBar(
                title = Strings.securityActivity(lang),
                action = Strings.viewReport(lang),
                onActionClick = { onOpenTool("alerts") }
            )
        }

        item {
            GlassCard {
                if (activities.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = null,
                                tint = CyberMuted,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = if (lang == Language.AR) "لا توجد سجلات أمان مسجلة بعد" else "No security activity recorded yet",
                                color = CyberMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    activities.take(5).forEachIndexed { index, activity ->
                        val icon = when (activity.type) {
                            "password" -> Icons.Filled.Lock
                            "link" -> Icons.Filled.Link
                            "wifi" -> Icons.Filled.Wifi
                            "qr" -> Icons.Filled.QrCodeScanner
                            "phishing-message" -> Icons.Filled.Mail
                            else -> Icons.Filled.School
                        }
                        val color = when (activity.status) {
                            "safe" -> CyberGreen
                            "warning" -> CyberYellow
                            "critical" -> CyberRed
                            else -> CyberPurple
                        }
                        ActivityItemRow(
                            icon = icon,
                            title = com.example.localization.ActivityLocalization.title(activity, lang),
                            detail = com.example.localization.ActivityLocalization.detail(activity, lang),
                            time = activity.time,
                            color = color
                        )
                        if (index < activities.size - 1) {
                            HorizontalDivider(
                                color = Color.White.copy(alpha = 0.06f),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // 5. Recommended section restored above the Academy shortcut.
        item {
            SectionTitleBar(title = Strings.recommended(lang))
        }

        // Academy shortcut — same featured-card layout, now opens the Academy tab.
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF4A101D),
                                Color(0xFF24121A),
                                Color(0xFF111116)
                            )
                        )
                    )
                    .border(1.dp, CyberRed.copy(alpha = 0.55f), RoundedCornerShape(22.dp))
                    .clickable {
                        com.example.sound.SoundManager.playButtonClick()
                        onOpenTool("academy")
                    }
                    .padding(18.dp)
                    .testTag("academy_shortcut_card"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    CyberTag(
                        text = Strings.academyShortcutTag(lang),
                        color = CyberRed
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = Strings.academyShortcutTitle(lang),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = Strings.academyShortcutDesc(lang),
                        color = CyberMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
                Spacer(Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(76.dp, 86.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(Color(0xFFFF3158), Color(0xFF3A0D1B))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.School,
                        contentDescription = Strings.academyShortcutTitle(lang),
                        tint = Color(0xFFFFB0C0),
                        modifier = Modifier.size(42.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickAccessCard(
    title: String,
    sub: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = 142.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        color.copy(alpha = 0.22f),
                        CyberCard,
                        Color(0xFF101012)
                    )
                )
            )
            .border(1.dp, color.copy(alpha = 0.28f), RoundedCornerShape(22.dp))
            .clickable {
                com.example.sound.SoundManager.playButtonClick()
                onClick()
            }
            .padding(14.dp)
            .testTag(testTag),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.45f))
                .border(1.dp, color.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 18.sp
            )
            Text(
                text = sub,
                color = CyberMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
