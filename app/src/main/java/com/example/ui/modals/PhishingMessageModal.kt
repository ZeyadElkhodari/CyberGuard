package com.example.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.localization.Strings
import com.example.model.Language
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.*
import com.example.util.BidiHelper

@Composable
fun PhishingMessageModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    lang: Language,
    onAnalysisCompleted: (messageText: String, flaggedIndicators: Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val isAr = lang == Language.AR
    var messageText by remember { mutableStateOf("") }
    var analyzed by remember { mutableStateOf(false) }
    var flagUrgency by remember { mutableStateOf(false) }
    var flagFinancial by remember { mutableStateOf(false) }
    var flagLink by remember { mutableStateOf(false) }
    var flagIdentity by remember { mutableStateOf(false) }
    var flagCredential by remember { mutableStateOf(false) }
    var flagAttachment by remember { mutableStateOf(false) }
    var flagOtp by remember { mutableStateOf(false) }

    val samplePhish = if (isAr)
        "إشعار عاجل من البنك: تم إيقاف بطاقتك الائتمانية بسبب عمليات مشبوهة. يرجى الضغط هنا فوراً خلال 12 ساعة لتأكيد هويتك: http://bank-sa-security.xyz"
    else
        "URGENT: Your account has been temporarily locked due to unauthorized logins. Verify your credentials within 24 hours at http://netflix-account-renew.xyz/billing"

    fun performAnalysis() {
        if (messageText.isBlank()) return
        val lower = messageText.lowercase()
        val urgentWords = listOf("urgent", "immediately", "suspended", "within 24", "action required", "act now", "عاجل", "فوراً", "إيقاف", "خلال", "بشكل فوري")
        val financeWords = listOf("bank", "card", "payment", "transfer", "prize", "gift", "invoice", "refund", "بنك", "بطاقة", "دفع", "تحويل", "جائزة", "فاتورة", "استرداد")
        val linkWords = listOf("http://", "https://", "www.", "bit.ly", "tinyurl", "click", "verify", "رابط", "اضغط")
        val identityWords = listOf("verify your account", "confirm your identity", "account suspended", "security team", "support team", "تأكيد هويتك", "تأكيد الحساب", "تم تعليق حسابك", "فريق الدعم")
        val credentialWords = listOf("password", "passcode", "login", "sign in", "credentials", "username", "كلمة المرور", "رمز الدخول", "بيانات الدخول", "اسم المستخدم")
        val attachmentWords = listOf("open the attachment", "attached file", "enable macros", "download the file", "افتح المرفق", "الملف المرفق", "حمّل الملف")
        val otpWords = listOf("one-time password", "verification code", "otp", "2fa code", "security code", "رمز التحقق", "رمز لمرة واحدة", "كود التأكيد")
        val trustedBrandNames = listOf("paypal", "amazon", "microsoft", "apple", "google", "netflix", "facebook", "instagram", "whatsapp", "بنك مصر", "البنك الأهلي", "فودافون كاش")
        val extractedUrls = Regex("""https?://[^\s<>()]+""", RegexOption.IGNORE_CASE)
            .findAll(messageText).map { it.value.trimEnd('.', ',', ';', ')', ']') }.toList()
        val suspiciousUrlPatterns = listOf(".xyz", ".top", ".click", ".zip", ".mov", "bit.ly", "tinyurl", "cutt.ly", "t.co", "xn--")
        val urlHasRedFlags = extractedUrls.any { url ->
            val normalized = url.lowercase()
            suspiciousUrlPatterns.any { normalized.contains(it) } ||
                normalized.contains("@") ||
                Regex("""https?://\d{1,3}(\.\d{1,3}){3}""").containsMatchIn(normalized)
        }

        flagUrgency = urgentWords.any { lower.contains(it) }
        flagFinancial = financeWords.any { lower.contains(it) }
        flagLink = linkWords.any { lower.contains(it) } || extractedUrls.isNotEmpty() || urlHasRedFlags
        flagIdentity = identityWords.any { lower.contains(it) } ||
            (trustedBrandNames.any { lower.contains(it) } &&
                (credentialWords.any { lower.contains(it) } || urlHasRedFlags || flagUrgency))
        flagCredential = credentialWords.any { lower.contains(it) }
        flagAttachment = attachmentWords.any { lower.contains(it) }
        flagOtp = otpWords.any { lower.contains(it) }
        analyzed = true
        val flaggedIndicators = listOf(flagUrgency, flagFinancial, flagLink, flagIdentity, flagCredential, flagAttachment, flagOtp).count { it }
        onAnalysisCompleted(messageText.trim(), flaggedIndicators)
    }

    Dialog(onDismissRequest = onClose) {
        GlassCard(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .testTag("phishing_message_modal"),
            borderColor = CyberPurple.copy(alpha = 0.35f)
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
                            .background(CyberPurple.copy(alpha = 0.18f))
                            .border(1.dp, CyberPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Mail, contentDescription = null, tint = CyberPurple, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Strings.phishingMessageCheck(lang),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (isAr) "كشف أساليب الاحتيال وانتحال الهوية وسرقة الحسابات" else "Detects social engineering, impersonation and account theft patterns",
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isAr) "ألصق نص الرسالة المشبوهة:" else "Paste message text:",
                        color = CyberMuted,
                        fontSize = 11.sp
                    )
                    TextButton(
                        onClick = {
                            messageText = samplePhish
                            analyzed = false
                        }
                    ) {
                        Text(
                            text = if (isAr) "تحميل نموذج تصيد" else "Load Sample Phish",
                            color = CyberPink,
                            fontSize = 11.sp
                        )
                    }
                }

                OutlinedTextField(
                    value = messageText,
                    onValueChange = {
                        messageText = it
                        analyzed = false
                    },
                    placeholder = {
                        Text(
                            if (isAr) BidiHelper.formatBiDi("ألصق رسالة SMS أو بريد إلكتروني هنا...", isAr = true) else "Paste message text here...",
                            color = CyberMuted,
                            fontSize = 11.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPurple,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                NeonButton(
                    text = if (isAr) "تحليل مؤشرات التهديد" else "Analyze Threat Indicators",
                    onClick = { performAnalysis() },
                    enabled = messageText.isNotBlank(),
                    testTag = "analyze_phishing_message_button"
                )

                if (analyzed) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CyberCard2)
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IndicatorRow(
                            label = if (isAr) "إشارات الاستعجال والضغط النفسي" else "Urgency & Psychological Panic Signals",
                            flagged = flagUrgency,
                            isAr = isAr
                        )
                        IndicatorRow(
                            label = if (isAr) "مطالبات مالية أو طلب بيانات حساسة" else "Financial or Credential Demands",
                            flagged = flagFinancial,
                            isAr = isAr
                        )
                        IndicatorRow(
                            label = if (isAr) "روابط ويب أو نطاقات مختصرة" else "Embedded links or URL shorteners",
                            flagged = flagLink,
                            isAr = isAr
                        )
                        IndicatorRow(
                            label = if (isAr) "انتحال هوية جهة موثوقة أو حساب" else "Brand or account impersonation",
                            flagged = flagIdentity,
                            isAr = isAr
                        )
                        IndicatorRow(
                            label = if (isAr) "طلب كلمات مرور أو بيانات دخول" else "Credential harvesting",
                            flagged = flagCredential,
                            isAr = isAr
                        )
                        IndicatorRow(
                            label = if (isAr) "طلب رمز تحقق أو OTP" else "One-time verification code request",
                            flagged = flagOtp,
                            isAr = isAr
                        )
                        IndicatorRow(
                            label = if (isAr) "مرفقات أو ملفات مشبوهة" else "Suspicious attachments or downloads",
                            flagged = flagAttachment,
                            isAr = isAr
                        )
                        val riskScore = (listOf(flagUrgency, flagFinancial, flagLink, flagIdentity, flagCredential, flagAttachment, flagOtp).count { it } * 14).coerceAtMost(98)
                        Text(
                            text = if (isAr) "مؤشر الاشتباه: $riskScore/100" else "Suspicion indicator: $riskScore/100",
                            color = if (riskScore >= 56) CyberRed else if (riskScore >= 28) CyberYellow else CyberGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = if (isAr) "النتيجة تحليل إرشادي للنص ولا تؤكد سلامة المرسل أو الرابط. لا تفتح الروابط ولا تشارك رموز التحقق عند الشك." else "This is a text-based heuristic, not sender authentication. Do not open links or share verification codes when in doubt.",
                            color = CyberMuted,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IndicatorRow(label: String, flagged: Boolean, isAr: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (flagged) CyberRed.copy(alpha = 0.15f) else CyberGreen.copy(alpha = 0.15f))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (flagged) (if (isAr) "تم الرصد ⚠️" else "Flagged ⚠️") else (if (isAr) "سليم ✓" else "Clean ✓"),
            color = if (flagged) CyberRed else CyberGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
