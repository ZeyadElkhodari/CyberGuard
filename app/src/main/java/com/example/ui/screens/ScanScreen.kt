package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.model.Language
import com.example.sound.SoundManager
import com.example.ui.theme.*

@Composable
fun ScanScreen(
    lang: Language,
    onOpenScanTool: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAr = lang == Language.AR

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp)
            .testTag("scan_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Page Title & Subtitle
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = Strings.securityScanTitle(lang),
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
                text = Strings.securityScanSub(lang),
                color = CyberMuted,
                fontSize = 13.sp,
                fontFamily = Strings.getFontFamily(lang),
                lineHeight = 20.sp
            )
        }

        // Prominent Featured QR Code Scanner Card
        QrScannerFeaturedCard(
            title = Strings.qrScannerTitle(lang),
            description = Strings.qrScannerDesc(lang),
            actionText = Strings.scanQrAction(lang),
            isAr = isAr,
            onClick = {
                SoundManager.playButtonClick()
                onOpenScanTool("qr")
            },
            testTag = "scan_tool_qr"
        )

        Spacer(Modifier.height(4.dp))

        // 4 Standard Scan Tools (All open their dedicated screens)
        ScanOptionCard(
            icon = Icons.Filled.Link,
            title = Strings.linkScanner(lang),
            detail = Strings.linkScannerDesc(lang),
            color = CyberRed,
            isAr = isAr,
            onClick = {
                SoundManager.playButtonClick()
                onOpenScanTool("link")
            },
            testTag = "scan_tool_link"
        )

        ScanOptionCard(
            icon = Icons.Filled.Lock,
            title = Strings.passwordCheck(lang),
            detail = Strings.passwordCheckDesc(lang),
            color = CyberBlue,
            isAr = isAr,
            onClick = {
                SoundManager.playButtonClick()
                onOpenScanTool("password")
            },
            testTag = "scan_tool_password"
        )

        ScanOptionCard(
            icon = Icons.Filled.Mail,
            title = Strings.phishingMessageCheck(lang),
            detail = Strings.phishingMessageDesc(lang),
            color = CyberPurple,
            isAr = isAr,
            onClick = {
                SoundManager.playButtonClick()
                onOpenScanTool("phishing-msg")
            },
            testTag = "scan_tool_phishing_msg"
        )

        // Wi-Fi Safety Option Card (Independent option navigating to dedicated Wi-Fi scanning screen)
        ScanOptionCard(
            icon = Icons.Filled.Wifi,
            title = Strings.wifiSafety(lang),
            detail = Strings.wifiSafetyDesc(lang),
            color = CyberGreen,
            isAr = isAr,
            onClick = {
                SoundManager.playButtonClick()
                onOpenScanTool("wifi")
            },
            testTag = "scan_tool_wifi"
        )

        Spacer(Modifier.height(4.dp))

        // Privacy First Information Callout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.verticalGradient(listOf(CyberCard.copy(alpha = 0.82f), CyberCard.copy(alpha = 0.62f))))
                .border(1.dp, CyberBlue.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = null,
                tint = CyberBlue,
                modifier = Modifier
                    .size(24.dp)
                    .padding(top = 2.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = Strings.privacyFirst(lang),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = Strings.privacyFirstDesc(lang),
                    color = CyberMuted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ScanOptionCard(
    icon: ImageVector,
    title: String,
    detail: String,
    color: Color,
    isAr: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        color.copy(alpha = 0.18f),
                        CyberCard
                    )
                )
            )
            .border(1.dp, color.copy(alpha = 0.28f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color.copy(alpha = 0.18f))
                .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = detail,
                color = CyberMuted,
                fontSize = 11.sp
            )
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = CyberMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun QrScannerFeaturedCard(
    title: String,
    description: String,
    actionText: String,
    isAr: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFF48122B),
                        CyberCard,
                        Color(0xFF131018)
                    )
                )
            )
            .border(1.5.dp, CyberPink.copy(alpha = 0.45f), RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(18.dp)
            .testTag(testTag),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberPink.copy(alpha = 0.20f))
                    .border(1.dp, CyberPink.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.QrCodeScanner,
                    contentDescription = title,
                    tint = CyberPink,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberPink.copy(alpha = 0.18f))
                            .border(0.5.dp, CyberPink.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isAr) "فوري" else "LIVE",
                            color = CyberPink,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = description,
                    color = CyberMuted,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // Action button
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CyberPink,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("scan_qr_action_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = actionText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
