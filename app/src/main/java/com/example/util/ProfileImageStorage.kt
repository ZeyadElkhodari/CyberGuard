package com.example.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/** Copies picked profile images into app-private storage so they survive picker URI grant expiry. */
object ProfileImageStorage {
    suspend fun persist(context: Context, source: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val directory = File(context.filesDir, "profile_photos").apply { mkdirs() }
            val destination = File(directory, "profile_${UUID.randomUUID()}.jpg")
            val input = context.contentResolver.openInputStream(source) ?: return@withContext null
            input.use { stream -> destination.outputStream().use { output -> stream.copyTo(output) } }
            Uri.fromFile(destination).toString()
        } catch (_: Exception) {
            null
        }
    }
}
