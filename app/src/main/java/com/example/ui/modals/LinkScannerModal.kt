package com.example.ui.modals

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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.localization.Strings
import com.example.localization.LinkResultLocalization
import com.example.model.Language
import com.example.model.ScanResult
import com.example.repository.SecurityRepository
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.*
import com.example.util.BidiHelper
import kotlinx.coroutines.launch

@Composable
fun LinkScannerModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    lang: Language,
    onLogScan: (ScanResult) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val securityRepository = remember { SecurityRepository(context) }
    val clipboardManager = LocalClipboardManager.current
    val isAr = lang == Language.AR

    var urlInput by remember { mutableStateOf("https://www.google.com") }
    var isScanning by remember { mutableStateOf(false) }
    var scanResult by remember { mutableStateOf<ScanResult?>(null) }
    var copied by remember { mutableStateOf(false) }

    DisposableEffect(isOpen) {
        onDispose {
            com.example.sound.SoundManager.stopScanHum()
        }
    }

    fun runScan(target: String) {
        if (target.isBlank() || isScanning) return
        isScanning = true
        com.example.sound.SoundManager.startScanHum()
        coroutineScope.launch {
            try {
                val res = securityRepository.scanUrl(target)
                scanResult = res
                onLogScan(res)
                com.example.sound.SoundManager.stopScanHum()
                if (res.overallVerdict != "Invalid input - not assessed") {
                    if (res.score >= 80) {
                        com.example.sound.SoundManager.playSuccess()
                    } else if (res.score < 60) {
                        com.example.sound.SoundManager.playAlert()
                    }
                }
            } catch (_: Exception) {
                com.example.sound.SoundManager.stopScanHum()
            } finally {
                isScanning = false
                com.example.sound.SoundManager.stopScanHum()
            }
        }
    }

    Dialog(onDismissRequest = {
        com.example.sound.SoundManager.stopScanHum()
        onClose()
    }) {
        GlassCard(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("link_scanner_modal"),
            borderColor = CyberRed.copy(alpha = 0.35f)
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
                            .background(CyberRed.copy(alpha = 0.18f))
                            .border(1.dp, CyberRed.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Link, contentDescription = null, tint = CyberRed, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Strings.linkScanner(lang),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (isAr) BidiHelper.formatBiDi("فحص استخبارات التهديدات الحية وحماية SSRF", isAr = true) else "Live Threat Feeds & SSRF Guard",
                            color = CyberMuted,
                            fontSize = 10.sp
                        )
                    }
                }
                IconButton(onClick = {
                    com.example.sound.SoundManager.stopScanHum()
                    com.example.sound.SoundManager.playButtonClick()
                    onClose()
                }) {
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
                // Privacy / SSRF notice
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF09090C))
                        .border(1.dp, CyberRed.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Filled.Security, null, tint = CyberPink, modifier = Modifier.size(16.dp).padding(top = 1.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isAr)
                            BidiHelper.formatBiDi("حماية SSRF: يتم حظر أي فحص يستهدف الشبكات الداخلية (127.0.0.1 أو 192.168.x أو بيانات السحابة) وتمرير الرابط عبر فلاتر الأمان دون تنفيذ برمجيات خبيثة.", true)
                        else
                            "SSRF Shield: Internal loopback (127.0.0.1), private RFC1918 IPs, and cloud metadata (169.254.169.254) are strictly blocked without script execution.",
                        color = CyberMuted,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }

                // Presets
                Text(
                    text = Strings.quickScanSamples(lang),
                    color = CyberMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    softWrap = false
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        (if (isAr) "آمن (Google)" else "Safe (Google)") to "https://www.google.com",
                        (if (isAr) "تصيد (PayPal)" else "Phish (PayPal)") to "http://paypal-security-update.xyz/login",
                        (if (isAr) "اختبار SSRF" else "SSRF IP") to "http://192.168.1.1/admin"
                    ).forEach { (label, sampleUrl) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberCard2)
                                .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                                .clickable {
                                    urlInput = sampleUrl
                                    runScan(sampleUrl)
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(label, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }

                // Input Field
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    placeholder = { Text(if (isAr) "أدخل الرابط المراد فحصه..." else "Enter URL to scan...", color = CyberMuted, fontSize = 11.sp, maxLines = 1, softWrap = false, overflow = TextOverflow.Clip) },
                    trailingIcon = {
                        if (urlInput.isNotEmpty()) {
                            IconButton(onClick = { urlInput = "" }) {
                                Icon(Icons.Filled.Clear, null, tint = CyberMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("link_scanner_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPink,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                // Scan Action Button
                NeonButton(
                    text = if (isScanning) (if (isAr) "جارِ الفحص عبر قواعد التهديدات..." else "Scanning threat intelligence databases...") else (if (isAr) "بدء الفحص الشامل" else "Run Threat Intelligence Scan"),
                    textSize = if (isScanning) 11.sp else 15.sp,
                    singleLine = isScanning,
                    onClick = { runScan(urlInput) },
                    enabled = !isScanning && urlInput.isNotBlank(),
                    testTag = "run_link_scan_button"
                )

                if (isScanning) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = CyberRed,
                        trackColor = CyberCard2
                    )
                }

                // Results view
                scanResult?.let { res ->
                    val hasConfirmedThreat = res.score < 50 ||
                        res.providers.any { it.status == "malicious" }
                    val hasUnavailableProvider = res.providers.any {
                        it.status in setOf("unavailable", "unconfigured", "submitted", "rate_limited", "configuration_error", "not_indexed")
                    }
                    val isInconclusive = res.isPartialAssessment || hasUnavailableProvider ||
                        res.overallVerdict.contains("Inconclusive", ignoreCase = true)
                    val color = when {
                        hasConfirmedThreat -> CyberRed
                        isInconclusive || res.score < 90 -> CyberYellow
                        else -> CyberGreen
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(color.copy(alpha = 0.12f))
                            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Shield, null, tint = color, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    BidiHelper.formatBiDi(LinkResultLocalization.riskLevel(res.riskLevel, lang), isAr = isAr),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                BidiHelper.formatBiDi("${res.score} / 100", isAr = isAr),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }

                        Text(
                            BidiHelper.formatBiDi(LinkResultLocalization.recommendation(res.recommendation, lang), isAr = isAr),
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        // Threat Feeds Box
                        Text(
                            text = if (isAr) "تقارير مزودي استخبارات التهديدات:" else "Threat Intelligence Feeds:",
                            color = CyberMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        res.providers.forEach { provider ->
                            val statusColor = when (provider.status) {
                                "clean" -> CyberGreen
                                "malicious" -> CyberRed
                                "suspicious" -> CyberYellow
                                else -> Color(0xFF9E9E9E)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .border(1.dp, statusColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(BidiHelper.formatBiDi(provider.name, isAr = isAr), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Text(
                                        BidiHelper.formatBiDi(LinkResultLocalization.providerDetails(provider.details, lang), isAr = isAr),
                                        color = CyberMuted,
                                        fontSize = 9.sp
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(statusColor.copy(0.2f))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        BidiHelper.formatBiDi(LinkResultLocalization.verdict(provider.verdict, lang), isAr = isAr),
                                        color = statusColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Local structural heuristics
                        Text(
                            text = if (isAr) "الفحوصات الهيكلية والأمنية:" else "Structural & Security Heuristics:",
                            color = CyberMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        res.findings.forEach { finding ->
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = if (finding.passed) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                    contentDescription = null,
                                    tint = if (finding.passed) CyberGreen else CyberRed,
                                    modifier = Modifier.size(14.dp).padding(top = 2.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Column {
                                    Text(
                                        BidiHelper.formatBiDi(LinkResultLocalization.findingTitle(finding.title, lang), isAr = isAr),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        BidiHelper.formatBiDi(LinkResultLocalization.findingDescription(finding.description, lang), isAr = isAr),
                                        color = CyberMuted,
                                        fontSize = 9.sp,
                                        lineHeight = 13.sp
                                    )
                                }
                            }
                        }

                        // Copy report button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    clipboardManager.setText(
                                        AnnotatedString(
                                            if (isAr) {
                                                "تقرير فحص CyberGuard:\nالرابط: ${res.target}\nالنتيجة: ${res.score}/100\nالتقييم: ${LinkResultLocalization.verdict(res.overallVerdict, lang)}\nمستوى الخطورة: ${LinkResultLocalization.riskLevel(res.riskLevel, lang)}\n${LinkResultLocalization.recommendation(res.recommendation, lang)}"
                                            } else {
                                                "CyberGuard Scan Report:\nTarget: ${res.target}\nScore: ${res.score}/100\nVerdict: ${res.overallVerdict}\nRisk: ${res.riskLevel}\n${res.recommendation}"
                                            }
                                        )
                                    )
                                    copied = true
                                }
                            ) {
                                Icon(
                                    imageVector = if (copied) Icons.Filled.Done else Icons.Filled.ContentCopy,
                                    contentDescription = "Copy",
                                    tint = if (copied) CyberGreen else CyberPink,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = if (copied) (if (isAr) "تم النسخ" else "Copied") else (if (isAr) "نسخ التقرير" else "Copy Report"),
                                    color = if (copied) CyberGreen else CyberPink,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
