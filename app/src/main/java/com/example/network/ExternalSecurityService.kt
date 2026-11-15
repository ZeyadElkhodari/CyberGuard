package com.example.network

import com.example.model.ScanFinding
import com.example.model.ScanResult
import com.example.model.ThreatProviderInfo
import com.example.model.PasswordBreachResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException
import okhttp3.Request
import org.json.JSONObject
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import java.io.IOException
import java.net.URI
import java.net.IDN
import java.net.InetAddress
import java.util.Locale
import java.util.concurrent.TimeUnit

object ExternalSecurityService {

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(35, TimeUnit.SECONDS)
        .writeTimeout(12, TimeUnit.SECONDS)
        .callTimeout(38, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private fun nowIso(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        .apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())

    /**
     * Checks whether a password has been compromised using HaveIBeenPwned's
     * zero-knowledge k-Anonymity API.
     * The plaintext password NEVER leaves this device; only the first 5 characters of
     * the SHA-1 hash are queried.
     */
    suspend fun checkPasswordBreach(password: String): PasswordBreachResult = withContext(Dispatchers.IO) {
        if (password.isEmpty()) {
            return@withContext PasswordBreachResult(
                checked = false,
                breached = false,
                count = 0,
                prefixSent = "",
                checkedAt = nowIso(),
                error = "Empty password provided"
            )
        }

        val sha1 = try {
            val md = MessageDigest.getInstance("SHA-1")
            val bytes = md.digest(password.toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { "%02X".format(it) }
        } catch (e: Exception) {
            return@withContext PasswordBreachResult(
                checked = false,
                breached = false,
                count = 0,
                prefixSent = "",
                checkedAt = nowIso(),
                error = "SHA-1 calculation failed: ${e.message}"
            )
        }

        val prefix = sha1.take(5)
        val suffix = sha1.substring(5)

        val request = Request.Builder()
            .url("https://api.pwnedpasswords.com/range/$prefix")
            .header("User-Agent", "CyberGuard-Security-App")
            .header("Accept", "text/plain")
            .get()
            .build()

        try {
            executeCancellable(httpClient.newCall(request)).use { response ->
                if (!response.isSuccessful) {
                    return@withContext PasswordBreachResult(
                        checked = false,
                        breached = false,
                        count = 0,
                        prefixSent = prefix,
                        checkedAt = nowIso(),
                        error = "HIBP service returned HTTP ${response.code}. Status inconclusive."
                    )
                }

                val bodyText = response.body?.string() ?: ""
                var count = 0
                var breached = false

                val lines = bodyText.split("\r\n", "\n")
                for (line in lines) {
                    val parts = line.split(":")
                    if (parts.size >= 2) {
                        if (parts[0].trim().equals(suffix, ignoreCase = true)) {
                            breached = true
                            count = parts[1].trim().toIntOrNull() ?: 1
                            break
                        }
                    }
                }

                PasswordBreachResult(
                    checked = true,
                    breached = breached,
                    count = count,
                    prefixSent = prefix,
                    checkedAt = nowIso(),
                    error = null
                )
            }
        } catch (e: Exception) {
            PasswordBreachResult(
                checked = false,
                breached = false,
                count = 0,
                prefixSent = prefix,
                checkedAt = nowIso(),
                error = "HIBP service unreachable (${e.localizedMessage ?: "Offline"}). Inconclusive."
            )
        }
    }

    /**
     * Checks if hostname points to private, loopback, or cloud metadata IP ranges (SSRF defense).
     */
    fun isPrivateOrLocalAddress(hostname: String): Boolean {
        val host = hostname.trim().lowercase().removePrefix("[").removeSuffix("]")
        if (host.isEmpty()) return false
        if (host == "localhost" || host.endsWith(".localhost") || host.endsWith(".local") ||
            host == "metadata.google.internal" || host == "metadata.google" ||
            host == "0.0.0.0" || host == "::" || host == "::1" ||
            (host.contains(':') && (host.startsWith("fc") || host.startsWith("fd") || host.startsWith("2001:db8:")))) return true
        val ipv4 = parseIpv4(host)
        if (ipv4 != null) {
            val (a, b, c, d) = ipv4
            return a == 0 || a == 10 || a == 127 || a >= 224 ||
                (a == 169 && b == 254) || (a == 172 && b in 16..31) ||
                (a == 192 && b == 168) || (a == 100 && b in 64..127) ||
                (a == 192 && b == 0 && c == 0) || (a == 198 && b in 18..19) ||
                (a == 255 && b == 255 && c == 255 && d == 255)
        }
        if (host.contains(':')) return try {
            val address = InetAddress.getByName(host)
            address.isAnyLocalAddress || address.isLoopbackAddress ||
                address.isLinkLocalAddress || address.isSiteLocalAddress || address.isMulticastAddress
        } catch (_: Exception) { false }
        return false
    }

    private fun parseIpv4(host: String): List<Int>? {
        val parts = host.split('.')
        if (parts.size != 4) return null
        return parts.map { part ->
            if (part.isEmpty() || part.length > 3 || part.any { !it.isDigit() } ||
                part.length > 1 && part.startsWith('0')) return null
            part.toIntOrNull()?.takeIf { it in 0..255 } ?: return null
        }
    }

    private fun parseHttpUrl(raw: String): URI? {
        val trimmed = raw.trim()
        if (trimmed.isEmpty() || trimmed.any { it.isWhitespace() || it.isISOControl() }) return null
        val candidate = if (trimmed.matches(Regex("(?i)^https?://.*"))) trimmed else "https://$trimmed"
        return try {
            val uri = URI(candidate).normalize()
            val scheme = uri.scheme?.lowercase(Locale.ROOT)
            if (scheme !in setOf("http", "https")) return null
            val authority = uri.rawAuthority ?: return null
            if (authority.contains('@')) return null

            val rawHost: String
            val port: Int
            if (authority.startsWith("[")) {
                val closing = authority.indexOf(']')
                if (closing <= 1) return null
                rawHost = authority.substring(1, closing)
                val suffix = authority.substring(closing + 1)
                if (suffix.isNotEmpty() && (!suffix.startsWith(":") || suffix == ":")) return null
                port = if (suffix.length > 1) suffix.drop(1).toIntOrNull() ?: return null else -1
                if (!rawHost.contains(':')) return null
            } else {
                val colon = authority.lastIndexOf(':')
                val hasPort = colon > 0 && authority.substring(colon + 1).all { it.isDigit() }
                rawHost = if (hasPort) authority.substring(0, colon) else authority
                port = if (hasPort) authority.substring(colon + 1).toIntOrNull() ?: return null else -1
                if (rawHost.contains(':')) return null
            }
            if (rawHost.isBlank() || port != -1 && port !in 1..65535) return null
            val normalizedHost = if (rawHost.contains(':')) rawHost.lowercase(Locale.ROOT)
                else IDN.toASCII(rawHost, IDN.USE_STD3_ASCII_RULES).lowercase(Locale.ROOT)
            if (normalizedHost.isBlank() || normalizedHost.startsWith(".") ||
                normalizedHost.endsWith(".") || normalizedHost.contains("..")) return null
            if ((normalizedHost.all { it.isDigit() || it == '.' } && parseIpv4(normalizedHost) == null) ||
                normalizedHost.matches(Regex("(?i)^(0x[0-9a-f]+|[0-9]+)$"))) return null

            val authorityHost = if (normalizedHost.contains(':')) "[$normalizedHost]" else normalizedHost
            val portPart = if (port >= 0) ":$port" else ""
            val suffix = buildString {
                append(uri.rawPath.orEmpty())
                uri.rawQuery?.let { append('?').append(it) }
                uri.rawFragment?.let { append('#').append(it) }
            }
            URI("$scheme://$authorityHost$portPart$suffix")
        } catch (_: Exception) { null }
    }

    internal fun isBrandSpoofingHost(hostname: String): Boolean {
        val host = hostname.trim().lowercase(Locale.ROOT).removeSuffix(".")
        val trustedDomains = listOf("paypal.com", "google.com", "apple.com", "netflix.com", "microsoft.com", "amazon.com", "facebook.com")
        if (trustedDomains.any { host == it || host.endsWith(".$it") }) return false
        val brands = listOf("paypal", "google", "apple", "netflix", "microsoft", "amazon", "facebook")
        return brands.any { brand -> host.split('.').any { it.contains(brand) } }
    }

    private fun hasBrandSpoofing(host: String, fullUrl: String): Boolean {
        val trustedDomains = listOf("paypal.com", "google.com", "apple.com", "netflix.com", "microsoft.com", "amazon.com", "facebook.com")
        if (trustedDomains.any { host == it || host.endsWith(".$it") }) return false
        val pathAndQuery = fullUrl.substringAfter(host, "")
        val brandTokens = listOf("paypal", "google", "apple", "netflix", "microsoft", "amazon", "facebook")
        val phishingTokens = listOf("secure-bank", "login-support", "wallet-connect", "chase-security", "binance-verify")
        return isBrandSpoofingHost(host) ||
            brandTokens.any { pathAndQuery.contains(it, ignoreCase = true) } ||
            phishingTokens.any { fullUrl.contains(it, ignoreCase = true) }
    }

    internal fun isValidHttpUrl(raw: String): Boolean = parseHttpUrl(raw) != null

    private suspend fun executeCancellable(call: Call): Response = suspendCancellableCoroutine<Response> { continuation ->
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (continuation.isActive) continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (continuation.isActive) {
                    continuation.resumeWith(Result.success(response))
                } else {
                    response.close()
                }
            }
        })
        continuation.invokeOnCancellation { call.cancel() }
    }

    private suspend fun checkCyberGuardWorker(targetUrl: String): List<ThreatProviderInfo> {
        val body = JSONObject().put("url", targetUrl).toString()
            .toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url("https://cyberguard-api.zeyadelkhodari.workers.dev/api/scan/url")
            .header("Accept", "application/json")
            .post(body)
            .build()

        fun backendFailure(status: String, verdict: String, details: String, tag: String) =
            listOf(ThreatProviderInfo(
                name = "CyberGuard Cloudflare API",
                status = status,
                verdict = verdict,
                details = details,
                tags = listOf(tag)
            ))

        var lastNetworkError: IOException? = null
        // One bounded retry is reserved for transient connection/edge failures.
        // DNS and TLS configuration failures are returned immediately instead of waiting through
        // repeated retries that cannot repair the active Wi-Fi/network configuration.
        for (attempt in 0..1) {
            try {
                executeCancellable(httpClient.newCall(request)).use { response ->
                    if (!response.isSuccessful) {
                        if (response.code in listOf(502, 504) && attempt < 1) {
                            delay(250L)
                            return@use
                        }
                        val status = if (response.code == 429) "rate_limited" else "unavailable"
                        val message = when (response.code) {
                            429 -> "The Cloudflare Worker rate limit was reached. Try again later."
                            503 -> "Cloudflare returned HTTP 503. Check the Worker RATE_LIMITER binding and provider configuration."
                            in 500..599 -> "Cloudflare edge returned HTTP ${response.code}; threat-intelligence results are inconclusive."
                            else -> "Cloudflare Worker rejected the request with HTTP ${response.code}."
                        }
                        return backendFailure(status, "Service unavailable", message, "HTTP ${response.code}")
                    }

                    val raw = response.body?.string().orEmpty()
                    val root = try { JSONObject(raw) } catch (_: Exception) {
                        return backendFailure("unavailable", "Invalid backend response",
                            "Cloudflare returned a successful HTTP status but the response was not valid JSON.", "Invalid JSON")
                    }
                    val array = root.optJSONArray("providers")
                        ?: return backendFailure("unavailable", "Incomplete backend response",
                            "The Worker response did not contain the providers array.", "Missing providers")
                    if (array.length() == 0) {
                        return backendFailure("unavailable", "No provider results",
                            "The Worker returned no provider results. No clean verdict was assigned.", "Empty providers")
                    }
                    return buildList {
                        for (i in 0 until array.length()) {
                            val item = array.optJSONObject(i) ?: continue
                            val tagsJson = item.optJSONArray("tags")
                            val tags = buildList {
                                if (tagsJson != null) for (j in 0 until tagsJson.length()) add(tagsJson.optString(j))
                            }
                            add(ThreatProviderInfo(
                                name = item.optString("name", "Threat provider"),
                                status = item.optString("status", "unavailable"),
                                verdict = item.optString("verdict", "Inconclusive"),
                                details = item.optString("details", "No provider details returned."),
                                tags = tags
                            ))
                        }
                    }
                }
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: IOException) {
                lastNetworkError = e
                val retryable = e is java.net.SocketException
                if (attempt < 1 && retryable) {
                    delay(250L)
                } else {
                    break
                }
            } catch (e: Exception) {
                return backendFailure("unavailable", "Backend response error",
                    "The Worker response could not be processed (${e.javaClass.simpleName}).", "Response parsing")
            }
        }
        val error = lastNetworkError
        val details = when (error) {
            is java.net.UnknownHostException -> "DNS lookup failed for the CyberGuard Worker. Check the active network or DNS resolver."
            is javax.net.ssl.SSLException -> "A secure TLS connection to the CyberGuard Worker could not be established."
            is java.net.SocketTimeoutException -> "The connection to the CyberGuard Worker timed out after retries."
            else -> "The CyberGuard Worker could not be reached after retries (${error?.javaClass?.simpleName ?: "network error"})."
        }
        return backendFailure("unavailable", "Backend unreachable", details, error?.javaClass?.simpleName ?: "Network error")
    }

    /**
     * Evaluates URL safety by combining multiple external threat-intelligence providers
     * through the CyberGuard Cloudflare Worker with local zero-trust heuristics (SSRF, bare IP, risky TLD, brand spoofing, TLS).
     * Strictly differentiates between confirmed safe, confirmed malicious, and inconclusive results.
     */
    suspend fun evaluateUrl(rawUrl: String): ScanResult = withContext(Dispatchers.IO) {
        val parsedUri = parseHttpUrl(rawUrl)
        if (parsedUri == null) {
            return@withContext ScanResult(
                target = rawUrl, score = 0, riskLevel = "Invalid URL",
                overallVerdict = "Invalid input - not assessed", domain = "",
                findings = listOf(ScanFinding("Invalid URL", "The URL format or hostname is invalid; no threat verdict was assigned.", false)),
                recommendation = "Enter a valid public HTTP or HTTPS URL.",
                analyzedAt = nowIso(), isPartialAssessment = true, providers = emptyList()
            )
        }
        val urlStr = parsedUri.toASCIIString()
        val domainPart = parsedUri.host.lowercase(Locale.ROOT)
        val isHttps = parsedUri.scheme.equals("https", ignoreCase = true)

        val findings = mutableListOf<ScanFinding>()
        var score = 100

        // 1. SSRF and Private IP Check
        val isPrivate = isPrivateOrLocalAddress(domainPart)
        if (isPrivate) {
            score -= 50
            findings.add(
                ScanFinding(
                    title = "SSRF & Internal Network Guard Flag",
                    description = "URL targets internal loopback (127.0.0.1), private RFC1918 block, or cloud metadata. Access prohibited.",
                    passed = false
                )
            )
        } else {
            findings.add(
                ScanFinding(
                    title = "Public Internet Reachability",
                    description = "Destination points to a standard public domain rather than private infrastructure.",
                    passed = true
                )
            )
        }

        // 2. Transport Security (HTTPS)
        if (!isHttps) {
            score -= 25
            findings.add(
                ScanFinding(
                    title = "Unencrypted Protocol (HTTP)",
                    description = "Connection transmits data in cleartext without modern TLS encryption.",
                    passed = false
                )
            )
        } else {
            findings.add(
                ScanFinding(
                    title = "TLS / SSL Transport Security (HTTPS)",
                    description = "Connection uses encrypted transport layer security.",
                    passed = true
                )
            )
        }

        // 3. Raw IP Hostname Check
        val isBareIp = parseIpv4(domainPart) != null || domainPart.contains(':')
        if (isBareIp && !isPrivate) {
            score -= 30
            findings.add(
                ScanFinding(
                    title = "Direct Numerical IP Hostname",
                    description = "Bare IP addresses in links frequently conceal phishing or command-and-control servers.",
                    passed = false
                )
            )
        } else if (!isBareIp) {
            findings.add(
                ScanFinding(
                    title = "Registered FQDN Domain",
                    description = "Domain utilizes registered DNS name hierarchy.",
                    passed = true
                )
            )
        }

        // 4. Risky Top-Level Domains (TLD)
        val suspiciousTlds = listOf(".top", ".xyz", ".pw", ".tk", ".cc", ".buzz", ".work", ".click", ".gq", ".cf", ".ga")
        val hasSuspiciousTld = suspiciousTlds.any { domainPart.endsWith(it) }
        if (hasSuspiciousTld) {
            score -= 20
            findings.add(
                ScanFinding(
                    title = "Elevated Abuse Rate TLD",
                    description = "The extension used has an elevated spam and abuse registry history.",
                    passed = false
                )
            )
        } else {
            findings.add(
                ScanFinding(
                    title = "Standard Top-Level Domain Registry",
                    description = "Domain extension is not listed under high-abuse registry watchlists.",
                    passed = true
                )
            )
        }

        // 5. Phishing and Brand Impersonation Keywords
        val isSpoofedBrand = hasBrandSpoofing(domainPart, urlStr)
        if (isSpoofedBrand) {
            score -= 40
            findings.add(ScanFinding(
                title = "Brand Impersonation & Typosquatting Signal",
                description = "The hostname contains a protected brand name but is not that brand's domain or a legitimate subdomain.",
                passed = false
            ))
        } else {
            findings.add(ScanFinding(
                title = "No Suspicious Brand Spoofing",
                description = "The hostname does not impersonate a recognized brand.",
                passed = true
            ))
        }

        // 6. Multi-provider threat intelligence through the Cloudflare Worker.
        // Provider credentials remain server-side and are never packaged in the APK.
        val providers = (if (isPrivate) emptyList() else checkCyberGuardWorker(urlStr)).toMutableList()
        if (providers.isEmpty() && !isPrivate) {
            providers.add(ThreatProviderInfo(
                name = "CyberGuard Cloudflare API", status = "unavailable",
                verdict = "Backend unavailable",
                details = "Could not reach the CyberGuard API. Live threat intelligence is inconclusive.",
                tags = listOf("Backend Offline")
            ))
        }
        val maliciousProviders = providers.filter { it.status == "malicious" }
        val isExternalUnavailable = providers.any { it.status in listOf("unavailable", "unconfigured", "submitted", "rate_limited", "configuration_error", "not_indexed") }
        val isThreatDetected = maliciousProviders.isNotEmpty()

        if (isThreatDetected) {
            score = minOf(score, 20)
            findings.add(0, ScanFinding(
                title = "Live Threat Intelligence Alert",
                description = "One or more external threat-intelligence providers flagged this URL: ${maliciousProviders.joinToString { it.name }}.",
                passed = false
            ))
        }

        score = score.coerceIn(5, 100)

        // Distinguish confirmed safe vs confirmed malicious vs inconclusive
        val riskLevel: String
        val overallVerdict: String
        val recommendation: String
        val isPartialAssessment: Boolean

        if (isThreatDetected) {
            riskLevel = "Critical Danger"
            overallVerdict = "Malicious Threat Detected"
            recommendation = "DO NOT OPEN! One or more external threat-intelligence providers confirmed a threat."
            isPartialAssessment = isExternalUnavailable
        } else if (score < 45 && !isPrivate) {
            riskLevel = "Critical Danger"
            overallVerdict = "High-risk heuristic signals"
            recommendation = "DO NOT OPEN. Local URL indicators are strongly suspicious, but no external provider confirmed malware."
            isPartialAssessment = isExternalUnavailable
        } else if (isPrivate) {
            riskLevel = "Blocked / Local Address"
            overallVerdict = "Private destination blocked"
            recommendation = "This address is local, loopback, link-local, or reserved. External providers were not queried."
            isPartialAssessment = true
        } else if (score < 75) {
            riskLevel = "Medium Risk"
            overallVerdict = "Suspicious Signals Detected"
            recommendation = "Proceed with extreme caution. Verify sender authenticity and never enter credentials or download files."
            isPartialAssessment = isExternalUnavailable
        } else if (isExternalUnavailable) {
            // Any missing provider means the combined assessment is partial, never a confirmed clean result.
            riskLevel = "Inconclusive / Partial"
            overallVerdict = "Inconclusive (Some Threat Feeds Unavailable)"
            recommendation = "Some threat-intelligence providers were not configured or could not be reached. Available sources and local checks are shown, but this is not proof that the URL is safe. Exercise caution."
            isPartialAssessment = true
        } else if (score < 90) {
            riskLevel = "Low Risk"
            overallVerdict = "Minor Heuristic Warnings"
            recommendation = "URL appears mostly standard, but verify destination before interacting."
            isPartialAssessment = isExternalUnavailable
        } else {
            riskLevel = "Safe"
            overallVerdict = "Clean"
            recommendation = "This link appears clean based on security threat feeds and heuristics. Always verify sender identity."
            isPartialAssessment = isExternalUnavailable
        }

        ScanResult(
            target = rawUrl,
            type = "url",
            score = score,
            riskLevel = riskLevel,
            overallVerdict = overallVerdict,
            domain = domainPart,
            findings = findings,
            recommendation = recommendation,
            analyzedAt = nowIso(),
            isPartialAssessment = isPartialAssessment,
            providers = providers
        )
    }
}
