package com.example.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.SecurityData
import com.example.localization.Strings
import com.example.model.Language
import kotlinx.coroutines.launch
import com.example.model.WifiCheckItem
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

@Composable
fun WifiGuideModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    lang: Language,
    onUpdateAudit: (passed: Int, total: Int, score: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val isAr = lang == Language.AR
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val userProgressRepository = remember { com.example.repository.UserProgressRepository(context) }
    var checks by remember { mutableStateOf(SecurityData.initialWifiChecks) }

    LaunchedEffect(isOpen) {
        if (isOpen) {
            val saved = userProgressRepository.loadWifiChecklist()
            if (saved.isNotEmpty()) {
                checks = checks.map { item ->
                    val isChecked = saved[item.id] ?: item.checked
                    item.copy(checked = isChecked)
                }
            }
        }
    }

    val passedCount = checks.count { it.checked }
    val totalCount = checks.size
    val score = ((passedCount.toFloat() / totalCount.toFloat()) * 100).toInt()

    LaunchedEffect(passedCount) {
        onUpdateAudit(passedCount, totalCount, score)
    }

    Dialog(onDismissRequest = onClose) {
        GlassCard(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .testTag("wifi_guide_modal"),
            borderColor = CyberGreen.copy(alpha = 0.35f)
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
                            .background(CyberGreen.copy(alpha = 0.18f))
                            .border(1.dp, CyberGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Wifi, contentDescription = null, tint = CyberGreen, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Strings.wifiSafety(lang),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (isAr) "تدقيق تحصين الراوتر والشبكة المحلية" else "Perimeter & router security evaluation",
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
                // Score Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberCard2)
                        .border(1.dp, CyberGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isAr) "تقييم أمان الشبكة الحالية:" else "Current Network Security Rating:",
                            color = CyberMuted,
                            fontSize = 11.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = if (score >= 80) (if (isAr) "محصنة بشكل ممتاز" else "Well Hardened") else (if (isAr) "تتطلب إجراءات تحصين" else "Action Required"),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Text(
                        text = "$score%",
                        color = CyberGreen,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    )
                }

                Text(
                    text = if (isAr) "قائمة الفحص والتحصين:" else "Network Security Checklist:",
                    color = CyberMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                // Interactive Checklist
                checks.forEach { check ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (check.checked) CyberCard2 else CyberCard2.copy(alpha = 0.5f))
                            .border(
                                1.dp,
                                if (check.checked) CyberGreen.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.05f),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                com.example.sound.SoundManager.playButtonClick()
                                val newChecked = !check.checked
                                checks = checks.map { if (it.id == check.id) it.copy(checked = newChecked) else it }
                                coroutineScope.launch {
                                    userProgressRepository.updateWifiChecklist(check.id, newChecked)
                                }
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = check.checked,
                            onCheckedChange = { isChecked ->
                                checks = checks.map { if (it.id == check.id) it.copy(checked = isChecked) else it }
                            },
                            colors = CheckboxDefaults.colors(
                                checkedColor = CyberGreen,
                                checkmarkColor = Color.Black
                            ),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = if (isAr) check.titleAr else check.titleEn,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = if (isAr) check.descAr else check.descEn,
                                color = CyberMuted,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }

                // Tip callout
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF09090C))
                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = if (isAr)
                            "نصيحة أمنية: عند الاتصال بشبكات Wi-Fi العامة في المقاهي أو المطارات، استخدم دائماً نفق VPN مشفر لحماية جلساتك وبياناتك البنكية."
                        else
                            "Security Tip: When connecting to public Wi-Fi at airports or cafes, always use an encrypted VPN tunnel to protect passwords and credentials.",
                        color = CyberMuted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
