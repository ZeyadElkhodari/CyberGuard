package com.example.ui.modals

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.localization.Strings
import com.example.logic.WifiDetectedRisk
import com.example.logic.WifiOverallResult
import com.example.logic.WifiRiskSeverity
import com.example.logic.WifiSecurityReport
import com.example.logic.WifiSecurityScanner
import com.example.model.Language
import com.example.util.BidiHelper
import com.example.sound.SoundManager
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun WifiScannerModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    lang: Language,
    onUpdateAssessment: (score: Int, passedControls: Int, totalControls: Int, report: WifiSecurityReport) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    val isAr = lang == Language.AR
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var isScanning by remember { mutableStateOf(false) }
    var report by remember { mutableStateOf<WifiSecurityReport?>(null) }
    var hasLocationPermission by remember {
        mutableStateOf(WifiSecurityScanner.hasLocationPermission(context))
    }
    var locationServicesEnabled by remember {
        mutableStateOf(WifiSecurityScanner.isLocationServicesEnabled(context))
    }

    DisposableEffect(isOpen) {
        onDispose {
            SoundManager.stopScanHum()
        }
    }

    fun executeScan() {
        if (isScanning) return
        isScanning = true
        SoundManager.startScanHum()
        coroutineScope.launch {
            try {
                delay(850) // Smooth visual and sound feedback
                val result = withContext(Dispatchers.IO) {
                    WifiSecurityScanner.scanConnectedWifi(context, isAr)
                }
                report = result
                SoundManager.stopScanHum()

                if (result.overallResult == WifiOverallResult.SECURE) {
                    SoundManager.playSuccess()
                } else if (result.overallResult == WifiOverallResult.RISKY || result.overallResult == WifiOverallResult.MODERATE) {
                    SoundManager.playAlert()
                }

                if (result.score != null) {
                    val passed = ((result.score / 100f) * 5).toInt().coerceIn(0, 5)
                    onUpdateAssessment(result.score, passed, 5, result)
                }
            } catch (_: Exception) {
                SoundManager.stopScanHum()
            } finally {
                isScanning = false
                SoundManager.stopScanHum()
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        locationServicesEnabled = WifiSecurityScanner.isLocationServicesEnabled(context)
        executeScan()
    }

    // Refresh permission and Location Services state when the user returns from Android Settings.
    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val permissionNowGranted = WifiSecurityScanner.hasLocationPermission(context)
                val locationNowEnabled = WifiSecurityScanner.isLocationServicesEnabled(context)
                val locationWasDisabled = !locationServicesEnabled
                hasLocationPermission = permissionNowGranted
                locationServicesEnabled = locationNowEnabled
                if (permissionNowGranted && locationNowEnabled && locationWasDisabled) {
                    executeScan()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Run initial scan automatically when opened
    LaunchedEffect(isOpen) {
        if (isOpen && report == null) {
            executeScan()
        }
    }

    Dialog(onDismissRequest = {
        SoundManager.stopScanHum()
        onClose()
    }) {
        GlassCard(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
                .testTag("wifi_scanner_modal"),
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
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyberGreen.copy(alpha = 0.18f))
                            .border(1.dp, CyberGreen.copy(alpha = 0.45f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Wifi,
                            contentDescription = null,
                            tint = CyberGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = Strings.wifiScanCardTitle(lang),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = Strings.wifiScanCardSubtitle(lang),
                            color = CyberMuted,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                        Text(
                            text = Strings.wifiScanBilingualTitle(),
                            color = CyberGreen.copy(alpha = 0.9f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
                IconButton(
                    onClick = {
                        SoundManager.playButtonClick()
                        SoundManager.stopScanHum()
                        onClose()
                    },
                    modifier = Modifier.testTag("close_wifi_modal_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = Strings.close(lang),
                        tint = CyberMuted
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Main Content Scroll
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Location Permission / Services Notification Banner (Non-blocking)
                if (!hasLocationPermission) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CyberCard2)
                            .border(1.dp, CyberBlue.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = CyberBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            text = Strings.locationPermNotice(lang),
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = {
                                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyberBlue.copy(alpha = 0.25f),
                                contentColor = CyberBlue
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(
                                text = Strings.grantPermButton(lang),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else if (!locationServicesEnabled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CyberCard2)
                            .border(1.dp, CyberYellow.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = CyberYellow,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Strings.locationServicesDisabled(lang),
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = Strings.locationSettingsHelp(lang),
                                color = CyberMuted,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    SoundManager.playButtonClick()
                                    runCatching {
                                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
                                modifier = Modifier.height(34.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberYellow),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberYellow.copy(alpha = 0.6f))
                            ) {
                                Text(Strings.openLocationSettings(lang), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Primary Scan / Rescan Button
                Button(
                    onClick = {
                        SoundManager.playButtonClick()
                        if (!hasLocationPermission) {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        } else {
                            executeScan()
                        }
                    },
                    enabled = !isScanning,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberGreen,
                        contentColor = Color.Black,
                        disabledContainerColor = CyberGreen.copy(alpha = 0.4f),
                        disabledContentColor = Color.Black.copy(alpha = 0.6f)
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wifi_modal_scan_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = Strings.scanningWifi(lang),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        } else {
                            Icon(
                                imageVector = if (report == null) Icons.Filled.WifiFind else Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = if (report == null) Strings.startWifiScan(lang) else Strings.rescanLabel(lang),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Scanning Progress Bar
                if (isScanning) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape),
                        color = CyberGreen,
                        trackColor = CyberGreen.copy(alpha = 0.2f)
                    )
                }

                // Results Container
                report?.let { currentReport ->
                    val verdictColor = when (currentReport.overallResult) {
                        WifiOverallResult.SECURE -> CyberGreen
                        WifiOverallResult.MODERATE -> CyberYellow
                        WifiOverallResult.RISKY -> CyberRed
                        WifiOverallResult.UNKNOWN -> CyberMuted
                        WifiOverallResult.DISCONNECTED -> CyberMuted
                    }

                    val verdictText = when (currentReport.overallResult) {
                        WifiOverallResult.SECURE -> Strings.verdictSecure(lang)
                        WifiOverallResult.MODERATE -> Strings.verdictModerate(lang)
                        WifiOverallResult.RISKY -> Strings.verdictRisky(lang)
                        WifiOverallResult.UNKNOWN -> Strings.verdictUnknown(lang)
                        WifiOverallResult.DISCONNECTED -> Strings.verdictDisconnected(lang)
                    }

                    // 1. Overall Security Score Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(verdictColor.copy(alpha = 0.12f))
                            .border(1.dp, verdictColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = Strings.overallResultLabel(lang),
                                color = CyberMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = verdictText,
                                color = verdictColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        if (currentReport.score != null) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(verdictColor.copy(alpha = 0.22f))
                                    .border(1.dp, verdictColor.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${currentReport.score}/100",
                                        color = verdictColor,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = Strings.securityScoreLabel(lang),
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CyberCard2)
                                    .border(1.dp, CyberBorder, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = Strings.unableToVerify(lang),
                                    color = CyberMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (currentReport.isConnected) {
                    // 2. Network Specifications Card
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CyberCard2)
                            .border(1.dp, CyberBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ModalSpecRow(
                            label = Strings.networkNameLabel(lang),
                            value = currentReport.ssid ?: Strings.ssidUnavailable(lang),
                            icon = Icons.Filled.Wifi
                        )

                        currentReport.localIpv4Address?.let { address ->
                            ModalSpecRow(
                                label = Strings.localIpLabel(lang),
                                value = address,
                                icon = Icons.Filled.Wifi
                            )
                        }
                        currentReport.defaultGatewayAddress?.let { gateway ->
                            ModalSpecRow(
                                label = Strings.gatewayLabel(lang),
                                value = gateway,
                                icon = Icons.Filled.Language
                            )
                        }

                        ModalSpecRow(
                            label = Strings.securityTypeLabel(lang),
                            value = currentReport.securityType,
                            icon = Icons.Filled.Lock,
                            valueColor = verdictColor
                        )

                        if (currentReport.cipherSuite != null) {
                            ModalSpecRow(
                                label = Strings.cipherSuiteLabel(lang),
                                value = currentReport.cipherSuite,
                                icon = Icons.Filled.EnhancedEncryption
                            )
                        }

                        ModalSpecRow(
                            label = Strings.connectionStatusLabel(lang),
                            value = currentReport.connectionStatus,
                            icon = Icons.Filled.Sensors
                        )

                        if (currentReport.frequencyBand != null || currentReport.linkSpeedMbps != null) {
                            val freqSpeed = listOfNotNull(
                                currentReport.frequencyBand,
                                currentReport.linkSpeedMbps?.let { "$it Mbps" }
                            ).joinToString(" • ")

                            ModalSpecRow(
                                label = Strings.frequencyBandLabel(lang),
                                value = freqSpeed,
                                icon = Icons.Filled.Speed
                            )
                        }

                        if (currentReport.isConnected) {
                            ModalSpecRow(
                                label = Strings.internetValidatedLabel(lang),
                                value = if (currentReport.hasInternet) Strings.internetVerified(lang) else Strings.internetOffline(lang),
                                icon = Icons.Filled.Wifi,
                                valueColor = if (currentReport.hasInternet) CyberGreen else CyberYellow
                            )

                            ModalSpecRow(
                                label = Strings.captivePortalLabel(lang),
                                value = if (currentReport.isCaptivePortal) Strings.captivePortalDetected(lang) else Strings.captivePortalNone(lang),
                                icon = Icons.Filled.Security,
                                valueColor = if (currentReport.isCaptivePortal) CyberYellow else CyberMuted
                            )
                        }
                    }

                    // 3. Independent Signal Quality Section (Completely Separated from Security)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF101418))
                            .border(1.dp, CyberBlue.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.NetworkCheck,
                                    contentDescription = null,
                                    tint = CyberBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = Strings.signalSectionTitle(lang),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            currentReport.signalQuality?.let {
                                Text(
                                    text = it,
                                    color = CyberBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Text(
                            text = Strings.signalQualityDisclaimerDetail(lang),
                            color = CyberMuted,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(CyberCard2)
                                .border(1.dp, CyberBorder, RoundedCornerShape(14.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isAr) "لا يوجد اتصال بشبكة Wi-Fi" else "No Wi-Fi network connected",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isAr) "اتصل بشبكة Wi-Fi ثم أعد الفحص لعرض معلومات الشبكة الحالية. لا يتم عرض بيانات قديمة أو تقديرية." else "Connect to Wi-Fi and scan again to view current network details. No previous or simulated network data is displayed.",
                                color = CyberMuted,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // 4. Detected Security Risks Section
                    if (currentReport.risks.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF181214))
                                .border(1.dp, CyberRed.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = null,
                                    tint = CyberRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = Strings.detectedIssuesLabel(lang),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            currentReport.risks.forEach { risk ->
                                RiskItemCard(risk = risk, lang = lang, isAr = isAr)
                            }
                        }
                    } else if (currentReport.overallResult == WifiOverallResult.SECURE) {
                        // Clean status confirmation
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(CyberGreen.copy(alpha = 0.12f))
                                .border(1.dp, CyberGreen.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = CyberGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = Strings.noRisksDetectedTitle(lang),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = Strings.noRisksDetectedDesc(lang),
                                    color = CyberMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    // 5. Actionable Security Recommendations
                    if (currentReport.recommendations.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(CyberCard2)
                                .border(1.dp, CyberGreen.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.Shield,
                                    contentDescription = null,
                                    tint = CyberGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = Strings.recommendationsLabel(lang),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }

                            currentReport.recommendations.forEach { rec ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "✓ ",
                                        color = CyberGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = BidiHelper.formatBiDi(rec, isAr = isAr),
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    // 6. API Limitation Footnote
                    currentReport.apiLimitationNote?.let { note ->
                        Text(
                            text = BidiHelper.formatBiDi(note, isAr = isAr),
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
private fun RiskItemCard(
    risk: WifiDetectedRisk,
    lang: Language,
    isAr: Boolean
) {
    val severityColor = when (risk.severity) {
        WifiRiskSeverity.CRITICAL -> CyberRed
        WifiRiskSeverity.HIGH -> Color(0xFFFF5252)
        WifiRiskSeverity.MEDIUM -> CyberYellow
        WifiRiskSeverity.LOW -> CyberBlue
    }

    val severityText = when (risk.severity) {
        WifiRiskSeverity.CRITICAL -> Strings.severityCritical(lang)
        WifiRiskSeverity.HIGH -> Strings.severityHigh(lang)
        WifiRiskSeverity.MEDIUM -> Strings.severityMedium(lang)
        WifiRiskSeverity.LOW -> Strings.severityLow(lang)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF100C0E))
            .border(1.dp, severityColor.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isAr) risk.nameAr else risk.nameEn,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(severityColor.copy(alpha = 0.18f))
                    .border(0.5.dp, severityColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = severityText,
                    color = severityColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        Text(
            text = if (isAr) BidiHelper.formatBiDi(risk.explanationAr, true) else risk.explanationEn,
            color = Color.White.copy(alpha = 0.82f),
            fontSize = 10.sp,
            lineHeight = 14.sp
        )

        // Reason
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Text(
                text = "${Strings.riskReasonLabel(lang)}: ",
                color = CyberMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isAr) BidiHelper.formatBiDi(risk.reasonAr, true) else risk.reasonEn,
                color = CyberMuted,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                modifier = Modifier.weight(1f)
            )
        }

        // Practical Recommendation
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Text(
                text = "${Strings.riskRecommendationLabel(lang)}: ",
                color = CyberGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isAr) BidiHelper.formatBiDi(risk.recommendationAr, true) else risk.recommendationEn,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 10.sp,
                lineHeight = 14.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ModalSpecRow(
    label: String,
    value: String,
    icon: ImageVector,
    valueColor: Color = Color.White
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                color = CyberMuted,
                fontSize = 11.sp,
                maxLines = 1,
                softWrap = false
            )
        }
        Text(
            text = value,
            color = valueColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2
        )
    }
}
