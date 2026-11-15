package com.example.ui.modals

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.example.localization.Strings
import com.example.logic.SecurityEvaluator
import com.example.model.Language
import com.example.model.ScanFinding
import com.example.model.ScanResult
import com.example.repository.SecurityRepository
import com.example.sound.SoundManager
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.*
import com.example.util.BidiHelper
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Composable
fun QrScannerModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    lang: Language,
    onUpdateQrAudit: (isThreat: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val securityRepository = remember { SecurityRepository(context) }
    val isAr = lang == Language.AR

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var scannedPayload by remember { mutableStateOf<String?>(null) }
    var payloadType by remember { mutableStateOf("url") }
    var threatResult by remember { mutableStateOf<ScanResult?>(null) }
    var isEvaluatingThreat by remember { mutableStateOf(false) }
    var isTorchEnabled by remember { mutableStateOf(false) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var isDecodingImage by remember { mutableStateOf(false) }
    var isCopied by remember { mutableStateOf(false) }

    // Ensure Scan Hum stops whenever modal closes or is dismissed
    DisposableEffect(isOpen) {
        onDispose {
            SoundManager.stopScanHum()
        }
    }

    fun stopCamera() {
        try {
            isTorchEnabled = false
            cameraControl?.enableTorch(false)
        } catch (_: Exception) {}
    }

    // Process scanned content safely
    fun processPayload(payload: String) {
        val trimmed = payload.trim()
        if (trimmed.isEmpty()) return

        scannedPayload = trimmed
        isCopied = false

        // Detect payload category
        val isUrl = trimmed.startsWith("http://", ignoreCase = true) ||
                trimmed.startsWith("https://", ignoreCase = true) ||
                (trimmed.contains(".") && !trimmed.contains(" ") && (
                        trimmed.startsWith("www.", ignoreCase = true) ||
                                trimmed.contains(".com", ignoreCase = true) ||
                                trimmed.contains(".org", ignoreCase = true) ||
                                trimmed.contains(".net", ignoreCase = true) ||
                                trimmed.contains(".xyz", ignoreCase = true) ||
                                trimmed.contains(".top", ignoreCase = true) ||
                                trimmed.contains(".cc", ignoreCase = true)
                        ))

        val type = when {
            trimmed.startsWith("WIFI:", ignoreCase = true) -> "wifi_config"
            isUrl -> "url"
            trimmed.startsWith("BEGIN:VCARD", ignoreCase = true) -> "vcard"
            else -> "text"
        }
        payloadType = type

        if (isUrl) {
            val targetUrl = if (!trimmed.startsWith("http://", ignoreCase = true) && !trimmed.startsWith("https://", ignoreCase = true)) {
                "https://$trimmed"
            } else trimmed

            isEvaluatingThreat = true
            SoundManager.startScanHum()
            coroutineScope.launch {
                try {
                    // Reuses the four-provider SecurityRepository URL scan for URL payloads only
                    val scanRes = securityRepository.scanUrl(targetUrl)
                    threatResult = scanRes
                    val isThreat = scanRes.score < 60 && scanRes.overallVerdict != "Invalid input - not assessed"

                    SoundManager.stopScanHum()
                    if (isThreat) {
                        SoundManager.playAlert()
                    } else {
                        SoundManager.playSuccess()
                    }

                    onUpdateQrAudit(isThreat)
                } catch (e: Exception) {
                    SoundManager.stopScanHum()
                    val fallback = SecurityEvaluator.analyzeUrlSafety(targetUrl).copy(
                        riskLevel = "Inconclusive / Partial",
                        overallVerdict = "Inconclusive (Live threat intelligence unavailable)",
                        recommendation = if (isAr) {
                            "تعذر الوصول إلى خدمة الفحص المباشر. هذه نتيجة محلية فقط ولا تثبت أن الرابط آمن. لا تفتح الرابط قبل التحقق منه."
                        } else {
                            "Live threat intelligence could not be reached. This is a local-only assessment and does not prove the URL is safe. Do not open it until independently verified."
                        },
                        isPartialAssessment = true
                    )
                    threatResult = fallback
                    onUpdateQrAudit(fallback.score < 60 && fallback.overallVerdict != "Invalid input - not assessed")
                } finally {
                    isEvaluatingThreat = false
                }
            }
        } else {
            // Content-aware non-URL payload analysis (NOT automatically marked Safe!)
            val evalResult: ScanResult = when (type) {
                "wifi_config" -> {
                    val isOpen = trimmed.contains("T:nopass", ignoreCase = true) ||
                            trimmed.contains("T:open", ignoreCase = true) ||
                            trimmed.contains(";P:;", ignoreCase = true) ||
                            !trimmed.contains("P:", ignoreCase = true)
                    val score = if (isOpen) 40 else 75
                    val risk = if (isOpen) "Medium Risk" else "Low Risk"
                    val verdict = if (isOpen) {
                        if (isAr) "شبكة مفتوحة غير مشفرة" else "Unencrypted Wi-Fi Config"
                    } else {
                        if (isAr) "إعدادات واي فاي مشفرة" else "Encrypted Wi-Fi Config"
                    }
                    val findings = listOf(
                        ScanFinding(
                            title = if (isAr) "نوع المحتوى: إعدادات شبكة Wi-Fi" else "Payload: Wi-Fi Configuration",
                            description = if (isAr) "تم استخراج معرّف نقطة الوصول وبيانات التشفير." else "Access point SSID and authentication detected.",
                            passed = true
                        ),
                        ScanFinding(
                            title = if (isOpen) (if (isAr) "تشفير الشبكة غير مفعل" else "Open / Insecure Network") else (if (isAr) "تشفير الشبكة مفعل" else "WPA Security Enforced"),
                            description = Strings.qrWifiConfigAdvice(lang, isOpen),
                            passed = !isOpen
                        )
                    )
                    ScanResult(
                        target = trimmed,
                        type = type,
                        score = score,
                        riskLevel = risk,
                        overallVerdict = verdict,
                        findings = findings,
                        recommendation = Strings.qrWifiConfigAdvice(lang, isOpen),
                        analyzedAt = "Just now",
                        isPartialAssessment = true,
                        providers = emptyList()
                    )
                }
                "vcard" -> {
                    ScanResult(
                        target = trimmed,
                        type = type,
                        score = 70,
                        riskLevel = "Low Risk",
                        overallVerdict = Strings.qrVerdictNotEvaluated(lang),
                        findings = listOf(
                            ScanFinding(
                                title = if (isAr) "نوع المحتوى: بطاقة جهة اتصال (vCard)" else "Payload: Contact Card (vCard)",
                                description = if (isAr) "تحتوي البيانات على معلومات اتصال رقمية." else "Contains structured digital contact info.",
                                passed = true
                            ),
                            ScanFinding(
                                title = Strings.qrVerdictUnableToVerify(lang),
                                description = Strings.qrNonUrlAdvice(lang),
                                passed = false
                            )
                        ),
                        recommendation = Strings.qrNonUrlAdvice(lang),
                        analyzedAt = "Just now",
                        isPartialAssessment = true,
                        providers = emptyList()
                    )
                }
                else -> {
                    val lower = trimmed.lowercase()
                    val hasSuspiciousCommands = listOf("rm -rf", "cmd.exe", "powershell", "curl ", "wget ", "javascript:", "invoke-expression").any { lower.contains(it) }
                    val score = if (hasSuspiciousCommands) 35 else 70
                    val risk = if (hasSuspiciousCommands) "Critical Danger" else "Low Risk"
                    val verdict = if (hasSuspiciousCommands) {
                        if (isAr) "أوامر نظام مشبوهة" else "Suspicious Command String"
                    } else {
                        Strings.qrVerdictNotEvaluated(lang)
                    }
                    val findings = listOf(
                        ScanFinding(
                            title = if (isAr) "نوع المحتوى: نص عادي" else "Payload: Plain Text Data",
                            description = if (isAr) "تم فك ترميز المحتوى النصي بدون روابط توجيه." else "Decoded plain text payload without web redirection.",
                            passed = true
                        ),
                        ScanFinding(
                            title = if (hasSuspiciousCommands) (if (isAr) "رصد كلمات أوامر تنفيذية مشبوهة" else "Command / Script Keywords Flagged") else Strings.qrVerdictUnableToVerify(lang),
                            description = if (hasSuspiciousCommands) {
                                if (isAr) "يحتوي النص على كلمات مفتاحية لأوامر تنفيذية قد تشكل خطورة إذا نُفذت في الطرفية." else "Payload contains script or terminal command keywords. Do not execute."
                            } else {
                                Strings.qrNonUrlAdvice(lang)
                            },
                            passed = !hasSuspiciousCommands
                        )
                    )
                    ScanResult(
                        target = trimmed,
                        type = type,
                        score = score,
                        riskLevel = risk,
                        overallVerdict = verdict,
                        findings = findings,
                        recommendation = if (hasSuspiciousCommands) {
                            if (isAr) "تحذير: لا تقم بتنفيذ هذا المحتوى أو لصقه في موجه الأوامر." else "Warning: Do not execute or paste this text into a terminal."
                        } else {
                            Strings.qrNonUrlAdvice(lang)
                        },
                        analyzedAt = "Just now",
                        isPartialAssessment = true,
                        providers = emptyList()
                    )
                }
            }

            threatResult = evalResult
            val isThreat = evalResult.score < 60
            if (isThreat) {
                SoundManager.playAlert()
            } else {
                SoundManager.playSuccess()
            }
            onUpdateQrAudit(isThreat)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Photo picker for decoding QR from gallery images
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            isDecodingImage = true
            SoundManager.startScanHum()
            decodeBarcodeFromUri(context, uri) { decoded ->
                isDecodingImage = false
                SoundManager.stopScanHum()
                if (!decoded.isNullOrBlank()) {
                    processPayload(decoded)
                }
            }
        }
    }

    Dialog(onDismissRequest = {
        stopCamera()
        SoundManager.stopScanHum()
        onClose()
    }) {
        GlassCard(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("qr_scanner_modal"),
            borderColor = CyberPink.copy(alpha = 0.35f)
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
                            .background(CyberPink.copy(alpha = 0.18f))
                            .border(1.dp, CyberPink.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.QrCodeScanner,
                            contentDescription = null,
                            tint = CyberPink,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isAr) BidiHelper.formatBiDi("ماسح QR والباركود الآمن (Anti-Quishing)", isAr = true) else "Verified QR & Barcode Scanner",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = if (isAr) "كاميرا حية مع فحص فوري للتهديدات" else "Live CameraX & ML Kit Vision Engine",
                            color = CyberMuted,
                            fontSize = 10.sp
                        )
                    }
                }
                IconButton(onClick = {
                    stopCamera()
                    SoundManager.stopScanHum()
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
                if (scannedPayload == null) {
                    // Camera / Viewfinder Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.Black)
                            .border(2.dp, CyberPink.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasCameraPermission) {
                            // Real CameraX preview
                            AndroidView(
                                factory = { ctx ->
                                    val previewView = PreviewView(ctx).apply {
                                        scaleType = PreviewView.ScaleType.FILL_CENTER
                                    }
                                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                    val executor = ContextCompat.getMainExecutor(ctx)

                                    cameraProviderFuture.addListener({
                                        val cameraProvider = cameraProviderFuture.get()
                                        val preview = Preview.Builder().build().also {
                                            it.surfaceProvider = previewView.surfaceProvider
                                        }

                                        val barcodeScanner = BarcodeScanning.getClient(
                                            BarcodeScannerOptions.Builder()
                                                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                                                .build()
                                        )

                                        val analysisExecutor = Executors.newSingleThreadExecutor()
                                        var isScanningDone = false

                                        val imageAnalysis = ImageAnalysis.Builder()
                                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                            .build()

                                        imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy ->
                                            @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
                                            val mediaImage = imageProxy.image
                                            if (mediaImage != null && !isScanningDone) {
                                                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                                barcodeScanner.process(image)
                                                    .addOnSuccessListener { barcodes ->
                                                        for (barcode in barcodes) {
                                                            val raw = barcode.rawValue
                                                            if (!raw.isNullOrBlank() && !isScanningDone) {
                                                                isScanningDone = true
                                                                executor.execute {
                                                                    processPayload(raw)
                                                                }
                                                                break
                                                            }
                                                        }
                                                    }
                                                    .addOnCompleteListener {
                                                        imageProxy.close()
                                                    }
                                            } else {
                                                imageProxy.close()
                                            }
                                        }

                                        try {
                                            cameraProvider.unbindAll()
                                            val camera = cameraProvider.bindToLifecycle(
                                                lifecycleOwner,
                                                CameraSelector.DEFAULT_BACK_CAMERA,
                                                preview,
                                                imageAnalysis
                                            )
                                            cameraControl = camera.cameraControl
                                        } catch (_: Exception) {
                                            // Camera bind error
                                        }
                                    }, executor)
                                    previewView
                                },
                                modifier = Modifier.fillMaxSize()
                            )

                            // Targeting Overlay Frame
                            Box(
                                modifier = Modifier
                                    .size(190.dp)
                                    .border(2.dp, CyberPink.copy(alpha = 0.85f), RoundedCornerShape(18.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.QrCode,
                                    contentDescription = "Target",
                                    tint = CyberPink.copy(alpha = 0.25f),
                                    modifier = Modifier.size(80.dp)
                                )
                            }

                            // Torch / Flash button on top right of camera preview
                            IconButton(
                                onClick = {
                                    isTorchEnabled = !isTorchEnabled
                                    cameraControl?.enableTorch(isTorchEnabled)
                                    SoundManager.playButtonClick()
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(12.dp)
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = if (isTorchEnabled) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                                    contentDescription = "Torch",
                                    tint = if (isTorchEnabled) CyberPink else Color.White
                                )
                            }
                        } else {
                            // Permission Request Prompt
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(20.dp)
                            ) {
                                Icon(
                                    Icons.Filled.CameraAlt,
                                    contentDescription = null,
                                    tint = CyberPink,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = if (isAr) "إذن الكاميرا مطلوب" else "Camera Permission Required",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = if (isAr) "امنح إذن الكاميرا لتمكين المسح الفوري لرموز QR" else "Grant camera access to enable real-time QR code scanning.",
                                    color = CyberMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        SoundManager.playButtonClick()
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberPink)
                                ) {
                                    Text(if (isAr) "تفعيل الكاميرا" else "Enable Camera", color = Color.White, fontSize = 12.sp)
                                }
                            }
                        }

                        Text(
                            text = if (isAr) "وجّه الكاميرا نحو الرمز أو اختر صورة من المعرض" else "Point at QR code or pick an image",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }

                    // Action buttons: Pick from Gallery + Camera Permission
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Gallery photo picker button
                        OutlinedButton(
                            onClick = {
                                SoundManager.playButtonClick()
                                photoPickerLauncher.launch("image/*")
                            },
                            modifier = Modifier.weight(1f).testTag("qr_pick_image_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBlue.copy(alpha = 0.4f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberBlue)
                        ) {
                            Icon(Icons.Filled.PhotoLibrary, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (isAr) "فحص صورة QR" else "Pick Image", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        if (!hasCameraPermission) {
                            Button(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberPink)
                            ) {
                                Icon(Icons.Filled.Videocam, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text(if (isAr) "تفعيل الكاميرا" else "Camera", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (isDecodingImage || isEvaluatingThreat) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth(),
                            color = CyberPink,
                            trackColor = CyberCard2
                        )
                    }

                } else {
                    // Result View (Decoded QR Payload & Threat Assessment)
                    if (isEvaluatingThreat) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                color = CyberPink,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = if (isAr) "جاري فحص الرابط عبر استخبارات التهديدات..." else "Inspecting link via threat intelligence...",
                                color = CyberMuted,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        val result = threatResult
                        val isDangerous = result != null && (
                            result.score < 60 ||
                                result.providers.any { it.status == "malicious" } ||
                                result.overallVerdict.contains("Malicious", ignoreCase = true)
                            )
                        val isNonUrl = payloadType != "url"
                        val hasUnavailableProvider = result?.providers?.any {
                            it.status in setOf("unavailable", "unconfigured", "submitted", "rate_limited", "configuration_error", "not_indexed")
                        } == true
                        val isInconclusive = result == null || result.isPartialAssessment ||
                            result.overallVerdict.contains("Inconclusive", ignoreCase = true) ||
                            hasUnavailableProvider
                        val isWarning = !isDangerous && (isNonUrl || isInconclusive || (result != null && result.score < 90))
                        val (bannerBg, bannerBorder, bannerTint) = when {
                            isDangerous -> Triple(CyberRed.copy(alpha = 0.15f), CyberRed.copy(alpha = 0.45f), CyberRed)
                            isWarning -> Triple(CyberYellow.copy(alpha = 0.15f), CyberYellow.copy(alpha = 0.45f), CyberYellow)
                            else -> Triple(CyberGreen.copy(alpha = 0.15f), CyberGreen.copy(alpha = 0.45f), CyberGreen)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(bannerBg)
                                .border(1.dp, bannerBorder, RoundedCornerShape(18.dp))
                                .padding(16.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            imageVector = when {
                                                isDangerous -> Icons.Filled.Warning
                                                isWarning -> Icons.Filled.HelpOutline
                                                else -> Icons.Filled.CheckCircle
                                            },
                                            contentDescription = null,
                                            tint = bannerTint,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Column {
                                            val titleText = when {
                                                isDangerous -> if (isAr) "تحذير: رمز QR خطير ومشبوه!" else "CRITICAL: Malicious QR Code Detected!"
                                                isNonUrl -> result?.overallVerdict ?: Strings.qrVerdictNotEvaluated(lang)
                                                isInconclusive -> if (isAr) "نتيجة غير حاسمة — لم يتم تأكيد الأمان" else "Inconclusive — Safety Not Verified"
                                                isWarning -> if (isAr) "تنبيه: راجع نتيجة الفحص بحذر" else "Caution: Review Scan Result"
                                                else -> if (isAr) "رمز QR سليم وفق الفحوصات المتاحة" else "No Threat Found in Available Checks"
                                            }
                                            Text(
                                                text = titleText,
                                                color = bannerTint,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = if (isAr) "النوع: $payloadType | التقييم: ${result?.score ?: 70}/100" else "Type: $payloadType | Score: ${result?.score ?: 70}/100",
                                                color = CyberMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    // Score badge
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${result?.score ?: 70}%",
                                            color = bannerTint,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                // Decoded Raw Payload Box with Copy Button
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isAr) "المحتوى المستخرج بأمان:" else "Decoded Raw Payload:",
                                            color = CyberMuted,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        TextButton(
                                            onClick = {
                                                scannedPayload?.let {
                                                    clipboardManager.setText(AnnotatedString(it))
                                                    isCopied = true
                                                    SoundManager.playButtonClick()
                                                }
                                            },
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isCopied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                                                contentDescription = "Copy",
                                                tint = if (isCopied) CyberGreen else CyberPink,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = if (isCopied) (if (isAr) "تم النسخ" else "Copied") else (if (isAr) "نسخ المحتوى" else "Copy"),
                                                color = if (isCopied) CyberGreen else CyberPink,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = scannedPayload ?: "",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            lineHeight = 15.sp
                                        )
                                    }
                                }

                                if (result != null) {
                                    Text(
                                        text = result.recommendation,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    )

                                    // If URL and has threat providers, display them cleanly
                                    if (result.type == "url" && result.providers.isNotEmpty()) {
                                        Text(
                                            text = if (isAr) "مزودو استخبارات التهديدات:" else "Threat Intelligence Providers:",
                                            color = CyberMuted,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        result.providers.forEach { provider ->
                                            val pColor = when (provider.status) {
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
                                                    .border(1.dp, pColor.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                                                    .padding(8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(Modifier.weight(1f)) {
                                                    Text(provider.name, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                                    Text(provider.details, color = CyberMuted, fontSize = 9.sp)
                                                }
                                                Spacer(Modifier.width(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(pColor.copy(alpha = 0.2f))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = provider.verdict,
                                                        color = pColor,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Findings checklist
                                    if (result.findings.isNotEmpty()) {
                                        Text(
                                            text = if (isAr) "مؤشرات وتدقيقات المحتوى:" else "Content Security Indicators:",
                                            color = CyberMuted,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        result.findings.forEach { finding ->
                                            Row(verticalAlignment = Alignment.Top) {
                                                Icon(
                                                    imageVector = if (finding.passed) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                                                    contentDescription = null,
                                                    tint = if (finding.passed) CyberGreen else (if (isNonUrl) CyberYellow else CyberRed),
                                                    modifier = Modifier.size(14.dp).padding(top = 2.dp)
                                                )
                                                Spacer(Modifier.width(6.dp))
                                                Column {
                                                    Text(finding.title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                                    Text(finding.description, color = CyberMuted, fontSize = 9.sp, lineHeight = 13.sp)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Rescan button
                        NeonButton(
                            text = if (isAr) "مسح رمز آخر" else "Scan Another QR Code",
                            onClick = {
                                SoundManager.playButtonClick()
                                scannedPayload = null
                                threatResult = null
                                isCopied = false
                            },
                            testTag = "qr_rescan_button"
                        )
                    }
                }
            }
        }
    }
}

/**
 * Decodes barcode/QR code from a gallery image URI using Google ML Kit with ZXing fallback.
 */
private fun decodeBarcodeFromUri(
    context: Context,
    uri: Uri,
    onDecoded: (String?) -> Unit
) {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (bitmap == null) {
            onDecoded(null)
            return
        }

        // 1. Try Google ML Kit Barcode Scanner
        val image = InputImage.fromBitmap(bitmap, 0)
        val scanner = BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build()
        )

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val first = barcodes.firstOrNull()?.rawValue
                if (!first.isNullOrBlank()) {
                    onDecoded(first)
                } else {
                    // 2. Fallback to ZXing
                    val zxingResult = decodeWithZxing(bitmap)
                    onDecoded(zxingResult)
                }
            }
            .addOnFailureListener {
                val zxingResult = decodeWithZxing(bitmap)
                onDecoded(zxingResult)
            }
    } catch (_: Exception) {
        onDecoded(null)
    }
}

/**
 * ZXing pure-software decoder fallback
 */
private fun decodeWithZxing(bitmap: Bitmap): String? {
    return try {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
        val source = RGBLuminanceSource(width, height, pixels)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = MultiFormatReader()
        val result = reader.decode(binaryBitmap)
        result.text
    } catch (_: Exception) {
        null
    }
}
