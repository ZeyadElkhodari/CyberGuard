package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.localization.Strings
import com.example.model.Language
import com.example.model.PreferredAddress
import com.example.model.UserProfile
import com.example.ui.components.NeonButton
import com.example.ui.components.UserProfileAvatar
import com.example.ui.theme.*
import com.example.util.ProfileImageStorage

@Composable
fun ProfileSetupScreen(
    lang: Language,
    onCompleteProfile: (UserProfile) -> Unit,
    modifier: Modifier = Modifier,
    initialProfile: UserProfile = UserProfile()
) {
    val isAr = lang == Language.AR
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val imageScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf(initialProfile.name) }
    var preferredAddress by remember { mutableStateOf(initialProfile.preferredAddress) }
    var selectedPictureUri by remember { mutableStateOf<String?>(initialProfile.profilePictureUri) }
    var selectedAvatarId by remember { mutableStateOf<String?>(initialProfile.avatarId) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var hasAttemptedSubmit by remember { mutableStateOf(false) }

    // Android Photo Picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageScope.launch {
                val savedPath = ProfileImageStorage.persist(context, uri)
                if (savedPath != null) {
                    selectedPictureUri = savedPath
                    selectedAvatarId = null
                }
            }
        }
    }

    fun validate(): Boolean {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> {
                nameError = Strings.nameValidationEmpty(lang)
                false
            }
            trimmed.length > 30 -> {
                nameError = Strings.nameValidationTooLong(lang)
                false
            }
            else -> {
                nameError = null
                true
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF380E24),
                        Color(0xFF140A15),
                        CyberDarkBg
                    ),
                    radius = 1400f
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .testTag("profile_setup_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 22.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Step Progress Indicator & Brand
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Strings.appName(lang),
                    color = CyberPink,
                    letterSpacing = if (isAr) 0.sp else 2.sp,
                    fontFamily = Strings.getFontFamily(lang),
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberCard2)
                        .border(1.dp, CyberPink.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = Strings.profileSetupStep(lang),
                        color = CyberPink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // 1. Live Profile Avatar Preview with Glowing Frame
            Box(
                modifier = Modifier
                    .size(114.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                CyberPink.copy(alpha = 0.35f),
                                CyberPurple.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                val previewProfile = UserProfile(
                    name = name.trim(),
                    preferredAddress = preferredAddress,
                    profilePictureUri = selectedPictureUri,
                    avatarId = selectedAvatarId,
                    isCompleted = false
                )
                UserProfileAvatar(
                    profile = previewProfile,
                    size = 96.dp,
                    borderColor = CyberPink
                )
            }

            Spacer(Modifier.height(12.dp))

            // 2. Welcome Title & Description
            Text(
                text = Strings.profileSetupTitle(lang),
                color = Color.White,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                fontFamily = Strings.getFontFamily(lang)
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = Strings.profileSetupSub(lang),
                color = CyberMuted,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
                fontFamily = Strings.getFontFamily(lang),
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(Modifier.height(20.dp))

            // 3. Name Input Field (Mandatory)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = CyberPink,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = Strings.nameLabel(lang),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = " *",
                        color = CyberRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        if (it.length <= 30) {
                            name = it
                            if (hasAttemptedSubmit) validate()
                        }
                    },
                    placeholder = {
                        Text(
                            text = Strings.namePlaceholder(lang),
                            color = CyberMuted.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    },
                    singleLine = true,
                    isError = nameError != null,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberPink,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        errorBorderColor = CyberRed,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CyberCard,
                        unfocusedContainerColor = CyberCard
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("profile_name_input")
                )

                AnimatedVisibility(
                    visible = nameError != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = nameError ?: "",
                        color = CyberRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 6.dp, top = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            // 4. Gender / Preferred Address Selection Cards
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = Strings.genderTitle(lang),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = Strings.genderSub(lang),
                    color = CyberMuted,
                    fontSize = 11.sp
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val options = listOf(
                        PreferredAddress.MALE,
                        PreferredAddress.FEMALE,
                        PreferredAddress.UNSPECIFIED
                    )

                    options.forEach { option ->
                        val isSelected = preferredAddress == option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) CyberPink.copy(alpha = 0.16f) else CyberCard)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) CyberPink else Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { preferredAddress = option }
                                .padding(vertical = 12.dp, horizontal = 4.dp)
                                .testTag("address_option_${option.name.lowercase()}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = Strings.addressOptionLabel(option, lang),
                                color = if (isSelected) CyberPink else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // 5. Profile Picture (Optional) & Gallery Picker
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = Strings.photoPickerTitle(lang),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = Strings.optionalBadge(lang),
                            color = CyberMuted,
                            fontSize = 11.sp
                        )
                    }

                    if (selectedPictureUri != null || selectedAvatarId != null) {
                        TextButton(
                            onClick = {
                                selectedPictureUri = null
                                selectedAvatarId = null
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                            modifier = Modifier.testTag("remove_photo_button")
                        ) {
                            Text(
                                text = Strings.removePhoto(lang),
                                color = CyberRed,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Gallery Upload Button
                OutlinedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = CyberCard,
                        contentColor = CyberBlue
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        CyberBlue.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("gallery_picker_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.AddPhotoAlternate,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = Strings.uploadFromGallery(lang),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(Modifier.height(14.dp))

                // 6. Built-in Sample Avatars (Male, Female, Default)
                Text(
                    text = Strings.sampleAvatarsHeader(lang),
                    color = CyberMuted,
                    fontSize = 11.sp
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Male Avatar Choice
                    val isMaleSelected = selectedPictureUri == null && selectedAvatarId == "avatar_male"
                    AvatarChoiceItem(
                        drawableRes = R.drawable.img_avatar_male,
                        label = Strings.avatarMaleName(lang),
                        isSelected = isMaleSelected,
                        accentColor = CyberBlue,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedAvatarId = "avatar_male"
                            selectedPictureUri = null
                        },
                        testTag = "sample_avatar_male"
                    )

                    // Female Avatar Choice
                    val isFemaleSelected = selectedPictureUri == null && selectedAvatarId == "avatar_female"
                    AvatarChoiceItem(
                        drawableRes = R.drawable.img_avatar_female,
                        label = Strings.avatarFemaleName(lang),
                        isSelected = isFemaleSelected,
                        accentColor = CyberPink,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedAvatarId = "avatar_female"
                            selectedPictureUri = null
                        },
                        testTag = "sample_avatar_female"
                    )

                    // Default Avatar Choice
                    val isDefaultSelected = selectedPictureUri == null && selectedAvatarId == null
                    AvatarChoiceItem(
                        drawableRes = R.drawable.img_avatar_default,
                        label = Strings.avatarDefaultName(lang),
                        isSelected = isDefaultSelected,
                        accentColor = CyberPurple,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedAvatarId = null
                            selectedPictureUri = null
                        },
                        testTag = "sample_avatar_default"
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            // 7. Continue Button
            NeonButton(
                text = Strings.continueAction(lang),
                onClick = {
                    hasAttemptedSubmit = true
                    if (validate()) {
                        focusManager.clearFocus()
                        onCompleteProfile(
                            UserProfile(
                                name = name.trim(),
                                preferredAddress = preferredAddress,
                                profilePictureUri = selectedPictureUri,
                                avatarId = selectedAvatarId,
                                isCompleted = true
                            )
                        )
                    }
                },
                icon = if (isAr) null else Icons.AutoMirrored.Filled.ArrowForward,
                testTag = "profile_continue_button"
            )

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun AvatarChoiceItem(
    drawableRes: Int,
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.16f) else CyberCard)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) accentColor else Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .border(1.dp, if (isSelected) accentColor else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = label,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            color = if (isSelected) accentColor else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
