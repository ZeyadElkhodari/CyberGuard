package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.model.UserProfile
import com.example.ui.theme.CyberCard2
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberPurple

@Composable
fun UserProfileAvatar(
    profile: UserProfile,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    borderColor: Color = CyberPink
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        borderColor.copy(alpha = 0.35f),
                        CyberCard2
                    )
                )
            )
            .border(1.5.dp, borderColor.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        val uri = profile.profilePictureUri
        if (!uri.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(uri)
                    .crossfade(true)
                    .build(),
                contentDescription = "User profile picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    DefaultAvatarGraphic(size = size, borderColor = borderColor)
                },
                error = {
                    FallbackAvatarGraphic(avatarId = profile.avatarId, size = size, borderColor = borderColor)
                }
            )
        } else {
            FallbackAvatarGraphic(avatarId = profile.avatarId, size = size, borderColor = borderColor)
        }
    }
}

@Composable
fun FallbackAvatarGraphic(
    avatarId: String?,
    size: Dp,
    borderColor: Color
) {
    when (avatarId) {
        "avatar_male" -> {
            Image(
                painter = painterResource(id = R.drawable.img_avatar_male),
                contentDescription = "Cyber Agent Male Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        "avatar_female" -> {
            Image(
                painter = painterResource(id = R.drawable.img_avatar_female),
                contentDescription = "Cyber Specialist Female Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        else -> {
            DefaultAvatarGraphic(size = size, borderColor = borderColor)
        }
    }
}

@Composable
fun DefaultAvatarGraphic(
    size: Dp,
    borderColor: Color
) {
    Image(
        painter = painterResource(id = R.drawable.img_avatar_default),
        contentDescription = "CyberGuard Shield Avatar",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}
