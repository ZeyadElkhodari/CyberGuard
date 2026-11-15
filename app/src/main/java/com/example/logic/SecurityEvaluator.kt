package com.example.logic

import androidx.compose.ui.graphics.Color
import com.example.model.*
import com.example.ui.theme.*
import java.security.MessageDigest
import kotlin.math.log2
import java.security.SecureRandom

object SecurityEvaluator {
    private val secureRandom = SecureRandom()

    fun evaluatePassword(pwd: String): PasswordEvaluation {
        val length = pwd.length
        val hasLower = pwd.any { it.isLowerCase() }
        val hasUpper = pwd.any { it.isUpperCase() }
        val hasNumber = pwd.any { it.isDigit() }
        val hasSymbol = pwd.any { !it.isLetterOrDigit() }
        val normalized = pwd.lowercase()
            .replace('0', 'o').replace('1', 'l').replace('3', 'e')
            .replace('4', 'a').replace('@', 'a').replace('$', 's')
            .replace('5', 's').replace('7', 't')
        val commonWeak = setOf(
            "password", "123456", "123456789", "qwerty", "admin", "welcome",
            "letmein", "pass123", "iloveyou", "abc123", "password123"
        )
        val isCommon = normalized in commonWeak || commonWeak.any { normalized.contains(it) }
        val repeated = pwd.length >= 3 && pwd.all { it == pwd.first() }
        val hasRepeatedRun = Regex("(.)\\1{2,}").containsMatchIn(pwd)
        val ascending = "abcdefghijklmnopqrstuvwxyz0123456789"
        val descending = ascending.reversed()
        val compact = pwd.lowercase().filter { it.isLetterOrDigit() }
        val hasSequence = compact.length >= 3 && (0..compact.length - 3).any { i ->
            val part = compact.substring(i, i + 3)
            ascending.contains(part) || descending.contains(part) ||
                part.all { it.isDigit() } && part[1].digitToInt() == part[0].digitToInt() + 1 &&
                part[2].digitToInt() == part[1].digitToInt() + 1
        }
        val poolSize = (if (hasLower) 26 else 0) + (if (hasUpper) 26 else 0) +
            (if (hasNumber) 10 else 0) + (if (hasSymbol) 33 else 0)
        val rawEntropy = if (poolSize > 0) length * log2(poolSize.toDouble()) else 0.0
        // Conservative heuristic estimate: pattern/common-password penalties are applied to
        // entropy, and the estimate assumes 10^10 guesses/second for offline cracking.
        val penalty = (if (isCommon) 45.0 else 0.0) +
            (if (repeated || hasRepeatedRun) 20.0 else 0.0) +
            (if (hasSequence) 18.0 else 0.0) +
            (if (normalized != pwd.lowercase() && normalized.contains("password")) 25.0 else 0.0)
        val entropy = (rawEntropy - penalty).coerceAtLeast(0.0).toInt()
        var score = when {
            isCommon -> 5
            entropy < 28 -> 15
            entropy < 36 -> 30
            entropy < 50 -> 50
            entropy < 65 -> 70
            entropy < 80 -> 85
            else -> 95
        }
        if (length < 8) score = minOf(score, 25)
        if (repeated || hasRepeatedRun) score -= 20
        if (hasSequence) score -= 15
        if (length >= 16 && entropy >= 65 && !isCommon && !hasSequence && !hasRepeatedRun) score += 5
        score = score.coerceIn(0, 100)

        val guesses = if (entropy >= 1023) Double.POSITIVE_INFINITY else Math.pow(2.0, entropy.toDouble())
        val seconds = guesses / 10_000_000_000.0
        val crackTime = when {
            isCommon || score < 25 -> "Seconds to minutes"
            seconds < 60 -> "Under a minute"
            seconds < 3600 -> "Minutes to hours"
            seconds < 86400 -> "Hours to days"
            seconds < 31557600 -> "Days to months"
            seconds < 3_155_760_000.0 -> "Years to centuries"
            else -> "Centuries or longer"
        }
        val checks = listOf(
            PasswordCheckItem("Minimum 12 characters", length >= 12),
            PasswordCheckItem("Uppercase & Lowercase letters", hasLower && hasUpper),
            PasswordCheckItem("Includes numbers (0-9)", hasNumber),
            PasswordCheckItem("Includes special symbols (!@#$)", hasSymbol),
            PasswordCheckItem("Not a common or predictable password", !isCommon && !hasSequence && !hasRepeatedRun)
        )
        return PasswordEvaluation(score, entropy, crackTime, checks)
    }

    fun generateStrongPassword(length: Int = 16, includeSymbols: Boolean = true): String {
        val lowers = "abcdefghjkmnpqrstuvwxyz"
        val uppers = "ABCDEFGHJKLMNPQRSTUVWXYZ"
        val numbers = "23456789"
        val symbols = "!@#$%^&*()-_=+[]{}|;:,.<>?"

        var charset = lowers + uppers + numbers
        if (includeSymbols) charset += symbols

        // Preserve the requested size while ensuring every enabled character class is represented.
        val minimumLength = if (includeSymbols) 4 else 3
        val safeLength = length.coerceAtLeast(minimumLength)
        val chars = mutableListOf<Char>()
        chars.add(lowers[secureRandom.nextInt(lowers.length)])
        chars.add(uppers[secureRandom.nextInt(uppers.length)])
        chars.add(numbers[secureRandom.nextInt(numbers.length)])
        if (includeSymbols) {
            chars.add(symbols[secureRandom.nextInt(symbols.length)])
        }

        while (chars.size < safeLength) {
            chars.add(charset[secureRandom.nextInt(charset.length)])
        }

        // Cryptographically secure Fisher-Yates shuffle (Kotlin's default shuffle is not secure).
        for (index in chars.lastIndex downTo 1) {
            val swapIndex = secureRandom.nextInt(index + 1)
            val current = chars[index]
            chars[index] = chars[swapIndex]
            chars[swapIndex] = current
        }
        return chars.joinToString("")
    }

    fun calculateSha1(text: String): String {
        if (text.isEmpty()) return ""
        val bytes = MessageDigest.getInstance("SHA-1").digest(text.toByteArray())
        return bytes.joinToString("") { "%02X".format(it) }
    }

    fun analyzeUrlSafety(rawUrl: String): ScanResult {
        val candidate = rawUrl.trim().let {
            if (it.startsWith("http://", true) || it.startsWith("https://", true)) it else "https://$it"
        }
        val parsed = try { java.net.URI(candidate).normalize() } catch (_: Exception) { null }
        val host = parsed?.host?.removePrefix("[")?.removeSuffix("]")?.lowercase()
        if (parsed == null || host.isNullOrBlank() ||
            parsed.scheme?.lowercase() !in setOf("http", "https") ||
            parsed.rawUserInfo != null || parsed.port !in -1..65535) {
            return ScanResult(
                target = rawUrl, score = 0, riskLevel = "Invalid URL",
                overallVerdict = "Invalid input - not assessed", domain = "",
                findings = listOf(ScanFinding("Invalid URL", "The URL format is invalid; no threat verdict was assigned.", false)),
                recommendation = "Enter a valid HTTP or HTTPS URL.", analyzedAt = "Just now",
                isPartialAssessment = true
            )
        }
        val urlStr = parsed.toASCIIString()

        val findings = mutableListOf<ScanFinding>()
        var score = 100
        val isHttps = urlStr.startsWith("https://")

        if (!isHttps) {
            score -= 30
            findings.add(
                ScanFinding(
                    title = "Insecure Protocol (HTTP)",
                    description = "Connection is unencrypted. Login credentials can be intercepted.",
                    passed = false
                )
            )
        } else {
            findings.add(
                ScanFinding(
                    title = "Encrypted Transport (HTTPS)",
                    description = "Traffic is secured with modern TLS/SSL encryption.",
                    passed = true
                )
            )
        }

        val domainPart = host
        val isIp = Regex("""^(\d{1,3}\.){3}\d{1,3}$""").matches(domainPart)
        if (isIp) {
            score -= 35
            findings.add(
                ScanFinding(
                    title = "Direct IP Hostname Detected",
                    description = "Legitimate sites rarely use raw IP addresses in consumer URLs.",
                    passed = false
                )
            )
        } else {
            findings.add(
                ScanFinding(
                    title = "Standard Domain Name",
                    description = "Uses a registered host rather than an obscure bare IP.",
                    passed = true
                )
            )
        }

        val suspiciousTlds = listOf(".top", ".xyz", ".pw", ".tk", ".cc", ".buzz", ".work", ".click")
        val hasSuspiciousTld = suspiciousTlds.any { domainPart.endsWith(it) }
        if (hasSuspiciousTld) {
            score -= 25
            findings.add(
                ScanFinding(
                    title = "High-Risk Top Level Domain (TLD)",
                    description = "The extension has a historically elevated abuse rate.",
                    passed = false
                )
            )
        } else {
            findings.add(
                ScanFinding(
                    title = "Reputable Domain Suffix",
                    description = "TLD is not listed on active spam/phishing warning registries.",
                    passed = true
                )
            )
        }

        val phishKeywords = listOf("paypal", "apple-verify", "secure-bank", "login-support", "wallet-connect", "netflix-billing")
        val containsPhish = phishKeywords.any { urlStr.lowercase().contains(it) }
        val trusted = listOf("paypal.com", "apple.com", "google.com", "netflix.com", "microsoft.com", "amazon.com")
        val isLegit = trusted.any { domainPart == it || domainPart.endsWith(".$it") }
        val brandHost = listOf("paypal", "apple", "google", "netflix", "microsoft", "amazon")
            .any { token -> domainPart.split('.').any { it.contains(token) } }
        if ((containsPhish || brandHost) && !isLegit) {
            score -= 40
            findings.add(
                ScanFinding(
                    title = "Brand Impersonation Keywords",
                    description = "Contains sensitive financial or brand terms on an unaffiliated domain.",
                    passed = false
                )
            )
        } else {
            findings.add(
                ScanFinding(
                    title = "No Suspicious Brand Spoofing",
                    description = "Domain and path do not mimic known financial institutions or tech platforms.",
                    passed = true
                )
            )
        }

        score = score.coerceIn(5, 100)

        val riskLevel = when {
            score < 45 -> "Critical Danger"
            score < 75 -> "Medium Risk"
            score < 90 -> "Low Risk"
            else -> "Safe"
        }

        val recommendation = when {
            score < 45 -> "DO NOT OPEN! This link exhibits strong characteristics of phishing, malware, or brand spoofing."
            score < 75 -> "Proceed with caution. Check sender authenticity and never enter sensitive passwords."
            score < 90 -> "Appears mostly standard, but stay alert for unexpected redirects."
            else -> "This link appears clean based on security heuristics. Always verify before entering credentials."
        }

        // This legacy heuristic-only path must not fabricate results for external providers.
        // Live provider results are populated exclusively by ExternalSecurityService.
        val providers = emptyList<ThreatProviderInfo>()

        return ScanResult(
            target = rawUrl,
            type = "url",
            score = score,
            riskLevel = riskLevel,
            overallVerdict = if (score >= 80) "Clean" else if (score >= 50) "Suspicious" else "Malicious",
            domain = domainPart,
            findings = findings,
            recommendation = recommendation,
            analyzedAt = "Just now",
            isPartialAssessment = false,
            providers = providers
        )
    }

    fun calculateDynamicAssessment(
        audit: SecurityAuditState,
        lang: Language
    ): DynamicSecurityAssessment {
        val isAr = lang == Language.AR
        val pillars = mutableListOf<ScorePillarContribution>()

        // 1. Password Strength (25 max pts)
        if (audit.passwordStrength != null) {
            val strength = audit.passwordStrength
            val earned = ((strength / 100f) * 25).toInt()
            val deductions = mutableListOf<String>()
            if (strength < 60) {
                deductions.add(if (isAr) "انخفاض طول كلمة المرور (< 12 حرف)" else "Low entropy / length < 12 characters")
            } else if (strength < 85) {
                deductions.add(if (isAr) "تفتقر لرموز خاصة أو طول < 16 حرف" else "Missing special characters or length < 16")
            }
            pillars.add(
                ScorePillarContribution(
                    id = "password",
                    title = if (isAr) "تعقيد وقوة كلمة المرور" else "Password Complexity & Entropy",
                    status = "assessed",
                    score = earned,
                    maxScore = 25,
                    deductions = deductions,
                    summary = if (isAr) "$earned / 25 نقطة (${audit.passwordEntropy ?: 0} بت إنتروبيا)" else "$earned / 25 pts (${audit.passwordEntropy ?: 0} bits entropy)"
                )
            )
        } else {
            pillars.add(
                ScorePillarContribution(
                    id = "password",
                    title = if (isAr) "تعقيد وقوة كلمة المرور" else "Password Complexity & Entropy",
                    status = "pending",
                    score = 0,
                    maxScore = 25,
                    deductions = emptyList(),
                    summary = if (isAr) "معلق: اختبر كلمة مرور في فاحص كلمات المرور" else "Pending: Evaluate a password in Password Check"
                )
            )
        }

        // 2. Credential Breach (20 max pts)
        if (audit.passwordBreachStatus != "untested") {
            val isBreached = audit.passwordBreachStatus == "breached"
            val deductions = if (isBreached) {
                listOf(if (isAr) "كلمة المرور مسربة في قواعد بيانات عامة (${audit.passwordBreachCount} مرة)!" else "Password exposed in public leaks (${audit.passwordBreachCount} times)!")
            } else emptyList()

            pillars.add(
                ScorePillarContribution(
                    id = "breach",
                    title = if (isAr) "التحقق من التسريبات (HIBP)" else "Known Breach Status (HIBP)",
                    status = "assessed",
                    score = if (isBreached) 0 else 20,
                    maxScore = 20,
                    deductions = deductions,
                    summary = if (isBreached) {
                        if (isAr) "مخترقة: يلزم الاستبدال الفوري (0 / 20)" else "Compromised: Urgent replacement required (0 / 20 pts)"
                    } else {
                        if (isAr) "نظيفة: لم يُعثر عليها في التسريبات (+20)" else "Clean: Not found in 800M+ breaches (+20 pts)"
                    }
                )
            )
        } else {
            pillars.add(
                ScorePillarContribution(
                    id = "breach",
                    title = if (isAr) "التحقق من التسريبات (HIBP)" else "Known Breach Status (HIBP)",
                    status = "pending",
                    score = 0,
                    maxScore = 20,
                    deductions = emptyList(),
                    summary = if (isAr) "معلق: افحص التسريبات عبر HIBP" else "Pending: Check for leaks via HIBP"
                )
            )
        }

        // 3. Web Link Threat Inspection (25 max pts)
        if (audit.urlScansCount > 0) {
            val earned = ((audit.urlAverageScore / 100f) * 25).toInt()
            val deductions = if (audit.urlThreatsCount > 0) {
                listOf(if (isAr) "تم رصد ${audit.urlThreatsCount} روابط مشبوهة أو غير آمنة" else "${audit.urlThreatsCount} suspicious links inspected")
            } else emptyList()

            pillars.add(
                ScorePillarContribution(
                    id = "url",
                    title = if (isAr) "فحص الروابط المشبوهة" else "Web Link Threat Inspection",
                    status = "assessed",
                    score = earned,
                    maxScore = 25,
                    deductions = deductions,
                    summary = if (isAr) "$earned / 25 نقطة (${audit.urlScansCount} فحص روابط)" else "$earned / 25 pts (${audit.urlScansCount} URL scans)"
                )
            )
        } else {
            pillars.add(
                ScorePillarContribution(
                    id = "url",
                    title = if (isAr) "فحص الروابط المشبوهة" else "Web Link Threat Inspection",
                    status = "pending",
                    score = 0,
                    maxScore = 25,
                    deductions = emptyList(),
                    summary = if (isAr) "معلق: افحص رابطاً في فاحص الروابط" else "Pending: Scan a URL in Link Scanner"
                )
            )
        }

        // 4. Wi-Fi Perimeter Hardening (20 max pts)
        if (audit.wifiScore > 0 || audit.wifiPassed > 0) {
            val earnedWifi = ((audit.wifiScore / 100f) * 20).toInt()
            val missingWifi = (audit.wifiTotal - audit.wifiPassed).coerceAtLeast(0)
            val deductionsWifi = if (missingWifi > 0) {
                listOf(if (isAr) "تفتقر الشبكة لـ $missingWifi ضوابط أمنية موصى بها" else "$missingWifi recommended network controls missing")
            } else emptyList()

            pillars.add(
                ScorePillarContribution(
                    id = "wifi",
                    title = if (isAr) "تحصين شبكة الواي فاي" else "Wi-Fi Perimeter Hardening",
                    status = "assessed",
                    score = earnedWifi,
                    maxScore = 20,
                    deductions = deductionsWifi,
                    summary = if (isAr) "$earnedWifi / 20 نقطة (${audit.wifiPassed}/${audit.wifiTotal} ضوابط مكتملة)" else "$earnedWifi / 20 pts (${audit.wifiPassed}/${audit.wifiTotal} controls passed)"
                )
            )
        } else {
            pillars.add(
                ScorePillarContribution(
                    id = "wifi",
                    title = if (isAr) "تحصين شبكة الواي فاي" else "Wi-Fi Perimeter Hardening",
                    status = "pending",
                    score = 0,
                    maxScore = 20,
                    deductions = emptyList(),
                    summary = if (isAr) "معلق: راجع ضوابط الشبكة في دليل الواي فاي" else "Pending: Review controls in Wi-Fi Guide"
                )
            )
        }

        // 5. QR Code Vector (10 max pts)
        if (audit.qrScansCount > 0) {
            val earnedQr = if (audit.qrThreatsCount > 0) 7 else 10
            pillars.add(
                ScorePillarContribution(
                    id = "qr",
                    title = if (isAr) "حماية مسح QR والتصيد الفيزيائي" else "QR Code & Quishing Protection",
                    status = "assessed",
                    score = earnedQr,
                    maxScore = 10,
                    deductions = if (audit.qrThreatsCount > 0) listOf(if (isAr) "تم اعتراض رمز QR مشبوه" else "Suspicious QR code intercepted") else emptyList(),
                    summary = if (isAr) "$earnedQr / 10 نقاط (${audit.qrScansCount} عمليات مسح)" else "$earnedQr / 10 pts (${audit.qrScansCount} QR scans)"
                )
            )
        } else {
            pillars.add(
                ScorePillarContribution(
                    id = "qr",
                    title = if (isAr) "حماية مسح QR والتصيد الفيزيائي" else "QR Code & Quishing Protection",
                    status = "pending",
                    score = 0,
                    maxScore = 10,
                    deductions = emptyList(),
                    summary = if (isAr) "معلق: افحص رمز QR في فاحص الرموز" else "Pending: Scan a QR in QR Scanner"
                )
            )
        }

        val assessedPillars = pillars.filter { it.status == "assessed" }
        val assessedCount = assessedPillars.size
        val totalPoints = assessedPillars.sumOf { it.score }

        val compositeScore = if (assessedCount == 0) 0 else totalPoints.coerceIn(0, 100)
        val (ratingText, ratingColor) = when {
            assessedCount == 0 -> (if (isAr) "غير مقيم" else "Not Assessed") to CyberMuted
            compositeScore >= 90 -> (if (isAr) "ممتاز" else "Excellent") to CyberGreen
            compositeScore >= 75 -> (if (isAr) "جيد" else "Good") to CyberBlue
            compositeScore >= 55 -> (if (isAr) "متوسط" else "Moderate") to CyberYellow
            compositeScore >= 35 -> (if (isAr) "ضعيف" else "Weak") to Color(0xFFFF8C42)
            else -> (if (isAr) "حرج" else "Critical") to CyberRed
        }

        val totalScanned = audit.urlScansCount + audit.qrScansCount + (if (audit.passwordStrength != null) 1 else 0) + audit.wifiPassed
        val scansDone = audit.urlScansCount + audit.qrScansCount + (if (audit.passwordStrength != null) 1 else 0) + (if (audit.wifiPassed > 0) 1 else 0)
        val threatsBlocked = audit.urlThreatsCount + audit.qrThreatsCount + (if (audit.passwordBreachStatus == "breached") 1 else 0)

        return DynamicSecurityAssessment(
            compositeScore = compositeScore,
            status = if (assessedCount == pillars.size) "complete" else if (assessedCount > 0) "partial" else "not_started",
            assessedPillarsCount = assessedCount,
            totalPillarsCount = pillars.size,
            ratingText = ratingText,
            ratingColor = ratingColor,
            pillars = pillars,
            threatsBlockedCount = threatsBlocked,
            scansDoneCount = scansDone,
            scannedContentCount = totalScanned,
            earnedPoints = totalPoints,
            maxAssessedPoints = assessedPillars.sumOf { it.maxScore }
        )
    }
}
