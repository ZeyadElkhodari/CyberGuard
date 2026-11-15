package com.example.localization

import androidx.compose.ui.text.font.FontFamily
import com.example.model.Language
import com.example.model.PreferredAddress
import com.example.model.UserProfile
import com.example.ui.theme.PlusJakartaSansFamily
import com.example.ui.theme.TajawalFamily
import com.example.util.BidiHelper

object Strings {
    private fun ar(text: String): String = BidiHelper.formatBiDi(text, isAr = true)

    fun appName(lang: Language) = if (lang == Language.AR) "سايبر جارد" else "CYBERGUARD"
    fun tagline(lang: Language) = if (lang == Language.AR) "حماية ذكية لحياتك الرقمية" else "Smart Protection for Your Digital Life"
    fun welcomeSub(lang: Language) = if (lang == Language.AR) 
        "احمِ حياتك الرقمية بأدوات أمنية ذكية، وإرشادات عملية، ووعي أمني حقيقي لمواجهة التهديدات السيبرانية."
    else 
        "Protect your digital life with smart security tools, practical guidance, and real-world awareness."
    fun getStarted(lang: Language) = if (lang == Language.AR) "ابدأ الآن" else "Get Started"
    fun skip(lang: Language) = if (lang == Language.AR) "تخطي" else "Skip"
    fun next(lang: Language) = if (lang == Language.AR) "التالي" else "Next"
    fun back(lang: Language) = if (lang == Language.AR) "السابق" else "Back"

    // Onboarding 1
    fun onboarding1Title(lang: Language) = if (lang == Language.AR) "التوعية بالأمن السيبراني" else "Cybersecurity Awareness"
    fun onboarding1Sub(lang: Language) = if (lang == Language.AR) "مساعدك الشخصي للوعي والحماية الرقمية" else "Your Personal Security Assistant"
    fun onboarding1Desc(lang: Language) = if (lang == Language.AR)
        ar("يعمل سايبر جارد كمساعدك الشخصي للحماية والوعي السيبراني؛ لتمكينك من استيعاب التهديدات الرقمية، ورصد الأنشطة المشبوهة، والتعرف على هجمات Phishing و Malware وحماية بياناتك بفعالية.")
    else
        "CyberGuard serves as your personal cybersecurity awareness and protection assistant, empowering you to understand digital threats, identify suspicious activities, and defend against Malware and Phishing vectors."

    // Onboarding 2
    fun onboarding2Title(lang: Language) = if (lang == Language.AR) "استكشف إمكانيات سايبر جارد" else "Explore CyberGuard"
    fun onboarding2Sub(lang: Language) = if (lang == Language.AR) "دفاع ذكي وتوعية تفاعلية متقدمة" else "Smart Defense & Practical Tools"
    fun onboarding2Feature1Title(lang: Language) = if (lang == Language.AR) ar("فاحص الروابط (Link Scanner)") else "Link Scanner"
    fun onboarding2Feature1Desc(lang: Language) = if (lang == Language.AR) ar("يساعدك في اكتشاف الروابط المشبوهة والتحقق من النطاقات الخبيثة ومكافحة Phishing قبل فتحها.") else "Helps identify suspicious or potentially dangerous links and unverified web domains."
    fun onboarding2Feature2Title(lang: Language) = if (lang == Language.AR) ar("أمان كلمات المرور (Password Security)") else "Password Security"
    fun onboarding2Feature2Desc(lang: Language) = if (lang == Language.AR) ar("يساعدك على تقييم قوة التشفير والإنتروبيا وفحص كشف التسريبات العالمية عبر تقنية k-Anonymity.") else "Helps users evaluate password strength, Shannon entropy, and test against known leak breaches."
    fun onboarding2Feature3Title(lang: Language) = if (lang == Language.AR) ar("التوعية الأمنية (Security Awareness)") else "Security Awareness"
    fun onboarding2Feature3Desc(lang: Language) = if (lang == Language.AR) ar("فهم التهديدات السيبرانية الشائعة مثل هجمات الهندسة الاجتماعية، وتعلم ممارسات رقمية آمنة.") else "Helps users understand common cyber threats, social engineering, and safer digital practices."

    // Dashboard
    fun welcomeBack(lang: Language) = if (lang == Language.AR) "مرحباً بك مجدداً،" else "Welcome back,"
    fun userName(lang: Language) = if (lang == Language.AR) "مستخدم سايبر" else "Cyber User"
    fun securityScore(lang: Language) = if (lang == Language.AR) "مؤشر الأمان الشامل" else "Security Score"

    // Meaningful Verbal Assessments
    fun ratingExcellent(lang: Language) = if (lang == Language.AR) "ممتاز" else "Excellent"
    fun ratingGood(lang: Language) = if (lang == Language.AR) "جيد" else "Good"
    fun ratingModerate(lang: Language) = if (lang == Language.AR) "متوسط" else "Moderate"
    fun ratingWeak(lang: Language) = if (lang == Language.AR) "ضعيف" else "Weak"
    fun ratingCritical(lang: Language) = if (lang == Language.AR) "حرج" else "Critical"

    fun pointsEarned(lang: Language, earned: Int, max: Int) =
        if (lang == Language.AR) ar("$earned/$max نقطة مكتسبة") else "$earned/$max points earned"

    fun threatsBlocked(lang: Language) = if (lang == Language.AR) "التهديدات المحجوبة" else "Threats Blocked"
    fun scannedContent(lang: Language) = if (lang == Language.AR) "المحتوى المفحوص" else "Scanned Content"
    fun scansDone(lang: Language) = if (lang == Language.AR) "الفحوصات المنجزة" else "Scans Done"
    fun activeIntercepts(lang: Language) = if (lang == Language.AR) "اعتراضات نشطة" else "Active Intercepts"
    fun itemsAudited(lang: Language) = if (lang == Language.AR) "عناصر تم فحصها" else "Items evaluated"
    fun realChecks(lang: Language) = if (lang == Language.AR) "عمليات فحص" else "Completed audits"

    // Assessment Card Content
    fun assessmentCardTitle(lang: Language) = if (lang == Language.AR) "تقرير التقييم والجاهزية الأمنية" else "Security Readiness & Defense Audit"
    fun assessmentCardStatusReady(lang: Language) = if (lang == Language.AR) "تم فحص محاور الأمان الرئيسية" else "Core Security Pillars Evaluated"
    fun assessmentCardStatusPending(lang: Language) = if (lang == Language.AR) "يلزم استكمال فحص المحاور المعلقة" else "Pending Checks Require Evaluation"
    fun assessmentAdviceExcellent(lang: Language) = if (lang == Language.AR) 
        ar("حالتك الأمنية ممتازة. كلمات المرور تملك إنتروبيا قوية، ولم تسجل الروابط أي تواصل مع نطاقات Phishing أو Malware معروفة.")
    else
        "Your security status is Excellent. Credentials maintain high entropy, and scanned links show clean reputations with no Malware or Phishing indicators."
    fun assessmentAdviceGood(lang: Language) = if (lang == Language.AR)
        ar("مستوى أمانك جيد. نوصي بتفعيل التحقق بخطوتين (Two-Factor Authentication) ومراجعة إعدادات تشفير شبكة Wi-Fi.")
    else
        "Your security level is Good. We recommend enabling Two-Factor Authentication and reviewing Wi-Fi Encryption configurations."
    fun assessmentAdviceModerate(lang: Language) = if (lang == Language.AR)
        "مستوى أمانك متوسط. يوجد تحذير بشأن ضعف بعض كلمات المرور أو وجود ضوابط راوتر غير مفعلة."
    else
        "Your security level is Moderate. Some passwords lack sufficient complexity or network controls remain unconfigured."
    fun assessmentAdviceWeak(lang: Language) = if (lang == Language.AR)
        "مستوى أمانك ضعيف. يُنصح بإجراء فحص للروابط المشبوهة واستبدال كلمات المرور المعرضة للمخاطر فوراً."
    else
        "Your security level is Weak. Immediate review of vulnerable credentials and suspicious links is advised."
    fun assessmentAdviceCritical(lang: Language) = if (lang == Language.AR)
        "مستوى أمانك حرج! تم رصد مؤشرات تسريب أو روابط عالية الخطورة. تجنب إدخال أي بيانات حساسة."
    else
        "Critical Risk detected! Breached credentials or malicious destinations flagged. Do not enter sensitive data."

    fun quickAccess(lang: Language) = if (lang == Language.AR) "الوصول السريع" else "Quick Access"
    fun seeAll(lang: Language) = if (lang == Language.AR) "عرض الكل" else "See all"
    fun securityActivity(lang: Language) = if (lang == Language.AR) "سجل نشاطك الأمني" else "Your Security Activity Log"
    fun viewReport(lang: Language) = if (lang == Language.AR) "عرض التقرير" else "View report"
    fun recommended(lang: Language) = if (lang == Language.AR) "موصى به لك" else "Recommended for you"
    fun featuredCourse(lang: Language) = if (lang == Language.AR) "دورة مميزة" else "FEATURED COURSE"
    fun masterEthicalHacking(lang: Language) = if (lang == Language.AR) "احتراف الهاكر الأخلاقي" else "Master Ethical Hacking"
    fun featuredDesc(lang: Language) = if (lang == Language.AR) 
        ar("مهارات أمنية عملية من خلال دروس تفاعلية موجهة حول ثغرات SQL Injection و XSS و Firewall.") 
    else 
        "Practical security skills through guided interactive lessons on SQL Injection, XSS, and Firewall defense."
    fun academyShortcutTitle(lang: Language) = if (lang == Language.AR) "أكاديمية الأمن السيبراني" else "Cybersecurity Academy"
    fun academyShortcutDesc(lang: Language) = if (lang == Language.AR) "طوّر مهاراتك عبر مسارات ودروس وتحديات تفاعلية." else "Build your skills with interactive learning tracks, lessons, and challenges."
    fun academyShortcutTag(lang: Language) = if (lang == Language.AR) "تعلّم واستكشف" else "LEARN & EXPLORE"

    // Quick Cards
    fun linkScanner(lang: Language) = if (lang == Language.AR) "فاحص الروابط" else "Link Scanner"
    fun linkScannerDesc(lang: Language) = if (lang == Language.AR) ar("كشف مواقع Phishing وروابط الاحتيال") else "Check suspicious URLs"
    fun passwordCheck(lang: Language) = if (lang == Language.AR) "فحص كلمة المرور" else "Password Check"
    fun passwordCheckDesc(lang: Language) = if (lang == Language.AR) "اختبار قوة التشفير والتسريبات" else "Test password strength"

    // Scan Screen - Short Title with non-breaking space to guarantee single-line rendering
    fun securityScanTitle(lang: Language) = if (lang == Language.AR) "الفحص\u00A0الأمني" else "Security Scan"
    fun securityScanSub(lang: Language) = if (lang == Language.AR) "اختر المحور الذي تود تدقيقه" else "Choose what you want to check"
    fun qrScannerTitle(lang: Language) = if (lang == Language.AR) ar("ماسح رمز QR") else "QR Code Scanner"
    fun qrScannerDesc(lang: Language) = if (lang == Language.AR)
        ar("فحص وتحليل رموز QR وكشف مخاطر الاحتيال وروابط التصيد (Quishing).")
    else
        "Scan QR codes and inspect their contents for potential security and quishing risks."
    fun scanQrAction(lang: Language) = if (lang == Language.AR) ar("مسح رمز QR") else "Scan QR Code"
    fun qrVerdictNotEvaluated(lang: Language) = if (lang == Language.AR) "التحقق الأمني غير مكتمل" else "Not Fully Evaluated"
    fun qrVerdictUnableToVerify(lang: Language) = if (lang == Language.AR) "تعذر التحقق الأمني الكامل" else "Unable to Verify"
    fun qrNonUrlAdvice(lang: Language) = if (lang == Language.AR)
        "تم استخراج المحتوى بنجاح، لكنه لا يحتوي على رابط إنترنت يمكن فحصه في قواعد التهديدات. تجنب تنفيذ أي أوامر غير موثوقة."
    else
        "Content decoded safely, but cannot be fully security-verified against threat databases because it is not an inspectable URL."
    fun qrWifiConfigAdvice(lang: Language, isOpen: Boolean) = if (lang == Language.AR) {
        if (isOpen) "تحذير: شبكة Wi-Fi هذه مفتوحة وغير مشفرة! بياناتك قد تكون معرضة للاعتراض."
        else "إعدادات شبكة Wi-Fi مشفرة. تأكد من ثقتك بالشبكة قبل إضافتها إلى جهازك."
    } else {
        if (isOpen) "Warning: Open/unencrypted Wi-Fi configuration! Network traffic could be intercepted."
        else "Protected Wi-Fi network configuration. Verify trust before connecting."
    }
    fun qrSimulatedSample(lang: Language) = if (lang == Language.AR) "عينة تجريبية للمحاكاة" else "Simulated Test Sample"
    fun qrSampleNotice(lang: Language) = if (lang == Language.AR)
        "هذه عينة فحص تجريبية للمطورين والمختبرين ولا تؤثر على نتائج فحصك الحقيقي."
    else
        "This is a pre-tested simulation sample for testing and does not alter your real audit results."
    fun phishingMessageCheck(lang: Language) = if (lang == Language.AR) "فحص رسائل التصيد" else "Phishing Message Check"
    fun phishingMessageDesc(lang: Language) = if (lang == Language.AR) ar("تحليل نصوص SMS والبريد الاحتيالي") else "Learn to identify suspicious messages"
    fun wifiSafety(lang: Language) = if (lang == Language.AR) ar("أمان شبكة Wi-Fi") else "Wi-Fi Safety"
    fun wifiSafetyDesc(lang: Language) = if (lang == Language.AR) "فحص أمان وتشفير شبكة الاتصال الحالية" else "Scan connected network security & encryption"
    fun startWifiScan(lang: Language) = if (lang == Language.AR) ar("بدء فحص شبكة Wi-Fi") else "Start Wi-Fi Scan"
    fun scanningWifi(lang: Language) = if (lang == Language.AR) "جاري فحص إعدادات التشفير والشبكة..." else "Checking Wi-Fi security & encryption..."
    fun wifiScanCardTitle(lang: Language) = if (lang == Language.AR) "فحص أمان شبكة Wi-Fi" else "Wi-Fi Security Scan"
    fun wifiScanBilingualTitle() = "فحص أمان شبكة Wi-Fi | Wi-Fi Security Scan"
    fun openLocationSettings(lang: Language) = if (lang == Language.AR) "فتح إعدادات الموقع" else "Open Location Settings"
    fun locationSettingsHelp(lang: Language) = if (lang == Language.AR)
        "فعّل مفتاح الموقع من إعدادات الجهاز، ثم ارجع إلى CyberGuard واضغط إعادة الفحص. لا يستطيع التطبيق تشغيل الموقع نيابةً عنك."
    else
        "Turn on the device Location switch, then return to CyberGuard and scan again. Android does not allow the app to enable Location for you."
    fun wifiScanCardSubtitle(lang: Language) = if (lang == Language.AR) "تحليل تشفير الشبكة الحالية، كشف الثغرات، ومستوى الحماية" else "Inspect connected network encryption, weaknesses, and security"
    fun networkNameLabel(lang: Language) = if (lang == Language.AR) "اسم الشبكة (SSID)" else "Network Name (SSID)"
    fun localIpLabel(lang: Language) = if (lang == Language.AR) "عنوان IP المحلي" else "Local IPv4 Address"
    fun gatewayLabel(lang: Language) = if (lang == Language.AR) "البوابة الافتراضية" else "Default Gateway"
    fun ssidUnavailable(lang: Language) = if (lang == Language.AR) "غير متاح (قيود خصوصية Android)" else "Unavailable (Android privacy restrictions)"
    fun securityTypeLabel(lang: Language) = if (lang == Language.AR) "نوع التشفير والحماية" else "Security & Encryption Type"
    fun connectionStatusLabel(lang: Language) = if (lang == Language.AR) "حالة الاتصال" else "Connection Status"
    fun overallResultLabel(lang: Language) = if (lang == Language.AR) "التقييم الأمني الشامل" else "Overall Result"
    fun securityScoreLabel(lang: Language) = if (lang == Language.AR) "درجة الأمان" else "Security Score"
    fun signalQualityLabel(lang: Language) = if (lang == Language.AR) "جودة الإشارة" else "Signal Quality"
    fun signalDisclaimer(lang: Language) = if (lang == Language.AR) "مؤشر اتصال فيزيائي، لا يعد دليلاً على ثغرة أمنية" else "Connection quality indicator (not proof of vulnerability)"
    fun frequencyBandLabel(lang: Language) = if (lang == Language.AR) "تردد الشبكة" else "Frequency Band"
    fun linkSpeedLabel(lang: Language) = if (lang == Language.AR) "سرعة الربط" else "Link Speed"
    fun detectedIssuesLabel(lang: Language) = if (lang == Language.AR) "الملاحظات والمخاطر المرصودة" else "Detected Security Issues"
    fun recommendationsLabel(lang: Language) = if (lang == Language.AR) "توصيات تعزيز الأمان" else "Security Recommendations"
    fun rescanLabel(lang: Language) = if (lang == Language.AR) "إعادة الفحص" else "Scan Again"
    fun locationPermNotice(lang: Language) = if (lang == Language.AR) "يتطلب نظام أندرويد إذن الموقع الدقيق لقراءة اسم الشبكة؛ وقد يلزم تفعيل خدمات الموقع من إعدادات الجهاز." else "Android requires Precise Location permission to read the network SSID; the device Location switch may also need to be enabled."
    fun grantPermButton(lang: Language) = if (lang == Language.AR) "منح الإذن" else "Grant Permission"
    fun unableToVerify(lang: Language) = if (lang == Language.AR) "تعذر التحقق / غير محدد" else "Unknown / Unable to Verify"
    fun wifiDisconnectedNotice(lang: Language) = if (lang == Language.AR) "الجهاز غير متصل بشبكة Wi-Fi حالياً. يُرجى الاتصال بشبكة لإجراء الفحص الأمني." else "Device is not currently connected to Wi-Fi. Please connect to a network to perform a security scan."
    fun wifiDisabledNotice(lang: Language) = if (lang == Language.AR) "محول Wi-Fi مغلق حالياً. يُرجى تفعيل Wi-Fi والاتصال بشبكة." else "Wi-Fi is currently disabled. Please enable Wi-Fi and connect to a network."
    fun verdictSecure(lang: Language) = if (lang == Language.AR) "آمنة ومحمية" else "Secure"
    fun verdictModerate(lang: Language) = if (lang == Language.AR) "متوسطة الحماية" else "Moderate"
    fun verdictRisky(lang: Language) = if (lang == Language.AR) "عالية المخاطر" else "Risky"
    fun verdictUnknown(lang: Language) = if (lang == Language.AR) "غير محدد" else "Unknown"
    fun verdictDisconnected(lang: Language) = if (lang == Language.AR) "غير متصل" else "Disconnected"
    fun severityCritical(lang: Language) = if (lang == Language.AR) "حرج" else "Critical"
    fun severityHigh(lang: Language) = if (lang == Language.AR) "مرتفع" else "High"
    fun severityMedium(lang: Language) = if (lang == Language.AR) "متوسط" else "Medium"
    fun severityLow(lang: Language) = if (lang == Language.AR) "منخفض" else "Low"
    fun riskReasonLabel(lang: Language) = if (lang == Language.AR) "سبب الرصد" else "Detection Reason"
    fun riskRecommendationLabel(lang: Language) = if (lang == Language.AR) "الإجراء الموصى به" else "Recommended Action"
    fun signalSectionTitle(lang: Language) = if (lang == Language.AR) "جودة إشارة الاتصال (فيزيائي)" else "Connection Signal Quality"
    fun signalQualityDisclaimerDetail(lang: Language) = if (lang == Language.AR)
        "جودة الإشارة تقيس قوة البث الراديوي للاتصال الفيزيائي فقط، ولا تعتبر مقياساً لمستوى التشفير أو دليلاً على وجود ثغرة أمنية."
    else
        "Signal strength measures radio frequency reception only and is separate from encryption security or vulnerabilities."
    fun noRisksDetectedTitle(lang: Language) = if (lang == Language.AR) "لم تُكتشف ثغرات أمنية نشطة" else "No Active Security Vulnerabilities"
    fun noRisksDetectedDesc(lang: Language) = if (lang == Language.AR)
        "إعدادات التشفير والبروتوكول اللاسلكي لهذه الشبكة مطابقة لمعايير الحماية الموصى بها."
    else
        "Wireless encryption and protocol settings on this network meet recommended security baselines."
    fun bssidLabel(lang: Language) = if (lang == Language.AR) "عنوان نقطة الوصول (BSSID)" else "Access Point (BSSID)"
    fun cipherSuiteLabel(lang: Language) = if (lang == Language.AR) "خوارزمية التشفير" else "Cipher Suite"
    fun internetValidatedLabel(lang: Language) = if (lang == Language.AR) "الوصول للإنترنت" else "Internet Access"
    fun internetVerified(lang: Language) = if (lang == Language.AR) "نشط ومتحقق" else "Validated"
    fun internetOffline(lang: Language) = if (lang == Language.AR) "شبكة محلية فقط" else "Local Only (No Internet)"
    fun captivePortalLabel(lang: Language) = if (lang == Language.AR) "بوابة التسجيل (Portal)" else "Captive Portal"
    fun captivePortalDetected(lang: Language) = if (lang == Language.AR) "نشطة (يلزم تسجيل ويب)" else "Active (Web Login Required)"
    fun captivePortalNone(lang: Language) = if (lang == Language.AR) "غير موجودة" else "None"
    fun locationServicesDisabled(lang: Language) = if (lang == Language.AR)
        "خدمات الموقع (Location Services) متوقفة في إعدادات الجهاز، يلزم تفعيلها مع إذن الموقع لقراءة اسم الشبكة بدقة."
    else
        "System Location Services are disabled in Android settings. Enable them alongside Location permission to read SSID."
    fun privacyFirst(lang: Language) = if (lang == Language.AR) "الخصوصية أولاً" else "Privacy first"
    fun privacyFirstDesc(lang: Language) = if (lang == Language.AR) 
        "هذه محطة عمل أمنية معزولة داخل التطبيق. لا يتم إرسال أي كلمات مرور أو مدخلات خاصة خارج جهازك."
    else 
        "This is a client-side security workstation. No passwords or private scan inputs leave your device."

    // Academy - Short Title with non-breaking space
    fun academyTitle(lang: Language) = if (lang == Language.AR) "أكاديمية\u00A0الأمن" else "Cyber Academy"
    fun academySub(lang: Language) = if (lang == Language.AR) "تعلم • تدرب • تطور" else "Learn • Practice • Improve"
    fun learningPath(lang: Language) = if (lang == Language.AR) "مسارك التعليمي" else "YOUR LEARNING PATH"
    fun level(lang: Language) = if (lang == Language.AR) "المستوى" else "Level"
    fun rank(lang: Language) = if (lang == Language.AR) "مستكشف أمني" else "Security Explorer"
    fun xpToNext(lang: Language) = if (lang == Language.AR) ar("XP للمستوى القادم") else "XP to next level"
    fun exploreCourses(lang: Language) = if (lang == Language.AR) "استكشف الدورات" else "Explore courses"

    // Alerts - Short Title with non-breaking space
    fun alertsTitle(lang: Language) = if (lang == Language.AR) "التنبيهات\u00A0الأمنية" else "Security Alerts"
    fun alertsSub(lang: Language) = if (lang == Language.AR) "نشاطك الأخير والتذكيرات الهامة" else "Your recent activity and reminders"
    fun all(lang: Language) = if (lang == Language.AR) "الكل" else "All"
    fun important(lang: Language) = if (lang == Language.AR) "هام" else "Important"
    fun read(lang: Language) = if (lang == Language.AR) "مقروء" else "Read"
    fun markAllRead(lang: Language) = if (lang == Language.AR) "تحديد الكل كمقروء" else "Mark all as read"
    fun noAlerts(lang: Language) = if (lang == Language.AR) "لا توجد تنبيهات في هذا التصنيف" else "No alerts in this category"

    // More - Single unified word for Arabic
    fun moreTitle(lang: Language) = if (lang == Language.AR) "المزيد" else "More"
    fun moreSub(lang: Language) = if (lang == Language.AR) "إدارة تجربة التطبيق وإعداداته" else "Manage your app experience"
    fun profileTagline(lang: Language) = if (lang == Language.AR) "أمانك الرقمي أولويتنا" else "Your Digital Security Matters"
    fun quickScanSamples(lang: Language) = if (lang == Language.AR) "نماذج فحص سريعة:" else "Quick evaluation samples:"
    fun settingsPrivacy(lang: Language) = if (lang == Language.AR) "الخصوصية وإعدادات الأمان" else "Privacy & Security Controls"
    fun settingsNotifications(lang: Language) = if (lang == Language.AR) "التنبيهات والأصوات" else "Security Alerts & Sounds"
    fun settingsSoundEffects(lang: Language) = if (lang == Language.AR) "المؤثرات الصوتية" else "Sound Effects"
    fun settingsLanguage(lang: Language) = if (lang == Language.AR) "اللغة" else "Language"
    fun settingsAppearance(lang: Language) = if (lang == Language.AR) "المظهر ووضع العرض" else "Appearance & Display Mode"
    fun settingsDevelopers(lang: Language) = if (lang == Language.AR) "المطورون" else "Developers"
    fun settingsDevelopersSubtitle(lang: Language) = if (lang == Language.AR) "فريق مهندسي مشروع سايبر جارد" else "CyberGuard Engineering Team"
    fun settingsResetStats(lang: Language) = if (lang == Language.AR) "إعادة تعيين الإحصائيات وجولة الترحيب" else "Reset Security Statistics & Onboarding"
    fun settingsHelp(lang: Language) = if (lang == Language.AR) "المساعدة وقاعدة المعرفة" else "Help & Knowledge Base"
    fun settingsPolicy(lang: Language) = if (lang == Language.AR) "سياسة الخصوصية والشروط" else "Privacy Policy & Terms"
    fun versionInfo(lang: Language) = if (lang == Language.AR) ar("سايبر جارد • إصدار أندرويد الأصلي • v1.0.0") else "CyberGuard • Native Android Edition • v1.0.0"

    // Developers Dialog
    fun developersTitle(lang: Language) = if (lang == Language.AR) "فريق\u00A0المطورين\u00A0والمهندسين" else "Developers & Engineering Team"
    fun developersSubtitle(lang: Language) = if (lang == Language.AR) 
        "المطور الرسمي للمشروع: Eng / Zeyad Yasser Elkhodari\nويضم سجل المساهمين أعضاء فريق التطوير:"
    else
        "Official project developer: Eng / Zeyad Yasser Elkhodari\nThe contributor list also recognizes the development team:"

    // Navigation
    fun navHome(lang: Language) = if (lang == Language.AR) "الرئيسية" else "Home"
    fun navScan(lang: Language) = if (lang == Language.AR) "فحص" else "Scan"
    fun navAcademy(lang: Language) = if (lang == Language.AR) "أكاديمية" else "Academy"
    fun navAlerts(lang: Language) = if (lang == Language.AR) "تنبيهات" else "Alerts"
    fun navMore(lang: Language) = if (lang == Language.AR) "المزيد" else "More"

    // Common
    fun close(lang: Language) = if (lang == Language.AR) "إغلاق" else "Close"
    fun copy(lang: Language) = if (lang == Language.AR) "نسخ" else "Copy"
    fun copied(lang: Language) = if (lang == Language.AR) "تم النسخ!" else "Copied!"

    // Profile & Personalized Greetings
    fun personalizedGreeting(profile: UserProfile, lang: Language): String {
        val trimmed = profile.name.trim().ifBlank {
            if (lang == Language.AR) "مستخدم سايبر" else "Cyber User"
        }
        return if (lang == Language.AR) {
            when (profile.preferredAddress) {
                PreferredAddress.MALE -> "مرحباً بك يا $trimmed"
                PreferredAddress.FEMALE -> "مرحباً بكِ يا $trimmed"
                PreferredAddress.UNSPECIFIED -> "مرحباً $trimmed"
            }
        } else {
            "Welcome back, $trimmed"
        }
    }

    fun greetingPrefixOnly(address: PreferredAddress, lang: Language): String {
        return if (lang == Language.AR) {
            when (address) {
                PreferredAddress.MALE -> "مرحباً بك يا"
                PreferredAddress.FEMALE -> "مرحباً بكِ يا"
                PreferredAddress.UNSPECIFIED -> "مرحباً"
            }
        } else {
            "Welcome,"
        }
    }

    fun addressOptionLabel(address: PreferredAddress, lang: Language): String {
        return when (address) {
            PreferredAddress.MALE -> if (lang == Language.AR) "ذكر" else "Male"
            PreferredAddress.FEMALE -> if (lang == Language.AR) "أنثى" else "Female"
            PreferredAddress.UNSPECIFIED -> if (lang == Language.AR) "أفضل عدم التحديد" else "Prefer not to specify"
        }
    }

    // First-Time Profile Setup
    fun profileSetupTitle(lang: Language) = if (lang == Language.AR) "لنقم بتخصيص تجربتك" else "Let's personalize your experience"
    fun profileSetupSub(lang: Language) = if (lang == Language.AR)
        "أدخل اسمك وحدد صيغة المخاطبة المفضلة لتخصيص تنبيهات ورسائل سايبر جارد"
    else
        "Enter your name and address preference to tailor your CyberGuard experience"
    fun profileSetupStep(lang: Language) = if (lang == Language.AR) "الخطوة الأخيرة: إعداد الملف الشخصي" else "Final Step: Profile Setup"
    fun nameLabel(lang: Language) = if (lang == Language.AR) "الاسم الشخصي" else "Your Name"
    fun namePlaceholder(lang: Language) = if (lang == Language.AR) "مثال: عبد الله أو سارة" else "e.g. Alex"
    fun nameValidationEmpty(lang: Language) = if (lang == Language.AR) "يرجى كتابة الاسم للمتابعة" else "Please enter your name to continue"
    fun nameValidationTooLong(lang: Language) = if (lang == Language.AR) "يجب ألا يتجاوز الاسم 30 حرفاً" else "Name must not exceed 30 characters"
    fun genderTitle(lang: Language) = if (lang == Language.AR) "صيغة المخاطبة المفضلة" else "Preferred Address"
    fun genderSub(lang: Language) = if (lang == Language.AR) "تحدد هذه الصيغة صياغة التحية والتنبيهات الموجهة إليك" else "Used for respectful personalized greetings"
    fun photoPickerTitle(lang: Language) = if (lang == Language.AR) "صورة الملف الشخصي" else "Profile Picture"
    fun optionalBadge(lang: Language) = if (lang == Language.AR) "(اختياري)" else "(Optional)"
    fun uploadFromGallery(lang: Language) = if (lang == Language.AR) "اختيار من المعرض" else "Choose from Gallery"
    fun removePhoto(lang: Language) = if (lang == Language.AR) "إزالة الصورة" else "Remove Photo"
    fun sampleAvatarsHeader(lang: Language) = if (lang == Language.AR) "أو اختر أحد الرموز السيبرانية" else "Or select a Cyber Avatar"
    fun avatarMaleName(lang: Language) = if (lang == Language.AR) "عميل سايبر (ذكر)" else "Cyber Agent (Male)"
    fun avatarFemaleName(lang: Language) = if (lang == Language.AR) "أخصائية سايبر (أنثى)" else "Cyber Specialist (Female)"
    fun avatarDefaultName(lang: Language) = if (lang == Language.AR) "درع الحماية الافتراضي" else "Default Cyber Shield"
    fun continueAction(lang: Language) = if (lang == Language.AR) "متابعة إلى لوحة التحكم" else "Continue to Dashboard"

    // Profile Management (More Screen)
    fun profileSectionTitle(lang: Language) = if (lang == Language.AR) "الملف الشخصي والمعلومات الشخصية" else "Profile & Personal Information"
    fun profileSectionSub(lang: Language) = if (lang == Language.AR) "إدارة اسمك وصورتك وصيغة المخاطبة" else "Manage your name, photo, and address preference"
    fun editProfile(lang: Language) = if (lang == Language.AR) "تعديل الملف الشخصي" else "Edit Profile"
    fun saveChanges(lang: Language) = if (lang == Language.AR) "حفظ التغييرات" else "Save Changes"
    fun cancel(lang: Language) = if (lang == Language.AR) "إلغاء" else "Cancel"
    fun profileUpdatedSuccess(lang: Language) = if (lang == Language.AR) "تم تحديث الملف الشخصي بنجاح" else "Profile updated successfully"

    fun getFontFamily(lang: Language): FontFamily {
        return if (lang == Language.AR) TajawalFamily else PlusJakartaSansFamily
    }
}
