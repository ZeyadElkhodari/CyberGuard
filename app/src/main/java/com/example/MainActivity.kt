package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SecurityData
import com.example.localization.Strings
import com.example.util.BidiHelper
import com.example.logic.SecurityEvaluator
import com.example.model.*
import com.example.ui.modals.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.components.GlassCard
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

private fun currentActivityTime(): String =
    java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault()).format(java.util.Date())

class MainActivity : ComponentActivity() {
    var pendingNotificationMessage by mutableStateOf<Pair<String, String>?>(null)
        private set
    var pendingNotificationTrackId by mutableStateOf<String?>(null)
        private set
    var pendingNotificationOpenQuiz by mutableStateOf(false)
        private set

    private fun readNotificationMessage(intent: android.content.Intent?) {
        val title = intent?.getStringExtra("notification_title")
        val message = intent?.getStringExtra("notification_message")
        pendingNotificationMessage = if (!title.isNullOrBlank() && !message.isNullOrBlank()) title to message else null
        pendingNotificationTrackId = intent?.getStringExtra("track_id")
        pendingNotificationOpenQuiz = intent?.getBooleanExtra("open_quiz", false) == true
    }

    fun consumeNotificationNavigation() {
        pendingNotificationTrackId = null
        pendingNotificationOpenQuiz = false
        intent?.removeExtra("track_id")
        intent?.removeExtra("open_quiz")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readNotificationMessage(intent)
        com.example.sound.SoundManager.init(this)
        com.example.academy.notifications.AcademyNotificationScheduler.createNotificationChannel(this)
        val notificationPrefs = getSharedPreferences("academy_notification_schedule", MODE_PRIVATE)
        com.example.academy.notifications.AcademyNotificationScheduler.scheduleDailyNotifications(
            context = this,
            enabled = notificationPrefs.getBoolean("enabled", false),
            intervalHours = notificationPrefs.getLong("interval_hours", 12L)
        )
        
        // Eagerly pre-seed Academy content offline in background with NonCancellable protection
        lifecycleScope.launch(Dispatchers.IO + NonCancellable) {
            com.example.academy.data.AcademyRepository.getInstance(applicationContext).seedFromAssetsIfEmpty(applicationContext)
        }

        enableEdgeToEdge()
        setContent {
            CyberGuardMainApp()
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        readNotificationMessage(intent)
    }

    fun dismissNotificationMessage() {
        pendingNotificationMessage = null
        intent?.removeExtra("notification_title")
        intent?.removeExtra("notification_message")
    }

    override fun onDestroy() {
        super.onDestroy()
        com.example.sound.SoundManager.release()
    }
}

@Composable
fun CyberGuardMainApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val hostActivity = context as? MainActivity
    val incomingNotificationMessage = hostActivity?.pendingNotificationMessage
    var showNotificationMessage by remember { mutableStateOf(incomingNotificationMessage != null) }
    LaunchedEffect(incomingNotificationMessage) {
        if (incomingNotificationMessage != null) showNotificationMessage = true
    }
    val coroutineScope = rememberCoroutineScope()
    val pageStateHolder = rememberSaveableStateHolder()
    val userProgressRepository = remember { com.example.repository.UserProgressRepository(context) }
    val userProfileRepository = remember { com.example.repository.UserProfileRepository(context) }

    var userProfile by remember { mutableStateOf(UserProfile()) }
    val settingsPrefs = remember { context.getSharedPreferences("cyberguard_settings_prefs", android.content.Context.MODE_PRIVATE) }
    val initialLang = remember {
        if (settingsPrefs.getString("app_language", "ar") == "en") Language.EN else Language.AR
    }
    var lang by remember { mutableStateOf(initialLang) }
    val initialAppearance = remember {
        AppAppearance.fromKey(settingsPrefs.getString("appearance_theme", "cyber_dark"))
    }
    var appearance by remember { mutableStateOf(initialAppearance) }
    val initialDisplayMode = remember {
        AppDisplayMode.fromKey(settingsPrefs.getString("display_mode", "standard"))
    }
    var displayMode by remember { mutableStateOf(initialDisplayMode) }
    val hasCompletedOnboarding = remember {
        settingsPrefs.getBoolean("onboarding_completed", false)
    }
    var onboardingCompleted by remember { mutableStateOf(hasCompletedOnboarding) }
    var showProfileSetup by rememberSaveable { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(TabType.HOME) }
    var soundEnabled by remember { mutableStateOf(com.example.sound.SoundManager.isSoundEnabled) }

    LaunchedEffect(hostActivity?.pendingNotificationTrackId) {
        if (!hostActivity?.pendingNotificationTrackId.isNullOrBlank()) {
            currentTab = TabType.ACADEMY
        }
    }

    // Ask for notification opt-in once, after the first-run onboarding flow.
    var showNotificationOptIn by rememberSaveable {
        mutableStateOf(!settingsPrefs.getBoolean("notification_opt_in_prompt_shown", false))
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        context.getSharedPreferences("academy_notification_schedule", android.content.Context.MODE_PRIVATE)
            .edit().putBoolean("enabled", granted).apply()
        if (granted) {
            com.example.academy.notifications.AcademyNotificationScheduler.scheduleDailyNotifications(
                context = context, enabled = true
            )
        } else {
            com.example.academy.notifications.AcademyNotificationScheduler.cancelNotifications(context)
        }
    }

    LaunchedEffect(onboardingCompleted, showProfileSetup) {
        if (onboardingCompleted && !showProfileSetup && showNotificationOptIn) {
            settingsPrefs.edit().putBoolean("notification_opt_in_prompt_shown", true).apply()
        }
    }

    val toggleLanguage: () -> Unit = {
        val newLang = if (lang == Language.AR) Language.EN else Language.AR
        lang = newLang
        settingsPrefs.edit().putString("app_language", if (newLang == Language.EN) "en" else "ar").apply()
    }

    // Core Data States
    var stats by remember { mutableStateOf(SecurityData.initialUserStats) }
    var auditState by remember { mutableStateOf(SecurityAuditState()) }
    var courses by remember { mutableStateOf(SecurityData.initialCourses) }
    var alerts by remember { mutableStateOf(SecurityData.initialAlerts) }
    val activities by userProgressRepository.observeActivities().collectAsStateWithLifecycle(initialValue = emptyList())

    val securityRepository = remember { com.example.repository.SecurityRepository(context) }

    // Sync from local Room DB on app launch
    LaunchedEffect(Unit) {
        try {
            userProgressRepository.trimActivityHistory()
            val loadedProfile = userProfileRepository.getProfile()
            if (loadedProfile != null) {
                userProfile = loadedProfile
            }

            val loadedStats = userProgressRepository.loadStats()
            stats = loadedStats

            val progressMap = userProgressRepository.loadCourseProgress()
            if (progressMap.isNotEmpty()) {
                courses = courses.map { c ->
                    val saved = progressMap[c.id]
                    if (saved != null) {
                        val updatedLessons = c.lessons.map { l ->
                            if (saved.second.contains(l.id)) l.copy(completed = true) else l
                        }
                        c.copy(progress = saved.first, lessons = updatedLessons)
                    } else {
                        c
                    }
                }
            }

            val loadedAlerts = userProgressRepository.loadAlerts()
            alerts = loadedAlerts

            // Reconstruct SecurityAuditState from local Room records
            val scanMetrics = securityRepository.getScanMetrics()
            val wifiCheckedCount = userProgressRepository.countCheckedWifi()
            val savedPasswordAudit = if (settingsPrefs.contains("last_pwd_audit_strength")) settingsPrefs.getInt("last_pwd_audit_strength", 0) else null
            val savedPasswordEntropy = if (settingsPrefs.contains("last_pwd_audit_entropy")) settingsPrefs.getInt("last_pwd_audit_entropy", 0) else null
            val savedPasswordBreachStatus = settingsPrefs.getString("last_pwd_audit_status", "untested") ?: "untested"
            val savedPasswordBreachCount = settingsPrefs.getInt("last_pwd_audit_breach_count", 0)

            val savedWifiScore = if (settingsPrefs.contains("last_wifi_scan_score")) settingsPrefs.getInt("last_wifi_scan_score", 0) else if (wifiCheckedCount > 0) ((wifiCheckedCount.toFloat() / 8f) * 100f).toInt() else 0
            val savedWifiPassed = if (settingsPrefs.contains("last_wifi_scan_passed")) settingsPrefs.getInt("last_wifi_scan_passed", 0) else wifiCheckedCount
            val savedWifiTotal = if (settingsPrefs.contains("last_wifi_scan_total")) settingsPrefs.getInt("last_wifi_scan_total", 5) else 5

            val reconstructedAuditState = SecurityAuditState(
                urlScansCount = scanMetrics.urlScansCount,
                urlThreatsCount = scanMetrics.urlThreatsCount,
                urlAverageScore = scanMetrics.urlAverageScore,
                qrScansCount = scanMetrics.qrScansCount,
                qrThreatsCount = scanMetrics.qrThreatsCount,
                wifiPassed = savedWifiPassed,
                wifiTotal = savedWifiTotal,
                wifiScore = savedWifiScore,
                passwordStrength = savedPasswordAudit,
                passwordEntropy = savedPasswordEntropy,
                passwordBreachStatus = savedPasswordBreachStatus,
                passwordBreachCount = savedPasswordBreachCount
            )
            auditState = reconstructedAuditState

            // Sync dynamic score and local totals with stats
            val localAssessment = SecurityEvaluator.calculateDynamicAssessment(reconstructedAuditState, lang)
            val totalThreats = scanMetrics.urlThreatsCount + scanMetrics.qrThreatsCount
            val totalScans = scanMetrics.urlScansCount + scanMetrics.qrScansCount
            val syncedStats = loadedStats.copy(
                score = localAssessment.compositeScore,
                threatsBlocked = totalThreats,
                scansDone = totalScans,
                scannedContentCount = totalScans
            )
            stats = syncedStats
            userProgressRepository.saveLocalStats(syncedStats)
        } catch (_: Exception) {
            // Keep existing state on error
        }
    }

    // Modals
    var activeModal by remember { mutableStateOf<String?>(null) }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }

    val dynamicAssessment = remember(auditState, lang) {
        SecurityEvaluator.calculateDynamicAssessment(auditState, lang)
    }

    val unreadAlertsCount = alerts.count { !it.read }

    // Helper functions
    fun rewardXp(amount: Int) {
        coroutineScope.launch {
            stats = userProgressRepository.rewardXp(
                current = stats,
                amount = amount,
                reason = "Challenge progress",
                isAr = lang == Language.AR,
                onLevelUpAlert = { newAlert ->
                    alerts = listOf(newAlert) + alerts
                }
            )
        }
    }

    fun logScanActivity(result: ScanResult) {
        val newTotal = auditState.urlScansCount + 1
        val newThreats = if (result.score < 60) auditState.urlThreatsCount + 1 else auditState.urlThreatsCount
        val newAvg = if (auditState.urlScansCount == 0) {
            result.score
        } else {
            ((auditState.urlAverageScore * auditState.urlScansCount) + result.score) / newTotal
        }

        val updatedAudit = auditState.copy(
            urlScansCount = newTotal,
            urlThreatsCount = newThreats,
            urlAverageScore = newAvg
        )
        auditState = updatedAudit

        val assessment = SecurityEvaluator.calculateDynamicAssessment(updatedAudit, lang)
        val updatedStats = stats.copy(
            score = assessment.compositeScore,
            scansDone = stats.scansDone + 1,
            scannedContentCount = stats.scannedContentCount + 1,
            threatsBlocked = if (result.score < 60) stats.threatsBlocked + 1 else stats.threatsBlocked
        )
        stats = updatedStats
        coroutineScope.launch {
            userProgressRepository.saveLocalStats(updatedStats)
        }

        val newActivity = SecurityActivity(
            id = "act-${System.currentTimeMillis()}",
            type = if (result.type == "url") "link" else "password",
            title = if (result.riskLevel == "Safe") {
                if (lang == Language.AR) "فحص رابط: سليم" else "URL scan: Safe"
            } else {
                if (lang == Language.AR) "رصد رابط مشبوه" else "Suspicious link detected"
            },
            detail = result.target.take(35) + "...",
            time = currentActivityTime(),
            status = if (result.score >= 80) "safe" else "warning"
        )
        coroutineScope.launch {
            userProgressRepository.logActivity(newActivity)
        }

        if (result.score < 60) {
            com.example.sound.SoundManager.playAlert()
            val threatAlert = SecurityAlert(
                id = "alert-${System.currentTimeMillis()}",
                title = if (lang == Language.AR) "حظر رابط خطير" else "Dangerous link blocked",
                detail = "${result.target} (${result.riskLevel})",
                time = "Just now",
                severity = AlertSeverity.CRITICAL,
                read = false,
                category = "link"
            )
            alerts = listOf(threatAlert) + alerts
            coroutineScope.launch {
                userProgressRepository.saveLocalAlert(threatAlert)
            }
        } else if (result.score >= 80) {
            com.example.sound.SoundManager.playSuccess()
        }
    }

    fun completeLesson(courseId: String, lessonId: String, xpGain: Int) {
        com.example.sound.SoundManager.playSuccess()
        rewardXp(xpGain)
        courses = courses.map { c ->
            if (c.id == courseId) {
                val updatedLessons = c.lessons.map { l ->
                    if (l.id == lessonId) l.copy(completed = true) else l
                }
                val completedCount = updatedLessons.count { it.completed }
                val newProgress = (completedCount.toFloat() / updatedLessons.size.toFloat()).coerceIn(0f, 1f)
                c.copy(lessons = updatedLessons, progress = newProgress)
            } else {
                c
            }
        }
        coroutineScope.launch {
            userProgressRepository.completeLesson(courseId, lessonId, 8, xpGain)
        }
    }

    fun resetStats() {
        auditState = SecurityAuditState()
        stats = SecurityData.initialUserStats
        courses = SecurityData.initialCourses
        alerts = emptyList()
        settingsPrefs.edit()
            .remove("last_pwd_audit_strength")
            .remove("last_pwd_audit_entropy")
            .remove("last_pwd_audit_status")
            .remove("last_pwd_audit_breach_count")
            .remove("last_wifi_scan_score")
            .remove("last_wifi_scan_passed")
            .remove("last_wifi_scan_total")
            .remove("last_wifi_scan_security_type")
            .remove("last_wifi_scan_ssid")
            .apply()
        coroutineScope.launch {
            userProgressRepository.resetAllData()
            securityRepository.clearScanHistory()
        }
    }

    // Back handling
    BackHandler(enabled = activeModal != null || currentTab != TabType.HOME || showProfileSetup || !onboardingCompleted) {
        when {
            activeModal != null -> activeModal = null
            showProfileSetup -> {
                showProfileSetup = false
            }
            !onboardingCompleted -> {
                (context as? ComponentActivity)?.finish()
            }
            currentTab != TabType.HOME -> currentTab = TabType.HOME
        }
    }

    val mainTabs = remember {
        listOf(
            TabType.HOME,
            TabType.SCAN,
            TabType.ACADEMY,
            TabType.ALERTS,
            TabType.MORE
        )
    }
    val pagerState = rememberPagerState(initialPage = currentTab.ordinal) { mainTabs.size }

    // Synchronize only after a swipe settles; this avoids tab/pager feedback during a drag.
    LaunchedEffect(pagerState.settledPage) {
        val target = mainTabs.getOrElse(pagerState.settledPage) { TabType.HOME }
        if (currentTab != target) {
            currentTab = target
        }
    }

    // Synchronize programmatic or bottom nav tab changes with pager page
    LaunchedEffect(currentTab) {
        if (pagerState.currentPage != currentTab.ordinal) {
            pagerState.animateScrollToPage(currentTab.ordinal)
        }
    }

    CyberGuardTheme(
        lang = lang,
        appearance = appearance,
        displayMode = displayMode
    ) {
        // Keep the complete application layout direction LTR in both Arabic and English.
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LocalCyberBackground.current)
            ) {
                when {
                    !onboardingCompleted -> {
                        OnboardingScreens(
                            lang = lang,
                            onToggleLang = toggleLanguage,
                            onFinish = {
                                onboardingCompleted = true
                                settingsPrefs.edit().putBoolean("onboarding_completed", true).apply()
                                if (!userProfile.isCompleted) {
                                    showProfileSetup = true
                                }
                            }
                        )
                    }
                    showProfileSetup -> {
                        ProfileSetupScreen(
                            lang = lang,
                            initialProfile = userProfile,
                            onCompleteProfile = { completed ->
                                val finalized = completed.copy(isCompleted = true)
                                userProfile = finalized
                                showProfileSetup = false
                                com.example.sound.SoundManager.playSuccess()
                                coroutineScope.launch {
                                    userProfileRepository.saveProfile(finalized)
                                }
                            }
                        )
                    }
                    else -> {
                        Scaffold(
                            containerColor = LocalCyberBackground.current,
                            contentColor = Color.White,
                            bottomBar = {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                CyberBottomNavigationBar(
                                    currentTab = currentTab,
                                    onSelectTab = { selectedTab ->
                                        currentTab = selectedTab
                                    },
                                    unreadAlertsCount = unreadAlertsCount,
                                    lang = lang
                                )
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                HorizontalPager(
                                    state = pagerState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .testTag("main_horizontal_pager"),
                                    beyondViewportPageCount = 0,
                                    userScrollEnabled = activeModal == null && !showProfileSetup && onboardingCompleted
                                ) { pageIndex ->
                                    pageStateHolder.SaveableStateProvider(mainTabs[pageIndex]) {
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    when (mainTabs[pageIndex]) {
                                        TabType.HOME -> DashboardScreen(
                                            stats = stats,
                                            activities = activities,
                                            dynamicAssessment = dynamicAssessment,
                                            lang = lang,
                                            userProfile = userProfile,
                                            onOpenTool = { tool ->
                                                when (tool) {
                                                    "link" -> activeModal = "link"
                                                    "password" -> activeModal = "password"
                                                    "phishing-msg" -> activeModal = "phishing-msg"
                                                    "wifi" -> activeModal = "wifi"
                                                    "qr" -> activeModal = "qr"
                                                    "alerts" -> currentTab = TabType.ALERTS
                                                    "scan" -> currentTab = TabType.SCAN
                                                    "edit_profile" -> activeModal = "edit_profile"
                                                    "course" -> {
                                                        selectedCourse = courses.firstOrNull()
                                                        activeModal = "course"
                                                    }
                                                    "academy" -> currentTab = TabType.ACADEMY
                                                }
                                            }
                                        )
                                        TabType.SCAN -> ScanScreen(
                                            lang = lang,
                                            onOpenScanTool = { tool ->
                                                when (tool) {
                                                    "link" -> activeModal = "link"
                                                    "password" -> activeModal = "password"
                                                    "phishing-msg" -> activeModal = "phishing-msg"
                                                    "wifi" -> activeModal = "wifi"
                                                    "qr" -> activeModal = "qr"
                                                }
                                            }
                                        )
                                        TabType.ACADEMY -> AcademyScreen(
                                            stats = stats,
                                            lang = lang,
                                            notificationTrackId = hostActivity?.pendingNotificationTrackId,
                                            notificationOpenQuiz = hostActivity?.pendingNotificationOpenQuiz == true,
                                            onNotificationNavigationConsumed = { hostActivity?.consumeNotificationNavigation() }
                                        )
                                        TabType.ALERTS -> AlertsScreen(
                                            alerts = alerts,
                                            activities = activities,
                                            lang = lang,
                                            onMarkRead = { id ->
                                                alerts = alerts.map { if (it.id == id) it.copy(read = true) else it }
                                                coroutineScope.launch { userProgressRepository.markAlertRead(id) }
                                            },
                                            onMarkAllRead = {
                                                alerts = alerts.map { it.copy(read = true) }
                                                coroutineScope.launch { userProgressRepository.markAllAlertsRead() }
                                            },
                                            onDeleteAlert = { id ->
                                                alerts = alerts.filter { it.id != id }
                                                coroutineScope.launch { userProgressRepository.deleteAlert(id) }
                                            }
                                        )
                                        TabType.MORE -> MoreScreen(
                                            stats = stats,
                                            lang = lang,
                                            onToggleLang = toggleLanguage,
                                            soundEnabled = soundEnabled,
                                            onToggleSound = {
                                                val newState = !soundEnabled
                                                soundEnabled = newState
                                                com.example.sound.SoundManager.setSoundEnabled(context, newState)
                                                if (newState) {
                                                    com.example.sound.SoundManager.playButtonClick()
                                                }
                                            },
                                            onResetStats = {
                                                resetStats()
                                                settingsPrefs.edit().putBoolean("onboarding_completed", false).apply()
                                                onboardingCompleted = false
                                            },
                                            appearance = appearance,
                                            onSelectAppearance = { newAppearance ->
                                                appearance = newAppearance
                                                settingsPrefs.edit().putString("appearance_theme", newAppearance.key).apply()
                                            },
                                            displayMode = displayMode,
                                            onSelectDisplayMode = { newMode ->
                                                displayMode = newMode
                                                settingsPrefs.edit().putString("display_mode", newMode.key).apply()
                                            },
                                            userProfile = userProfile,
                                            onUpdateProfile = { updated ->
                                                userProfile = updated
                                                com.example.sound.SoundManager.playSuccess()
                                                coroutineScope.launch {
                                                    userProfileRepository.saveProfile(updated)
                                                }
                                            }
                                        )
                                    }
                                    }
                                    }
                                }
                                }
                            }
                        }
                    }
                }

            if (onboardingCompleted && !showProfileSetup && showNotificationOptIn) {
                AlertDialog(
                    onDismissRequest = {
                        showNotificationOptIn = false
                        context.getSharedPreferences("academy_notification_schedule", android.content.Context.MODE_PRIVATE)
                            .edit().putBoolean("enabled", false).apply()
                    },
                    title = {
                        Text(if (lang == Language.EN) "Enable notifications?" else "هل تريد تفعيل الإشعارات؟")
                    },
                    text = {
                        Text(
                            BidiHelper.formatBiDi(
                                if (lang == Language.EN)
                                    "Enable notifications to receive helpful learning messages and reminders that help you track your progress in CyberGuard. You can manage or turn them off anytime in Settings."
                                else
                                    "فعّل الإشعارات لتصلك رسائل تعليمية وتذكيرات مفيدة تساعدك على متابعة تقدمك في CyberGuard. يمكنك التحكم بها أو إيقافها في أي وقت من الإعدادات.",
                                isAr = lang == Language.AR
                            )
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showNotificationOptIn = false
                            val schedulePrefs = context.getSharedPreferences("academy_notification_schedule", android.content.Context.MODE_PRIVATE)
                            schedulePrefs.edit().putBoolean("enabled", true).apply()
                            if (android.os.Build.VERSION.SDK_INT >= 33) {
                                if (context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                    com.example.academy.notifications.AcademyNotificationScheduler.scheduleDailyNotifications(context, true)
                                } else {
                                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                }
                            } else {
                                com.example.academy.notifications.AcademyNotificationScheduler.scheduleDailyNotifications(context, true)
                            }
                        }) {
                            Text(if (lang == Language.EN) "Enable" else "تفعيل")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showNotificationOptIn = false
                            context.getSharedPreferences("academy_notification_schedule", android.content.Context.MODE_PRIVATE)
                                .edit().putBoolean("enabled", false).apply()
                        }) {
                            Text(if (lang == Language.EN) "Not now" else "ليس الآن")
                        }
                    },
                    containerColor = LocalCyberCard.current,
                    titleContentColor = Color.White,
                    textContentColor = CyberMuted
                )
            }

            // Interactive Modals
            LinkScannerModal(
                isOpen = activeModal == "link",
                onClose = { activeModal = null },
                lang = lang,
                onLogScan = { res -> logScanActivity(res) }
            )

            PasswordCheckerModal(
                isOpen = activeModal == "password",
                onClose = { activeModal = null },
                lang = lang,
                onUpdateAssessment = { strengthScore, entropy, breachStatus, breachCount ->
                    val updatedAudit = auditState.copy(
                        passwordStrength = strengthScore,
                        passwordEntropy = entropy,
                        passwordBreachStatus = breachStatus,
                        passwordBreachCount = breachCount
                    )
                    auditState = updatedAudit
                    val assessment = SecurityEvaluator.calculateDynamicAssessment(updatedAudit, lang)
                    val updatedStats = stats.copy(score = assessment.compositeScore)
                    stats = updatedStats
                    coroutineScope.launch {
                        userProgressRepository.saveLocalStats(updatedStats)
                    }

                    // Save password audit summary in SharedPreferences
                    settingsPrefs.edit()
                        .putInt("last_pwd_audit_strength", strengthScore)
                        .putInt("last_pwd_audit_entropy", entropy)
                        .putString("last_pwd_audit_status", breachStatus)
                        .putInt("last_pwd_audit_breach_count", breachCount)
                        .apply()
                },
                onBreachCheckCompleted = { breach ->
                    val isConfirmed = breach.checked && breach.error == null
                    val passwordActivity = SecurityActivity(
                        id = "act-password-check-${java.util.UUID.randomUUID()}",
                        type = "password",
                        title = if (lang == Language.AR) "فحص تسريب كلمة المرور" else "Password breach check",
                        detail = if (breach.breached) {
                            if (lang == Language.AR) "تم العثور على ${breach.count} تطابقات في بيانات التسريب"
                            else "${breach.count} matches found in breach data"
                        } else if (isConfirmed) {
                            if (lang == Language.AR) "لم يتم العثور على تطابق في قواعد البيانات التي تم فحصها"
                            else "No match found in the datasets checked"
                        } else {
                            if (lang == Language.AR) "تعذر إكمال الفحص؛ النتيجة غير حاسمة"
                            else "Check could not complete; result inconclusive"
                        },
                        time = currentActivityTime(),
                        status = if (breach.breached) "critical" else if (isConfirmed) "safe" else "warning"
                    )
                    coroutineScope.launch { userProgressRepository.logActivity(passwordActivity) }
                },
                onBreachDetected = { breach ->
                    if (breach.breached) {
                        val threatAlert = SecurityAlert(
                            id = "alert-password-${java.util.UUID.randomUUID()}",
                            title = if (lang == Language.AR) "تم رصد كلمة مرور مسرّبة" else "Compromised password detected",
                            detail = if (lang == Language.AR)
                                "ظهرت كلمة المرور ضمن بيانات مسرّبة ${breach.count} مرة. غيّرها فورًا في كل الحسابات التي تستخدمها."
                            else
                                "This password appeared ${breach.count} times in breach data. Change it immediately anywhere it is reused.",
                            time = currentActivityTime(),
                            severity = AlertSeverity.CRITICAL,
                            read = false,
                            category = "password"
                        )
                        alerts = listOf(threatAlert) + alerts
                        coroutineScope.launch {
                            userProgressRepository.saveLocalAlert(threatAlert)
                            userProgressRepository.logActivity(
                                SecurityActivity(
                                    id = "act-password-${java.util.UUID.randomUUID()}",
                                    type = "password",
                                    title = if (lang == Language.AR) "تحذير: كلمة مرور مسرّبة" else "Warning: compromised password",
                                    detail = if (lang == Language.AR) "تم العثور على تطابق في بيانات التسريب" else "Password matched breach data",
                                    time = currentActivityTime(),
                                    status = "critical"
                                )
                            )
                        }
                    }
                }
            )

            WifiScannerModal(
                isOpen = activeModal == "wifi",
                onClose = { activeModal = null },
                lang = lang,
                onUpdateAssessment = { score, passed, total, report ->
                    val updatedAudit = auditState.copy(
                        wifiPassed = passed,
                        wifiTotal = total,
                        wifiScore = score
                    )
                    auditState = updatedAudit
                    val assessment = SecurityEvaluator.calculateDynamicAssessment(updatedAudit, lang)
                    val updatedStats = stats.copy(score = assessment.compositeScore)
                    stats = updatedStats
                    coroutineScope.launch {
                        userProgressRepository.saveLocalStats(updatedStats)
                    }

                    // Save Wi-Fi assessment in SharedPreferences so it restores on relaunch
                    settingsPrefs.edit()
                        .putInt("last_wifi_scan_score", score)
                        .putInt("last_wifi_scan_passed", passed)
                        .putInt("last_wifi_scan_total", total)
                        .putString("last_wifi_scan_security_type", report.securityType)
                        .putString("last_wifi_scan_ssid", report.ssid ?: "Unknown")
                        .apply()

                    val wifiActivity = SecurityActivity(
                        id = "act-wifi-${java.util.UUID.randomUUID()}",
                        type = "wifi",
                        title = if (lang == Language.AR) "فحص أمان شبكة الواي فاي" else "Wi-Fi security scan",
                        detail = "${report.ssid ?: report.securityType} • $score%",
                        time = currentActivityTime(),
                        status = if (score >= 80) "safe" else if (score >= 50) "warning" else "critical"
                    )
                    coroutineScope.launch {
                        userProgressRepository.logActivity(wifiActivity)
                    }
                    if (score < 60) {
                        val wifiAlert = SecurityAlert(
                            id = "alert-wifi-${java.util.UUID.randomUUID()}",
                            title = if (lang == Language.AR) "تحذير أمني: شبكة Wi-Fi ضعيفة" else "Security warning: weak Wi-Fi network",
                            detail = if (lang == Language.AR)
                                "نتيجة أمان الشبكة ${score}%. ${report.risks.firstOrNull()?.explanationAr ?: "توجد إعدادات تحتاج إلى مراجعة."}"
                            else
                                "Network security score: ${score}%. ${report.risks.firstOrNull()?.explanationEn ?: "Some settings need attention."}",
                            time = currentActivityTime(),
                            severity = if (score < 40) AlertSeverity.CRITICAL else AlertSeverity.WARNING,
                            read = false,
                            category = "wifi"
                        )
                        alerts = listOf(wifiAlert) + alerts
                        coroutineScope.launch { userProgressRepository.saveLocalAlert(wifiAlert) }
                    }
                }
            )

            PhishingMessageModal(
                isOpen = activeModal == "phishing-msg",
                onClose = { activeModal = null },
                lang = lang,
                onAnalysisCompleted = { _, flaggedCount ->
                    val phishingActivity = SecurityActivity(
                        id = "act-phishing-${java.util.UUID.randomUUID()}",
                        type = "phishing-message",
                        title = if (lang == Language.AR) "فحص رسالة تصيد" else "Phishing message scan",
                        detail = if (lang == Language.AR)
                            if (flaggedCount > 0) "تم رصد $flaggedCount مؤشرات تهديد في الرسالة" else "لم يتم رصد مؤشرات تصيد واضحة"
                        else
                            if (flaggedCount > 0) "$flaggedCount suspicious indicators detected" else "No clear phishing indicators detected",
                        time = currentActivityTime(),
                        status = if (flaggedCount > 0) "warning" else "safe"
                    )
                    coroutineScope.launch {
                        userProgressRepository.logActivity(phishingActivity)
                    }
                    if (flaggedCount >= 2) {
                        val phishingAlert = SecurityAlert(
                            id = "alert-phishing-${java.util.UUID.randomUUID()}",
                            title = if (lang == Language.AR) "رصد مؤشرات رسالة تصيّد" else "Phishing message indicators detected",
                            detail = if (lang == Language.AR)
                                "تم رصد $flaggedCount مؤشرات احتيال. لا تضغط على الروابط ولا تشارك كلمات المرور أو رموز التحقق."
                            else
                                "$flaggedCount suspicious indicators were detected. Do not open links or share passwords or verification codes.",
                            time = currentActivityTime(),
                            severity = if (flaggedCount >= 4) AlertSeverity.CRITICAL else AlertSeverity.WARNING,
                            read = false,
                            category = "phishing"
                        )
                        alerts = listOf(phishingAlert) + alerts
                        coroutineScope.launch { userProgressRepository.saveLocalAlert(phishingAlert) }
                    }
                }
            )

            QrScannerModal(
                isOpen = activeModal == "qr",
                onClose = { activeModal = null },
                lang = lang,
                onUpdateQrAudit = { isThreat ->
                    val updatedAudit = auditState.copy(
                        qrScansCount = auditState.qrScansCount + 1,
                        qrThreatsCount = if (isThreat) auditState.qrThreatsCount + 1 else auditState.qrThreatsCount
                    )
                    auditState = updatedAudit
                    val assessment = SecurityEvaluator.calculateDynamicAssessment(updatedAudit, lang)
                    val updatedStats = stats.copy(
                        score = assessment.compositeScore,
                        scansDone = stats.scansDone + 1,
                        scannedContentCount = stats.scannedContentCount + 1,
                        threatsBlocked = if (isThreat) stats.threatsBlocked + 1 else stats.threatsBlocked
                    )
                    stats = updatedStats
                    coroutineScope.launch {
                        userProgressRepository.saveLocalStats(updatedStats)
                        val qrScanResult = ScanResult(
                            target = if (isThreat) "qr://suspicious-quishing-token" else "qr://verified-safe-credential",
                            type = "qr",
                            score = if (isThreat) 15 else 95,
                            riskLevel = if (isThreat) "Critical Danger" else "Safe",
                            overallVerdict = if (isThreat) "Dangerous Quishing QR" else "Safe QR Code",
                            domain = "qr-code",
                            findings = emptyList(),
                            recommendation = if (isThreat) "Do not open intercepted QR link" else "QR payload verified",
                            analyzedAt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US).format(java.util.Date())
                        )
                        securityRepository.recordQrScan(qrScanResult)
                    }

                    val qrActivity = SecurityActivity(
                        id = "act-${System.currentTimeMillis()}",
                        type = "qr",
                        title = if (isThreat) (if (lang == Language.AR) "حظر رمز QR مشبوه" else "Suspicious QR blocked") else (if (lang == Language.AR) "مسح QR: سليم" else "QR scan: Safe"),
                        detail = if (isThreat) "Quishing vector intercepted" else "Verified credential payload",
                        time = currentActivityTime(),
                        status = if (isThreat) "critical" else "safe"
                    )
                    coroutineScope.launch {
                        userProgressRepository.logActivity(qrActivity)
                    }

                    if (isThreat) {
                        com.example.sound.SoundManager.playAlert()
                        val threatAlert = SecurityAlert(
                            id = "alert-${System.currentTimeMillis()}",
                            title = if (lang == Language.AR) "حظر رمز QR مشبوه" else "Dangerous QR Code blocked",
                            detail = if (lang == Language.AR) "تم اعتراض رابط تصيد احتيالي في رمز الاستجابة السريعة" else "Phishing payload detected in QR scan.",
                            time = "Just now",
                            severity = AlertSeverity.CRITICAL,
                            read = false,
                            category = "qr"
                        )
                        alerts = listOf(threatAlert) + alerts
                        coroutineScope.launch {
                            userProgressRepository.saveLocalAlert(threatAlert)
                        }
                    } else {
                        com.example.sound.SoundManager.playSuccess()
                    }
                }
            )

            CourseLessonModal(
                course = selectedCourse,
                isOpen = activeModal == "course",
                onClose = {
                    activeModal = null
                    selectedCourse = null
                },
                lang = lang,
                onCompleteLesson = { courseId, lessonId, xp ->
                    completeLesson(courseId, lessonId, xp)
                }
            )

            EditProfileModal(
                isOpen = activeModal == "edit_profile",
                onClose = { activeModal = null },
                currentProfile = userProfile,
                onSaveProfile = { updated ->
                    userProfile = updated
                    com.example.sound.SoundManager.playSuccess()
                    coroutineScope.launch {
                        userProfileRepository.saveProfile(updated)
                    }
                    activeModal = null
                },
                lang = lang
            )

            if (showNotificationMessage && incomingNotificationMessage != null) {
                Dialog(onDismissRequest = {
                    showNotificationMessage = false
                    hostActivity?.dismissNotificationMessage()
                }) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = CyberRed.copy(alpha = 0.9f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 540.dp)
                                .verticalScroll(rememberScrollState())
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(17.dp))
                                        .background(Brush.linearGradient(listOf(CyberRed.copy(alpha = 0.30f), CyberPink.copy(alpha = 0.10f))))
                                        .border(1.dp, CyberRed.copy(alpha = 0.65f), RoundedCornerShape(17.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Filled.Shield, contentDescription = null, tint = CyberRed, modifier = Modifier.size(27.dp))
                                }
                                Spacer(Modifier.width(13.dp))
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Text(
                                        text = if (lang == Language.AR) "موجز الحماية اليومي" else "DAILY SECURITY BRIEF",
                                        color = CyberRed,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp,
                                        letterSpacing = 1.1.sp
                                    )
                                    Text(
                                        text = if (lang == Language.AR) "رسالة توعوية من CyberGuard" else "A security insight from CyberGuard",
                                        color = CyberMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                Box(
                                    Modifier.size(8.dp).clip(CircleShape).background(CyberRed)
                                )
                            }
                            Box(
                                Modifier.fillMaxWidth().height(1.dp)
                                    .background(Brush.horizontalGradient(listOf(Color.Transparent, CyberRed.copy(alpha = 0.8f), Color.Transparent)))
                            )
                            Column(
                                modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(Color(0xFF17131B))
                                    .border(1.dp, CyberRed.copy(alpha = 0.30f), RoundedCornerShape(18.dp))
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = BidiHelper.formatBiDi(incomingNotificationMessage.first, isAr = lang == Language.AR),
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 19.sp,
                                    lineHeight = 27.sp,
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                                Text(
                                    text = BidiHelper.formatBiDi(incomingNotificationMessage.second, isAr = lang == Language.AR),
                                    color = Color(0xFFE5DDE6),
                                    fontSize = 14.sp,
                                    lineHeight = 24.sp,
                                    fontFamily = Strings.getFontFamily(lang)
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Shield, contentDescription = null, tint = CyberRed, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(7.dp))
                                Text(
                                    text = if (lang == Language.AR) "توقف،\u00A0تحقّق،\u00A0ثم\u00A0تصرّف\u00A0بأمان." else "Pause, verify, and stay protected.",
                                    color = CyberMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Clip
                                )
                            }
                            androidx.compose.material3.Button(
                                onClick = {
                                    showNotificationMessage = false
                                    hostActivity?.dismissNotificationMessage()
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(15.dp),
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = CyberRed)
                            ) {
                                Text(
                                    text = if (lang == Language.AR) "تمت القراءة" else "Mark as read",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(Modifier.width(8.dp))
                                Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(17.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun CyberBottomNavigationBar(
    currentTab: TabType,
    onSelectTab: (TabType) -> Unit,
    unreadAlertsCount: Int,
    lang: Language
) {
    val tabs = listOf(
        Triple(TabType.HOME, Strings.navHome(lang), Icons.Filled.Home),
        Triple(TabType.SCAN, Strings.navScan(lang), Icons.Filled.Radar),
        Triple(TabType.ACADEMY, Strings.navAcademy(lang), Icons.AutoMirrored.Filled.MenuBook),
        Triple(TabType.ALERTS, Strings.navAlerts(lang), Icons.Filled.NotificationsNone),
        Triple(TabType.MORE, Strings.navMore(lang), Icons.Filled.GridView)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 26.dp, end = 26.dp, top = 5.dp, bottom = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xE8171922), Color(0xD90A0B11))
                    )
                )
                .border(
                    1.dp,
                    Brush.linearGradient(
                        listOf(Color.White.copy(alpha = 0.30f), CyberPink.copy(alpha = 0.16f), Color.White.copy(alpha = 0.08f))
                    ),
                    RoundedCornerShape(30.dp)
                )
                .padding(horizontal = 5.dp, vertical = 4.dp)
                .testTag("cyber_bottom_nav"),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { (tab, label, icon) ->
                val selected = currentTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(25.dp))
                        .background(
                            if (selected) Brush.verticalGradient(
                                listOf(CyberPink.copy(alpha = 0.24f), CyberRed.copy(alpha = 0.12f))
                            ) else Brush.verticalGradient(listOf(Color.Transparent, Color.Transparent))
                        )
                        .then(
                            if (selected) Modifier.border(1.dp, Color.White.copy(alpha = 0.13f), RoundedCornerShape(25.dp))
                            else Modifier
                        )
                        .clickable {
                            com.example.sound.SoundManager.playButtonClick()
                            onSelectTab(tab)
                        }
                        .testTag("nav_tab_${tab.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (selected) Color.White else CyberMuted,
                                modifier = Modifier.size(if (selected) 22.dp else 21.dp)
                            )
                            if (tab == TabType.ALERTS && unreadAlertsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(15.dp)
                                        .align(Alignment.TopEnd)
                                        .offset(x = 6.dp, y = (-4).dp)
                                        .clip(CircleShape)
                                        .background(CyberRed)
                                        .border(1.dp, Color(0xFF171922), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = unreadAlertsCount.coerceAtMost(99).toString(),
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                        if (selected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 3.dp)
                                    .width(12.dp)
                                    .height(2.5.dp)
                                    .clip(CircleShape)
                                    .background(Brush.horizontalGradient(listOf(CyberPink, CyberRed)))
                            )
                        }
                    }
                }
            }
        }
    }
}
