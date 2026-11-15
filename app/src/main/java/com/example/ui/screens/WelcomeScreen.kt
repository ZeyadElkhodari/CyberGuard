package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.Strings
import com.example.model.Language
import com.example.ui.components.NeonButton
import com.example.ui.theme.*

@Composable
fun WelcomeScreen(
    onStart: () -> Unit,
    lang: Language,
    modifier: Modifier = Modifier,
    onSkip: () -> Unit = onStart
) {
    val isAr = lang == Language.AR

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF491027),
                        Color(0xFF160A12),
                        CyberDarkBg
                    ),
                    radius = 1200f
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp)
            .testTag("welcome_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar: Brand tag & version
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.appName(lang),
                    color = CyberPink,
                    letterSpacing = if (lang == Language.AR) 0.sp else 3.sp,
                    fontFamily = Strings.getFontFamily(lang),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.White.copy(alpha = 0.16f), Color.White.copy(alpha = 0.06f))
                            )
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "v1.0.0",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Center Graphic: Glowing Shield with Lock
            val infiniteTransition = rememberInfiniteTransition(label = "shield_pulse")
            val glowAlpha by infiniteTransition.animateFloat(
                initialValue = 0.22f,
                targetValue = 0.45f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1800, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "glow_alpha"
            )

            Box(
                modifier = Modifier
                    .size(240.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                CyberPink.copy(alpha = glowAlpha),
                                CyberPurple.copy(alpha = glowAlpha * 0.5f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.dp, CyberPink.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = com.example.R.drawable.ic_launcher_foreground_image),
                        contentDescription = "CyberGuard Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Title & Subtitle
            Text(
                text = Strings.tagline(lang),
                color = Color.White,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(14.dp))

            Text(
                text = Strings.welcomeSub(lang),
                color = CyberMuted,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(Modifier.weight(1f))

            // Pagination Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .width(if (index == 1) 24.dp else 7.dp)
                            .height(7.dp)
                            .clip(CircleShape)
                            .background(if (index == 1) Color.White else CyberMuted.copy(alpha = 0.4f))
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Neon Start Button
            NeonButton(
                text = Strings.getStarted(lang),
                onClick = onStart,
                icon = if (isAr) null else Icons.AutoMirrored.Filled.ArrowForward,
                testTag = "get_started_button"
            )

            // Skip Button
            TextButton(
                onClick = {
                    com.example.sound.SoundManager.playButtonClick()
                    onSkip()
                },
                modifier = Modifier.testTag("skip_button")
            ) {
                Text(
                    text = Strings.skip(lang),
                    color = CyberMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}
