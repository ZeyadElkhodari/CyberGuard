package com.example.logic

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import java.net.Inet4Address

enum class WifiOverallResult {
    SECURE,
    MODERATE,
    RISKY,
    UNKNOWN,
    DISCONNECTED
}

enum class WifiRiskSeverity {
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW
}

data class WifiDetectedRisk(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val severity: WifiRiskSeverity,
    val explanationEn: String,
    val explanationAr: String,
    val reasonEn: String,
    val reasonAr: String,
    val recommendationEn: String,
    val recommendationAr: String
)

data class WifiSecurityReport(
    val isWifiEnabled: Boolean,
    val isConnected: Boolean,
    val ssid: String?,
    val bssid: String?,
    val localIpv4Address: String?,
    val defaultGatewayAddress: String?,
    val securityType: String,
    val cipherSuite: String?,
    val isProtected: Boolean?,
    val score: Int?,
    val overallResult: WifiOverallResult,
    val connectionStatus: String,
    val signalDbm: Int?,
    val signalQuality: String?,
    val frequencyMhz: Int?,
    val frequencyBand: String?,
    val linkSpeedMbps: Int?,
    val hasInternet: Boolean,
    val isCaptivePortal: Boolean,
    val isMetered: Boolean,
    val risks: List<WifiDetectedRisk>,
    val recommendations: List<String>,
    val permissionGranted: Boolean,
    val locationServicesEnabled: Boolean,
    val apiLimitationNote: String?,
    val scannedAt: Long = System.currentTimeMillis()
)

object WifiSecurityScanner {

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationServicesEnabled(context: Context): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return false
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            lm.isLocationEnabled
        } else {
            @Suppress("DEPRECATION")
            try {
                val mode = Settings.Secure.getInt(
                    context.contentResolver,
                    Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF
                )
                mode != Settings.Secure.LOCATION_MODE_OFF
            } catch (_: Exception) {
                false
            }
        }
    }

    fun scanConnectedWifi(context: Context, isAr: Boolean): WifiSecurityReport {
        val appContext = context.applicationContext
        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val wm = appContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

        val hasLocationPerm = hasLocationPermission(appContext)
        val hasLocServices = isLocationServicesEnabled(appContext)

        // 1. Check Wi-Fi Adapter State
        val isWifiEnabled = wm?.isWifiEnabled ?: false
        if (!isWifiEnabled) {
            return WifiSecurityReport(
                isWifiEnabled = false,
                isConnected = false,
                ssid = null,
                bssid = null,
                localIpv4Address = null,
                defaultGatewayAddress = null,
                securityType = if (isAr) "غير متاح (Wi-Fi معطل)" else "Unavailable (Wi-Fi Disabled)",
                cipherSuite = null,
                isProtected = null,
                score = null,
                overallResult = WifiOverallResult.DISCONNECTED,
                connectionStatus = if (isAr) "محول Wi-Fi مغلق" else "Wi-Fi is turned off",
                signalDbm = null,
                signalQuality = null,
                frequencyMhz = null,
                frequencyBand = null,
                linkSpeedMbps = null,
                hasInternet = false,
                isCaptivePortal = false,
                isMetered = false,
                risks = emptyList(),
                recommendations = listOf(
                    if (isAr) "قم بتفعيل Wi-Fi في إعدادات النظام والاتصال بشبكة لتنفيذ الفحص الأمني."
                    else "Turn on Wi-Fi in Android device settings and connect to a network to scan."
                ),
                permissionGranted = hasLocationPerm,
                locationServicesEnabled = hasLocServices,
                apiLimitationNote = null
            )
        }

        // 2. Check Active Connection
        val activeNetwork = cm?.activeNetwork
        val caps = if (activeNetwork != null) cm.getNetworkCapabilities(activeNetwork) else null
        val isWifiConnected = caps != null && caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)

        if (!isWifiConnected) {
            return WifiSecurityReport(
                isWifiEnabled = true,
                isConnected = false,
                ssid = null,
                bssid = null,
                localIpv4Address = null,
                defaultGatewayAddress = null,
                securityType = if (isAr) "غير متصل" else "Not Connected",
                cipherSuite = null,
                isProtected = null,
                score = null,
                overallResult = WifiOverallResult.DISCONNECTED,
                connectionStatus = if (isAr) "الجهاز غير متصل بأي شبكة Wi-Fi" else "Device not connected to any Wi-Fi network",
                signalDbm = null,
                signalQuality = null,
                frequencyMhz = null,
                frequencyBand = null,
                linkSpeedMbps = null,
                hasInternet = false,
                isCaptivePortal = false,
                isMetered = false,
                risks = emptyList(),
                recommendations = listOf(
                    if (isAr) "اتصل بشبكة Wi-Fi التي ترغب في تدقيقها ثم أعد المحاولة."
                    else "Connect to a Wi-Fi network in device settings, then tap Start Wi-Fi Scan."
                ),
                permissionGranted = hasLocationPerm,
                locationServicesEnabled = hasLocServices,
                apiLimitationNote = null
            )
        }

        // 3. Network Transport Properties
        val linkProperties = if (cm != null && activeNetwork != null) {
            runCatching { cm.getLinkProperties(activeNetwork) }.getOrNull()
        } else null
        val localIpv4Address = linkProperties?.linkAddresses
            ?.firstOrNull { it.address is Inet4Address }
            ?.address?.hostAddress
        val defaultGatewayAddress = linkProperties?.routes
            ?.firstOrNull { it.isDefaultRoute && it.gateway is Inet4Address }
            ?.gateway?.hostAddress

        val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        val isCaptivePortal = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_CAPTIVE_PORTAL)
        val isMetered = !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)

        // Retrieve WifiInfo safely across Android versions
        // Android may redact transportInfo on some releases/device builds. Try both
        // supported access paths and prefer the first result that contains a usable SSID.
        val transportWifiInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            caps.transportInfo as? WifiInfo
        } else null
        @Suppress("DEPRECATION")
        val legacyWifiInfo = try { wm?.connectionInfo } catch (_: SecurityException) { null }
        val wifiInfo = sequenceOf(transportWifiInfo, legacyWifiInfo)
            .filterNotNull()
            .firstOrNull { info ->
                val candidate = runCatching { info.ssid }.getOrNull()?.removeSurrounding("\"")
                !candidate.isNullOrBlank() &&
                    candidate != "<unknown ssid>" &&
                    !candidate.equals("unknown", ignoreCase = true)
            } ?: transportWifiInfo ?: legacyWifiInfo

        // Extract SSID (handling Android OS redaction <unknown ssid>)
        var rawSsid: String? = runCatching { wifiInfo?.ssid }.getOrNull()
        if (rawSsid != null) {
            rawSsid = rawSsid.removeSurrounding("\"")
            if (rawSsid == "<unknown ssid>" || rawSsid.equals("unknown", ignoreCase = true) || rawSsid.isBlank()) {
                rawSsid = null
            }
        }

        val rawBssid: String? = runCatching { wifiInfo?.bssid }.getOrNull()?.takeIf {
            it != "02:00:00:00:00:00" && !it.equals("unknown", ignoreCase = true)
        }

        // 4. Physical Signal Quality (Independent of Security Assessment)
        val rssi = runCatching { wifiInfo?.rssi }.getOrNull()?.takeIf { it in -120..0 }
        val signalQuality = when {
            rssi == null -> null
            rssi >= -50 -> if (isAr) "ممتازة ($rssi dBm)" else "Excellent ($rssi dBm)"
            rssi >= -65 -> if (isAr) "جيدة ($rssi dBm)" else "Good ($rssi dBm)"
            rssi >= -75 -> if (isAr) "متوسطة ($rssi dBm)" else "Fair ($rssi dBm)"
            else -> if (isAr) "ضعيفة ($rssi dBm)" else "Weak ($rssi dBm)"
        }

        // Frequency & Band (2.4 GHz / 5 GHz / 6 GHz Wi-Fi 6E/7)
        val freq = runCatching { wifiInfo?.frequency }.getOrNull()?.takeIf { it > 0 }
        val band = when {
            freq == null -> null
            freq in 2400..2500 -> "2.4 GHz"
            freq in 4900..5900 -> "5 GHz"
            freq in 5925..7125 -> "6 GHz (Wi-Fi 6E/7)"
            else -> "${freq} MHz"
        }

        val linkSpeed = runCatching { wifiInfo?.linkSpeed }.getOrNull()?.takeIf { it > 0 }

        // 5. Security & Encryption Analysis
        var detectedSecurityType: String? = null
        var detectedCipher: String? = null
        var isProtected: Boolean? = null

        // Method A: Android 12+ (API 31+) currentSecurityType API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && wifiInfo != null) {
            val currentSecurityType = runCatching { wifiInfo.currentSecurityType }
                .getOrDefault(WifiInfo.SECURITY_TYPE_UNKNOWN)
            when (currentSecurityType) {
                WifiInfo.SECURITY_TYPE_OPEN -> {
                    detectedSecurityType = "Open (Unencrypted)"
                    detectedCipher = "None (Plaintext)"
                    isProtected = false
                }
                WifiInfo.SECURITY_TYPE_WEP -> {
                    detectedSecurityType = "WEP"
                    detectedCipher = "RC4 (Obsolete)"
                    isProtected = true
                }
                WifiInfo.SECURITY_TYPE_PSK -> {
                    detectedSecurityType = "WPA2-Personal (PSK)"
                    // WifiInfo exposes the authentication family, not the negotiated cipher.
                    detectedCipher = null
                    isProtected = true
                }
                WifiInfo.SECURITY_TYPE_EAP -> {
                    detectedSecurityType = "WPA/WPA2-Enterprise (EAP)"
                    // Do not claim AES/CCMP unless Android exposes a verified cipher value.
                    detectedCipher = null
                    isProtected = true
                }
                WifiInfo.SECURITY_TYPE_SAE -> {
                    detectedSecurityType = "WPA3-Personal (SAE)"
                    detectedCipher = null
                    isProtected = true
                }
                WifiInfo.SECURITY_TYPE_OWE -> {
                    detectedSecurityType = "Enhanced Open (OWE)"
                    detectedCipher = null
                    isProtected = true
                }
                WifiInfo.SECURITY_TYPE_EAP_WPA3_ENTERPRISE_192_BIT,
                WifiInfo.SECURITY_TYPE_EAP_WPA3_ENTERPRISE -> {
                    detectedSecurityType = "WPA3-Enterprise"
                    detectedCipher = null
                    isProtected = true
                }
                WifiInfo.SECURITY_TYPE_WAPI_PSK,
                WifiInfo.SECURITY_TYPE_WAPI_CERT -> {
                    detectedSecurityType = "WAPI"
                    detectedCipher = "SMS4"
                    isProtected = true
                }
                WifiInfo.SECURITY_TYPE_UNKNOWN -> {
                    // Will attempt scanResults fallback
                }
            }
        }

        // Method B: Inspect ScanResults if location permission and services are enabled
        if (hasLocationPerm && hasLocServices && wm != null) {
            try {
                @Suppress("DEPRECATION")
                val scans = wm.scanResults
                // Prioritize exact BSSID match first to prevent misattribution in multi-AP environments
                val matchingByBssid = if (rawBssid != null) {
                    scans.firstOrNull { scan -> scan.BSSID.equals(rawBssid, ignoreCase = true) }
                } else null
                val matching = matchingByBssid ?: if (rawSsid != null) {
                    scans.firstOrNull { scan -> scan.SSID.equals(rawSsid, ignoreCase = true) }
                } else null

                if (matching != null) {
                    val capsUpper = matching.capabilities.uppercase()

                    if (capsUpper.contains("SAE")) {
                        detectedSecurityType = "WPA3-Personal (SAE)"
                        detectedCipher = "SAE / AES-CCMP"
                        isProtected = true
                    } else if (capsUpper.contains("WPA2") && capsUpper.contains("TKIP") && !capsUpper.contains("CCMP")) {
                        detectedSecurityType = "WPA2-PSK (TKIP)"
                        detectedCipher = "TKIP (Vulnerable)"
                        isProtected = true
                    } else if (capsUpper.contains("WPA2") && capsUpper.contains("TKIP") && capsUpper.contains("CCMP")) {
                        detectedSecurityType = "WPA/WPA2 Mixed (TKIP/CCMP)"
                        detectedCipher = "Mixed TKIP/CCMP"
                        isProtected = true
                    } else if (capsUpper.contains("WPA2") && capsUpper.contains("CCMP")) {
                        detectedSecurityType = "WPA2-PSK"
                        detectedCipher = "AES/CCMP"
                        isProtected = true
                    } else if (capsUpper.contains("WPA") && capsUpper.contains("TKIP")) {
                        detectedSecurityType = "WPA-PSK (TKIP)"
                        detectedCipher = "TKIP (Deprecated)"
                        isProtected = true
                    } else if (capsUpper.contains("WEP")) {
                        detectedSecurityType = "WEP"
                        detectedCipher = "RC4 (Obsolete)"
                        isProtected = true
                    } else if (capsUpper.contains("OWE")) {
                        detectedSecurityType = "Enhanced Open (OWE)"
                        detectedCipher = "OWE Diffie-Hellman"
                        isProtected = true
                    } else if (capsUpper.contains("EAP")) {
                        detectedSecurityType = "WPA/WPA2-Enterprise"
                        detectedCipher = "EAP / 802.1X"
                        isProtected = true
                    } else if (capsUpper.contains("ESS") &&
                        !capsUpper.contains("WPA") && !capsUpper.contains("WEP") &&
                        !capsUpper.contains("SAE") && !capsUpper.contains("OWE") &&
                        !capsUpper.contains("EAP")) {
                        // A non-empty ESS capability set with no protection flags is evidence
                        // of an open AP. Empty/unknown capabilities must remain UNKNOWN.
                        detectedSecurityType = "Open (Unencrypted)"
                        detectedCipher = "None (Plaintext)"
                        isProtected = false
                    }
                }
            } catch (_: SecurityException) {
                // Handled gracefully
            } catch (_: Exception) {
                // Other unexpected reflection/system failure handled
            }
        }

        // 6. Comprehensive Risk Assessment & Scoring
        val risks = mutableListOf<WifiDetectedRisk>()
        val recommendations = mutableListOf<String>()
        val overallResult: WifiOverallResult
        val score: Int?

        if (detectedSecurityType == null) {
            overallResult = WifiOverallResult.UNKNOWN
            score = null
            risks.add(
                WifiDetectedRisk(
                    id = "unverified_security",
                    nameEn = "Security Protocol Unverified",
                    nameAr = "تعذر التحقق من بروتوكول التشفير",
                    severity = WifiRiskSeverity.MEDIUM,
                    explanationEn = "Android restricts access to Wi-Fi access point security flags unless Location permission is granted and Location Services are active.",
                    explanationAr = "يقيد نظام أندرويد الوصول لخصائص تشفير نقطة الوصول ما لم يتم منح إذن الموقع وتفعيل خدمات الموقع بالجهاز.",
                    reasonEn = "Neither API 31 currentSecurityType nor access point scan telemetry was accessible.",
                    reasonAr = "تعذر قراءة خصائص التشفير المباشرة من النظام بسبب قيود الصلاحيات أو خصوصية النظام.",
                    recommendationEn = "Grant Location permission and ensure Location Services are toggled on, or verify router security in its admin page.",
                    recommendationAr = "امنح إذن الموقع وتأكد من تفعيل خدمات الموقع، أو راجع إعدادات التشفير في صفحة الراوتر مباشرة."
                )
            )
            recommendations.add(
                if (isAr) "تأكد من استخدام بروتوكول WPA2-AES أو WPA3 في جهاز التوجيه (الراوتر) الخاص بك."
                else "Ensure your wireless router is configured with pure WPA2-AES or WPA3."
            )
        } else {
            val typeUpper = detectedSecurityType.uppercase()
            when {
                // OPEN / UNENCRYPTED
                typeUpper.contains("OPEN") && !typeUpper.contains("ENHANCED") && !typeUpper.contains("OWE") -> {
                    overallResult = WifiOverallResult.RISKY
                    score = 20
                    risks.add(
                        WifiDetectedRisk(
                            id = "open_network",
                            nameEn = "Unencrypted Wireless Medium",
                            nameAr = "شبكة لاسلكية مفتوحة بدون تشفير",
                            severity = WifiRiskSeverity.CRITICAL,
                            explanationEn = "Open Wi-Fi networks transmit all 802.11 wireless frames in cleartext without encryption. Any nearby device can passively sniff network packets.",
                            explanationAr = "تنقل شبكات Wi-Fi المفتوحة كافة حزم البيانات كنص واضح دون أي تشفير راديوي، مما يتيح لأي جهاز قريب التنصت عليها سلبياً.",
                            reasonEn = "Access point reports 802.11 Open Authentication with no WPA/RSN security flags.",
                            reasonAr = "أفادت نقطة الوصول باعتماد مصادقة مفتوحة دون تفعيل تشفير WPA أو WPA2 أو WPA3.",
                            recommendationEn = "Do not enter passwords, credit cards, or confidential data on this network. Use a trustworthy VPN at all times.",
                            recommendationAr = "تجنب إدخال كلمات المرور أو البيانات البنكية أو الحساسة عبر هذه الشبكة، واستخدم دائماً تطبيق VPN مشفر وموثوق."
                        )
                    )
                    recommendations.add(
                        if (isAr) "فعّل نفق VPN مشفر وموثوق لتأمين كافة اتصالاتك على هذه الشبكة المفتوحة."
                        else "Always activate an encrypted VPN tunnel to protect all device traffic on open networks."
                    )
                    recommendations.add(
                        if (isAr) "إذا كنت تدير هذا الراوتر، ضع كلمة مرور قوية باستخدام تشفير WPA2-AES أو WPA3 فوراً."
                        else "If you control this access point, configure WPA2-AES or WPA3 encryption immediately."
                    )
                }

                // WEP (OBSOLETE)
                typeUpper.contains("WEP") -> {
                    overallResult = WifiOverallResult.RISKY
                    score = 15
                    risks.add(
                        WifiDetectedRisk(
                            id = "wep_obsolete",
                            nameEn = "Obsolete WEP Encryption",
                            nameAr = "تشفير WEP منتهي الصلاحية ومكسور",
                            severity = WifiRiskSeverity.CRITICAL,
                            explanationEn = "WEP uses the 1987 RC4 stream cipher with weak 24-bit initialization vectors (IVs). Its secret key can be recovered cryptographically in under 5 minutes.",
                            explanationAr = "يعتمد تشفير WEP على خوارزمية RC4 القديمة مع متجهات تهيئة ضعيفة؛ ويمكن كسر مفتاح الشبكة خلال دقائق معدودة بأدوات شائعة.",
                            reasonEn = "Access point capabilities advertise legacy WEP cipher suite.",
                            reasonAr = "نقطة الوصول تستخدم خوارزمية تشفير WEP المهجورة عالمياً.",
                            recommendationEn = "Disconnect from this network immediately and reconfigure the router to WPA2-PSK (AES) or WPA3.",
                            recommendationAr = "افصل اتصالك بهذه الشبكة فوراً، وقم بترقية إعدادات الراوتر إلى WPA2-AES أو WPA3."
                        )
                    )
                    recommendations.add(
                        if (isAr) "لا تعتمد على WEP لأي اتصال أمني؛ قم بالترقية إلى WPA2 أو WPA3."
                        else "Never rely on WEP for network defense; upgrade to WPA2 or WPA3 immediately."
                    )
                }

                // TKIP (DEPRECATED CIPHER)
                typeUpper.contains("TKIP") -> {
                    overallResult = WifiOverallResult.MODERATE
                    score = 55
                    risks.add(
                        WifiDetectedRisk(
                            id = "tkip_weakness",
                            nameEn = "Deprecated TKIP Cipher",
                            nameAr = "تشفير TKIP قديم وضعيف أمنياً",
                            severity = WifiRiskSeverity.HIGH,
                            explanationEn = "TKIP (Temporal Key Integrity Protocol) was a legacy stopgap for WEP. It suffers from known packet forgery and keystream recovery vulnerabilities (Beck-Tews / chopchop).",
                            explanationAr = "يحتوي بروتوكول TKIP على ثغرات معروفة في تكسير المفاتيح الجزئي وحقن الحزم الاحتيالية، وقد تم حظره في معايير Wi-Fi الحديثة.",
                            reasonEn = "Network capabilities indicate TKIP cipher negotiation enabled.",
                            reasonAr = "أظهر فحص الشبكة تفعيل خوارزمية TKIP القديمة في وضع التوافق.",
                            recommendationEn = "Configure your router wireless settings to pure AES/CCMP cipher mode and disable TKIP.",
                            recommendationAr = "قم بتعديل إعدادات الراوتر اللاسلكية لاعتماد خوارزمية AES / CCMP الصافية وتعطيل TKIP تماماً."
                        )
                    )
                    recommendations.add(
                        if (isAr) "قم بتحديث البرنامج الثابت للراوتر لضمان سد الثغرات."
                        else "Keep router firmware updated with manufacturer security patches."
                    )
                }

                // WPA3 must be evaluated before the generic Enterprise branch.
                typeUpper.contains("WPA3") || typeUpper.contains("SAE") || typeUpper.contains("OWE") -> {
                    overallResult = WifiOverallResult.SECURE
                    var calculatedScore = 100

                    if (isCaptivePortal) {
                        calculatedScore -= 10
                        risks.add(
                            WifiDetectedRisk(
                                id = "captive_portal_intermediary",
                                nameEn = "Captive Portal Authentication",
                                nameAr = "بوابة تسجيل دخول ويب (Captive Portal)",
                                severity = WifiRiskSeverity.MEDIUM,
                                explanationEn = "The network forces web browser authentication before granting full internet access.",
                                explanationAr = "تفرض الشبكة تسجيل الدخول عبر متصفح الويب قبل إتاحة الوصول الكامل للإنترنت.",
                                reasonEn = "Android NetworkCapabilities detected NET_CAPABILITY_CAPTIVE_PORTAL.",
                                reasonAr = "رصدت إمكانيات شبكة أندرويد وجود بوابة تسجيل ويب نشطة.",
                                recommendationEn = "Verify the authenticity of the portal page before entering credentials.",
                                recommendationAr = "تحقق من موثوقية صفحة تسجيل الدخول قبل إدخال أي بيانات."
                            )
                        )
                    }

                    score = calculatedScore
                    recommendations.add(
                        if (isAr) "بروتوكول أمان الشبكة يتبع أحدث المعايير العالمية؛ ومع ذلك لا يعني ذلك خلو الشبكة من جميع المخاطر."
                        else "The detected protocol follows modern security standards; this does not guarantee that the network is risk-free."
                    )
                    recommendations.add(
                        if (isAr) "حافظ على تحديث البرامج الثابتة لجهاز الراوتر بانتظام."
                        else "Keep router firmware updated with official security patches."
                    )
                }

                // WPA2-PERSONAL / WPA2-ENTERPRISE / LEGACY ENTERPRISE
                (typeUpper.contains("WPA2") || typeUpper.contains("ENTERPRISE")) && !typeUpper.contains("WPA3") -> {
                    overallResult = WifiOverallResult.SECURE
                    var calculatedScore = 85

                    // Check for Captive Portal
                    if (isCaptivePortal) {
                        calculatedScore -= 10
                        risks.add(
                            WifiDetectedRisk(
                                id = "captive_portal_intermediary",
                                nameEn = "Captive Portal Authentication",
                                nameAr = "بوابة تسجيل دخول ويب (Captive Portal)",
                                severity = WifiRiskSeverity.MEDIUM,
                                explanationEn = "The network intercepts HTTP/HTTPS requests to force web browser login. Malicious portals can mimic legitimate login forms or attempt certificate spoofing.",
                                explanationAr = "تعترض الشبكة اتصالات التصفح لإلزامك بتسجيل الدخول عبر صفحة ويب؛ قد يسهل ذلك هجمات التصيد وتزوير شهادات الأمان.",
                                reasonEn = "Android NetworkCapabilities detected NET_CAPABILITY_CAPTIVE_PORTAL.",
                                reasonAr = "رصدت إمكانيات شبكة أندرويد وجود بوابة تسجيل ويب نشطة.",
                                recommendationEn = "Carefully verify the domain name and SSL/TLS certificate of the login page before entering credentials.",
                                recommendationAr = "تحقق بدقة من عنوان النطاق وشهادة التشفير (قفل الأمان HTTPS) في صفحة الدخول قبل إدخال أي بيانات."
                            )
                        )
                    }

                    score = calculatedScore
                    recommendations.add(
                        if (isAr) "استخدم كلمة مرور شبكة طويلة ومعقدة (16 حرفاً عشوائياً فأكثر) لمنع هجمات التخمين."
                        else "Ensure your Wi-Fi pre-shared key is long and high-entropy (16+ characters)."
                    )
                    recommendations.add(
                        if (isAr) "عطّل ميزة WPS (Wi-Fi Protected Setup) في الراوتر لحمايته من هجمات PIN."
                        else "Disable WPS (Wi-Fi Protected Setup) in router settings to prevent PIN brute-force."
                    )
                    recommendations.add(
                        if (isAr) "إذا كان الراوتر وأجهزتك تدعم WPA3 (SAE)، فعّلها للحصول على أفضل حماية مستقبلية."
                        else "Upgrade to WPA3-Personal (SAE) if your router hardware and devices support it."
                    )
                }

                else -> {
                    overallResult = WifiOverallResult.UNKNOWN
                    score = null
                    risks.add(
                        WifiDetectedRisk(
                            id = "unclassified_security",
                            nameEn = "Unclassified Security Configuration",
                            nameAr = "تكوين أمني غير مصنف بدقة",
                            severity = WifiRiskSeverity.LOW,
                            explanationEn = "Security flag '$detectedSecurityType' detected but could not be safely graded by standard rules.",
                            explanationAr = "تم رصد بروتوكول '$detectedSecurityType' لكن يتعذر تقييمه برمجياً بشكل قاطع.",
                            reasonEn = "Protocol string not matching standard WPA2/WPA3/WEP/Open profiles.",
                            reasonAr = "سلسلة البروتوكول لا تطابق القوالب القياسية المعتمدة.",
                            recommendationEn = "Verify router settings directly to confirm WPA2 or WPA3 deployment.",
                            recommendationAr = "تحقق من إعدادات الراوتر للتأكد من تفعيل WPA2 أو WPA3."
                        )
                    )
                    recommendations.add(
                        if (isAr) "راجع لوحة تحكم الراوتر للتأكد من استخدام WPA2 أو WPA3."
                        else "Verify router configuration directly in its management portal."
                    )
                }
            }
        }

        // 7. API Limitation / Permission Notes
        val apiNote = when {
            !hasLocationPerm -> {
                if (isAr) "ملاحظة: يشترط نظام أندرويد إذن الموقع لعرض اسم الشبكة (SSID) بدقة وحمايتها من الإخفاء."
                else "Note: Android requires Location permission to display the network SSID and inspect access point capabilities."
            }
            !hasLocServices -> {
                if (isAr) "ملاحظة: خدمات الموقع (Location Services) متوقفة بالجهاز، مما قد يعيق قراءة اسم الشبكة."
                else "Note: System Location Services are toggled off in Android settings, which may redact the network SSID."
            }
            rawSsid == null -> {
                if (isAr) "ملاحظة: منح إذن الموقع وحده قد لا يكفي؛ يجب تفعيل خدمات الموقع من إعدادات الجهاز. وقد يظل Android يخفي الاسم على بعض الأجهزة أو الإصدارات."
                else "Note: Location permission alone may not be enough. Enable the device Location switch; Android may still redact the SSID on some devices or OS versions."
            }
            else -> null
        }

        return WifiSecurityReport(
            isWifiEnabled = true,
            isConnected = true,
            ssid = rawSsid,
            bssid = rawBssid,
            localIpv4Address = localIpv4Address,
            defaultGatewayAddress = defaultGatewayAddress,
            securityType = detectedSecurityType ?: (if (isAr) "تعذر التحقق" else "Unknown / Unable to Verify"),
            cipherSuite = detectedCipher,
            isProtected = isProtected,
            score = score,
            overallResult = overallResult,
            connectionStatus = if (isAr) "متصل بالشبكة اللاسلكية" else "Connected to Wi-Fi",
            signalDbm = rssi,
            signalQuality = signalQuality,
            frequencyMhz = freq,
            frequencyBand = band,
            linkSpeedMbps = linkSpeed,
            hasInternet = hasInternet,
            isCaptivePortal = isCaptivePortal,
            isMetered = isMetered,
            risks = risks,
            recommendations = recommendations,
            permissionGranted = hasLocationPerm,
            locationServicesEnabled = hasLocServices,
            apiLimitationNote = apiNote
        )
    }
}
