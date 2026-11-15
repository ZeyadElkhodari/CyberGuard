package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.IconButton
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
import com.example.model.AlertSeverity
import com.example.model.Language
import com.example.model.SecurityAlert
import com.example.model.SecurityActivity
import com.example.util.BidiHelper
import com.example.ui.theme.*

@Composable
fun AlertsScreen(
    alerts: List<SecurityAlert>,
    activities: List<SecurityActivity>,
    lang: Language,
    onMarkRead: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    onDeleteAlert: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var filter by remember { mutableStateOf("all") } // "all", "important", "read"
    var selectedAlert by remember { mutableStateOf<SecurityAlert?>(null) }

    val filteredAlerts = alerts.filter { alert ->
        when (filter) {
            "important" -> alert.severity == AlertSeverity.CRITICAL || alert.severity == AlertSeverity.WARNING
            "read" -> alert.read
            else -> true
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("alerts_screen"),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f, fill = false)) {
                    Text(
                        text = Strings.alertsTitle(lang),
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
                        text = Strings.alertsSub(lang),
                        color = CyberMuted,
                        fontSize = 13.sp,
                        fontFamily = Strings.getFontFamily(lang),
                        lineHeight = 20.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onMarkAllRead() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.DoneAll,
                        contentDescription = "Mark all read",
                        tint = CyberPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = Strings.markAllRead(lang),
                        color = CyberPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Alert filters
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterTag(
                    label = Strings.all(lang),
                    selected = filter == "all",
                    onClick = { filter = "all" }
                )
                FilterTag(
                    label = Strings.important(lang),
                    selected = filter == "important",
                    onClick = { filter = "important" }
                )
                FilterTag(
                    label = Strings.read(lang),
                    selected = filter == "read",
                    onClick = { filter = "read" }
                )

            }
        }

        // Alerts List
        if (filteredAlerts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = CyberGreen.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = Strings.noAlerts(lang),
                            color = CyberMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(filteredAlerts, key = { it.id }) { alert ->
                AlertItemCard(
                    alert = alert,
                    onClick = {
                        onMarkRead(alert.id)
                        selectedAlert = alert
                    },
                    onDelete = { onDeleteAlert(alert.id) },
                    lang = lang
                )
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = if (lang == Language.AR) "سجل آخر 30 عملية أمنية" else "Latest 30 Security Activities",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (lang == Language.AR)
                        "يحتفظ التطبيق بآخر 30 عملية أمنية فقط، ويتم حذف الأقدم تلقائيًا."
                    else
                        "Only the latest 30 security activities are kept. Older entries are removed automatically.",
                    color = CyberMuted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        if (activities.isEmpty()) {
            item {
                Text(
                    text = if (lang == Language.AR) "لا توجد عمليات مسجلة حتى الآن" else "No activity has been recorded yet",
                    color = CyberMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp)
                )
            }
        } else {
            items(activities.sortedByDescending { it.timestamp }, key = { "activity_${it.id}" }) { activity ->
                ActivityHistoryCard(activity = activity, lang = lang)
            }
        }
    }

    selectedAlert?.let { alert ->
        val dialogType = when (alert.category.lowercase()) {
            "threat" -> when {
                alert.title.contains("password", true) || alert.title.contains("كلمة مرور") -> "password"
                alert.title.contains("wifi", true) || alert.title.contains("واي فاي") -> "wifi"
                alert.title.contains("qr", true) || alert.title.contains("رمز") -> "qr"
                alert.title.contains("phishing", true) || alert.title.contains("تصيد") || alert.title.contains("تصيّد") -> "phishing"
                else -> "link"
            }
            else -> alert.category.lowercase()
        }
        val dialogTitle = when (dialogType) {
            "link" -> if (lang == Language.AR) "تحذير: رابط خطير" else "Dangerous link detected"
            "password" -> if (lang == Language.AR) "تحذير: كلمة مرور مسرّبة" else "Compromised password detected"
            "wifi" -> if (lang == Language.AR) "تحذير: شبكة Wi-Fi ضعيفة" else "Weak Wi-Fi security detected"
            "qr" -> if (lang == Language.AR) "تحذير: رمز QR ضار" else "Malicious QR code detected"
            "phishing" -> if (lang == Language.AR) "تحذير: رسالة تصيّد محتملة" else "Potential phishing message detected"
            "learning" -> if (lang == Language.AR) "إنجاز تعليمي" else "Learning achievement"
            else -> if (lang == Language.AR) "تنبيه أمني" else "Security alert"
        }
        val dialogDetail = when (dialogType) {
            "link" -> if (lang == Language.AR) "رصد التطبيق رابطًا يحمل مؤشرات خطورة. تجنب فتحه أو إدخال أي بيانات من خلاله." else "The scan detected risk indicators in this link. Do not open it or enter information on the page."
            "password" -> if (lang == Language.AR) "ظهرت كلمة المرور ضمن بيانات اعتماد مسرّبة. غيّرها فورًا في كل حساب تستخدمها فيه، ولا تعِد استخدامها." else "This password was found in exposed credential data. Change it immediately on every account where it is used, and do not reuse it."
            "wifi" -> if (lang == Language.AR) "كشف فحص الشبكة عن إعدادات أمان ضعيفة. استخدم تشفير WPA2-AES أو WPA3 وغيّر كلمة مرور الراوتر الافتراضية." else "The network scan found weak security settings. Use WPA2-AES or WPA3 encryption and change the router's default password."
            "qr" -> if (lang == Language.AR) "يحتوي رمز QR على وجهة أو محتوى مشبوه. لا تفتح الرابط المرتبط به قبل التحقق من مصدره." else "This QR code contains a suspicious destination or payload. Do not open its link until you verify the source."
            "phishing" -> if (lang == Language.AR) "رُصدت مؤشرات احتيال في الرسالة. لا تضغط على روابطها ولا تشارك كلمات المرور أو رموز التحقق." else "The message contains phishing indicators. Do not follow its links or share passwords or verification codes."
            else -> alert.detail
        }
        val (dialogAccent, _) = when (alert.severity) {
            AlertSeverity.CRITICAL -> CyberRed to Icons.Filled.WarningAmber
            AlertSeverity.WARNING -> CyberYellow to Icons.Filled.Lock
            AlertSeverity.INFO -> CyberPurple to Icons.Filled.School
            AlertSeverity.SUCCESS -> CyberGreen to Icons.Filled.CheckCircle
        }
        AlertDialog(
            onDismissRequest = { selectedAlert = null },
            containerColor = Color(0xFF151018),
            title = {
                Text(
                    text = dialogTitle,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    fontFamily = Strings.getFontFamily(lang)
                )
            },
            text = {
                Surface(
                    color = Color(0xFF1B1119),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, dialogAccent.copy(alpha = 0.65f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .heightIn(max = 360.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = dialogDetail,
                            color = Color(0xFFF2EAF0),
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            fontFamily = Strings.getFontFamily(lang)
                        )
                        Text(
                            text = alert.time,
                            color = CyberMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedAlert = null }) {
                    Text(
                        text = if (lang == Language.AR) "إغلاق" else "Close",
                        color = CyberRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(22.dp)
        )
    }
}

@Composable
private fun ActivityHistoryCard(
    activity: SecurityActivity,
    lang: Language
) {
    val (color, icon) = when (activity.type) {
        "password" -> CyberBlue to Icons.Filled.Lock
        "link" -> CyberRed to Icons.Filled.Link
        "wifi" -> CyberGreen to Icons.Filled.Wifi
        "qr" -> CyberPurple to Icons.Filled.QrCodeScanner
        "phishing-message" -> CyberPurple to Icons.Filled.Mail
        else -> CyberYellow to Icons.Filled.History
    }
    val formattedTimestamp = remember(activity.timestamp) {
        java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            .format(java.util.Date(activity.timestamp))
    }
    val statusLabel = when (activity.status) {
        "safe" -> if (lang == Language.AR) "سليم" else "Safe"
        "critical" -> if (lang == Language.AR) "خطير" else "Critical"
        "warning" -> if (lang == Language.AR) "تحذير" else "Warning"
        else -> if (lang == Language.AR) "معلومات" else "Info"
    }
    val localizedActivityTitle = com.example.localization.ActivityLocalization.title(activity, lang)
    val localizedActivityDetail = com.example.localization.ActivityLocalization.detail(activity, lang)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(listOf(CyberCard.copy(alpha = 0.82f), CyberCard.copy(alpha = 0.62f))))
            .border(1.dp, color.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(19.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = BidiHelper.formatBiDi(localizedActivityTitle, isAr = lang == Language.AR),
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = BidiHelper.formatBiDi(localizedActivityDetail, isAr = lang == Language.AR),
                color = CyberMuted,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(formattedTimestamp, color = CyberMuted.copy(alpha = 0.8f), fontSize = 10.sp)
                Text(statusLabel, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FilterTag(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) CyberRed else CyberCard2)
            .border(
                1.dp,
                if (selected) CyberRed else Color.White.copy(alpha = 0.08f),
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else CyberMuted,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun AlertItemCard(
    alert: SecurityAlert,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    lang: Language
) {
    // Older installs stored every security event as category=threat. Infer its real
    // type from the persisted event title so old alerts are also displayed correctly.
    val alertType = when (alert.category.lowercase()) {
        "threat" -> when {
            alert.title.contains("password", true) || alert.title.contains("كلمة مرور") -> "password"
            alert.title.contains("wifi", true) || alert.title.contains("واي فاي") -> "wifi"
            alert.title.contains("qr", true) || alert.title.contains("رمز") -> "qr"
            alert.title.contains("phishing", true) || alert.title.contains("تصيد") || alert.title.contains("تصيّد") -> "phishing"
            else -> "link"
        }
        else -> alert.category.lowercase()
    }
    val localizedTitle = when (alertType) {
        "link" -> if (lang == Language.AR) "تحذير: رابط خطير" else "Dangerous link detected"
        "password" -> if (lang == Language.AR) "تحذير: كلمة مرور مسرّبة" else "Compromised password detected"
        "wifi" -> if (lang == Language.AR) "تحذير: شبكة Wi-Fi ضعيفة" else "Weak Wi-Fi security detected"
        "qr" -> if (lang == Language.AR) "تحذير: رمز QR ضار" else "Malicious QR code detected"
        "phishing" -> if (lang == Language.AR) "تحذير: رسالة تصيّد محتملة" else "Potential phishing message detected"
        "system" -> if (lang == Language.AR) "تنبيه أمني" else "Security notification"
        "learning" -> if (lang == Language.AR) "إنجاز تعليمي" else "Learning achievement"
        "auth" -> if (lang == Language.AR) "تنبيه الحساب" else "Account alert"
        else -> if (lang == Language.AR) "تنبيه أمني" else "Security alert"
    }
    val localizedDetail = when (alertType) {
        "link" -> if (lang == Language.AR) "رصد التطبيق رابطًا يحمل مؤشرات خطورة. تجنب فتحه أو إدخال أي بيانات من خلاله." else "The scan detected risk indicators in this link. Do not open it or enter information on the page."
        "password" -> if (lang == Language.AR) "ظهرت كلمة المرور ضمن بيانات اعتماد مسرّبة. غيّرها فورًا في كل حساب تستخدمها فيه، ولا تعِد استخدامها." else "This password was found in exposed credential data. Change it immediately on every account where it is used, and do not reuse it."
        "wifi" -> if (lang == Language.AR) "كشف فحص الشبكة عن إعدادات أمان ضعيفة. استخدم تشفير WPA2-AES أو WPA3 وغيّر كلمة مرور الراوتر الافتراضية." else "The network scan found weak security settings. Use WPA2-AES or WPA3 encryption and change the router's default password."
        "qr" -> if (lang == Language.AR) "يحتوي رمز QR على وجهة أو محتوى مشبوه. لا تفتح الرابط المرتبط به قبل التحقق من مصدره." else "This QR code contains a suspicious destination or payload. Do not open its link until you verify the source."
        "phishing" -> if (lang == Language.AR) "رُصدت مؤشرات احتيال في الرسالة. لا تضغط على روابطها ولا تشارك كلمات المرور أو رموز التحقق." else "The message contains phishing indicators. Do not follow its links or share passwords or verification codes."
        else -> alert.detail
    }
    val (color, icon: ImageVector) = when (alert.severity) {
        AlertSeverity.CRITICAL -> CyberRed to Icons.Filled.WarningAmber
        AlertSeverity.WARNING -> CyberYellow to Icons.Filled.Lock
        AlertSeverity.INFO -> CyberPurple to Icons.Filled.School
        AlertSeverity.SUCCESS -> CyberGreen to Icons.Filled.CheckCircle
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        color.copy(alpha = 0.14f),
                        CyberCard
                    )
                )
            )
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag("alert_item_${alert.id}"),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(14.dp))

        Column(Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = BidiHelper.formatBiDi(localizedTitle, isAr = lang == Language.AR),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )
                if (!alert.read) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(CyberRed)
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = BidiHelper.formatBiDi(localizedDetail, isAr = lang == Language.AR),
                color = CyberMuted,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = alert.time,
                    color = CyberMuted.copy(alpha = 0.8f),
                    fontSize = 10.sp
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete alert",
                        tint = CyberMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
