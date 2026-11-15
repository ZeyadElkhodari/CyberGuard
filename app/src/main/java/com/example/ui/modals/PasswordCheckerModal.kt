package com.example.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.BidiHelper
import androidx.compose.ui.window.Dialog
import com.example.localization.Strings
import com.example.logic.SecurityEvaluator
import com.example.model.Language
import com.example.model.PasswordBreachResult
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.*
import kotlinx.coroutines.launch

private fun localizedPasswordCheckLabel(label: String, isAr: Boolean): String {
    if (!isAr) return label
    return when (label) {
        "Minimum 12 characters" -> "12 حرفًا على الأقل"
        "Uppercase & Lowercase letters" -> "أحرف كبيرة وصغيرة"
        "Includes numbers (0-9)" -> "يحتوي على أرقام (0–9)"
        "Includes special symbols (!@#$)" -> "يحتوي على رموز خاصة"
        "Not a common or predictable password" -> "ليست كلمة مرور شائعة أو سهلة التوقع"
        else -> label
    }
}

private fun localizedCrackTime(value: String, isAr: Boolean): String {
    if (!isAr) return value
    return when (value) {
        "Seconds to minutes" -> "ثوانٍ إلى دقائق"
        "Under a minute" -> "أقل من دقيقة"
        "Minutes to hours" -> "دقائق إلى ساعات"
        "Hours to days" -> "ساعات إلى أيام"
        "Days to months" -> "أيام إلى أشهر"
        "Years to centuries" -> "سنوات إلى قرون"
        "Centuries or longer" -> "قرون أو أكثر"
        else -> value
    }
}

@Composable
fun PasswordCheckerModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    lang: Language,
    onUpdateAssessment: (Int, Int, String, Int) -> Unit,
    onBreachCheckCompleted: (PasswordBreachResult) -> Unit = {},
    onBreachDetected: (PasswordBreachResult) -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val isAr = lang == Language.AR
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val securityRepository = remember { com.example.repository.SecurityRepository(context) }
    val clipboardManager = LocalClipboardManager.current
    var passwordCopied by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("CyberP@ssw0rd2026!") }
    var showPassword by remember { mutableStateOf(false) }
    var genLength by remember { mutableFloatStateOf(16f) }
    var includeSymbols by remember { mutableStateOf(true) }
    var breachResult by remember { mutableStateOf<PasswordBreachResult?>(null) }
    var isCheckingBreach by remember { mutableStateOf(false) }

    val evaluation = SecurityEvaluator.evaluatePassword(password)

    DisposableEffect(isOpen) {
        onDispose {
            com.example.sound.SoundManager.stopScanHum()
        }
    }

    LaunchedEffect(password, breachResult) {
        onUpdateAssessment(
            evaluation.score,
            evaluation.entropy,
            breachResult?.let {
                if (it.breached) "breached"
                else if (it.checked && it.error == null) "clean"
                else "untested"
            } ?: "untested",
            breachResult?.count ?: 0
        )
    }

    Dialog(onDismissRequest = {
        com.example.sound.SoundManager.stopScanHum()
        onClose()
    }) {
        GlassCard(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("password_checker_modal"),
            borderColor = CyberBlue.copy(alpha = 0.35f)
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
                            .background(CyberBlue.copy(alpha = 0.18f))
                            .border(1.dp, CyberBlue.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = null, tint = CyberBlue, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = Strings.passwordCheck(lang),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Text(
                            text = if (isAr) BidiHelper.formatBiDi("تحليل قوة كلمة المرور والتحقق من التسريبات", true) else "Password strength and breach exposure check",
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
                // Privacy banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF09090C))
                        .border(1.dp, CyberBlue.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Filled.Info, null, tint = CyberBlue, modifier = Modifier.size(16.dp).padding(top = 1.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (isAr)
                            "حماية الخصوصية: لا نرسل كلمة المرور بصيغتها الأصلية أثناء الفحص."
                        else
                            "Privacy protection: your original password is never transmitted during the check.",
                        color = CyberMuted,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                }

                // Input
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        breachResult = null
                        passwordCopied = false
                    },
                    label = { Text(if (isAr) "كلمة المرور للاختبار" else "Password to test", fontSize = 11.sp) },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = "Toggle visibility",
                                    tint = CyberMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            if (password.isNotEmpty()) {
                                IconButton(onClick = {
                                    clipboardManager.setText(AnnotatedString(password))
                                    passwordCopied = true
                                    android.widget.Toast.makeText(
                                        context,
                                        if (isAr) "تم نسخ كلمة المرور" else "Password copied",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }) {
                                    Icon(Icons.Filled.ContentCopy, contentDescription = if (isAr) "نسخ كلمة المرور" else "Copy password", tint = if (passwordCopied) CyberGreen else CyberBlue, modifier = Modifier.size(17.dp))
                                }
                                IconButton(onClick = { password = ""; breachResult = null; passwordCopied = false }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Clear", tint = CyberRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("password_input_field"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberBlue,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )

                // Complexity & Entropy Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberCard2.copy(alpha = 0.8f))
                        .border(1.dp, Color.White.copy(alpha = 0.06f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) BidiHelper.formatBiDi("1. قوة كلمة المرور ومدى صعوبة تخمينها:", true) else "1. Password strength and how difficult it is to guess:",
                            color = CyberMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = BidiHelper.formatBiDi("${evaluation.score}% (${evaluation.entropy} bits)", isAr = isAr),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }

                    LinearProgressIndicator(
                        progress = { evaluation.score / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = when {
                            evaluation.score >= 80 -> CyberGreen
                            evaluation.score >= 50 -> CyberYellow
                            else -> CyberRed
                        },
                        trackColor = Color.Black.copy(alpha = 0.5f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isAr) "الوقت التقريبي لكسرها بالتخمين:" else "Crack time estimate:",
                            color = CyberMuted,
                            fontSize = 11.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyberGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = localizedCrackTime(evaluation.crackTimeEstimate, isAr),
                                color = CyberGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Text(
                        text = if (isAr)
                            "تقدير إرشادي يفترض هجومًا دون اتصال بسرعة 10 مليارات محاولة في الثانية؛ وقد يختلف الزمن الفعلي حسب العتاد وقوائم التخمين."
                        else
                            "Heuristic estimate assumes an offline attack at 10 billion guesses/second; real time varies with hardware and attack wordlists.",
                        color = CyberMuted,
                        fontSize = 9.sp,
                        lineHeight = 13.sp
                    )
                }

                // Breach check section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1B1528))
                        .border(1.dp, CyberPurple.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isAr) BidiHelper.formatBiDi("2. التحقق من ظهور كلمة المرور في التسريبات:", true) else "2. Password breach exposure check:",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = if (isAr)
                            BidiHelper.formatBiDi("تحقّق من ظهور كلمة مرورك في قواعد بيانات التسريبات المعروفة دون إرسالها.", true)
                        else
                            "Check whether your password appears in known public breach datasets. Your actual password is never sent.",
                        color = CyberMuted,
                        fontSize = 10.sp
                    )

                    Button(
                        onClick = {
                            if (!isCheckingBreach && password.isNotBlank()) {
                                isCheckingBreach = true
                                com.example.sound.SoundManager.startScanHum()
                                coroutineScope.launch {
                                    try {
                                        val res = securityRepository.checkPasswordBreach(password)
                                        breachResult = res
                                        onBreachCheckCompleted(res)
                                        com.example.sound.SoundManager.stopScanHum()
                                        if (res.breached) {
                                            onBreachDetected(res)
                                            com.example.sound.SoundManager.playAlert()
                                        } else {
                                            com.example.sound.SoundManager.playSuccess()
                                        }
                                    } catch (_: Exception) {
                                        com.example.sound.SoundManager.stopScanHum()
                                    } finally {
                                        isCheckingBreach = false
                                        com.example.sound.SoundManager.stopScanHum()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().testTag("check_breach_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPurple),
                        enabled = !isCheckingBreach && password.isNotBlank()
                    ) {
                        Text(
                            text = if (isCheckingBreach) (if (isAr) "جارٍ فحص التسريبات..." else "Checking breach exposure...") else (if (isAr) "افحص كلمة المرور الآن" else "Check password exposure"),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCheckingBreach) 11.sp else 12.sp,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    breachResult?.let { res ->
                        if (res.breached) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CyberRed.copy(alpha = 0.2f))
                                    .border(1.dp, CyberRed.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = if (isAr) "تحذير: كلمة المرور موجودة في تسريبات سابقة!" else "CRITICAL: Password Leaked in Public Breaches!",
                                        color = CyberRed,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = if (isAr) "تكررت ${res.count} مرة في قوائم الهجمات الآلية." else "Appears ${res.count} times in hacker credential stuffing dictionaries.",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        } else if (res.checked && res.error == null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CyberGreen.copy(alpha = 0.2f))
                                    .border(1.dp, CyberGreen.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = if (isAr) "لم نعثر على كلمة المرور ضمن قواعد بيانات التسريبات التي تم فحصها." else "No match was found in the breach datasets checked.",
                                    color = CyberGreen,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CyberYellow.copy(alpha = 0.15f))
                                    .border(1.dp, CyberYellow.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = if (isAr) "النتيجة غير حاسمة: تعذر الاتصال بقاعدة بيانات التسريبات" else "Inconclusive: Leak Database Unreachable",
                                        color = CyberYellow,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = if (isAr) "تحقق من اتصال الإنترنت ثم أعد المحاولة. تعذّر إكمال الفحص، لذلك لا يمكن اعتبار النتيجة آمنة." else "Check your internet connection and try again. The check could not be completed, so this result cannot be considered safe.",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Checklist
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isAr) "قائمة معايير التعقيد والتنوع:" else "Complexity & Diversity Checklist:",
                        color = CyberMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    evaluation.checks.forEach { check ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(CyberCard2.copy(alpha = 0.5f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                BidiHelper.formatBiDi(localizedPasswordCheckLabel(check.label, isAr), isAr = isAr),
                                color = if (check.passed) Color.White else CyberMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = if (check.passed) (if (isAr) "مكتمل" else "Passed") else (if (isAr) "مفقود" else "Missing"),
                                color = if (check.passed) CyberGreen else CyberRed,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Generator
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CyberCard)
                        .border(1.dp, CyberBlue.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isAr) "مولد كلمات المرور القوية" else "Strong Password Generator",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text("${genLength.toInt()} chars", color = CyberBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Slider(
                        value = genLength,
                        onValueChange = { genLength = it },
                        valueRange = 12f..32f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = CyberBlue,
                            activeTrackColor = CyberBlue,
                            inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (isAr) "تضمين رموز خاصة (!@#$)" else "Include special symbols", color = CyberMuted, fontSize = 11.sp)
                        Switch(
                            checked = includeSymbols,
                            onCheckedChange = { includeSymbols = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyberBlue, checkedTrackColor = CyberBlue.copy(0.4f))
                        )
                    }

                    NeonButton(
                        text = if (isAr) "توليد وتطبيق كلمة مرور" else "Generate & Apply Password",
                        onClick = {
                            val generated = SecurityEvaluator.generateStrongPassword(genLength.toInt(), includeSymbols)
                            password = generated
                            breachResult = null
                        },
                        testTag = "generate_password_button"
                    )
                }
            }
        }
    }
}
