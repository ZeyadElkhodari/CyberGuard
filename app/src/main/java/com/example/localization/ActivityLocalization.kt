package com.example.localization

import com.example.model.Language
import com.example.model.SecurityActivity

/**
 * Rebuilds persisted activity copy from stable activity types and status values.
 * Dynamic values (counts, SSIDs and scores) are retained rather than translating
 * or mutating the user's original data.
 */
object ActivityLocalization {
    fun title(activity: SecurityActivity, lang: Language): String {
        val ar = lang == Language.AR
        return when (activity.type) {
            "password" -> if (ar) "فحص تسريب كلمة المرور" else "Password breach check"
            "link" -> if (ar) "فحص الرابط" else "Link security scan"
            "wifi" -> if (ar) "فحص أمان شبكة الواي فاي" else "Wi-Fi security check"
            "qr" -> if (activity.status == "critical") {
                if (ar) "حظر رمز QR مشبوه" else "Suspicious QR blocked"
            } else if (ar) "فحص رمز QR" else "QR code security scan"
            "phishing-message" -> if (ar) "فحص رسالة تصيد" else "Phishing message check"
            "lesson" -> if (ar) "نشاط الأكاديمية" else "Academy activity"
            else -> activity.title
        }
    }

    fun detail(activity: SecurityActivity, lang: Language): String {
        val ar = lang == Language.AR
        return when (activity.type) {
            "password" -> {
                val count = Regex("""(\d+)""").find(activity.detail)?.groupValues?.get(1)
                when (activity.status) {
                    "critical" -> if (ar) {
                        if (count != null) "ظهرت كلمة المرور ضمن بيانات التسريب $count مرة"
                        else "تم العثور على تطابق في بيانات التسريب"
                    } else {
                        if (count != null) "Password appeared $count times in breach data"
                        else "Password matched breach data"
                    }
                    "safe" -> if (ar) "لم يتم العثور على تطابق في قواعد البيانات التي تم فحصها"
                        else "No match found in the datasets checked"
                    else -> if (ar) "تعذر إكمال الفحص؛ النتيجة غير حاسمة"
                        else "Check could not be completed; result is inconclusive"
                }
            }
            "wifi" -> {
                // The SSID and percentage are user/network data and must remain unchanged.
                val separator = activity.detail.lastIndexOf('•')
                if (separator >= 0) {
                    val network = activity.detail.substring(0, separator).trim()
                    val score = activity.detail.substring(separator + 1).trim()
                    if (ar) "الشبكة: ${network} • نتيجة الأمان: ${score}"
                    else "Network: ${network} • Security score: ${score}"
                } else activity.detail
            }
            "phishing-message" -> {
                val count = Regex("""(\d+)""").find(activity.detail)?.groupValues?.get(1)
                if (activity.status == "warning") {
                    if (ar) {
                        if (count != null) "تم رصد $count مؤشرات تهديد في الرسالة"
                        else "تم رصد مؤشرات تهديد في الرسالة"
                    } else {
                        if (count != null) "$count suspicious indicators detected"
                        else "Suspicious indicators detected"
                    }
                } else if (ar) "لم يتم رصد مؤشرات تصيد واضحة" else "No clear phishing indicators detected"
            }
            "qr" -> if (activity.status == "critical") {
                if (ar) "تم اعتراض مسار تصيد عبر رمز QR" else "Quishing vector intercepted"
            } else {
                if (ar) "تم التحقق من محتوى رمز QR" else "QR payload verified"
            }
            else -> if (lang == Language.EN &&
                activity.detail.any { it in '\u0600'..'\u06FF' }
            ) com.example.academy.data.AcademyTranslations.translateSentence(activity.detail)
            else activity.detail
        }
    }
}
