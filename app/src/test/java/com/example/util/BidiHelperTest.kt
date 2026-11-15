package com.example.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BidiHelperTest {

    @Test
    fun parenthesizedEnglishPhraseIsNotNestedInsideAnotherIsolate() {
        val formatted = BidiHelper.formatBiDi("افحص الرابط (Link Scanner) الآن")
        assertEquals(1, Regex(Regex.escape(BidiHelper.LRI)).findAll(formatted).count())
        assertTrue(formatted.contains("${BidiHelper.LRI}(Link Scanner)${BidiHelper.PDI}"))
    }

    @Test
    fun ratiosAndKnownTermsAreIsolatedWithoutChangingArabicText() {
        val formatted = BidiHelper.formatBiDi("نتيجة الفحص 460/500 عبر Wi-Fi")
        assertTrue(formatted.contains("${BidiHelper.LRI}460/500${BidiHelper.PDI}"))
        assertTrue(formatted.contains("${BidiHelper.LRI}Wi-Fi${BidiHelper.PDI}"))
        assertTrue(formatted.contains("نتيجة الفحص"))
    }

    @Test
    fun shortTechnicalTermsDoNotMatchInsideLongerWords() {
        val formatted = BidiHelper.formatBiDi("استخدم QRCODE")
        assertFalse(formatted.contains(BidiHelper.LRI))
    }


    @Test
    fun mixedArabicEnglishOnboardingAndPolicyPhrasesAreIsolated() {
        val samples = listOf(
            "احمِ حسابك باستخدام Two-Factor Authentication",
            "افحص الروابط باستخدام Google Safe Browsing",
            "إصدار التطبيق CyberGuard v1.0.0",
            "يمكنك التواصل عبر Email",
            "افحص رمز QR قبل فتح الرابط",
            "تضم الأكاديمية 13 مساراً بنسبة اجتياز 75%"
        )
        samples.forEach { sample ->
            val formatted = BidiHelper.formatBiDi(sample, isAr = true)
            assertTrue("Expected mixed technical terms to be isolated: $sample", formatted.contains(BidiHelper.LRI))
            assertTrue("Arabic host sentence should be preserved: $sample", formatted.any { it.code in 0x0600..0x06FF })
            assertTrue("Isolate markers should be balanced: $sample",
                formatted.count { it == BidiHelper.LRI } == formatted.count { it == BidiHelper.PDI })
        }
    }

    @Test
    fun englishTextRemainsUnchanged() {
        val original = "Pause, verify, and stay protected."
        assertEquals(original, BidiHelper.formatBiDi(original, isAr = false))
    }
}
