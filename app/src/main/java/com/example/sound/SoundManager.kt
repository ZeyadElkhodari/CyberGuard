package com.example.sound

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.SystemClock
import android.util.Log
import com.example.R

/**
 * Centralized, thread-safe sound effects manager for CyberGuard.
 * Uses Android SoundPool for low-latency playback of cyberpunk audio effects.
 *
 * Supports four core sound categories:
 * 1. Button Click (tactile, short subtle electronic click)
 * 2. Scan Hum (loopable electronic scanning ambience, actively stops on completion/exit)
 * 3. Success Chime (clean futuristic confirmation tone)
 * 4. Alert Tone (cybersecurity warning alarm for detected threats)
 *
 * Persists user preference across application restarts.
 */
object SoundManager {
    private const val TAG = "SoundManager"
    private const val PREFS_NAME = "cyberguard_sound_prefs"
    private const val KEY_SOUND_ENABLED = "sound_effects_enabled"

    private var soundPool: SoundPool? = null
    private var isInitialized = false

    private var clickSoundId: Int = 0
    private var scanHumSoundId: Int = 0
    private var successSoundId: Int = 0
    private var alertSoundId: Int = 0

    private var activeHumStreamId: Int = 0
    private var lastClickTime = 0L
    private val loadedSoundIds = mutableSetOf<Int>()
    private var pendingClickCount = 0

    private fun isSoundLoaded(soundId: Int): Boolean = synchronized(loadedSoundIds) {
        soundId != 0 && soundId in loadedSoundIds
    }

    var isSoundEnabled: Boolean = true
        private set

    /**
     * Initializes SoundPool and preloads sound assets.
     */
    fun init(context: Context) {
        if (isInitialized && soundPool != null) return

        try {
            val prefs = getPrefs(context)
            isSoundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true)

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val pool = SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(audioAttributes)
                .build()

            soundPool = pool
            loadedSoundIds.clear()
            pendingClickCount = 0
            pool.setOnLoadCompleteListener { _, sampleId, status ->
                if (status == 0) {
                    synchronized(loadedSoundIds) {
                        loadedSoundIds.add(sampleId)
                        if (sampleId == clickSoundId && pendingClickCount > 0 && isSoundEnabled) {
                            val queued = pendingClickCount.coerceAtMost(2)
                            pendingClickCount = 0
                            repeat(queued) {
                                pool.play(clickSoundId, 0.45f, 0.45f, 1, 0, 1.0f)
                            }
                        }
                    }
                } else {
                    Log.w(TAG, "Sound sample failed to load: $sampleId status=$status")
                }
            }

            clickSoundId = pool.load(context, R.raw.sound_button_click, 1)
            scanHumSoundId = pool.load(context, R.raw.sound_scan_hum, 1)
            successSoundId = pool.load(context, R.raw.sound_success, 1)
            alertSoundId = pool.load(context, R.raw.sound_alert, 1)

            isInitialized = true
            Log.d(TAG, "SoundManager initialized. soundEnabled=$isSoundEnabled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize SoundPool: ${e.message}")
        }
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Toggles or updates the sound enabled state, persisting to SharedPreferences.
     */
    fun setSoundEnabled(context: Context, enabled: Boolean) {
        isSoundEnabled = enabled
        try {
            getPrefs(context).edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save sound preference: ${e.message}")
        }
        if (!enabled) {
            stopScanHum()
        }
    }

    /**
     * Plays a short, subtle electronic interaction click.
     * Debounced to prevent repeated triggers caused by Compose recomposition.
     */
    fun playButtonClick() {
        if (!isSoundEnabled) return
        val now = SystemClock.elapsedRealtime()
        if (now - lastClickTime < 60) return
        lastClickTime = now

        try {
            val pool = soundPool ?: return
            synchronized(loadedSoundIds) {
                if (clickSoundId == 0 || clickSoundId !in loadedSoundIds) {
                    pendingClickCount = (pendingClickCount + 1).coerceAtMost(2)
                    return
                }
            }
            pool.play(clickSoundId, 0.45f, 0.45f, 1, 0, 1.0f)
        } catch (e: Exception) {
            Log.e(TAG, "playButtonClick error: ${e.message}")
        }
    }

    /**
     * Starts the quiet futuristic electronic scanning ambience.
     * Loops continuously while scanning is running.
     */
    fun startScanHum() {
        if (!isSoundEnabled) return
        stopScanHum() // Ensure no overlapping hum
        try {
            activeHumStreamId = soundPool?.takeIf { isSoundLoaded(scanHumSoundId) }?.play(scanHumSoundId, 0.35f, 0.35f, 2, -1, 1.0f) ?: 0
        } catch (e: Exception) {
            Log.e(TAG, "startScanHum error: ${e.message}")
        }
    }

    /**
     * Immediately stops the scanning ambience.
     * Guaranteed to stop playback on complete, fail, cancel, or screen leave.
     */
    fun stopScanHum() {
        try {
            if (activeHumStreamId != 0) {
                soundPool?.stop(activeHumStreamId)
                activeHumStreamId = 0
            }
        } catch (e: Exception) {
            Log.e(TAG, "stopScanHum error: ${e.message}")
        }
    }

    /**
     * Plays short, clean futuristic confirmation sound on genuine operation success.
     */
    fun playSuccess() {
        if (!isSoundEnabled) return
        try {
            soundPool?.takeIf { isSoundLoaded(successSoundId) }?.play(successSoundId, 0.6f, 0.6f, 2, 0, 1.0f)
        } catch (e: Exception) {
            Log.e(TAG, "playSuccess error: ${e.message}")
        }
    }

    /**
     * Plays distinctive cybersecurity warning sound when an actual threat or alert is detected.
     */
    fun playAlert() {
        if (!isSoundEnabled) return
        try {
            soundPool?.takeIf { isSoundLoaded(alertSoundId) }?.play(alertSoundId, 0.7f, 0.7f, 3, 0, 1.0f)
        } catch (e: Exception) {
            Log.e(TAG, "playAlert error: ${e.message}")
        }
    }

    // Academy Specific Sounds
    fun playCorrectAnswer() {
        if (!isSoundEnabled) return
        try {
            soundPool?.takeIf { isSoundLoaded(successSoundId) }?.play(successSoundId, 0.65f, 0.65f, 2, 0, 1.15f)
        } catch (e: Exception) {
            playSuccess()
        }
    }

    fun playIncorrectAnswer() {
        if (!isSoundEnabled) return
        try {
            soundPool?.takeIf { isSoundLoaded(alertSoundId) }?.play(alertSoundId, 0.6f, 0.6f, 2, 0, 0.9f)
        } catch (e: Exception) {
            playAlert()
        }
    }

    fun playArticleComplete() {
        playSuccess()
    }

    fun playQuizComplete() {
        playSuccess()
    }

    fun playAchievementUnlocked() {
        playSuccess()
    }

    fun playDailyChallengeComplete() {
        playSuccess()
    }

    /**
     * Releases SoundPool resources on application teardown.
     */
    fun release() {
        stopScanHum()
        try {
            soundPool?.release()
            soundPool = null
            synchronized(loadedSoundIds) {
                loadedSoundIds.clear()
                pendingClickCount = 0
            }
            clickSoundId = 0
            scanHumSoundId = 0
            successSoundId = 0
            alertSoundId = 0
            isInitialized = false
        } catch (e: Exception) {
            Log.e(TAG, "release error: ${e.message}")
        }
    }
}
