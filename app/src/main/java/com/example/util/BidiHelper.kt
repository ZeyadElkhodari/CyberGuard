package com.example.util

/**
 * Utility for Unicode Bidirectional (BiDi) text isolation and formatting.
 *
 * Solves mixed Arabic-English rendering challenges:
 * 1. Preserves natural Arabic Right-to-Left (RTL) reading order for host sentences.
 * 2. Isolates embedded English technical terms (e.g. Malware, Phishing, Firewall,
 *    SQL Injection, Two-Factor Authentication, Wi-Fi) with Left-to-Right Isolate (\u2066 ... \u2069).
 * 3. Prevents inverted/misplaced punctuation marks (periods, commas, colons, brackets, percent signs).
 * 4. Ensures numbers, version strings (e.g. v1.0.0), and score ratios (e.g. 460/500) don't flip.
 */
object BidiHelper {
    const val LRI = "\u2066" // Left-to-Right Isolate
    const val RLI = "\u2067" // Right-to-Left Isolate
    const val FSI = "\u2068" // First Strong Isolate
    const val PDI = "\u2069" // Pop Directional Isolate
    const val LRM = "\u200E" // Left-to-Right Mark
    const val RLM = "\u200F" // Right-to-Left Mark

    /**
     * Encloses an English or LTR phrase within an isolate block (\u2066 ... \u2069).
     */
    fun isolateLtr(phrase: String): String {
        if (phrase.isEmpty()) return phrase
        if (phrase.startsWith(LRI) && phrase.endsWith(PDI)) return phrase
        return "$LRI$phrase$PDI"
    }

    /**
     * Encloses an RTL phrase within an isolate block (\u2067 ... \u2069).
     */
    fun isolateRtl(phrase: String): String {
        if (phrase.isEmpty()) return phrase
        if (phrase.startsWith(RLI) && phrase.endsWith(PDI)) return phrase
        return "$RLI$phrase$PDI"
    }

    /**
     * Common cybersecurity technical terms that appear in Arabic copy.
     * Ordered by length descending so multi-word terms (e.g. Two-Factor Authentication)
     * match before single words (e.g. Authentication).
     */
    val CYBER_TERMS = listOf(
        "Two-Factor Authentication",
        "Google Safe Browsing",
        "Have I Been Pwned",
        "Learning Profile",
        "Cloudflare Worker",
        "Security Awareness",
        "Password Security",
        "Security Labs",
        "Link Scanner",
        "Mobile Data",
        "SQL Injection",
        "k-Anonymity",
        "CyberGuard",
        "VirusTotal",
        "URLhaus",
        "unlisted",
        "Cloudflare",
        "Phishing",
        "Malware",
        "Firewall",
        "Quishing",
        "Wi-Fi",
        "XSS",
        "SSRF",
        "SMS",
        "URL",
        "DNS",
        "HTTP",
        "HTTPS",
        "SHA-1",
        "Android",
        "Email",
        "Room",
        "SSID",
        "GPS",
        "API",
        "SSL",
        "TLS",
        "2FA",
        "CIA",
        "XP",
        "QR",
        "v1.0.0"
    ).sortedByDescending { it.length }

    private val PAREN_LATIN_REGEX = Regex("""\(([a-zA-Z0-9\s\-_/.:+]+)\)""")
    private val BRACKET_LATIN_REGEX = Regex("""\[([a-zA-Z0-9\s\-_/.:+]+)\]""")
    private val RATIO_REGEX = Regex("""\b\d+/\d+\b""")
    private val NUMBER_REGEX = Regex("""(?<![A-Za-z0-9٠-٩۰-۹])[0-9٠-٩۰-۹]+(?:[.,٫][0-9٠-٩۰-۹]+)*(?:%|٪)?(?![A-Za-z0-9٠-٩۰-۹])""")
    private val ISOLATED_SEGMENT_REGEX = Regex("""[\u2066\u2067\u2068].*?\u2069""")

    private fun replaceOutsideIsolates(
        input: String,
        pattern: Regex,
        transform: (MatchResult) -> String
    ): String = buildString {
        var cursor = 0
        ISOLATED_SEGMENT_REGEX.findAll(input).forEach { isolated ->
            if (isolated.range.first > cursor) {
                append(pattern.replace(input.substring(cursor, isolated.range.first), transform))
            }
            append(isolated.value)
            cursor = isolated.range.last + 1
        }
        if (cursor < input.length) {
            append(pattern.replace(input.substring(cursor), transform))
        }
    }

    /**
     * Formats mixed Arabic/English text:
     * - Preserves Arabic RTL flow
     * - Isolates embedded English technical terms and phrases in LTR
     * - Prevents punctuation (periods, colons, brackets) from flipping
     */
    fun formatBiDi(text: String, isAr: Boolean = true): String {
        if (!isAr || text.isEmpty()) return text

        var result = text

        // 1. Isolate parenthesized Latin phrases like (Link Scanner), (Phishing), (Password Security)
        result = replaceOutsideIsolates(result, PAREN_LATIN_REGEX) { match ->
            val inner = match.groupValues[1]
            "$LRI($inner)$PDI"
        }

        // 2. Isolate bracketed Latin phrases like [Safe], [Warning]
        result = replaceOutsideIsolates(result, BRACKET_LATIN_REGEX) { match ->
            val inner = match.groupValues[1]
            "$LRI[$inner]$PDI"
        }

        // 3. Isolate ratio patterns like 460/500 without nesting existing isolates.
        result = replaceOutsideIsolates(result, RATIO_REGEX) { match ->
            isolateLtr(match.value)
        }

        // 4. Isolate standalone numbers, versions and percentages so punctuation stays attached.
        result = replaceOutsideIsolates(result, NUMBER_REGEX) { match ->
            isolateLtr(match.value)
        }

        // 5. Isolate known cyber terms only outside existing isolates. Word boundaries
        // prevent short tokens such as QR, XP and DNS from matching inside other words.
        for (term in CYBER_TERMS) {
            val escaped = Regex.escape(term)
            val regex = Regex("""(?<![A-Za-z0-9])($escaped)(?![A-Za-z0-9])""")
            result = replaceOutsideIsolates(result, regex) { match ->
                isolateLtr(match.value)
            }
        }

        // 6. If the Arabic sentence ends with a punctuation mark, anchor it with RLM
        // to prevent it from flipping next to an adjacent LTR phrase.
        if (result.endsWith(".") || result.endsWith("!") || result.endsWith(":") || result.endsWith("؟")) {
            result = "$result$RLM"
        }

        return result
    }
}
