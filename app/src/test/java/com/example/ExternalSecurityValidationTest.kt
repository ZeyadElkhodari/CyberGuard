package com.example

import com.example.network.ExternalSecurityService
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test

class ExternalSecurityValidationTest {
    @Test fun rejectsBrandImpersonationHostsButAllowsRealSubdomains() {
        assertTrue(ExternalSecurityService.isBrandSpoofingHost("evilpaypal.com"))
        assertTrue(ExternalSecurityService.isBrandSpoofingHost("fakegoogle.com"))
        assertFalse(ExternalSecurityService.isBrandSpoofingHost("paypal.com"))
        assertFalse(ExternalSecurityService.isBrandSpoofingHost("login.paypal.com"))
        assertFalse(ExternalSecurityService.isBrandSpoofingHost("accounts.google.com"))
    }

    @Test fun validatesHttpUrlsAndCommonVariants() {
        assertTrue(ExternalSecurityService.isValidHttpUrl("https://Example.com:8443/path"))
        assertTrue(ExternalSecurityService.isValidHttpUrl("HTTP://example.com"))
        assertTrue(ExternalSecurityService.isValidHttpUrl("https://[2606:4700:4700::1111]/"))
        assertTrue(ExternalSecurityService.isValidHttpUrl("https://пример.рф"))
        assertFalse(ExternalSecurityService.isValidHttpUrl("https://999.1.1.1"))
        assertFalse(ExternalSecurityService.isValidHttpUrl("https://0177.0.0.1"))
        assertFalse(ExternalSecurityService.isValidHttpUrl("https://bad host"))
        assertFalse(ExternalSecurityService.isValidHttpUrl("https://example.com:99999"))
        assertFalse(ExternalSecurityService.isValidHttpUrl("https://user:pass@example.com"))
    }

    @Test fun identifiesPrivateAndSpecialAddressRanges() {
        listOf("127.0.0.1", "10.0.0.1", "172.16.0.1", "192.168.1.1",
            "169.254.169.254", "100.64.0.1", "::1", "fe80::1", "fc00::1", "2001:db8::1")
            .forEach { assertTrue("$it should be private", ExternalSecurityService.isPrivateOrLocalAddress(it)) }
        assertFalse(ExternalSecurityService.isPrivateOrLocalAddress("8.8.8.8"))
        assertFalse(ExternalSecurityService.isPrivateOrLocalAddress("2606:4700:4700::1111"))
    }
}
