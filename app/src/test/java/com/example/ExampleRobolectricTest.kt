package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.logic.SecurityEvaluator
import com.example.logic.WifiOverallResult
import com.example.logic.WifiRiskSeverity
import com.example.logic.WifiSecurityScanner
import com.example.model.Language
import com.example.model.SecurityAuditState
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CyberGuard", appName)
  }

  @Test
  fun `wifi scanner handles disconnected state gracefully`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val report = WifiSecurityScanner.scanConnectedWifi(context, isAr = false)
    assertNotNull(report)
    assertNotNull(report.connectionStatus)
    assertEquals(WifiOverallResult.DISCONNECTED, report.overallResult)
    assertNull(report.score)
    assertTrue(report.recommendations.isNotEmpty())
  }

  @Test
  fun `wifi score integration in SecurityEvaluator works accurately without duplication`() {
    val auditStateInitial = SecurityAuditState(
        wifiPassed = 0,
        wifiTotal = 5,
        wifiScore = 0
    )
    val assessmentInitial = SecurityEvaluator.calculateDynamicAssessment(auditStateInitial, Language.EN)
    val initialPillar = assessmentInitial.pillars.first { it.id == "wifi" }
    assertEquals("pending", initialPillar.status)
    assertEquals(0, initialPillar.score)

    // After Wi-Fi scan with score 85 (e.g. WPA2-AES)
    val auditStateScanned = auditStateInitial.copy(
        wifiPassed = 4,
        wifiTotal = 5,
        wifiScore = 85
    )
    val assessmentScanned = SecurityEvaluator.calculateDynamicAssessment(auditStateScanned, Language.EN)
    val scannedPillar = assessmentScanned.pillars.first { it.id == "wifi" }
    assertEquals("assessed", scannedPillar.status)
    assertEquals(17, scannedPillar.score) // 85% of 20 = 17
    assertEquals(20, scannedPillar.maxScore)

    // Re-evaluating with same score yields consistent, non-duplicated score
    val assessmentRescanned = SecurityEvaluator.calculateDynamicAssessment(auditStateScanned, Language.EN)
    assertEquals(assessmentScanned.compositeScore, assessmentRescanned.compositeScore)
  }

  @Test
  fun `risk severity enum values are correctly defined`() {
    val critical = WifiRiskSeverity.CRITICAL
    val high = WifiRiskSeverity.HIGH
    val medium = WifiRiskSeverity.MEDIUM
    val low = WifiRiskSeverity.LOW
    assertNotNull(critical)
    assertNotNull(high)
    assertNotNull(medium)
    assertNotNull(low)
  }

  @Test
  fun `security assessment composite score calculation is bounded between 0 and 100`() {
    val stateZero = SecurityAuditState()
    val assessmentZero = SecurityEvaluator.calculateDynamicAssessment(stateZero, Language.EN)
    assertTrue(assessmentZero.compositeScore in 0..100)

    val stateMax = SecurityAuditState(
      urlScansCount = 10,
      urlThreatsCount = 0,
      urlAverageScore = 100,
      qrScansCount = 5,
      qrThreatsCount = 0,
      wifiPassed = 5,
      wifiTotal = 5,
      wifiScore = 100,
      passwordStrength = 100,
      passwordEntropy = 90,
      passwordBreachStatus = "safe",
      passwordBreachCount = 0
    )
    val assessmentMax = SecurityEvaluator.calculateDynamicAssessment(stateMax, Language.EN)
    assertTrue(assessmentMax.compositeScore in 85..100)
    assertTrue(assessmentMax.pillars.all { it.score <= it.maxScore })
  }

  @Test
  fun `wifi overall results enum covers all expected states`() {
    val secure = WifiOverallResult.SECURE
    val moderate = WifiOverallResult.MODERATE
    val risky = WifiOverallResult.RISKY
    val unknown = WifiOverallResult.UNKNOWN
    val disconnected = WifiOverallResult.DISCONNECTED
    assertEquals(5, WifiOverallResult.values().size)
  }
}
