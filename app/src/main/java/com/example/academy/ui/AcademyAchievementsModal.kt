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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.academy.data.AcademyRepository
import com.example.academy.data.AcademyTranslations
import com.example.localization.Strings
import com.example.model.Language
import com.example.sound.SoundManager
import com.example.ui.theme.*

@Composable
fun AcademyAchievementsModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    lang: Language = Language.AR
) {
    if (!isOpen) return

    val context = LocalContext.current
    val repository = remember { AcademyRepository.getInstance(context) }
    val achievements by repository.getAchievementsFlow().collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        repository.refreshAchievements()
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
                .testTag("academy_achievements_modal")
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
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
                        text = if (lang == Language.AR) "الأوسمة والإنجازات" else "Badges & Achievements",
                        color = Color.White,
                        fontSize = academyTextSize(lang, 16),
                        fontWeight = FontWeight.Bold,
                        fontFamily = Strings.getFontFamily(lang)
                    )

                    val unlockedCount = achievements.count { it.isUnlocked }
                    Text(
                        text = "$unlockedCount/${achievements.size}",
                        color = CyberPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = academyTextSize(lang, 13),
                        fontFamily = Strings.getFontFamily(lang)
                    )
                }

                // Achievements List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
                ) {
                    items(achievements) { ach ->
                        val isUnlocked = ach.isUnlocked
                        val borderColor = if (isUnlocked) CyberYellow.copy(alpha = 0.5f) else CyberBorder
                        val iconBg = if (isUnlocked) CyberYellow.copy(alpha = 0.15f) else CyberCard2

                        val achTitle = if (lang == Language.EN) ach.titleEn?.takeIf { it.isNotBlank() } ?: AcademyTranslations.getAchievementTitle(ach.id, ach.titleAr, lang) else ach.titleAr
                        val achDesc = if (lang == Language.EN) ach.descriptionEn?.takeIf { it.isNotBlank() } ?: AcademyTranslations.getAchievementDescription(ach.id, ach.descriptionAr, lang) else ach.descriptionAr

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CyberCard.copy(alpha = 0.76f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(iconBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = if (isUnlocked) CyberYellow else CyberMuted,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = achTitle,
                                            modifier = Modifier.weight(1f),
                                            maxLines = 1,
                                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                            color = if (isUnlocked) Color.White else CyberMuted,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = academyTextSize(lang, 15),
                                            fontFamily = Strings.getFontFamily(lang)
                                        )

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(CyberPink.copy(alpha = 0.15f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "+${ach.xpReward} XP",
                                                color = CyberPink,
                                                fontSize = academyTextSize(lang, 11),
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = Strings.getFontFamily(lang)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = achDesc,
                                        color = Color.White.copy(alpha = 0.75f),
                                        fontSize = academyTextSize(lang, 12),
                                        lineHeight = academyTextSize(lang, 18),
                                        fontFamily = Strings.getFontFamily(lang)
                                    )

                                    if (isUnlocked && !ach.unlockedAt.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = if (lang == Language.AR) "تم الفتح: ${ach.unlockedAt.take(10)}" else "Unlocked: ${ach.unlockedAt.take(10)}",
                                            color = CyberGreen,
                                            fontSize = academyTextSize(lang, 10),
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
