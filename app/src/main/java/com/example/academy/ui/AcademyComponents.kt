package com.example.academy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Strings
import com.example.model.Language
import com.example.ui.theme.*


/** Keeps English Academy typography visually aligned with the Arabic Academy. */
fun academyTextSize(lang: Language, size: Int): androidx.compose.ui.unit.TextUnit =
    (if (lang == Language.EN) size * 0.90f else size.toFloat()).sp

@Composable
fun AcademyLevelBadge(
    level: Int,
    rankTitle: String,
    modifier: Modifier = Modifier,
    lang: Language = Language.AR
) {
    val levelColor = when (level) {
        1 -> CyberMuted
        2 -> CyberGreen
        3 -> CyberBlue
        4 -> CyberPurple
        5 -> CyberPink
        else -> CyberYellow
    }

    val label = if (lang == Language.AR) "المستوى $level • $rankTitle" else "Level $level • $rankTitle"

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(levelColor.copy(alpha = 0.15f))
            .border(1.dp, levelColor.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = levelColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            maxLines = 1,
            color = levelColor,
            fontSize = academyTextSize(lang, 12),
            fontWeight = FontWeight.Bold,
            fontFamily = Strings.getFontFamily(lang)
        )
    }
}

@Composable
fun AcademyStatusChip(
    status: String, // "not_started", "in_progress", "completed"
    modifier: Modifier = Modifier,
    lang: Language = Language.AR
) {
    val (bgColor, textColor, label) = when (status) {
        "completed" -> Triple(
            CyberGreen.copy(alpha = 0.15f),
            CyberGreen,
            if (lang == Language.AR) "مكتمل" else "Completed"
        )
        "in_progress" -> Triple(
            CyberBlue.copy(alpha = 0.15f),
            CyberBlue,
            if (lang == Language.AR) "قيد التقدم" else "In Progress"
        )
        else -> Triple(
            Color.White.copy(alpha = 0.08f),
            CyberMuted,
            if (lang == Language.AR) "لم يبدأ" else "Not Started"
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = academyTextSize(lang, 11),
            fontWeight = FontWeight.SemiBold,
            fontFamily = Strings.getFontFamily(lang)
        )
    }
}

@Composable
fun AcademyStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    lang: Language = Language.AR,
    titleFontSize: Int? = null
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.76f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = CyberMuted,
                    modifier = Modifier.weight(1f),
                    // A very small typography adjustment keeps the full label beside its icon.
                    fontSize = academyTextSize(lang, titleFontSize ?: if (lang == Language.EN) 9 else 11),
                    fontFamily = Strings.getFontFamily(lang),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Clip
                )
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Text(
                text = value,
                color = Color.White,
                fontSize = academyTextSize(lang, 20),
                fontWeight = FontWeight.ExtraBold,
                fontFamily = Strings.getFontFamily(lang)
            )

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = color.copy(alpha = 0.8f),
                    fontSize = academyTextSize(lang, 10),
                    fontFamily = Strings.getFontFamily(lang)
                )
            }
        }
    }
}

fun resolveAcademyIcon(name: String): ImageVector {
    return when (name.lowercase()) {
        "phishing" -> Icons.Default.Email
        "psychology" -> Icons.Default.Psychology
        "fraud", "scams" -> Icons.Default.Warning
        "pyramid", "groups" -> Icons.Default.Groups
        "privacy", "lock" -> Icons.Default.Lock
        "share", "social" -> Icons.Default.Share
        "wifi" -> Icons.Default.Wifi
        "password", "key" -> Icons.Default.VpnKey
        "work", "jobs" -> Icons.Default.Work
        "gift", "card_giftcard" -> Icons.Default.CardGiftcard
        "call", "phone" -> Icons.Default.Call
        "wallet", "account_balance_wallet" -> Icons.Default.AccountBalanceWallet
        "invest", "trending_up" -> Icons.Default.TrendingUp
        else -> Icons.Default.School
    }
}
