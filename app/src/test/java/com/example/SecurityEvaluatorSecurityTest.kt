package com.example

import com.example.logic.SecurityEvaluator
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class SecurityEvaluatorSecurityTest {
    @Test fun predictablePasswordDoesNotScoreHighly() {
        val result = SecurityEvaluator.evaluatePassword("P@ssword1234!")
        assertTrue("Predictable password score was ${result.score}", result.score < 50)
        assertTrue(result.checks.any { !it.passed })
    }

    @Test fun repeatedAndSequentialPasswordsArePenalized() {
        assertTrue(SecurityEvaluator.evaluatePassword("aaaaaaaaaaaa").score <= 30)
        assertTrue(SecurityEvaluator.evaluatePassword("Aaaabc123456!").score < 60)
    }

    @Test fun generatedComplexPasswordCanScoreHigher() {
        assertTrue(SecurityEvaluator.evaluatePassword("q9#Lx7!vR2@pM8\$z").score >= 70)
    }
}
