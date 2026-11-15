package com.example.localization

import com.example.model.Language

/** Localizes known, stable Link Checker result labels without altering URLs or provider identifiers. */
object LinkResultLocalization {
    private fun value(source: String, lang: Language, translations: Map<String, String>): String =
        if (lang == Language.AR) translations[source] ?: source else source

    fun riskLevel(source: String, lang: Language) = value(source, lang, mapOf(
        "Critical Danger" to "خطر حرج",
        "Medium Risk" to "مخاطر متوسطة",
        "Low Risk" to "مخاطر منخفضة",
        "Inconclusive / Offline" to "غير حاسم / الخدمة غير متاحة",
        "Inconclusive / Partial" to "غير حاسم / فحص جزئي",
        "Safe" to "آمن"
    ))

    fun verdict(source: String, lang: Language) = value(source, lang, mapOf(
        "Malicious Threat Detected" to "تم اكتشاف تهديد خبيث",
        "Suspicious Signals Detected" to "تم رصد مؤشرات مريبة",
        "Inconclusive (Threat Feeds Offline)" to "نتيجة غير حاسمة (مصادر التهديدات غير متاحة)",
        "Inconclusive (Some Threat Feeds Unavailable)" to "نتيجة غير حاسمة (بعض مصادر التهديدات غير متاحة)",
        "Inconclusive (Live threat intelligence unavailable)" to "نتيجة غير حاسمة (تعذر الوصول إلى استخبارات التهديدات المباشرة)",
        "Minor Heuristic Warnings" to "تحذيرات أمنية محدودة",
        "Malicious" to "خبيث",
        "Suspicious" to "مريب",
        "Clean" to "سليم",
        "Feed Notice" to "تنبيه من مصدر الفحص",
        "Active Malware Distribution" to "توزيع برمجيات خبيثة نشط",
        "Feed Offline" to "مصدر الفحص غير متاح",
        "Not Configured" to "غير مُعدّ"
    ))

    fun recommendation(source: String, lang: Language) = value(source, lang, mapOf(
        "DO NOT OPEN! This link exhibits strong characteristics of phishing, malware, or brand spoofing." to "لا تفتح الرابط! تظهر عليه مؤشرات قوية للتصيد أو البرمجيات الخبيثة أو انتحال العلامات التجارية.",
        "Proceed with caution. Check sender authenticity and never enter sensitive passwords." to "تابع بحذر. تحقق من هوية المرسل ولا تُدخل كلمات مرور أو بيانات حساسة.",
        "Appears mostly standard, but stay alert for unexpected redirects." to "يبدو الرابط اعتيادياً إلى حد كبير، لكن انتبه لأي إعادة توجيه غير متوقعة.",
        "This link appears clean based on security heuristics. Always verify before entering credentials." to "لم ترصد قواعد الفحص مؤشرات خطورة واضحة. تحقق من موثوقية المصدر قبل إدخال بياناتك.",
        "DO NOT OPEN! Confirmed dangerous target exhibiting phishing, malware, or unauthorized internal network exposure." to "لا تفتح الرابط! تم تأكيد خطورته لاحتوائه على مؤشرات تصيد أو برمجيات خبيثة أو وصول غير مصرح به إلى شبكة داخلية.",
        "Proceed with extreme caution. Verify sender authenticity and never enter credentials or download files." to "تعامل مع الرابط بحذر شديد. تحقق من هوية المرسل ولا تُدخل بيانات الدخول أو تُنزّل ملفات.",
        "External threat intelligence services could not be reached. Local heuristic evaluation passed, but live threat databases could not be verified. Exercise caution." to "تعذر الاتصال بمصادر استخبارات التهديدات الخارجية. اجتاز الرابط الفحوصات المحلية، لكن تعذر التحقق من قواعد التهديدات المباشرة؛ لذا تعامل معه بحذر.",
        "URL appears mostly standard, but verify destination before interacting." to "يبدو عنوان الرابط اعتيادياً، لكن تحقق من الوجهة قبل التفاعل معه.",
        "This link appears clean based on security threat feeds and heuristics. Always verify sender identity." to "لا تظهر مؤشرات خطورة في مصادر التهديدات والفحوصات المتاحة. تحقق دائماً من هوية المرسل.",
        "Some threat-intelligence providers were not configured or could not be reached. Available sources and local checks are shown, but this is not proof that the URL is safe. Exercise caution." to "بعض مصادر استخبارات التهديدات غير مهيأة أو تعذر الوصول إليها. تظهر المصادر المتاحة والفحوصات المحلية، لكن ذلك لا يثبت أن الرابط آمن. تعامل معه بحذر."
    ))

    fun findingTitle(source: String, lang: Language) = value(source, lang, mapOf(
        "Insecure Protocol (HTTP)" to "بروتوكول غير مشفر (HTTP)",
        "Encrypted Transport (HTTPS)" to "اتصال مشفر (HTTPS)",
        "Direct IP Hostname Detected" to "استخدام عنوان IP مباشر",
        "Standard Domain Name" to "اسم نطاق اعتيادي",
        "High-Risk Top Level Domain (TLD)" to "امتداد نطاق مرتفع المخاطر",
        "Reputable Domain Suffix" to "امتداد نطاق موثوق",
        "Brand Impersonation Keywords" to "مؤشرات انتحال علامة تجارية",
        "No Suspicious Brand Spoofing" to "لم تُرصد مؤشرات انتحال علامة تجارية",
        "SSRF & Internal Network Guard Flag" to "تحذير حماية الشبكات الداخلية (SSRF)",
        "Public Internet Reachability" to "وجهة متاحة عبر الإنترنت العام",
        "Unencrypted Protocol (HTTP)" to "بروتوكول غير مشفر (HTTP)",
        "TLS / SSL Transport Security (HTTPS)" to "حماية الاتصال عبر TLS / SSL (HTTPS)",
        "Direct Numerical IP Hostname" to "استخدام عنوان IP رقمي مباشر",
        "Registered FQDN Domain" to "نطاق مسجل بالكامل (FQDN)",
        "Elevated Abuse Rate TLD" to "امتداد نطاق ذو معدل إساءة مرتفع",
        "Standard Top-Level Domain Registry" to "امتداد نطاق ضمن السجلات الاعتيادية",
        "Brand Impersonation & Typosquatting Signal" to "مؤشرات انتحال علامة تجارية أو تحريف اسمها",
        "Live Threat Intelligence Alert" to "تنبيه استخبارات تهديدات مباشر"
    ))

    fun findingDescription(source: String, lang: Language) = value(source, lang, mapOf(
        "Connection is unencrypted. Login credentials can be intercepted." to "الاتصال غير مشفر؛ وقد يتمكن طرف آخر من اعتراض بيانات تسجيل الدخول.",
        "Traffic is secured with modern TLS/SSL encryption." to "تتم حماية حركة البيانات باستخدام تشفير TLS/SSL حديث.",
        "Legitimate sites rarely use raw IP addresses in consumer URLs." to "نادراً ما تستخدم المواقع الموثوقة عناوين IP مباشرة في روابطها العامة.",
        "Uses a registered host rather than an obscure bare IP." to "يستخدم الرابط نطاقاً مسجلاً بدلاً من عنوان IP مباشر وغير مألوف.",
        "The extension has a historically elevated abuse rate." to "للامتداد سجل سابق بمعدلات مرتفعة من إساءة الاستخدام.",
        "TLD is not listed on active spam/phishing warning registries." to "الامتداد غير مدرج في قوائم التحذير النشطة من الرسائل المزعجة والتصيد.",
        "Contains sensitive financial or brand terms on an unaffiliated domain." to "يحتوي النطاق على كلمات مالية أو أسماء علامات تجارية دون ارتباط موثوق بها.",
        "Domain and path do not mimic known financial institutions or tech platforms." to "لا يحاكي النطاق أو مسار الرابط مؤسسات مالية أو منصات تقنية معروفة.",
        "URL targets internal loopback (127.0.0.1), private RFC1918 block, or cloud metadata. Access prohibited." to "تستهدف الوجهة عنواناً داخلياً أو نطاقاً خاصاً أو بيانات تعريف سحابية؛ وتم حظر الوصول إليها.",
        "Destination points to a standard public domain rather than private infrastructure." to "تشير الوجهة إلى نطاق عام اعتيادي وليست بنية تحتية خاصة.",
        "Connection transmits data in cleartext without modern TLS encryption." to "ينقل الاتصال البيانات دون تشفير TLS حديث.",
        "Connection uses encrypted transport layer security." to "يستخدم الاتصال طبقة نقل مشفرة لحماية البيانات.",
        "Bare IP addresses in links frequently conceal phishing or command-and-control servers." to "قد تُستخدم عناوين IP المباشرة لإخفاء وجهات تصيد أو خوادم تحكم ضارة.",
        "Domain utilizes registered DNS name hierarchy." to "يستخدم النطاق تسلسلاً مسجلاً لأسماء DNS.",
        "The extension used has an elevated spam and abuse registry history." to "للامتداد سجل مرتفع من الرسائل المزعجة وإساءة الاستخدام.",
        "Domain extension is not listed under high-abuse registry watchlists." to "امتداد النطاق غير مدرج في قوائم مراقبة الامتدادات مرتفعة الإساءة.",
        "URL references prominent brand or financial keywords on an unaffiliated domain." to "يحتوي الرابط على أسماء علامات تجارية أو كلمات مالية ضمن نطاق غير تابع لها.",
        "Domain does not mimic known financial institutions or major services." to "لا ينتحل النطاق أسماء مؤسسات مالية أو خدمات رئيسية معروفة.",
        "Global security intelligence provider (URLhaus) confirmed this URL is actively distributing malware." to "أكد مصدر استخبارات أمني عالمي (URLhaus) أن الرابط يوزع برمجيات خبيثة حالياً."
    ))

    fun providerDetails(source: String, lang: Language): String {
        val translations = mapOf(
            "Listed on malware distribution registry." to "مدرج في سجل توزيع البرمجيات الخبيثة.",
            "Clean. Not indexed in malware registry." to "سليم؛ غير مدرج في سجل البرمجيات الخبيثة.",
            "Flagged in community phishing reports." to "تم الإبلاغ عنه في تقارير التصيد المجتمعية.",
            "Clean. Target URL is not listed as active phishing." to "سليم؛ عنوان الرابط غير مدرج كوجهة تصيد نشطة.",
            "Sandbox crawl analysis completed." to "اكتمل تحليل الرابط في بيئة اختبار معزولة.",
            "Target URL is not indexed in abuse.ch malware distribution repository." to "عنوان الرابط غير مدرج في مستودع abuse.ch لتوزيع البرمجيات الخبيثة.",
            "Direct client integration ready. Configurable via API key." to "التكامل المباشر متاح، ويمكن ضبطه باستخدام مفتاح API.",
            "Google Safe Browsing API key is not configured on the Worker." to "مفتاح Google Safe Browsing غير مضبوط على Worker.",
            "URLhaus authentication key is not configured on the Worker." to "مفتاح URLhaus غير مضبوط على Worker.",
            "VirusTotal API key is not configured on the Worker." to "مفتاح VirusTotal غير مضبوط على Worker.",
            "DNS lookup failed for the CyberGuard Worker. Check the active network or DNS resolver." to "فشل العثور على عنوان خادم CyberGuard عبر DNS. تحقق من شبكة Wi-Fi أو إعدادات DNS في الشبكة.",
            "A secure TLS connection to the CyberGuard Worker could not be established." to "تعذر إنشاء اتصال TLS آمن بخادم CyberGuard. تحقق من تاريخ الجهاز وإعدادات الشبكة أو وسيط الاتصال.",
            "The connection to the CyberGuard Worker timed out after retries." to "انتهت مهلة الاتصال بخادم CyberGuard بعد إعادة المحاولة المحدودة. قد تكون الشبكة بطيئة أو تحجب الاتصال.",
            "The CyberGuard Worker could not be reached after retries (ConnectException)." to "تعذر الوصول إلى خادم CyberGuard بعد محاولة اتصال محدودة. تحقق من الشبكة أو إعدادات الراوتر.",
            "The CyberGuard Worker could not be reached after retries (SocketTimeoutException)." to "انتهت مهلة الاتصال بخادم CyberGuard. تحقق من سرعة الشبكة وإمكانية الوصول إلى الخدمة.",
            "The CyberGuard Worker could not be reached after retries (UnknownHostException)." to "تعذر حل اسم خادم CyberGuard عبر DNS. تحقق من إعدادات DNS في شبكة Wi-Fi.",
            "The CyberGuard Worker could not be reached after retries (SSLException)." to "فشل الاتصال الآمن TLS بخادم CyberGuard. تحقق من إعدادات الشبكة أو VPN/Proxy.",
            "This URL has not been indexed by VirusTotal. No new analysis was submitted." to "لم يسبق فهرسة الرابط في VirusTotal؛ لم نرسل الرابط لإنشاء تحليل جديد.",
            "URL is not listed in URLhaus." to "عنوان الرابط غير مدرج في URLhaus.",
            "Google Safe Browsing found no threat match for this URL." to "لم تُرجع قاعدة Google تطابقًا لهذا الرابط.",
            "No available engines detected malicious indicators in the latest scan (93 engines)." to "لم ترصد المحركات المتاحة مؤشرات ضارة في آخر تحليل (93 محركًا)."
        )
        return when (lang) {
            Language.AR -> translations[source] ?: source
            Language.EN -> translations.entries.firstOrNull { it.value == source }?.key ?: source
        }
    }
}
