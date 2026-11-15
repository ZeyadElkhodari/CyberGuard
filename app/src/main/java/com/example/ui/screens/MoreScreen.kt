package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import com.example.util.BidiHelper
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import com.example.data.SecurityData
import com.example.localization.Strings
import com.example.model.Language
import com.example.model.UserProfile
import com.example.model.UserStats
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.components.UserProfileAvatar
import com.example.ui.modals.EditProfileModal
import com.example.ui.theme.*

@Composable
fun MoreScreen(
    stats: UserStats,
    lang: Language,
    onToggleLang: () -> Unit,
    soundEnabled: Boolean,
    onToggleSound: () -> Unit,
    onResetStats: () -> Unit,
    appearance: AppAppearance = AppAppearance.CYBER_DARK,
    onSelectAppearance: (AppAppearance) -> Unit = {},
    displayMode: AppDisplayMode = AppDisplayMode.STANDARD,
    onSelectDisplayMode: (AppDisplayMode) -> Unit = {},
    userProfile: UserProfile = UserProfile(),
    onUpdateProfile: (UserProfile) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isAr = lang == Language.AR
    val context = LocalContext.current
    var activeDialog by remember { mutableStateOf<String?>(null) }
    var isEditingProfile by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp)
            .testTag("more_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Page Title & Subtitle
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = Strings.moreTitle(lang),
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = Strings.getFontFamily(lang),
                lineHeight = 34.sp,
                letterSpacing = 0.sp,
                maxLines = 1,
                softWrap = false
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = Strings.moreSub(lang),
                color = CyberMuted,
                fontSize = 13.sp,
                fontFamily = Strings.getFontFamily(lang),
                lineHeight = 20.sp
            )
        }

        // User Profile Section: Profile & Personal Information
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.profileSectionTitle(lang),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                TextButton(
                    onClick = { isEditingProfile = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    modifier = Modifier.testTag("edit_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        tint = CyberPink,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = Strings.editProfile(lang),
                        color = CyberPink,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            GlassCard(
                borderColor = CyberPink.copy(alpha = 0.25f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserProfileAvatar(
                        profile = userProfile,
                        size = 54.dp,
                        borderColor = CyberPink
                    )

                    Spacer(Modifier.width(14.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            text = userProfile.name.ifBlank { Strings.userName(lang) },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.height(5.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = Strings.profileTagline(lang),
                                color = CyberMuted,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                maxLines = 1,
                                softWrap = false,
                                modifier = Modifier.weight(1f)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberPink.copy(alpha = 0.15f))
                                    .border(0.5.dp, CyberPink.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = Strings.addressOptionLabel(userProfile.preferredAddress, lang),
                                    color = CyberPink,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = { isEditingProfile = true },
                        modifier = Modifier.testTag("edit_profile_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronRight,
                            contentDescription = "Edit Profile",
                            tint = CyberMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Settings Rows
        SettingActionRow(
            icon = Icons.Filled.Language,
            title = Strings.settingsLanguage(lang),
            actionLabel = if (lang == Language.AR) "العربية" else "English",
            isAr = isAr,
            onClick = onToggleLang,
            testTag = "setting_language_row"
        )

        SettingActionRow(
            icon = if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
            title = Strings.settingsSoundEffects(lang),
            actionLabel = if (soundEnabled) (if (isAr) "مفعل" else "Enabled") else (if (isAr) "صامت" else "Muted"),
            isAr = isAr,
            onClick = onToggleSound,
            testTag = "setting_sound_row"
        )

        // Developers Page Option
        SettingActionRow(
            icon = Icons.Filled.Groups,
            title = Strings.settingsDevelopers(lang),
            color = CyberPink,
            isAr = isAr,
            onClick = { activeDialog = "developers" },
            testTag = "setting_developers_row"
        )


        val appearanceLabel = when (appearance) {
            AppAppearance.CYBER_DARK -> if (isAr) "ليلي سيبراني" else "Cyber Dark"
            AppAppearance.OLED_BLACK -> if (isAr) "أسود OLED" else "OLED Black"
            AppAppearance.MATRIX_NAVY -> if (isAr) "أزرق ماتركس" else "Matrix Navy"
        }
        val displayModeLabel = when (displayMode) {
            AppDisplayMode.STANDARD -> if (isAr) "قياسي" else "Standard"
            AppDisplayMode.COMPACT -> if (isAr) "مدمج" else "Compact"
            AppDisplayMode.LARGE -> if (isAr) "مكبر" else "Large"
        }

        SettingActionRow(
            icon = Icons.Filled.DarkMode,
            title = Strings.settingsAppearance(lang),
            actionLabel = "$appearanceLabel • $displayModeLabel",
            isAr = isAr,
            onClick = {
                com.example.sound.SoundManager.playButtonClick()
                activeDialog = "appearance"
            },
            testTag = "setting_appearance_row"
        )

        SettingActionRow(
            icon = Icons.Filled.RestartAlt,
            title = Strings.settingsResetStats(lang),
            color = CyberRed,
            isAr = isAr,
            onClick = { activeDialog = "reset" },
            testTag = "setting_reset_row"
        )

        SettingActionRow(
            icon = Icons.Filled.HelpOutline,
            title = Strings.settingsHelp(lang),
            isAr = isAr,
            onClick = { activeDialog = "help" },
            testTag = "setting_help_row"
        )

        SettingActionRow(
            icon = Icons.Filled.Description,
            title = Strings.settingsPolicy(lang),
            isAr = isAr,
            onClick = { activeDialog = "privacy" },
            testTag = "setting_privacy_row"
        )

        Spacer(Modifier.height(8.dp))

        // Version Footer
        Text(
            text = Strings.versionInfo(lang),
            color = CyberMuted,
            fontSize = 11.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }

    // Dialogs
    when (activeDialog) {
        "appearance" -> {
            Dialog(onDismissRequest = { activeDialog = null }) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 500.dp)
                        .padding(vertical = 8.dp),
                    borderColor = CyberBlue.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Filled.DarkMode, contentDescription = null, tint = CyberBlue, modifier = Modifier.size(22.dp))
                                Text(
                                    text = if (isAr) "المظهر ووضع العرض" else "Appearance & Display Mode",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }
                            IconButton(onClick = {
                                com.example.sound.SoundManager.playButtonClick()
                                activeDialog = null
                            }) {
                                Icon(Icons.Filled.Close, contentDescription = "Close", tint = CyberMuted)
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                        // Theme Selection Section
                        Text(
                            text = if (isAr) "السمة والتباين اللوني" else "Theme & Color Contrast",
                            color = CyberBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Strings.getFontFamily(lang)
                        )

                        val themes = listOf(
                            Triple(AppAppearance.CYBER_DARK, if (isAr) "ليلي سيبراني (افتراضي)" else "Cyber Dark (Default)", if (isAr) "تصميم كحلي داكن بألوان نيون هادئة" else "Deep dark navy with neon accents"),
                            Triple(AppAppearance.OLED_BLACK, if (isAr) "أسود نقي OLED" else "Pure Black OLED", if (isAr) "تباين مطلق موفر للطاقة وشاشات الأموليد" else "High contrast pure black for AMOLED screens"),
                            Triple(AppAppearance.MATRIX_NAVY, if (isAr) "أزرق ماتركس عميق" else "Matrix Deep Navy", if (isAr) "طابع أمني كلاسيكي بأزرق ليلي مريح" else "Classic security navy with cyan accents")
                        )

                        themes.forEach { (itemApp, itemTitle, itemDesc) ->
                            val isSelected = appearance == itemApp
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) CyberBlue.copy(alpha = 0.15f) else CyberCard2)
                                    .border(1.dp, if (isSelected) CyberBlue else Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        com.example.sound.SoundManager.playButtonClick()
                                        onSelectAppearance(itemApp)
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            com.example.sound.SoundManager.playButtonClick()
                                            onSelectAppearance(itemApp)
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = CyberBlue, unselectedColor = CyberMuted)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = itemTitle, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = Strings.getFontFamily(lang))
                                        Text(text = itemDesc, color = CyberMuted, fontSize = 11.sp, fontFamily = Strings.getFontFamily(lang))
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = Color.White.copy(alpha = 0.08f))

                        // Display Mode Section
                        Text(
                            text = if (isAr) "وضع العرض وحجم الخط" else "Display Mode & Scaling",
                            color = CyberPink,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Strings.getFontFamily(lang)
                        )

                        val displayModes = listOf(
                            Triple(AppDisplayMode.STANDARD, if (isAr) "الوضع القياسي المتوازن" else "Standard Balanced View", if (isAr) "الحجم الافتراضي لجميع الهواتف" else "Default sizing optimized for all screens"),
                            Triple(AppDisplayMode.COMPACT, if (isAr) "الوضع المدمج" else "Compact View", if (isAr) "كثافة عناصر أعلى للهواتف الأصغر واللوحيات" else "Denser layout for smaller phones & tablets"),
                            Triple(AppDisplayMode.LARGE, if (isAr) "وضع القراءة المريح / خط مكبر" else "Comfortable / Large Text", if (isAr) "تكبير الخط وتحسين سهولة القراءة لكبار السن" else "Enlarged fonts and touch targets for comfort")
                        )

                        displayModes.forEach { (itemMode, itemTitle, itemDesc) ->
                            val isSelected = displayMode == itemMode
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) CyberPink.copy(alpha = 0.15f) else CyberCard2)
                                    .border(1.dp, if (isSelected) CyberPink else Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                    .clickable {
                                        com.example.sound.SoundManager.playButtonClick()
                                        onSelectDisplayMode(itemMode)
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = {
                                            com.example.sound.SoundManager.playButtonClick()
                                            onSelectDisplayMode(itemMode)
                                        },
                                        colors = RadioButtonDefaults.colors(selectedColor = CyberPink, unselectedColor = CyberMuted)
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = itemTitle, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = Strings.getFontFamily(lang))
                                        Text(text = itemDesc, color = CyberMuted, fontSize = 11.sp, fontFamily = Strings.getFontFamily(lang))
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        NeonButton(
                            text = Strings.close(lang),
                            onClick = {
                                com.example.sound.SoundManager.playButtonClick()
                                activeDialog = null
                            }
                        )
                    }
                }
            }
        }
        "developers" -> {
            Dialog(onDismissRequest = { activeDialog = null }) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.82f),
                    borderColor = CyberPink.copy(alpha = 0.4f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(CyberPink.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Groups, contentDescription = null, tint = CyberPink, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = Strings.developersTitle(lang),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        }
                        IconButton(onClick = { activeDialog = null }) {
                            Icon(Icons.Filled.Close, "Close", tint = CyberMuted)
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SecurityData.developersList.forEachIndexed { index, dev ->
                            val linkedInUrls = listOf(
                                "https://www.linkedin.com/in/zeyadelkhodari",
                                "https://www.linkedin.com/in/saja-ayman-74687736a",
                                "https://www.linkedin.com/in/haneen-mahmoud77",
                                "https://www.linkedin.com/in/shahd-adel-5503b836b",
                                "https://www.linkedin.com/in/ranim-hany-635372371",
                                "https://www.linkedin.com/in/donia-khaled-a3633a36a"
                            )
                            val githubUrl = "https://github.com/ZeyadElkhodari"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(CyberCard2)
                                    .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = BidiHelper.isolateLtr(dev.name),
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp,
                                    modifier = Modifier.weight(1f),
                                    style = LocalTextStyle.current.copy(textDirection = TextDirection.ContentOrLtr)
                                )
                                if (index == 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .clip(CircleShape)
                                            .background(Color.White.copy(alpha = 0.08f))
                                            .border(1.dp, Color(0xFFB8C7D9).copy(alpha = 0.65f), CircleShape)
                                            .clickable {
                                                runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl))) }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Filled.Code, contentDescription = "GitHub", tint = Color(0xFFE6F0FF), modifier = Modifier.size(17.dp))
                                    }
                                    Spacer(Modifier.width(6.dp))
                                }
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF0A66C2).copy(alpha = 0.18f))
                                        .border(1.dp, Color(0xFF00C8FF).copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                                        .clickable {
                                            runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(linkedInUrls[index]))) }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("in", color = Color(0xFF38D9FF), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    NeonButton(
                        text = Strings.close(lang),
                        onClick = { activeDialog = null }
                    )
                }
            }
        }
        "reset" -> {
            Dialog(onDismissRequest = { activeDialog = null }) {
                GlassCard {
                    Text(
                        text = Strings.settingsResetStats(lang),
                        color = CyberRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = if (isAr)
                            "هل أنت متأكد من إعادة تعيين جميع إحصائيات الفحص، سجلات الأنشطة، وإعادة جولة الترحيب الاستكشافية؟"
                        else
                            "Are you sure you want to reset all security statistics, scan logs, and replay the introductory onboarding?",
                        color = CyberMuted,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { activeDialog = null },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberCard2),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isAr) "إلغاء" else "Cancel", color = Color.White)
                        }
                        Button(
                            onClick = {
                                onResetStats()
                                activeDialog = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(if (isAr) "إعادة تعيين" else "Reset", color = Color.White)
                        }
                    }
                }
            }
        }
        "help" -> {
            Dialog(onDismissRequest = { activeDialog = null }) {
                GlassCard {
                    Text(
                        text = BidiHelper.formatBiDi(Strings.settingsHelp(lang), isAr = isAr),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        style = LocalTextStyle.current.copy(textDirection = if (isAr) TextDirection.ContentOrRtl else TextDirection.ContentOrLtr)
                    )
                    Spacer(Modifier.height(10.dp))
                    BidiDocumentText(
                        content = if (isAr) HELP_CONTENT_AR else HELP_CONTENT_EN,
                        isAr = isAr,
                        modifier = Modifier.fillMaxWidth().heightIn(max = 520.dp).verticalScroll(rememberScrollState()),
                        lang = lang
                    )
                    Spacer(Modifier.height(18.dp))
                    NeonButton(
                        text = Strings.close(lang),
                        onClick = { activeDialog = null }
                    )
                }
            }
        }

        "privacy" -> {
            Dialog(onDismissRequest = { activeDialog = null }) {
                GlassCard {
                    Text(
                        text = BidiHelper.formatBiDi(Strings.settingsPolicy(lang), isAr = isAr),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        style = LocalTextStyle.current.copy(textDirection = if (isAr) TextDirection.ContentOrRtl else TextDirection.ContentOrLtr)
                    )
                    Spacer(Modifier.height(10.dp))
                    BidiDocumentText(
                        content = if (isAr) PRIVACY_CONTENT_AR else PRIVACY_CONTENT_EN,
                        isAr = isAr,
                        modifier = Modifier.fillMaxWidth().heightIn(max = 520.dp).verticalScroll(rememberScrollState()),
                        lang = lang
                    )
                    Spacer(Modifier.height(18.dp))
                    NeonButton(
                        text = Strings.close(lang),
                        onClick = { activeDialog = null }
                    )
                }
            }
        }
    }

    EditProfileModal(
        isOpen = isEditingProfile,
        onClose = { isEditingProfile = false },
        currentProfile = userProfile,
        onSaveProfile = { updated ->
            onUpdateProfile(updated)
        },
        lang = lang
    )
}


/** Renders each document line independently so Arabic and English paragraphs keep their own direction. */
@Composable
private fun BidiDocumentText(
    content: String,
    isAr: Boolean,
    modifier: Modifier = Modifier,
    lang: Language
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        content.lines().forEachIndexed { index, line ->
            if (line.isBlank()) {
                Spacer(Modifier.height(5.dp))
            } else {
                val lineIsArabic = line.any { it in '\u0600'..'\u06FF' || it in '\u0750'..'\u077F' }
                val formatted = BidiHelper.formatBiDi(line, isAr = isAr)
                Text(
                    text = formatted,
                    modifier = Modifier.fillMaxWidth(),
                    color = if (index == 0) Color.White else CyberMuted,
                    fontSize = if (index == 0) 13.sp else 12.sp,
                    fontWeight = if (index == 0 || line.matches(Regex("^[0-9]+[).].*")) || line in listOf("نطاق السياسة", "حلول سريعة", "Scope", "Quick troubleshooting")) FontWeight.SemiBold else FontWeight.Normal,
                    lineHeight = 20.sp,
                    fontFamily = Strings.getFontFamily(lang),
                    style = LocalTextStyle.current.copy(
                        textDirection = if (lineIsArabic) TextDirection.ContentOrRtl else TextDirection.ContentOrLtr,
                    )
                )
            }
        }
    }
}


private val HELP_CONTENT_AR = """
دليل استخدام CyberGuard

1) فحص الروابط
أدخل رابطاً أو نطاقاً في أداة فحص الروابط. يجمع التطبيق بين مؤشرات محلية ونتائج خدمات استخبارات التهديدات المتصلة عبر Cloudflare Worker. قد تكون النتيجة غير حاسمة إذا تعذر الوصول إلى خدمة خارجية؛ ونتيجة «لم يُعثر على تهديد» لا تعني ضمان سلامة الرابط.

2) كلمات المرور
تعرض الأداة مؤشرات القوة والتعقيد محلياً. وعند طلب التحقق من التسريب، يحسب التطبيق بصمة SHA-1 على الجهاز ويرسل أول خمسة أحرف فقط من البصمة إلى خدمة Have I Been Pwned بنظام k-Anonymity؛ لا يرسل كلمة المرور كنص صريح.

3) فحص Wi-Fi
يعرض الفاحص معلومات الأمان المتاحة من نظام Android، مثل نوع الحماية وبعض خصائص الاتصال. قد يحجب Android اسم الشبكة أو بعض التفاصيل بحسب إصدار النظام والصلاحيات. الفحص لا يختبر اختراق الراوتر ولا يضمن أمان الشبكة بالكامل.

4) رموز QR
امسح الرمز بالكاميرا لمعاينة نوع المحتوى. إذا احتوى على رابط، يتعامل التطبيق معه كهدف للفحص؛ لا تفتح رابطاً غير موثوق لمجرد ظهوره في الرمز. راجع النص أو بيانات الاتصال قبل استخدامها.

5) التوعية بالتصيد
استخدم مواد التوعية وأسئلة التصيد المتاحة للتعرف على علامات الاستعجال، وانتحال الهوية، والروابط غير المتطابقة، وطلبات كلمات المرور أو رموز التحقق.

6) الأكاديمية
افتح المسارات والمقالات، ثم أكمل الاختبارات وفق شروط النجاح المعروضة. تُستخدم بيانات التقدم وسجل النقاط والإنجازات لعرض مستوى التعلم داخل الملف التعليمي.

7) الملف التعليمي والإنجازات
يعرض Learning Profile مستوى التعلم والنقاط والإحصاءات وسجل المكافآت ومحاولات الاختبارات المتاحة. إذا لم تظهر البيانات، تحقق من الاتصال ثم أعد فتح الصفحة.

8) الإشعارات والإعدادات
يمكنك إدارة إعدادات الإشعارات والصوت والمظهر واللغة من صفحات الإعدادات. قد يتطلب Android منح إذن الإشعارات بشكل منفصل. تغيير لغة التطبيق لا يغير لغة نظام الهاتف.

حلول سريعة
• تعذر فحص رابط: تحقق من الإنترنت وأعد المحاولة لاحقاً؛ قد تكون إحدى الخدمات الخارجية غير متاحة.
• تعذر قراءة QR: امنح إذن الكاميرا وتأكد من وضوح الرمز والإضاءة.
• معلومات Wi-Fi ناقصة: راجع أذونات Android وإعدادات الموقع/الشبكة، فقد يقيّد النظام عرضها.
• لم يظهر تقدم الأكاديمية: أعد فتح الصفحة وتحقق من اكتمال تحميل البيانات.
• لا تصل الإشعارات: تحقق من إذن الإشعارات وإعدادات البطارية وعدم تقييد التطبيق.

هذه الإرشادات للتوعية والمساعدة، وليست بديلاً عن تحديث النظام وممارسات الحماية الأساسية.
""".trimIndent()

private val HELP_CONTENT_EN = """
CyberGuard Help & Knowledge Base

1) Link scanning
Enter a URL or domain in the Link Scanner. The app combines local indicators with threat-intelligence results requested through the Cloudflare Worker. A result may be inconclusive when an external provider cannot be reached. “No threat found” is not a guarantee that a URL is safe.

2) Password checks
Strength and complexity indicators are calculated on-device. For a breach lookup, the app computes a SHA-1 hash locally and sends only its first five characters to Have I Been Pwned using k-anonymity; the plaintext password is not sent.

3) Wi-Fi assessment
The scanner displays security information exposed by Android, such as the reported security type and available connection properties. Android may hide the SSID or other details depending on OS version and permissions. This is not a router penetration test and cannot guarantee that a network is secure.

4) QR codes
Scan a code to preview its content type. If it contains a URL, the app can submit that URL for assessment. Do not open an untrusted link simply because it appears in a QR code. Review text and contact details before using them.

5) Phishing awareness
Use the available awareness material and phishing exercises to learn warning signs such as urgency, impersonation, mismatched links, and requests for passwords or verification codes.

6) Academy
Open learning tracks and articles, then complete quizzes according to the displayed pass criteria. Learning progress, XP history, and achievements are used to show progress in the Learning Profile.

7) Learning Profile & achievements
The Learning Profile displays the available learning level, XP, statistics, reward history, and quiz attempts. If information is missing, check connectivity and reopen the page.

8) Notifications & settings
Manage notifications, sound, appearance, and app language in the relevant settings. Android may require notification permission separately. Changing the app language does not change your phone's system language.

Quick troubleshooting
• URL scan failed: check connectivity and retry later; an external provider may be unavailable.
• QR scan failed: grant camera permission and improve lighting/focus.
• Wi-Fi details are missing: review Android permissions and location/network settings; the OS may restrict these details.
• Academy progress is missing: reopen the page and allow its data to finish loading.
• Notifications do not arrive: check notification permission, battery settings, and app restrictions.

This guide is educational and does not replace OS updates or basic security practices.
""".trimIndent()

private val PRIVACY_CONTENT_AR = """
سياسة الخصوصية وشروط الاستخدام — CyberGuard

نطاق السياسة
توضح هذه الصفحة كيفية تعامل إصدار التطبيق الحالي مع المعلومات. قد تتغير الممارسات عند تحديث التطبيق أو الخدمات المتصلة.

1) المعلومات والمعالجة المحلية
يعالج التطبيق على الجهاز مدخلات أدوات الفحص ونتائج التقييم وإعدادات التطبيق وبيانات الملف الشخصي والتقدم التعليمي. تُحفظ بعض بيانات الملف الشخصي والتقدم وسجلات التطبيق محلياً باستخدام قاعدة بيانات Room وتفضيلات التطبيق. لا يعني ذلك أن كل البيانات مشفرة؛ لا نقدم ضمان تشفير شامل.

2) البيانات التي قد تغادر الجهاز
• فحص الروابط: يرسل التطبيق الرابط الذي تطلب فحصه إلى Cloudflare Worker الخاص بالخدمة. ويقوم Worker بإرساله إلى مزودي استخبارات التهديدات المهيئين، وهم Google Safe Browsing وURLhaus وVirusTotal. قد تتم معالجة الرابط والنتائج لدى هذه الجهات وفق سياساتها. تجنب إرسال روابط خاصة أو تحتوي على رموز وصول أو بيانات شخصية أو جلسات تسجيل دخول.
• فحص تسريبات كلمات المرور: يحسب التطبيق بصمة SHA-1 محلياً ويرسل أول خمسة أحرف من البصمة إلى واجهة Have I Been Pwned؛ لا يرسل كلمة المرور كنص صريح، لكن طلب البادئة يظل اتصالاً بخدمة خارجية.
• خدمات أخرى: قد تنشئ وظائف التطبيق اتصالات شبكية لتحميل بيانات أو محتوى الأكاديمية بحسب المسار المستخدم. لا تفترض أن كل استخدام يعمل دون اتصال.

3) الكاميرا والشبكة والصلاحيات
يُستخدم إذن الكاميرا لمسح رموز QR. ويطلب فاحص Wi-Fi إذن الموقع الدقيق ويتحقق من تفعيل خدمات الموقع لأن Android يقيّد إظهار بعض معلومات الشبكة مثل SSID؛ لا يستخدم مسار الفحص الحالي إحداثيات GPS. وقد يحتاج Android إلى إذن الإشعارات لإرسال التنبيهات. رفض الإذن قد يعطل الوظيفة المعتمدة عليه.

4) الاستخدام والاحتفاظ
تُستخدم البيانات لتشغيل أدوات الفحص وعرض النتائج وحفظ إعداداتك وتقدمك. تُخزن البيانات المحلية حتى تحذفها أو تمسح بيانات التطبيق أو تزيله، ولا يرسل التطبيق هذه البيانات إلى نسخة احتياطية سحابية عبر إعداد Android الحالي. لا تحدد هذه الصفحة مدة احتفاظ مزودي الخدمات الخارجيين ببيانات الطلبات؛ راجع سياساتهم مباشرة.

5) المشاركة والحماية
لا نعد بعدم مشاركة البيانات مطلقاً؛ فطلبات الفحص المذكورة تصل إلى مزودي الخدمة الخارجيين. تُستخدم اتصالات HTTPS للخدمات المحددة في الكود، لكن لا يمكن ضمان خلو أي نظام أو اتصال من المخاطر. احمِ جهازك ولا ترسل بيانات سرية ضمن الروابط.

6) حقوق المستخدم
يمكنك التحكم في أذونات Android وتغيير إعدادات التطبيق ومسح بياناته أو إلغاء تثبيته. قد يؤدي مسح البيانات إلى فقد الإحصائيات والتقدم المخزن محلياً. لا يوفر التطبيق الحالي بوابة حساب أو آلية داخلية لطلب حذف البيانات لدى مزودي الخدمات.

7) حدود نتائج الفحص
الأداة مساعدة للتوعية وتقييم المؤشرات وليست ضماناً أو شهادة أمنية. قد لا تحتوي قواعد بيانات التهديدات على كل الروابط الضارة، وقد تتغير النتائج أو تتأخر أو تتعذر. لا تعتمد على نتيجة واحدة لاتخاذ قرار عالي المخاطر.

8) شروط الاستخدام المقبول
استخدم التطبيق لأغراض مشروعة وعلى بيانات أو أنظمة تملكها أو لديك تصريح بفحصها. لا تستخدمه لانتهاك الخصوصية أو تجاوز الصلاحيات أو الإضرار بالآخرين. أنت مسؤول عن التحقق من الروابط والقرارات التي تتخذها.

9) حدود الخدمة وإخلاء المسؤولية
يُقدم التطبيق والخدمات الخارجية بحسب التوافر، دون ضمان استمرار الخدمة أو دقة كل نتيجة أو ملاءمتها لغرض محدد. إلى الحد الذي يسمح به القانون، لا يتحمل المطور مسؤولية الأضرار الناتجة عن الاعتماد الحصري على نتائج آلية أو عن انقطاع مزود خارجي. لا يلغي ذلك أي حقوق قانونية لا يجوز استبعادها.

10) التحديثات والتواصل
قد تُحدّث هذه السياسة مع تحديث التطبيق. لا تعرض النسخة الحالية وسيلة دعم مخصصة داخل هذه الصفحة؛ استخدم قناة التوزيع الرسمية التي حصلت منها على التطبيق لأي استفسار.

باستخدام التطبيق، فإنك تقر بأنك قرأت هذه المعلومات وتفهم حدود أدوات الفحص والاتصالات الخارجية.
""".trimIndent()

private val PRIVACY_CONTENT_EN = """
CyberGuard Privacy Policy & Terms of Use

Scope
This page describes how the current app build handles information. Practices may change when the app or connected services are updated.

1) Information processed on-device
The app processes scanner inputs, assessment results, app settings, profile information, and learning progress on the device. Some profile, progress, and app records are stored locally using Room and app preferences. This does not mean every stored item is encrypted; no blanket encryption guarantee is made here.

2) Information that may leave the device
• URL scanning: the URL you submit is sent to the app's Cloudflare Worker and then to Google Safe Browsing, URLhaus, and VirusTotal when configured. Each provider may process the URL and resulting data under its own terms and privacy policies. Avoid submitting private URLs containing access tokens, personal data, or authenticated sessions.
• Password breach lookup: the app computes a SHA-1 hash locally and sends its first five characters to the Have I Been Pwned API. The plaintext password is not sent, but the prefix request is still an external service request.
• Other services: app features may make network requests to retrieve data or Academy content depending on the flow used. Do not assume every feature is fully offline.

3) Camera, network, and permissions
The camera permission is used for QR scanning. The Wi-Fi scanner requests fine-location permission and checks whether Android location services are enabled because Android restricts access to some network details such as the SSID; the current scanner flow does not use GPS coordinates. Notification permission may also be needed to deliver alerts. Denying a permission may disable the dependent feature.

4) Use and retention
Information is used to operate scanners, display results, retain settings and learning progress, and support feature reliability. Local data remains until you clear it, clear app storage, or uninstall the app, Android backup is disabled by the current app manifest. This page does not specify how long external providers retain submitted requests; consult their policies directly.

5) Sharing and security
This policy does not promise that data is never shared: the scanning requests described above are sent to external providers. HTTPS is used for the network services specified in the code, but no system or connection can be guaranteed risk-free. Protect your device and avoid submitting confidential data in URLs.

6) User choices
You can manage Android permissions, change app settings, clear app data, or uninstall the app. Clearing data may erase locally stored statistics and progress. The current app does not provide an in-app account portal or a provider-side deletion request workflow.

7) Assessment limitations
The app is an awareness and indicator-assessment tool, not a security guarantee or certification. Threat databases may not contain every malicious URL, and results may change, be delayed, or be unavailable. Do not rely on a single automated result for high-risk decisions.

8) Acceptable use
Use the app lawfully and only with data or systems you own or are authorized to assess. Do not use it to violate privacy, bypass permissions, or harm others. You remain responsible for reviewing URLs and decisions made based on results.

9) Service limits and disclaimer
The app and external services are provided subject to availability, without a guarantee of uninterrupted service, complete accuracy, or fitness for a particular purpose. To the extent permitted by law, the developer is not responsible for harm caused by relying exclusively on automated results or by an external provider outage. This does not exclude rights that cannot legally be waived.

10) Updates and contact
This policy may be updated with future app releases. The current screen does not provide a dedicated support channel; use the official distribution channel through which you obtained the app for inquiries.

By using the app, you acknowledge this information and understand the limitations of automated assessments and external network requests.
""".trimIndent()

@Composable
private fun SettingActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    actionLabel: String? = null,
    color: Color = CyberPink,
    isAr: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(CyberCard.copy(alpha = 0.82f), CyberCard.copy(alpha = 0.62f))))
            .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = CyberMuted,
                    fontSize = 10.sp
                )
            }
        }
        if (actionLabel != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = actionLabel,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(6.dp))
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = CyberMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}
