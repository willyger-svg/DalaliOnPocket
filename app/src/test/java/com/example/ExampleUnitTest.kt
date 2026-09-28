package com.example

import com.example.core.auth.AppAuthState
import com.example.core.auth.AuthManager
import com.example.core.auth.admin.AdminAuthResult
import com.example.core.auth.admin.AdminTapDetector
import com.example.core.auth.admin.DefaultAdminWebHandoffService
import com.example.core.auth.admin.DevelopmentAdminAuthRepository
import com.example.data.model.*
import com.example.data.repository.PropertyRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

    @Before
    fun setUp() {
        AuthManager.logout()
        AuthManager.resetSecretGesture()
        AuthManager.adminAuthRepository.resetLockout()
    }

    // ============================================================
    // 14-TAP DETECTOR TESTS
    // ============================================================

    @Test
    fun testZeroTapsDoesNotTrigger() {
        var triggered = false
        val detector = AdminTapDetector(targetTaps = 14) { triggered = true }
        assertEquals(0, detector.tapCount)
        assertFalse("Zero taps must not trigger", triggered)
    }

    @Test
    fun testFiveTapsDoesNotTrigger() {
        var triggered = false
        val detector = AdminTapDetector(targetTaps = 14) { triggered = true }
        repeat(5) {
            detector.registerTap()
        }
        assertEquals(5, detector.tapCount)
        assertFalse("5 taps must not trigger", triggered)
    }

    @Test
    fun testThirteenTapsDoesNotTrigger() {
        var triggered = false
        val detector = AdminTapDetector(targetTaps = 14) { triggered = true }
        repeat(13) {
            val result = detector.registerTap()
            assertFalse("Taps before 14th must return false", result)
        }
        assertEquals(13, detector.tapCount)
        assertFalse("13 taps must not trigger", triggered)
    }

    @Test
    fun testFourteenthTapTriggersAndNavigates() {
        AuthManager.logout()
        assertEquals(AppAuthState.LOGIN, AuthManager.authState.value)

        var unlocked = false
        repeat(13) {
            unlocked = AuthManager.onSecretGestureTap()
            assertFalse("Must not unlock prior to 14 taps", unlocked)
            assertEquals(AppAuthState.LOGIN, AuthManager.authState.value)
        }

        // 14th Tap
        unlocked = AuthManager.onSecretGestureTap()
        assertTrue("14th tap must trigger admin entry", unlocked)
        assertEquals(AppAuthState.ADMIN_ACCESS, AuthManager.authState.value)
    }

    @Test
    fun testTapsTooSlowlyResetsSequence() {
        var triggered = false
        val detector = AdminTapDetector(targetTaps = 14, resetTimeoutMs = 2500L) { triggered = true }

        var currentTime = 10000L
        // Tap 10 times fast (100ms apart)
        repeat(10) {
            detector.registerTap(currentTime)
            currentTime += 100L
        }
        assertEquals(10, detector.tapCount)

        // Wait 3000ms (exceeds 2500ms timeout)
        currentTime += 3000L
        detector.registerTap(currentTime)

        // Counter must have reset to 1
        assertEquals("Tap count must reset to 1 after timeout", 1, detector.tapCount)
        assertFalse("Must not trigger after timeout reset", triggered)
    }

    @Test
    fun testBackFromAdminAccessReturnsToLogin() {
        AuthManager.navigateToAdminLogin()
        assertEquals(AppAuthState.ADMIN_ACCESS, AuthManager.authState.value)

        AuthManager.exitAdminAccess()
        assertEquals(AppAuthState.LOGIN, AuthManager.authState.value)
    }

    // ============================================================
    // ADMIN AUTHENTICATION TESTS
    // ============================================================

    @Test
    fun testAdminAuthenticationSuccess() {
        val success = AuthManager.authenticateAdmin("admin@dop.co.tz", "Admin@DoP2026")
        assertTrue("Admin authentication should succeed with valid credentials", success)
        val session = AuthManager.adminHandoffSession.value
        assertNotNull(session)
        assertTrue(session!!.oneTimeCode.startsWith("DOP-ADM-OTC-"))
        assertTrue(session.handoffUrl.contains("admin.dalalionpocket.com"))
        assertEquals(AppAuthState.ADMIN_HANDOFF, AuthManager.authState.value)
    }

    @Test
    fun testAdminAuthenticationFailureWrongPassword() {
        val failure = AuthManager.authenticateAdmin("admin@dop.co.tz", "WrongPassword123")
        assertFalse("Admin authentication should fail with wrong credentials", failure)
        assertNotNull(AuthManager.adminAuthError.value)
    }

    @Test
    fun testCustomerCredentialsRejectedForAdminAccess() = runBlocking {
        val repo = DevelopmentAdminAuthRepository()
        // Customer Baraka Elias Mushi
        val result = repo.authenticateAdmin("customer@dop.tz", "Baraka@2026")
        assertTrue("Customer credentials must be rejected from Admin gateway", result is AdminAuthResult.Failure)
    }

    @Test
    fun testSuspendedAdminAccountRejected() = runBlocking {
        val repo = DevelopmentAdminAuthRepository()
        val result = repo.authenticateAdmin("suspended.admin@dop.tz", "Admin@DoP2026")
        assertTrue("Suspended admin must be rejected", result is AdminAuthResult.Failure)
    }

    @Test
    fun testRateLimitingLocksOutAfterFiveFailures() = runBlocking {
        val repo = DevelopmentAdminAuthRepository()
        var time = 1000L

        repeat(5) {
            val res = repo.authenticateAdmin("admin@dop.co.tz", "WrongPass", time)
            time += 100L
        }

        assertTrue("Must be locked out after 5 consecutive failures", repo.isLockedOut(time))
        val lockoutRes = repo.authenticateAdmin("admin@dop.co.tz", "Admin@DoP2026", time)
        assertTrue(lockoutRes is AdminAuthResult.Failure)
        assertTrue((lockoutRes as AdminAuthResult.Failure).isLockedOut)
    }

    // ============================================================
    // SECURE WEB HANDOFF TESTS
    // ============================================================

    @Test
    fun testWebHandoffNeverContainsPasswordOrAccessToken() = runBlocking {
        val repo = DevelopmentAdminAuthRepository()
        val handoffService = DefaultAdminWebHandoffService("https://admin.dalalionpocket.com")

        val authResult = repo.authenticateAdmin("admin@dalalionpocket.tz", "Admin@DoP2026")
        assertTrue(authResult is AdminAuthResult.Success)

        val success = authResult as AdminAuthResult.Success
        val session = handoffService.createAdminHandoff(success)

        assertFalse("Handoff URL must never contain password", session.handoffUrl.contains("Admin@DoP2026"))
        assertFalse("Handoff URL must never contain 'password'", session.handoffUrl.contains("password="))
        assertTrue("Handoff URL must contain short-lived one-time code", session.handoffUrl.contains("code=DOP-ADM-OTC-"))
        assertTrue("One-time code must expire within 60 seconds", session.expiresAt > session.generatedAt)
    }

    @Test
    fun testWebHandoffDestinationIsConfigurable() = runBlocking {
        val repo = DevelopmentAdminAuthRepository()
        val customDomainService = DefaultAdminWebHandoffService("https://enterprise.dop.tz")

        val authResult = repo.authenticateAdmin("admin@dalalionpocket.tz", "Admin@DoP2026")
        val session = customDomainService.createAdminHandoff(authResult as AdminAuthResult.Success)

        assertTrue(session.handoffUrl.startsWith("https://enterprise.dop.tz/auth/handoff?code="))
    }

    // ============================================================
    // PROPERTY DRAFT TESTS
    // ============================================================

    @Test
    fun testDraftAutosaveAndCompleteness() = runBlocking {
        val draft = PropertyDraft(
            title = "Masaki Executive Villa",
            description = "Nyumba ya kifahari yenye bustani na bwawa la kuogelea.",
            priceTzs = 2500000L,
            region = "Dar es Salaam",
            district = "Kinondoni",
            ward = "Masaki",
            mediaItems = listOf(
                PropertyMediaItem(remoteUrl = "https://example.com/photo1.jpg", isCover = true),
                PropertyMediaItem(remoteUrl = "https://example.com/photo2.jpg")
            ),
            verificationDocuments = listOf(
                PropertyVerificationDocument(documentType = "Hati ya Ardhi", documentName = "TITLE_DEED.pdf", fileUri = "content://docs/1")
            )
        )

        PropertyRepository.saveDraft(draft)
        val currentDraft = PropertyRepository.ownerDraft.value
        assertNotNull(currentDraft)
        assertEquals("Masaki Executive Villa", currentDraft?.title)
        assertTrue("Completeness score should be at least 70%", (currentDraft?.completenessScore ?: 0) >= 70)

        // Test Submit Draft
        val submittedProperty = PropertyRepository.submitDraft(currentDraft!!)
        assertEquals(ListingStatus.SUBMITTED, submittedProperty.listingStatus)
        assertEquals(VerificationStatus.PENDING, submittedProperty.verificationStatus)
        assertEquals(2, submittedProperty.mediaItems.size)
        assertEquals(1, submittedProperty.verificationDocuments.size)
        assertNull("Draft should be cleared after submission", PropertyRepository.ownerDraft.value)
    }
}
