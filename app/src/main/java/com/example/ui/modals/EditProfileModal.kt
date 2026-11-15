package com.example.ui.modals

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
fun EditProfileModal(
    isOpen: Boolean,
    onClose: () -> Unit,
    currentProfile: UserProfile,
    onSaveProfile: (UserProfile) -> Unit,
    lang: Language
) {
    if (!isOpen) return

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val imageScope = rememberCoroutineScope()
    var editName by remember(currentProfile) { mutableStateOf(currentProfile.name) }
    var editAddress by remember(currentProfile) { mutableStateOf(currentProfile.preferredAddress) }
    var editPictureUri by remember(currentProfile) { mutableStateOf(currentProfile.profilePictureUri) }
    var editAvatarId by remember(currentProfile) { mutableStateOf(currentProfile.avatarId) }

    var nameError by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            imageScope.launch {
                val savedPath = ProfileImageStorage.persist(context, uri)
                if (savedPath != null) {
                    editPictureUri = savedPath
                    editAvatarId = null
                }
            }
        }
    }

    fun validate(): Boolean {
        val trimmed = editName.trim()
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

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(24.dp))
                .background(CyberDarkBg)
                .border(1.5.dp, CyberPink.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .padding(20.dp)
                .testTag("edit_profile_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Modal Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.editProfile(lang),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Strings.getFontFamily(lang)
                    )
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("close_edit_profile_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = CyberMuted
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Avatar Preview
                val preview = UserProfile(
                    name = editName.trim(),
                    preferredAddress = editAddress,
                    profilePictureUri = editPictureUri,
                    avatarId = editAvatarId,
                    isCompleted = true
                )
                UserProfileAvatar(
                    profile = preview,
                    size = 80.dp,
                    borderColor = CyberPink
                )

                Spacer(Modifier.height(16.dp))

                // Name Input
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = Strings.nameLabel(lang),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value = editName,
                        onValueChange = {
                            if (it.length <= 30) {
                                editName = it
                                if (nameError != null) validate()
                            }
                        },
                        singleLine = true,
                        isError = nameError != null,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        shape = RoundedCornerShape(14.dp),
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
                            .testTag("edit_profile_name_input")
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
                            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Preferred Address
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = Strings.genderTitle(lang),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val options = listOf(
                            PreferredAddress.MALE,
                            PreferredAddress.FEMALE,
                            PreferredAddress.UNSPECIFIED
                        )
                        options.forEach { option ->
                            val isSelected = editAddress == option
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) CyberPink.copy(alpha = 0.16f) else CyberCard)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 1.dp,
                                        color = if (isSelected) CyberPink else Color.White.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { editAddress = option }
                                    .padding(vertical = 10.dp, horizontal = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = Strings.addressOptionLabel(option, lang),
                                    color = if (isSelected) CyberPink else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Profile Picture Actions
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings.photoPickerTitle(lang),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )

                        if (editPictureUri != null || editAvatarId != null) {
                            TextButton(
                                onClick = {
                                    editPictureUri = null
                                    editAvatarId = null
                                },
                                contentPadding = PaddingValues(0.dp)
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

                    Spacer(Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = CyberCard,
                            contentColor = CyberBlue
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberBlue.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("edit_gallery_button")
                    ) {
                        Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(Strings.uploadFromGallery(lang), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(10.dp))

                    // Sample Avatars
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Male
                        val isMale = editPictureUri == null && editAvatarId == "avatar_male"
                        SampleAvatarMini(
                            drawableRes = R.drawable.img_avatar_male,
                            label = Strings.avatarMaleName(lang),
                            isSelected = isMale,
                            accentColor = CyberBlue,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                editAvatarId = "avatar_male"
                                editPictureUri = null
                            }
                        )

                        // Female
                        val isFemale = editPictureUri == null && editAvatarId == "avatar_female"
                        SampleAvatarMini(
                            drawableRes = R.drawable.img_avatar_female,
                            label = Strings.avatarFemaleName(lang),
                            isSelected = isFemale,
                            accentColor = CyberPink,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                editAvatarId = "avatar_female"
                                editPictureUri = null
                            }
                        )

                        // Default
                        val isDef = editPictureUri == null && editAvatarId == null
                        SampleAvatarMini(
                            drawableRes = R.drawable.img_avatar_default,
                            label = Strings.avatarDefaultName(lang),
                            isSelected = isDef,
                            accentColor = CyberPurple,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                editAvatarId = null
                                editPictureUri = null
                            }
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Bottom Buttons: Save & Cancel
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onClose,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = CyberCard,
                            contentColor = CyberMuted
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .testTag("cancel_edit_profile_button")
                    ) {
                        Text(Strings.cancel(lang), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    NeonButton(
                        text = Strings.saveChanges(lang),
                        onClick = {
                            if (validate()) {
                                focusManager.clearFocus()
                                onSaveProfile(
                                    currentProfile.copy(
                                        name = editName.trim(),
                                        preferredAddress = editAddress,
                                        profilePictureUri = editPictureUri,
                                        avatarId = editAvatarId,
                                        isCompleted = true
                                    )
                                )
                                onClose()
                            }
                        },
                        modifier = Modifier.weight(1.5f),
                        testTag = "save_profile_button"
                    )
                }
            }
        }
    }
}

@Composable
private fun SampleAvatarMini(
    drawableRes: Int,
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.16f) else CyberCard)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) accentColor else Color.White.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
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
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isSelected) accentColor else Color.White,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 8.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
