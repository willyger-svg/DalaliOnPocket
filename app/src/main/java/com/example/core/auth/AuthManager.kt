package com.example.core.auth

import com.example.core.auth.admin.AdminAuthRepository
import com.example.core.auth.admin.AdminAuthResult
import com.example.core.auth.admin.AdminTapDetector
import com.example.core.auth.admin.AdminWebHandoffService
import com.example.core.auth.admin.DefaultAdminWebHandoffService
import com.example.core.auth.admin.DevelopmentAdminAuthRepository
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import java.util.UUID

/**
 * High-level authentication states for mobile application navigation
 */
enum class AppAuthState {
    SPLASH,
    ONBOARDING,
    LOGIN,
    SIGN_UP,
    AUTHENTICATED,
    ADMIN_BLOCKED,
    ADMIN_ACCESS,
    ADMIN_HANDOFF
}

sealed class AuthResult {
    data object Idle : AuthResult()
    data object Loading : AuthResult()
    data class Success(val user: UserSession) : AuthResult()
    data class Error(val message: String) : AuthResult()
    data object AdminNotSupportedOnMobile : AuthResult()
}

data class StarterAccountInfo(
    val id: String,
    val name: String,
    val username: String,
    val phone: String,
    val rawPhone: String,
    val password: String,
    val role: UserRole,
    val location: String,
    val description: String
)

/**
 * Isolated Development & Mock Authentication Repository
 */
object AuthManager {
    // Official Starter Accounts for testing and demonstration
    val starterAccounts = listOf(
        StarterAccountInfo(
            id = "USR-TZA-0081",
            name = "Baraka Elias Mushi",
            username = "baraka.mushi",
            phone = "+255 754 210 334",
            rawPhone = "0754210334",
            password = "Baraka@2026",
            role = UserRole.CUSTOMER,
            location = "Mikocheni B, Dar es Salaam",
            description = "Mpangaji / Mteja (Customer) - Anatafuta nyumba na fremu zilizohakikiwa"
        ),
        StarterAccountInfo(
            id = "OWN-TZA-0882",
            name = "Eng. Grace Ndesamburo",
            username = "grace.ndesamburo",
            phone = "+255 784 567 890",
            rawPhone = "0784567890",
            password = "Grace@2026",
            role = UserRole.OWNER,
            location = "Masaki & Kinondoni, Dar es Salaam",
            description = "Mwenye Nyumba / Majengo (Owner) - Mmiliki wa apartimenti na viwanja"
        ),
        StarterAccountInfo(
            id = "GDE-TZA-0104",
            name = "Mohamed Said Kimbau",
            username = "mohamed.kimbau",
            phone = "+255 655 789 012",
            rawPhone = "0655789012",
            password = "Kimbau@2026",
            role = UserRole.GUIDE,
            location = "Sinza & Kijitonyama, Dar es Salaam",
            description = "DoP Guide Rasmi - Mwezeshaji wa ziara za ukaguzi wa nyumba mtaani"
        )
    )

    // Current user session
    private val _currentUser = MutableStateFlow<UserSession?>(
        UserSession(
            id = starterAccounts[0].id,
            name = starterAccounts[0].name,
            email = "baraka.mushi@dalalionpocket.tz",
            phone = starterAccounts[0].phone,
            role = UserRole.CUSTOMER,
            isVerified = true
        )
    )
    val currentUser: StateFlow<UserSession?> = _currentUser.asStateFlow()

    // Current authentication state (starts at SPLASH)
    private val _authState = MutableStateFlow(AppAuthState.SPLASH)
    val authState: StateFlow<AppAuthState> = _authState.asStateFlow()

    // Operation result status (Loading, Error, Success)
    private val _authResult = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authResult: StateFlow<AuthResult> = _authResult.asStateFlow()

    // Onboarding completed flag
    private val _hasCompletedOnboarding = MutableStateFlow(false)
    val hasCompletedOnboarding: StateFlow<Boolean> = _hasCompletedOnboarding.asStateFlow()

    // Username to phone mapping
    private val usernameToPhone = mapOf(
        "baraka.mushi" to "+255754210334",
        "grace.ndesamburo" to "+255784567890",
        "mohamed.kimbau" to "+255655789012"
    )

    // Mock registered database in memory for dev testing
    private val devUserDirectory = mutableMapOf(
        // Primary Starter Accounts
        "+255754210334" to UserSession(
            id = "USR-TZA-0081",
            name = "Baraka Elias Mushi",
            email = "baraka.mushi@dalalionpocket.tz",
            phone = "+255 754 210 334",
            accountStatus = AccountStatus.ACTIVE,
            isCustomerActive = true,
            ownerCapability = OwnerCapabilityStatus.NOT_STARTED,
            guideCapability = GuideCapabilityStatus.NOT_APPLIED,
            activeMode = UserRole.CUSTOMER,
            isVerified = true
        ),
        "+255784567890" to UserSession(
            id = "OWN-TZA-0882",
            name = "Eng. Grace Ndesamburo",
            email = "grace.ndesamburo@dop.co.tz",
            phone = "+255 784 567 890",
            accountStatus = AccountStatus.ACTIVE,
            isCustomerActive = true,
            ownerCapability = OwnerCapabilityStatus.ACTIVE,
            guideCapability = GuideCapabilityStatus.NOT_APPLIED,
            activeMode = UserRole.OWNER,
            ownerProfile = OwnerProfile(
                ownerType = OwnerType.INDIVIDUAL,
                displayName = "Eng. Grace Ndesamburo",
                operatingRegions = listOf("Dar es Salaam"),
                operatingDistricts = listOf("Masaki", "Kinondoni"),
                contactPhone = "+255 784 567 890",
                payoutNumber = "0784567890"
            ),
            isVerified = true
        ),
        "+255655789012" to UserSession(
            id = "GDE-TZA-0104",
            name = "Mohamed Said Kimbau",
            email = "mohamed.kimbau@dop.co.tz",
            phone = "+255 655 789 012",
            accountStatus = AccountStatus.ACTIVE,
            isCustomerActive = true,
            ownerCapability = OwnerCapabilityStatus.NOT_STARTED,
            guideCapability = GuideCapabilityStatus.APPROVED,
            activeMode = UserRole.GUIDE,
            guideApplication = GuideApplication(
                fullName = "Mohamed Said Kimbau",
                phone = "+255 655 789 012",
                nationalIdNin = "19880412-11101-00004-21",
                operatingZones = listOf("Sinza", "Kijitonyama", "Mwenge"),
                status = GuideCapabilityStatus.APPROVED
            ),
            isVerified = true
        ),
        // Fallback backward-compatible test accounts
        "+255754123456" to UserSession(
            id = "USR-TZA-0081",
            name = "Baraka Elias Mushi",
            email = "baraka.mushi@dalalionpocket.tz",
            phone = "+255 754 210 334",
            accountStatus = AccountStatus.ACTIVE,
            isCustomerActive = true,
            ownerCapability = OwnerCapabilityStatus.NOT_STARTED,
            guideCapability = GuideCapabilityStatus.NOT_APPLIED,
            activeMode = UserRole.CUSTOMER,
            isVerified = true
        ),
        "+255713999888" to UserSession(
            id = "OWN-TZA-0882",
            name = "Eng. Grace Ndesamburo",
            email = "grace.ndesamburo@dop.co.tz",
            phone = "+255 784 567 890",
            accountStatus = AccountStatus.ACTIVE,
            isCustomerActive = true,
            ownerCapability = OwnerCapabilityStatus.ACTIVE,
            guideCapability = GuideCapabilityStatus.NOT_APPLIED,
            activeMode = UserRole.OWNER,
            ownerProfile = OwnerProfile(
                ownerType = OwnerType.INDIVIDUAL,
                displayName = "Eng. Grace Ndesamburo",
                contactPhone = "+255 784 567 890"
            ),
            isVerified = true
        ),
        "+255682555111" to UserSession(
            id = "GDE-TZA-0104",
            name = "Mohamed Said Kimbau",
            email = "mohamed.kimbau@dop.co.tz",
            phone = "+255 655 789 012",
            accountStatus = AccountStatus.ACTIVE,
            isCustomerActive = true,
            ownerCapability = OwnerCapabilityStatus.NOT_STARTED,
            guideCapability = GuideCapabilityStatus.APPROVED,
            activeMode = UserRole.GUIDE,
            guideApplication = GuideApplication(
                fullName = "Mohamed Said Kimbau",
                phone = "+255 655 789 012",
                status = GuideCapabilityStatus.APPROVED
            ),
            isVerified = true
        ),
        // Demonstration admin to test rejection rule
        "+255222110000" to UserSession(
            id = "ADM-TZA-0001",
            name = "DoP Platform Administrator",
            email = "admin@dalalionpocket.tz",
            phone = "+255 22 211 0000",
            accountStatus = AccountStatus.ACTIVE,
            activeMode = UserRole.ADMIN,
            isVerified = true
        )
    )

    val mockUsers: Map<UserRole, UserSession> = mapOf(
        UserRole.CUSTOMER to devUserDirectory["+255754210334"]!!,
        UserRole.OWNER to devUserDirectory["+255784567890"]!!,
        UserRole.GUIDE to devUserDirectory["+255655789012"]!!
    )

    fun completeSplash() = onSplashFinished()

    fun switchRole(role: UserRole) = com.example.core.account.AccountManager.switchMobileRole(role)

    /**
     * Complete Splash and restore session or navigate to Onboarding/Login
     */
    fun onSplashFinished() {
        if (!_hasCompletedOnboarding.value) {
            _authState.value = AppAuthState.ONBOARDING
        } else if (_currentUser.value != null) {
            val user = _currentUser.value!!
            if (user.role == UserRole.ADMIN) {
                _authState.value = AppAuthState.ADMIN_BLOCKED
            } else {
                _authState.value = AppAuthState.AUTHENTICATED
            }
        } else {
            _authState.value = AppAuthState.LOGIN
        }
    }

    /**
     * Mark onboarding complete and move to Login
     */
    fun completeOnboarding() {
        _hasCompletedOnboarding.value = true
        _authState.value = AppAuthState.LOGIN
    }

    fun navigateToSignUp() {
        _authResult.value = AuthResult.Idle
        _authState.value = AppAuthState.SIGN_UP
    }

    fun navigateToLogin() {
        _authResult.value = AuthResult.Idle
        _authState.value = AppAuthState.LOGIN
    }

    /**
     * Normalize Tanzania phone number (+255 or 0...) or handle username aliases
     */
    fun normalizeTanzaniaPhone(rawInput: String): String {
        val trimmed = rawInput.trim().lowercase()
        if (usernameToPhone.containsKey(trimmed)) {
            return usernameToPhone[trimmed]!!
        }
        val digits = rawInput.filter { it.isDigit() }
        return when {
            rawInput.startsWith("+255") && digits.length >= 12 -> "+${digits.take(12)}"
            digits.startsWith("255") && digits.length >= 12 -> "+${digits.take(12)}"
            digits.startsWith("0") && digits.length >= 10 -> "+255${digits.substring(1).take(9)}"
            digits.length == 9 -> "+255$digits"
            else -> rawInput.trim()
        }
    }

    /**
     * Login with phone and password (or simulated OTP)
     */
    fun login(phoneInput: String, passwordInput: String) {
        val normalized = normalizeTanzaniaPhone(phoneInput)
        _authResult.value = AuthResult.Loading

        if (normalized.isBlank() || normalized.length < 10) {
            _authResult.value = AuthResult.Error("Tafadhali ingiza namba sahihi ya simu ya Tanzania (mfano 0754 123 456).")
            return
        }

        if (passwordInput.isBlank() || passwordInput.length < 4) {
            _authResult.value = AuthResult.Error("Nenosiri lazima liwe na angalau herufi 4.")
            return
        }

        // Clean lookup key without spaces
        val lookupKey = normalized.replace(" ", "")
        val foundUser = devUserDirectory[lookupKey]

        if (foundUser != null) {
            // ADMIN REJECTION RULE (Mandate 7 & 1)
            if (foundUser.role == UserRole.ADMIN) {
                _authResult.value = AuthResult.AdminNotSupportedOnMobile
                _authState.value = AppAuthState.ADMIN_BLOCKED
                return
            }

            // Validate active mode against current capabilities
            var safeUser = foundUser
            if (!safeUser.availableModes.contains(safeUser.activeMode)) {
                safeUser = safeUser.copy(activeMode = UserRole.CUSTOMER)
                devUserDirectory[lookupKey] = safeUser
            }

            _currentUser.value = safeUser
            _authResult.value = AuthResult.Success(safeUser)
            _authState.value = AppAuthState.AUTHENTICATED
        } else {
            // If user enters a valid format not yet in mock directory, register them as default customer
            val newUser = UserSession(
                id = "USR-${UUID.randomUUID().toString().take(6).uppercase()}",
                name = "Mtumiaji DoP",
                email = "user@dalalionpocket.tz",
                phone = normalized,
                accountStatus = AccountStatus.ACTIVE,
                isCustomerActive = true,
                ownerCapability = OwnerCapabilityStatus.NOT_STARTED,
                guideCapability = GuideCapabilityStatus.NOT_APPLIED,
                activeMode = UserRole.CUSTOMER,
                isVerified = true
            )
            devUserDirectory[lookupKey] = newUser
            _currentUser.value = newUser
            _authResult.value = AuthResult.Success(newUser)
            _authState.value = AppAuthState.AUTHENTICATED
        }
    }

    /**
     * Default customer registration: normal user registers naturally as Customer.
     * No role picker or decision between Owner/Guide is forced upon them.
     */
    fun registerCustomer(
        fullName: String,
        phoneInput: String,
        emailInput: String,
        passwordInput: String,
        confirmPasswordInput: String,
        acceptedTerms: Boolean
    ) {
        register(
            role = UserRole.CUSTOMER,
            fullName = fullName,
            phoneInput = phoneInput,
            emailInput = emailInput,
            passwordInput = passwordInput,
            confirmPasswordInput = confirmPasswordInput,
            acceptedTerms = acceptedTerms
        )
    }

    /**
     * Register new mobile user (defaults to CUSTOMER capability)
     */
    fun register(
        role: UserRole = UserRole.CUSTOMER,
        fullName: String,
        phoneInput: String,
        emailInput: String,
        passwordInput: String,
        confirmPasswordInput: String,
        acceptedTerms: Boolean
    ) {
        if (!acceptedTerms) {
            _authResult.value = AuthResult.Error("Lazima ukubali vigezo na masharti ya Dalalion Pocket ili kuendelea.")
            return
        }

        if (role == UserRole.ADMIN) {
            _authResult.value = AuthResult.Error("Akaunti za Usimamizi (Admin) haziwezi kuundwa kwenye mfumo wa simu.")
            return
        }

        if (fullName.trim().length < 3) {
            _authResult.value = AuthResult.Error("Tafadhali ingiza jina lako kamili.")
            return
        }

        val normalized = normalizeTanzaniaPhone(phoneInput)
        if (normalized.length < 10) {
            _authResult.value = AuthResult.Error("Namba ya simu ya Tanzania si sahihi.")
            return
        }

        if (passwordInput.length < 4) {
            _authResult.value = AuthResult.Error("Nenosiri liwe na angalau herufi au tarakimu 4.")
            return
        }

        if (passwordInput != confirmPasswordInput) {
            _authResult.value = AuthResult.Error("Nenosiri na uthibitisho wa nenosiri havilingani.")
            return
        }

        val newUser = UserSession(
            id = "USR-${UUID.randomUUID().toString().take(6).uppercase()}",
            name = fullName.trim(),
            email = emailInput.trim().ifBlank { "user@dalalionpocket.tz" },
            phone = normalized,
            accountStatus = AccountStatus.ACTIVE,
            isCustomerActive = true,
            ownerCapability = if (role == UserRole.OWNER) OwnerCapabilityStatus.ACTIVE else OwnerCapabilityStatus.NOT_STARTED,
            guideCapability = GuideCapabilityStatus.NOT_APPLIED,
            activeMode = UserRole.CUSTOMER, // ALWAYS enters into Customer Home
            ownerProfile = if (role == UserRole.OWNER) OwnerProfile(displayName = fullName.trim(), contactPhone = normalized) else null,
            isVerified = true
        )

        val key = normalized.replace(" ", "")
        devUserDirectory[key] = newUser
        _currentUser.value = newUser
        _authResult.value = AuthResult.Success(newUser)
        _authState.value = AppAuthState.AUTHENTICATED
    }

    /**
     * Activates OWNER capability for the CURRENT user session.
     * Preserves the same account (no logout, no new account).
     */
    

    /**
     * Switch role among mobile supported roles only (CUSTOMER, OWNER, GUIDE)
     */
        fun logout() {
        _currentUser.value = null
        _authResult.value = AuthResult.Idle
        _authState.value = AppAuthState.LOGIN
        
        // Reset devUserDirectory so tests don't leak state
        devUserDirectory.clear()
        starterAccounts.forEach { acc ->
            devUserDirectory[acc.rawPhone] = UserSession(
                id = acc.id,
                name = acc.name,
                email = "${acc.username}@dalalionpocket.tz",
                phone = acc.phone,
                role = acc.role,
                isVerified = true
            )
        }
    }

    /**
     * Clear Admin Blocked screen and return to Login
     */
    fun dismissAdminBlockAndReturnToLogin() {
        _currentUser.value = null
        _authResult.value = AuthResult.Idle
        _authState.value = AppAuthState.LOGIN
    }

    // ============================================================
    // HIDDEN ADMIN ACCESS ENTRY MECHANISM (14-TAP SEQUENCE)
    // ============================================================
    val adminTapDetector = AdminTapDetector(
        targetTaps = 14,
        resetTimeoutMs = 2500L,
        onAdminTrigger = {
            navigateToAdminLogin()
        }
    )

    fun navigateToAdminLogin() {
        _adminAuthError.value = null
        _authState.value = AppAuthState.ADMIN_ACCESS
    }

    /**
     * Records a tap on the hidden trigger area (DoP logo on Login screen).
     * Strictly requires 14 consecutive taps within sliding window of 2.5s.
     * Returns true ONLY on the 14th tap when gate unlocks.
     * Does NOT reveal tap counts or admin status prematurely.
     */
    fun onSecretGestureTap(currentTimeMs: Long = System.currentTimeMillis()): Boolean {
        return adminTapDetector.registerTap(currentTimeMs)
    }

    fun resetSecretGesture() {
        adminTapDetector.reset()
    }

    // ============================================================
    // ADMIN AUTHENTICATION & SECURE WEB HANDOFF
    // ============================================================
    var adminAuthRepository: AdminAuthRepository = DevelopmentAdminAuthRepository()
    var adminWebHandoffService: AdminWebHandoffService = DefaultAdminWebHandoffService()

    private val _adminHandoffSession = MutableStateFlow<AdminHandoffSession?>(null)
    val adminHandoffSession: StateFlow<AdminHandoffSession?> = _adminHandoffSession.asStateFlow()

    private val _adminAuthError = MutableStateFlow<String?>(null)
    val adminAuthError: StateFlow<String?> = _adminAuthError.asStateFlow()

    fun isLockedOut(currentTimeMs: Long = System.currentTimeMillis()): Boolean =
        adminAuthRepository.isLockedOut(currentTimeMs)

    fun getLockoutSecondsRemaining(currentTimeMs: Long = System.currentTimeMillis()): Long =
        adminAuthRepository.getLockoutSecondsRemaining(currentTimeMs)

    /**
     * Server-authoritative admin authentication.
     * Rejects normal roles (CUSTOMER, OWNER, GUIDE) and non-admins with generic error.
     * Never reveals user enumeration or admin existence.
     * On success: generates a single-use 60s authorization code for external web delegation.
     */
    fun authenticateAdmin(
        identifier: String,
        passwordInput: String,
        currentTimeMs: Long = System.currentTimeMillis()
    ): Boolean = runBlocking {
        when (val result = adminAuthRepository.authenticateAdmin(identifier, passwordInput, currentTimeMs)) {
            is AdminAuthResult.Success -> {
                _adminAuthError.value = null
                val session = adminWebHandoffService.createAdminHandoff(result, currentTimeMs)
                _adminHandoffSession.value = session
                // Never put Admin in mobile session _currentUser!
                _authState.value = AppAuthState.ADMIN_HANDOFF
                true
            }
            is AdminAuthResult.Failure -> {
                _adminAuthError.value = result.message
                false
            }
            is AdminAuthResult.NetworkError -> {
                _adminAuthError.value = "Hitilafu ya mtandao wakati wa kuwasiliana na seva ya usimamizi."
                false
            }
        }
    }

    fun exitAdminAccess() {
        _adminAuthError.value = null
        resetSecretGesture()
        _authState.value = AppAuthState.LOGIN
    }

    fun exitAdminHandoff() {
        _adminHandoffSession.value = null
        _adminAuthError.value = null
        resetSecretGesture()
        _authState.value = AppAuthState.LOGIN
    }

    fun updateCurrentUser(transform: (UserSession) -> UserSession): Boolean {
        val current = _currentUser.value ?: return false
        val updated = transform(current)
        _currentUser.value = updated
        val key = updated.phone.replace(" ", "")
        devUserDirectory[key] = updated
        return true
    }
}
