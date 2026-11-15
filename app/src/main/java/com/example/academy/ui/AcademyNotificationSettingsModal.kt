package com.example.academy.ui

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.academy.data.AcademyNotificationPreferencesDto
import com.example.academy.data.AcademyRepository
import com.example.academy.notifications.AcademyNotificationScheduler
import com.example.localization.Strings
import com.example.model.Language
import com.example.util.BidiHelper
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AcademyNotificationSettingsModal(
    repository: AcademyRepository,
    lang: Language,
    onDismiss: () -> Unit
) {
    val isEnglish = lang == Language.EN
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var enabled by remember { mutableStateOf(true) }
    var dailyTips by remember { mutableStateOf(true) }
    var quizReminders by remember { mutableStateOf(true) }
    var trackRecommendations by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var testSentMessage by remember { mutableStateOf<String?>(null) }
    var showPermissionRationale by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        enabled = isGranted
        if (isGranted) {
            AcademyNotificationScheduler.scheduleDailyNotifications(context = context, enabled = true, intervalHours = 12)
        } else {
            AcademyNotificationScheduler.cancelNotifications(context)
        }
    }

    LaunchedEffect(Unit) {
        val prefsResult = repository.getNotificationPreferences()
        if (prefsResult.isSuccess) {
            val p = prefsResult.getOrNull()
            if (p != null) {
                enabled = p.enabled
                dailyTips = p.dailyTips
                quizReminders = p.quizReminders
                trackRecommendations = p.trackRecommendations
            }
        }
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text(if (isEnglish) "Enable notifications" else "تفعيل الإشعارات", fontFamily = Strings.getFontFamily(lang)) },
            text = {
                Text(
                    BidiHelper.formatBiDi(
                        if (isEnglish) "Enable notifications to receive helpful learning messages and reminders that help you track your progress in CyberGuard. You can manage or turn them off anytime in Settings."
                        else "فعّل الإشعارات لتصلك رسائل تعليمية وتذكيرات مفيدة تساعدك على متابعة تقدمك في CyberGuard. يمكنك التحكم بها أو إيقافها في أي وقت من الإعدادات.",
                        isAr = !isEnglish
                    ),
                    fontFamily = Strings.getFontFamily(lang)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionRationale = false
                    enabled = true
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        AcademyNotificationScheduler.scheduleDailyNotifications(context = context, enabled = true, intervalHours = 12)
                    }
                }) { Text(if (isEnglish) "Continue" else "متابعة") }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text(if (isEnglish) "Not now" else "ليس الآن")
                }
            },
            containerColor = CyberCard.copy(alpha = 0.76f),
            titleContentColor = Color.White,
            textContentColor = CyberMuted
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CyberDarkBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = if (isEnglish) "Close" else "إغلاق",
                            tint = Color.White
                        )
                    }

                    Text(
                        text = if (isEnglish) "Educational notification settings" else "إعدادات الإشعارات التعليمية",
                        color = Color.White,
                        fontSize = academyTextSize(lang, 18),
                        fontWeight = FontWeight.Bold,
                        fontFamily = Strings.getFontFamily(lang)
                    )

                    Spacer(modifier = Modifier.size(48.dp))
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
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
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (isEnglish) "Enable educational notifications" else "تفعيل الإشعارات التعليمية",
                                            color = Color.White,
                                            fontSize = academyTextSize(lang, 15),
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = Strings.getFontFamily(lang)
                                        )
                                        Text(
                                            text = if (isEnglish) "Receive helpful learning messages and reminders from CyberGuard" else "رسائل تعليمية وتذكيرات مفيدة من CyberGuard",
                                            color = CyberMuted,
                                            fontSize = academyTextSize(lang, 12),
                                            fontFamily = Strings.getFontFamily(lang)
                                        )
                                    }

                                    Switch(
                                        checked = enabled,
                                        onCheckedChange = { isChecked ->
                                            if (isChecked) {
                                                showPermissionRationale = true
                                            } else {
                                                enabled = false
                                                AcademyNotificationScheduler.cancelNotifications(context)
                                            }
                                        },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = CyberGreen,
                                            checkedTrackColor = CyberGreen.copy(alpha = 0.3f),
                                            uncheckedThumbColor = CyberMuted,
                                            uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
                                        )
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = if (isEnglish) "Notification categories" else "أنواع التنبيهات المخصصة",
                            color = Color.White,
                            fontSize = academyTextSize(lang, 15),
                            fontWeight = FontWeight.Bold,
                            fontFamily = Strings.getFontFamily(lang)
                        )
                    }

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
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                NotificationToggleItem(
                                    title = if (isEnglish) "Daily security tips (three times a day)" else "نصائح أمنية دورية (ثلاث مرات يومياً)",
                                    subtitle = if (isEnglish) "Guidance to protect your accounts and money from fraud" else "إرشادات لحماية حساباتك وأموالك من الاحتيال",
                                    checked = dailyTips,
                                    enabled = enabled,
                                    lang = lang,
                                    onCheckedChange = { dailyTips = it }
                                )

                                Divider(color = CyberBorder)

                                NotificationToggleItem(
                                    title = if (isEnglish) "Incomplete quiz reminders" else "تذكير الاختبارات غير المكتملة",
                                    subtitle = if (isEnglish) "Tap the notification to open the unfinished quiz directly" else "اضغط على الإشعار للانتقال مباشرة إلى الاختبار غير المكتمل",
                                    checked = quizReminders,
                                    enabled = enabled,
                                    lang = lang,
                                    onCheckedChange = { quizReminders = it }
                                )

                                Divider(color = CyberBorder)

                                NotificationToggleItem(
                                    title = if (isEnglish) "Smart learning-track recommendations" else "ترشيحات المسارات الذكية",
                                    subtitle = if (isEnglish) "Suggestions for your next security track based on your level" else "اقتراح المسار الأمني التالي بناءً على مستواك",
                                    checked = trackRecommendations,
                                    enabled = enabled,
                                    lang = lang,
                                    onCheckedChange = { trackRecommendations = it }
                                )
                            }
                        }
                    }

                    // Test Notification Action
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
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = if (isEnglish) "Test notification system" else "اختبار نظام التنبيهات",
                                    color = Color.White,
                                    fontSize = academyTextSize(lang, 15),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                                Text(
                                    text = if (isEnglish) "Send an instant notification to verify delivery to your phone through the cyberguard_academy_tips channel." else "إرسال إشعار فوري للتحقق من وصول التنبيهات إلى هاتفك بنجاح عبر قناة cyberguard_academy_tips.",
                                    color = CyberMuted,
                                    fontSize = academyTextSize(lang, 12),
                                    fontFamily = Strings.getFontFamily(lang)
                                )

                                OutlinedButton(
                                    onClick = {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        }
                                        AcademyNotificationScheduler.postEducationalNotification(
                                            context = context,
                                            title = if (isEnglish) "💡 CyberGuard security tip" else "💡 نصيحة سايبر جارد الأمنية",
                                            message = if (isEnglish) "Never share your one-time password (OTP) with anyone claiming to represent your bank or customer support!" else "لا تشارك رمز التحقق لمرة واحدة (OTP) مع أي متصل يدعي أنه من البنك أو خدمة العملاء أبداً!"
                                        )
                                        testSentMessage = if (isEnglish) "Test notification sent successfully. Check your notification shade." else "تم إرسال إشعار تجريبي بنجاح! تفقد شريط التنبيهات."
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberGreen),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberGreen)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isEnglish) "Send a test notification now" else "إرسال إشعار تجريبي الآن",
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = Strings.getFontFamily(lang)
                                    )
                                }

                                if (testSentMessage != null) {
                                    Text(
                                        text = testSentMessage!!,
                                        color = CyberGreen,
                                        fontSize = academyTextSize(lang, 12),
                                        fontFamily = Strings.getFontFamily(lang)
                                    )
                                }
                            }
                        }
                    }

                    // Save Button
                    item {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    isSaving = true
                                    val newPrefs = AcademyNotificationPreferencesDto(
                                        enabled = enabled,
                                        dailyTips = dailyTips,
                                        quizReminders = quizReminders,
                                        trackRecommendations = trackRecommendations
                                    )
                                    repository.updateNotificationPreferences(newPrefs)
                                    if (enabled) {
                                        AcademyNotificationScheduler.scheduleDailyNotifications(context = context, enabled = true, intervalHours = 12)
                                    } else {
                                        AcademyNotificationScheduler.cancelNotifications(context)
                                    }
                                    isSaving = false
                                    onDismiss()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                            enabled = !isSaving
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    color = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = if (isEnglish) "Save settings" else "حفظ الإعدادات",
                                    color = Color.Black,
                                    fontSize = academyTextSize(lang, 16),
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

@Composable
private fun NotificationToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean,
    lang: Language,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (enabled) Color.White else CyberMuted,
                fontSize = academyTextSize(lang, 14),
                fontWeight = FontWeight.SemiBold,
                fontFamily = Strings.getFontFamily(lang)
            )
            Text(
                text = subtitle,
                color = CyberMuted,
                fontSize = academyTextSize(lang, 11),
                fontFamily = Strings.getFontFamily(lang)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyberGreen,
                checkedTrackColor = CyberGreen.copy(alpha = 0.3f),
                uncheckedThumbColor = CyberMuted,
                uncheckedTrackColor = Color.White.copy(alpha = 0.1f)
            )
        )
    }
}
