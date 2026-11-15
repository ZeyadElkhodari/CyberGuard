package com.example.repository

import android.content.Context
import com.example.db.CyberGuardDatabase
import com.example.db.UserProfileEntity
import com.example.model.PreferredAddress
import com.example.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserProfileRepository(context: Context) {

    private val db = CyberGuardDatabase.getInstance(context)
    private val profileDao = db.userProfileDao()

    suspend fun getProfile(): UserProfile = withContext(Dispatchers.IO) {
        val entity = profileDao.getProfile()
        if (entity != null) {
            val address = try {
                PreferredAddress.valueOf(entity.preferredAddress)
            } catch (_: Exception) {
                PreferredAddress.UNSPECIFIED
            }
            UserProfile(
                name = entity.name,
                preferredAddress = address,
                profilePictureUri = entity.profilePictureUri,
                avatarId = entity.avatarId,
                isCompleted = entity.isCompleted
            )
        } else {
            UserProfile(
                name = "",
                preferredAddress = PreferredAddress.UNSPECIFIED,
                profilePictureUri = null,
                avatarId = null,
                isCompleted = false
            )
        }
    }

    fun observeProfile(): Flow<UserProfile> {
        return profileDao.observeProfile().map { entity ->
            if (entity != null) {
                val address = try {
                    PreferredAddress.valueOf(entity.preferredAddress)
                } catch (_: Exception) {
                    PreferredAddress.UNSPECIFIED
                }
                UserProfile(
                    name = entity.name,
                    preferredAddress = address,
                    profilePictureUri = entity.profilePictureUri,
                    avatarId = entity.avatarId,
                    isCompleted = entity.isCompleted
                )
            } else {
                UserProfile(
                    name = "",
                    preferredAddress = PreferredAddress.UNSPECIFIED,
                    profilePictureUri = null,
                    avatarId = null,
                    isCompleted = false
                )
            }
        }
    }

    suspend fun saveProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        profileDao.saveProfile(
            UserProfileEntity(
                id = "primary_profile",
                name = profile.name.trim(),
                preferredAddress = profile.preferredAddress.name,
                profilePictureUri = profile.profilePictureUri,
                avatarId = profile.avatarId,
                isCompleted = profile.isCompleted,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun isProfileCompleted(): Boolean = withContext(Dispatchers.IO) {
        val entity = profileDao.getProfile()
        entity?.isCompleted == true
    }

    suspend fun resetProfile() = withContext(Dispatchers.IO) {
        profileDao.deleteProfile()
    }
}
