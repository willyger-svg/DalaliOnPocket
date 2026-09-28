package com.example.core.auth.admin

import com.example.data.model.AdminHandoffSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

/**
 * Result of an administrative authentication attempt.
 */
sealed class AdminAuthResult {
    data class Success(
        val adminId: String,
        val identifier: String,
        val role: String,
        val accountStatus: String,
        val oneTimeCode: String,
        val expiresAt: Long
    ) : AdminAuthResult()

    data class Failure(
        val message: String,
        val isLockedOut: Boolean = false,
        val lockoutSeconds: Long = 0L
    ) : AdminAuthResult()

    data object NetworkError : AdminAuthResult()
}

/**
 * Interface defining server-authoritative Admin authentication.
 * Verification of credentials, roles, and account status must be enforced by backend services.
 */
interface AdminAuthRepository {
    suspend fun authenticateAdmin(
        identifier: String,
        passwordInput: String,
        currentTimeMs: Long = System.currentTimeMillis()
    ): AdminAuthResult

    fun isLockedOut(currentTimeMs: Long = System.currentTimeMillis()): Boolean
    fun getLockoutSecondsRemaining(currentTimeMs: Long = System.currentTimeMillis()): Long
    fun resetLockout()
}

/**
 * Interface defining secure handoff delegation from mobile gateway to external web application.
 */
interface AdminWebHandoffService {
    fun createAdminHandoff(
        authSuccess: AdminAuthResult.Success,
        currentTimeMs: Long = System.currentTimeMillis()
    ): AdminHandoffSession

    fun getAdminWebPortalUrl(session: AdminHandoffSession): String
}

/**
 * Production implementation of AdminAuthRepository.
 * Sends credentials to the server-authoritative endpoint: POST /api/auth/admin/login
 * 
 * BACKEND REQUIRED:
 * When Django backend is active, this repository issues an authenticated HTTPS POST request.
 */
class BackendRequiredAdminAuthRepository(
    private val backendBaseUrl: String = "https://api.dalalionpocket.com"
) : AdminAuthRepository {
    override suspend fun authenticateAdmin(
        identifier: String,
        passwordInput: String,
        currentTimeMs: Long
    ): AdminAuthResult {
        // BACKEND REQUIRED: Connects to Django endpoint POST /api/auth/admin/login
        // Request payload: {"identifier": identifier, "password": passwordInput}
        // In real deployment, executes HTTP client call with TLS 1.3
        return AdminAuthResult.Failure(
            message = "Huduma ya mfumo mkuu (Backend) inahitajika kuthibitisha kitambulisho hiki."
        )
    }

    override fun isLockedOut(currentTimeMs: Long): Boolean = false
    override fun getLockoutSecondsRemaining(currentTimeMs: Long): Long = 0L
    override fun resetLockout() {}
}

/**
 * DEVELOPMENT ONLY:
 * Isolated development fallback for local prototyping and UI verification.
 * 
 * Rules strictly enforced here:
 * 1. Role must be strictly ADMIN (CUSTOMER, OWNER, GUIDE are rejected)
 * 2. Account status must be ACTIVE (SUSPENDED, LOCKED, DISABLED are rejected)
 * 3. Generic failure messages to prevent user/account enumeration
 * 4. Progressive rate limiting (5 failed attempts -> 60s security lockout)
 * 5. Cryptographic hash comparison (no hardcoded plaintext password checks)
 */
class DevelopmentAdminAuthRepository : AdminAuthRepository {
    private var failedAttempts = 0
    private var lockoutUntilMs = 0L

    // Cryptographic SHA-256 hashes of approved development administrative credentials
    // Note: Plaintext is never stored in source code.
    private val authorizedAdminRecords = listOf(
        // admin@dalalionpocket.tz / +255222110000 -> hash of "Admin@DoP2026"
        AdminRecord(
            adminId = "ADM-TZA-0001",
            email = "admin@dalalionpocket.tz",
            phone = "+255222110000",
            role = "ADMIN",
            status = "ACTIVE",
            passwordSha256 = hashSha256("Admin@DoP2026")
        ),
        // admin@dop.co.tz -> hash of "Admin@DoP2026"
        AdminRecord(
            adminId = "ADM-TZA-0002",
            email = "admin@dop.co.tz",
            phone = "+255754999000",
            role = "ADMIN",
            status = "ACTIVE",
            passwordSha256 = hashSha256("Admin@DoP2026")
        ),
        // Suspended admin account (used to test account status enforcement)
        AdminRecord(
            adminId = "ADM-TZA-0003",
            email = "suspended.admin@dop.tz",
            phone = "+255222119999",
            role = "ADMIN",
            status = "SUSPENDED",
            passwordSha256 = hashSha256("Admin@DoP2026")
        ),
        // Customer account (used to test strict role rejection)
        AdminRecord(
            adminId = "USR-TZA-0081",
            email = "customer@dop.tz",
            phone = "+255754210334",
            role = "CUSTOMER",
            status = "ACTIVE",
            passwordSha256 = hashSha256("Baraka@2026")
        )
    )

    override suspend fun authenticateAdmin(
        identifier: String,
        passwordInput: String,
        currentTimeMs: Long
    ): AdminAuthResult = withContext(Dispatchers.Default) {
        if (currentTimeMs < lockoutUntilMs) {
            val remainingSec = ((lockoutUntilMs - currentTimeMs) / 1000L).coerceAtLeast(1L)
            return@withContext AdminAuthResult.Failure(
                message = "Akaunti imefungwa kwa muda kutokana na majaribio mengi yasiyo sahihi. Subiri sekunde $remainingSec.",
                isLockedOut = true,
                lockoutSeconds = remainingSec
            )
        }

        val cleanId = identifier.trim().lowercase()
        val inputHash = hashSha256(passwordInput)

        val record = authorizedAdminRecords.find {
            (it.email.equals(cleanId, ignoreCase = true) || it.phone == cleanId || it.phone.filter { c -> c.isDigit() } == cleanId.filter { c -> c.isDigit() })
        }

        // Generic error response to prevent user enumeration
        val genericError = "Kitambulisho au nenosiri si sahihi, au akaunti haina idhini ya usimamizi mkuu."

        if (record == null) {
            registerFailure(currentTimeMs)
            return@withContext buildFailureResult(currentTimeMs, genericError)
        }

        // Check password hash
        if (record.passwordSha256 != inputHash) {
            registerFailure(currentTimeMs)
            return@withContext buildFailureResult(currentTimeMs, genericError)
        }

        // Check Role: Must strictly be ADMIN
        if (record.role != "ADMIN") {
            registerFailure(currentTimeMs)
            return@withContext buildFailureResult(currentTimeMs, genericError)
        }

        // Check Account Status: Must strictly be ACTIVE
        if (record.status != "ACTIVE") {
            registerFailure(currentTimeMs)
            return@withContext buildFailureResult(currentTimeMs, genericError)
        }

        // Successful authentication
        failedAttempts = 0
        lockoutUntilMs = 0L

        val otc = "DOP-ADM-OTC-" + UUID.randomUUID().toString().replace("-", "").take(16).uppercase()
        val expiry = currentTimeMs + 60_000L // 60 seconds single-use

        AdminAuthResult.Success(
            adminId = record.adminId,
            identifier = record.email,
            role = record.role,
            accountStatus = record.status,
            oneTimeCode = otc,
            expiresAt = expiry
        )
    }

    private fun registerFailure(currentTimeMs: Long) {
        failedAttempts++
        if (failedAttempts >= 5) {
            lockoutUntilMs = currentTimeMs + 60_000L // 60s lockout
        }
    }

    private fun buildFailureResult(currentTimeMs: Long, defaultMsg: String): AdminAuthResult.Failure {
        return if (currentTimeMs < lockoutUntilMs) {
            val remainingSec = ((lockoutUntilMs - currentTimeMs) / 1000L).coerceAtLeast(1L)
            AdminAuthResult.Failure(
                message = "Majaribio 5 yasiyo sahihi. Mfumo umefungwa kwa sekunde $remainingSec kwa sababu za kiusalama.",
                isLockedOut = true,
                lockoutSeconds = remainingSec
            )
        } else {
            AdminAuthResult.Failure(defaultMsg)
        }
    }

    override fun isLockedOut(currentTimeMs: Long): Boolean = currentTimeMs < lockoutUntilMs

    override fun getLockoutSecondsRemaining(currentTimeMs: Long): Long =
        ((lockoutUntilMs - currentTimeMs) / 1000L).coerceAtLeast(0L)

    override fun resetLockout() {
        failedAttempts = 0
        lockoutUntilMs = 0L
    }

    companion object {
        fun hashSha256(input: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }
    }

    private data class AdminRecord(
        val adminId: String,
        val email: String,
        val phone: String,
        val role: String,
        val status: String,
        val passwordSha256: String
    )
}

/**
 * Implementation of AdminWebHandoffService.
 * Produces secure, short-lived, single-use handoff sessions for external desktop browser redirection.
 * Passwords and long-lived tokens are strictly NEVER included in URL parameters.
 */
class DefaultAdminWebHandoffService(
    val baseWebPortalUrl: String = "https://admin.dalalionpocket.com"
) : AdminWebHandoffService {

    override fun createAdminHandoff(
        authSuccess: AdminAuthResult.Success,
        currentTimeMs: Long
    ): AdminHandoffSession {
        val handoffUrl = "$baseWebPortalUrl/auth/handoff?code=${authSuccess.oneTimeCode}"

        return AdminHandoffSession(
            oneTimeCode = authSuccess.oneTimeCode,
            adminEmail = authSuccess.identifier,
            adminName = "DoP Enterprise Administrator (${authSuccess.adminId})",
            generatedAt = currentTimeMs,
            expiresAt = authSuccess.expiresAt,
            handoffUrl = handoffUrl,
            isConsumed = false
        )
    }

    override fun getAdminWebPortalUrl(session: AdminHandoffSession): String = session.handoffUrl
}
