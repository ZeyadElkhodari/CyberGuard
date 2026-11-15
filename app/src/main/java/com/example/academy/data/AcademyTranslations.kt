package com.example.academy.data

import com.example.model.Language
import com.example.util.BidiHelper
import org.json.JSONArray

/**
 * Complete offline translation and localization engine for CyberGuard Academy.
 * Provides bilingual support (Arabic & English) for:
 * 1. UI labels, headers, badges, toasts, and buttons.
 * 2. Track names, slugs, and comprehensive descriptions.
 * 3. Article reading experiences (titles, scenarios, explanations, warning signs, recovery).
 * 4. Quiz experiences (titles, descriptions, questions, options, explanations, warnings).
 * 5. Daily challenge content and feedback states.
 * 6. Security achievements and user ranks.
 */
object AcademyTranslations {

    // =========================================================================
    // 1. UI LABELS & NAVIGATION
    // =========================================================================

    fun academyTitle(lang: Language): String =
        if (lang == Language.AR) "أكاديمية سايبر جارد" else "CyberGuard Academy"

    fun academySubtitle(lang: Language): String =
        if (lang == Language.AR) "منظومة الوعي والحماية السيبرانية الشاملة" else "Comprehensive Cybersecurity Training"

    fun levelLabel(lang: Language, level: Int): String =
        if (lang == Language.AR) "المستوى $level" else "Level $level"

    fun xpBadge(lang: Language, xp: Int): String =
        if (lang == Language.AR) "$xp نقطة XP" else "$xp XP"

    fun confirmedXp(lang: Language, xp: Int): String =
        if (lang == Language.AR) "$xp XP" else "$xp XP"

    fun overallProgress(lang: Language): String =
        if (lang == Language.AR) "التقدم الإجمالي بالأكاديمية" else "Overall Academy Progress"

    fun articlesCompleted(lang: Language, count: Int, total: Int = 130): String =
        if (lang == Language.AR) "$count/$total مقالاً مكتملاً" else "$count/$total Articles Read"

    fun quizzesPassed(lang: Language, count: Int, total: Int = 13): String =
        if (lang == Language.AR) "$count/$total اختباراً مجتازاً" else "$count/$total Quizzes Passed"

    fun tracksCompleted(lang: Language, count: Int, total: Int = 13): String =
        if (lang == Language.AR) "$count/$total مساراً مكتملاً" else "$count/$total Tracks Finished"

    fun dailyChallengeHeader(lang: Language): String =
        if (lang == Language.AR) "التحدي الأمني اليومي" else "Daily Security Challenge"

    fun dailyChallengeReward(lang: Language, xp: Int = 15): String =
        if (lang == Language.AR) "+$xp نقطة XP" else "+$xp XP"

    fun dailyChallengeCompleted(lang: Language): String =
        if (lang == Language.AR) "تم إكمال تحدي اليوم" else "Today's Challenge Completed"

    fun dailyChallengeSub(lang: Language): String =
        if (lang == Language.AR) "سؤال واقعي سريع لاختبار يقظتك وحماية بياناتك" else "Quick real-world scenario to test your reflexes"

    fun startChallenge(lang: Language): String =
        if (lang == Language.AR) "خوض التحدي الآن" else "Take Challenge"

    fun reviewChallenge(lang: Language): String =
        if (lang == Language.AR) "مراجعة النتيجة" else "Review Result"

    fun learningTracksHeader(lang: Language): String =
        if (lang == Language.AR) "المسارات التعليمية (13 مساراً)" else "Learning Tracks (13 Tracks)"

    fun filterAll(lang: Language): String =
        if (lang == Language.AR) "الكل" else "All"

    fun filterInProgress(lang: Language): String =
        if (lang == Language.AR) "قيد التعلم" else "In Progress"

    fun filterCompleted(lang: Language): String =
        if (lang == Language.AR) "المكتملة" else "Completed"

    fun statusNotStarted(lang: Language): String =
        if (lang == Language.AR) "لم يبدأ" else "Not Started"

    fun statusInProgress(lang: Language): String =
        if (lang == Language.AR) "قيد التعلم" else "In Progress"

    fun statusCompleted(lang: Language): String =
        if (lang == Language.AR) "مكتمل" else "Completed"

    fun articlesCount(lang: Language, count: Int): String =
        if (lang == Language.AR) "$count مقالات" else "$count Articles"

    fun questionsCount(lang: Language, count: Int): String =
        if (lang == Language.AR) "$count سؤالاً" else "$count Questions"

    fun trackQuiz(lang: Language): String =
        if (lang == Language.AR) "اختبار المسار" else "Track Quiz"

    fun trackQuizPassed(lang: Language, score: Int): String =
        if (lang == Language.AR) "اجتزت الاختبار ($score%)" else "Quiz Passed ($score%)"

    fun openTrackAction(lang: Language): String =
        if (lang == Language.AR) "استعراض الدروس" else "View Lessons"

    fun takeQuizAction(lang: Language): String =
        if (lang == Language.AR) "بدء الاختبار" else "Start Quiz"

    fun retakeQuizAction(lang: Language): String =
        if (lang == Language.AR) "إعادة الاختبار" else "Retake Quiz"

    fun closeAction(lang: Language): String =
        if (lang == Language.AR) "إغلاق" else "Close"

    fun backAction(lang: Language): String =
        if (lang == Language.AR) "رجوع" else "Back"

    fun nextAction(lang: Language): String =
        if (lang == Language.AR) "التالي" else "Next"

    fun submitAnswerAction(lang: Language): String =
        if (lang == Language.AR) "تأكيد الإجابة" else "Submit Answer"

    fun submitQuizAction(lang: Language): String =
        if (lang == Language.AR) "إنهاء وتسليم الاختبار" else "Submit Quiz"

    fun markArticleReadAction(lang: Language): String =
        if (lang == Language.AR) "إكمال القراءة وكسب الـ XP" else "Finish Reading & Claim XP"

    fun articleAlreadyRead(lang: Language): String =
        if (lang == Language.AR) "تمت قراءة المقال" else "Article Completed"

    fun readTimeMinutes(lang: Language, minutes: Int): String =
        if (lang == Language.AR) "$minutes دقائق قراءة" else "$minutes min read"

    fun realisticScenarioHeader(lang: Language): String =
        if (lang == Language.AR) "سيناريو واقعي من الحياة اليومية" else "Real-World Scenario"

    fun warningSignsHeader(lang: Language): String =
        if (lang == Language.AR) "علامات الخطر التي تكشف الفخ" else "Red Flags & Warning Signs"

    fun protectionStepsHeader(lang: Language): String =
        if (lang == Language.AR) "خطوات الحماية والوقاية الفورية" else "Immediate Protection Steps"

    fun recoveryActionHeader(lang: Language): String =
        if (lang == Language.AR) "ماذا تفعل لو وقعت في الفخ بالفعل؟" else "What to do if already targeted?"

    fun summaryHeader(lang: Language): String =
        if (lang == Language.AR) "خلاصة الدرس" else "Lesson Summary"

    fun explanationHeader(lang: Language): String =
        if (lang == Language.AR) "التفسير الأمني التوضيحي" else "Security Explanation"

    fun warningLessonHeader(lang: Language): String =
        if (lang == Language.AR) "الدرس المستفاد" else "Key Takeaway"

    fun soundEnabledToast(lang: Language): String =
        if (lang == Language.AR) "تم تفعيل المؤثرات الصوتية" else "Sound effects enabled"

    fun soundDisabledToast(lang: Language): String =
        if (lang == Language.AR) "تم كتم المؤثرات الصوتية" else "Sound effects muted"

    fun achievementsTitle(lang: Language): String =
        if (lang == Language.AR) "أوسمة وإنجازات الأكاديمية" else "Security Achievements"

    fun achievementsSubtitle(lang: Language): String =
        if (lang == Language.AR) "سجل إنجازاتك في التدريب والوعي السيبراني" else "Track your cybersecurity learning milestones"

    fun rankTitle(level: Int, lang: Language): String {
        return if (lang == Language.AR) {
            when (level) {
                1 -> "مبتدئ أمني"
                2 -> "متدرب أمني"
                3 -> "مستكشف سيبراني"
                4 -> "مدافع سيبراني"
                5 -> "خبير حماية"
                else -> "حارس سيبراني أسطوري"
            }
        } else {
            when (level) {
                1 -> "Security Cadet"
                2 -> "Security Trainee"
                3 -> "Cyber Explorer"
                4 -> "Cyber Defender"
                5 -> "Protection Specialist"
                else -> "Legendary Cyber Guard"
            }
        }
    }

    // =========================================================================
    // 2. TRACK DESCRIPTIONS
    // =========================================================================

    private val TRACK_DESCRIPTIONS_EN = mapOf(
        "track-1" to "Recognize phishing messages, fake delivery alerts, impersonation attempts, and malicious links before they steal your accounts or money.",
        "track-2" to "Learn how criminals exploit urgency, trust, authority, kindness, and fear to manipulate people into unsafe actions.",
        "track-3" to "Shop safely online by identifying fake stores, misleading discounts, advance-payment traps, and marketplace fraud.",
        "track-4" to "Distinguish legitimate direct selling from pyramid recruitment, Ponzi schemes, and deceptive investment opportunities.",
        "track-5" to "Reduce the personal information exposed through identity documents, receipts, travel posts, apps, and old devices.",
        "track-6" to "Protect family photos, location data, social profiles, private messages, and account recovery settings.",
        "track-7" to "Understand public Wi-Fi threats and use safer network settings, encryption, and personal hotspots.",
        "track-8" to "Build strong unique passwords, use password managers and passkeys, and respond safely to data breaches.",
        "track-9" to "Verify employers and remote-work offers, avoid task scams and money-mule schemes, and protect job applications.",
        "track-10" to "Identify fake prize campaigns, malicious giveaway apps, survey traps, and deceptive QR-code promotions.",
        "track-11" to "Recognize caller-ID spoofing, fake support calls, remote-access traps, and OTP theft attempts.",
        "track-12" to "Secure mobile wallets, transfers, QR codes, SIM cards, and payment transactions against fraud.",
        "track-13" to "Recognize Ponzi schemes, fake investment platforms, forged licences, and pressure-based financial scams."
    )

    fun getTrackTitle(track: AcademyTrackEntity, lang: Language): String {
        if (lang == Language.AR) return track.titleAr
        // Academy-owned translations take precedence over stale Room/API cache values.
        return getTrackTitle(track.id, track.titleAr, lang)
    }

    fun getTrackTitle(slugOrId: String, titleAr: String, lang: Language): String {
        if (lang == Language.AR) return titleAr
        return when (slugOrId) {
            "track-1", "phishing", "fake-messages-and-phishing" -> "Fake Messages & Phishing Attacks"
            "track-2", "social-engineering" -> "Social Engineering & Psychological Manipulation"
            "track-3", "online-fraud" -> "Online Shopping Scams & Commercial Fraud"
            "track-4", "pyramid-schemes" -> "Pyramid Schemes & Fake Investment Traps"
            "track-5", "banking-cards" -> "Personal Data Privacy & Everyday Exposure"
            "track-6", "passwords-auth" -> "Social Media Privacy & Account Security"
            "track-7", "wifi-networks" -> "Public Wi-Fi & Network Security"
            "track-8", "smartphones" -> "Password Security & Account Protection"
            "track-9", "social-media" -> "Fake Jobs & Remote-Work Scams"
            "track-10", "ai-deepfakes" -> "Fake Prizes, Giveaways & Free Offers"
            "track-11", "remote-work" -> "Fake Customer Support & Caller Impersonation"
            "track-12", "family-kids" -> "E-Wallet Fraud & Mobile Payments"
            "track-13", "emergency-response" -> "Investment Scams & Financial Fraud"
            else -> translateSentence(titleAr)
        }
    }

    fun getTrackDescription(trackId: String, lang: Language, fallbackAr: String): String {
        return getTrackDescription(trackId, fallbackAr, lang)
    }

    fun getTrackDescription(trackId: String, fallbackAr: String, lang: Language): String {
        val normalizedId = when {
            trackId.startsWith("track-") -> trackId
            trackId.matches(Regex("\\d+")) -> "track-$trackId"
            else -> trackId
        }
        return if (lang == Language.EN) TRACK_DESCRIPTIONS_EN[normalizedId] ?: translateSentence(fallbackAr) else fallbackAr
    }

    // =========================================================================
    // 3. ACHIEVEMENTS TRANSLATIONS
    // =========================================================================

    private val ACHIEVEMENT_TITLES_EN = mapOf(
        "ach-first-article" to "First Step in Defense",
        "ach-5-articles" to "Eager Learner",
        "ach-first-quiz" to "First Exam Conquered",
        "ach-track-1" to "Phishing Hunter",
        "ach-all-tracks" to "Cyber Master Defender",
        "ach-daily-streak" to "Daily Security Vigilance"
    )

    private val ACHIEVEMENT_DESC_EN = mapOf(
        "ach-first-article" to "Complete reading your first security awareness article.",
        "ach-5-articles" to "Read 5 educational articles across any cybersecurity tracks.",
        "ach-first-quiz" to "Pass your first track quiz with 75% or higher score.",
        "ach-track-1" to "Complete all 10 articles and pass the quiz in Fake Messages track.",
        "ach-all-tracks" to "Successfully complete all 13 educational cybersecurity tracks.",
        "ach-daily-streak" to "Complete the Daily Security Challenge."
    )

    fun getAchievementTitle(id: String, fallbackAr: String, lang: Language): String =
        if (lang == Language.EN) ACHIEVEMENT_TITLES_EN[id] ?: translateSentence(fallbackAr) else fallbackAr

    fun getAchievementDescription(id: String, fallbackAr: String, lang: Language): String =
        if (lang == Language.EN) ACHIEVEMENT_DESC_EN[id] ?: translateSentence(fallbackAr) else fallbackAr

    // =========================================================================
    // 4. ARTICLE & QUESTION TRANSLATOR
    // =========================================================================

    data class TranslatedArticle(
        val title: String,
        val intro: String,
        val simpleExplanation: String,
        val realisticScenario: String,
        val warningSigns: List<String>,
        val protectionSteps: List<String>,
        val recoveryAction: String?,
        val summary: String?
    )

    fun getArticleContent(art: AcademyArticleEntity, lang: Language): TranslatedArticle {
        if (lang == Language.AR) {
            val warnings = parseJsonList(art.warningSignsJson)
            val protections = parseJsonList(art.protectionStepsJson)
            return TranslatedArticle(
                title = BidiHelper.formatBiDi(art.titleAr, true),
                intro = BidiHelper.formatBiDi(art.introAr, true),
                simpleExplanation = BidiHelper.formatBiDi(art.simpleExplanationAr, true),
                realisticScenario = BidiHelper.formatBiDi(art.realisticScenarioAr, true),
                warningSigns = warnings.map { BidiHelper.formatBiDi(it, true) },
                protectionSteps = protections.map { BidiHelper.formatBiDi(it, true) },
                recoveryAction = art.recoveryActionAr?.let { BidiHelper.formatBiDi(it, true) },
                summary = art.summaryAr?.let { BidiHelper.formatBiDi(it, true) }
            )
        }

        // English Translation Engine
        val englishArticle = CuratedEnglishContent.getArticle(art.id)
        if (englishArticle != null) {
            return englishArticle
        }

        // Smart educational phrase translation for unmapped articles
        return TranslatedArticle(
            title = art.titleEn?.takeIf { it.isNotBlank() } ?: translateSentence(art.titleAr),
            intro = art.introEn?.takeIf { it.isNotBlank() } ?: translateSentence(art.introAr),
            simpleExplanation = art.simpleExplanationEn?.takeIf { it.isNotBlank() } ?: translateSentence(art.simpleExplanationAr),
            realisticScenario = art.realisticScenarioEn?.takeIf { it.isNotBlank() } ?: translateSentence(art.realisticScenarioAr),
            warningSigns = parseJsonList(art.warningSignsJsonEn ?: art.warningSignsJson).map { if (art.warningSignsJsonEn.isNullOrBlank()) translateSentence(it) else it },
            protectionSteps = parseJsonList(art.protectionStepsJsonEn ?: art.protectionStepsJson).map { if (art.protectionStepsJsonEn.isNullOrBlank()) translateSentence(it) else it },
            recoveryAction = art.recoveryActionEn?.takeIf { it.isNotBlank() } ?: art.recoveryActionAr?.let { translateSentence(it) },
            summary = art.summaryEn?.takeIf { it.isNotBlank() } ?: art.summaryAr?.let { translateSentence(it) }
        )
    }

    data class TranslatedQuestion(
        val questionText: String,
        val situation: String?,
        val options: List<String>,
        val explanation: String?,
        val warningLesson: String?
    )

    fun getQuestionContent(q: AcademyQuestionEntity, lang: Language): TranslatedQuestion {
        val originalOptions = parseJsonList(q.optionsJson)

        if (lang == Language.AR) {
            return TranslatedQuestion(
                questionText = BidiHelper.formatBiDi(q.questionTextAr, true),
                situation = q.situationAr?.let { BidiHelper.formatBiDi(it, true) },
                options = originalOptions.map { BidiHelper.formatBiDi(it, true) },
                explanation = q.explanationAr?.let { BidiHelper.formatBiDi(it, true) },
                warningLesson = q.warningLessonAr?.let { BidiHelper.formatBiDi(it, true) }
            )
        }

        val englishQuestion = CuratedEnglishContent.getQuestion(q.id)
        if (englishQuestion != null) {
            return englishQuestion
        }

        val englishOptions = q.optionsJsonEn?.let { parseJsonList(it) }?.takeIf { it.isNotEmpty() } ?: originalOptions.map { translateSentence(it) }
        return TranslatedQuestion(
            questionText = q.questionTextEn?.takeIf { it.isNotBlank() } ?: translateSentence(q.questionTextAr),
            situation = q.situationEn?.takeIf { it.isNotBlank() } ?: q.situationAr?.let { translateSentence(it) },
            options = englishOptions,
            explanation = q.explanationEn?.takeIf { it.isNotBlank() } ?: q.explanationAr?.let { translateSentence(it) },
            warningLesson = q.warningLessonEn?.takeIf { it.isNotBlank() } ?: q.warningLessonAr?.let { translateSentence(it) }
        )
    }

    private fun parseJsonList(json: String): List<String> {
        val list = mutableListOf<String>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                list.add(arr.getString(i))
            }
        } catch (_: Exception) {}
        return list
    }

    /**
     * Translates common cybersecurity expressions and vernacular terms into clear,
     * idiomatic English while maintaining technical accuracy.
     */
    fun translateSentence(arabicText: String): String {
        if (arabicText.isBlank()) return ""
        
        var text = arabicText

        // Common cybersecurity phrasing replacements
        val termsMap = listOf(
            "إيه هو التصيد الاحتيالي؟" to "What is Phishing?",
            "التصيد الاحتيالي" to "Phishing Scam",
            "الهندسة الاجتماعية" to "Social Engineering",
            "النصب والاحتيال الإلكتروني" to "Online Scams & Fraud",
            "التسويق الشبكي" to "Network Marketing & Pyramid Schemes",
            "الشراء الآمن عبر الإنترنت" to "Safe Online Shopping",
            "كلمات المرور" to "Passwords",
            "التحقق بخطوتين" to "Two-Factor Authentication (2FA)",
            "أمان شبكات الواي فاي" to "Wi-Fi & Router Security",
            "أمان الهواتف الذكية" to "Mobile Security & App Permissions",
            "وسائل التواصل الاجتماعي" to "Social Media Privacy & Security",
            "الذكاء الاصطناعي والتزييف العميق" to "AI, Deepfakes & Voice Cloning",
            "العمل عن بعد" to "Remote Work & Workplace Security",
            "أمان الأسرة والأطفال" to "Family, Kids & Senior Online Safety",
            "الاستجابة لطوارئ الاختراق" to "Emergency Incident Response & Extortion",
            "رسالة مزيفة" to "Fake Message",
            "كود التحقق" to "Verification Code (OTP)",
            "رمز الأمان" to "Security OTP",
            "تحديث البيانات" to "Account Update",
            "إيقاف الحساب" to "Account Suspension",
            "الرقم السري" to "Password / PIN",
            "خدمة العملاء" to "Customer Support",
            "البنك" to "Bank",
            "رابط مشبوه" to "Suspicious Link",
            "البريد المصري" to "Postal Service",
            "شركة الشحن" to "Delivery Courier",
            "فلاشة" to "USB Flash Drive",
            "فيروس فدية" to "Ransomware",
            "عمرك ما تضغط على رابط" to "Never click on a link",
            "اتصل بخدمة العملاء فوراً" to "Contact customer service immediately",
            "غير كلمة المرور فوراً" to "Change password immediately",
            "لا تشارك رمز OTP مع أي شخص" to "Never share your OTP with anyone",
            "تجاهل الرسالة واحذفها فوراً" to "Ignore and delete the message immediately",
            "فحص الرابط عبر فاحص الروابط" to "Check link with Link Scanner",
            "صح" to "Correct",
            "خطأ" to "Incorrect",
            "تصرف سليم" to "Safe Action",
            "تصرف خطير" to "Dangerous Action"
        )

        for ((ar, en) in termsMap) {
            text = text.replace(ar, en)
        }

        // If predominantly Arabic without exact match, provide clear English representation
        val hasArabic = text.any { it in '\u0600'..'\u06FF' }
        if (hasArabic) {
            // Normalize common patterns into readable educational security English
            return CuratedEnglishContent.fallbackTranslate(arabicText)
        }

        return text
    }
}
