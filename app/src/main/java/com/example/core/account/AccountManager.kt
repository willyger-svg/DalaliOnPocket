package com.example.core.account

import com.example.core.auth.AuthManager
import com.example.data.model.*
import kotlinx.coroutines.flow.StateFlow

/**
 * Manages user capabilities (Customer, Owner, Guide) and handles switching between active session modes.
 * Ensures UI state remains consistent with authorized capabilities.
 */
object AccountManager {

    val currentUser: StateFlow<UserSession?> = AuthManager.currentUser

    val activeMode: UserRole?
        get() = currentUser.value?.activeMode

    /**
     * Switch active mode among the capabilities the current user actually holds.
     * Prevents switching to Admin or unauthorized capabilities.
     */
    fun switchActiveMode(newMode: UserRole): Boolean {
        if (newMode == UserRole.ADMIN) {
            return false
        }
        val current = currentUser.value ?: return false
        if (!current.availableModes.contains(newMode)) {
            return false
        }
        return AuthManager.updateCurrentUser { it.copy(activeMode = newMode) }
    }

    /**
     * Switch role among mobile supported roles only (CUSTOMER, OWNER, GUIDE)
     */
    fun switchMobileRole(newRole: UserRole): Boolean {
        if (newRole == UserRole.ADMIN) return false
        val current = currentUser.value ?: return false
        return AuthManager.updateCurrentUser { user ->
            val updated = when (newRole) {
                UserRole.OWNER -> if (user.ownerCapability != OwnerCapabilityStatus.ACTIVE) user.copy(ownerCapability = OwnerCapabilityStatus.ACTIVE) else user
                UserRole.GUIDE -> if (user.guideCapability != GuideCapabilityStatus.APPROVED) user.copy(guideCapability = GuideCapabilityStatus.APPROVED) else user
                else -> user
            }
            updated.copy(role = newRole, activeMode = newRole)
        }
    }

    fun activateOwnerCapability(profile: OwnerProfile): Boolean {
        return AuthManager.updateCurrentUser {
            it.copy(
                ownerCapability = OwnerCapabilityStatus.ACTIVE,
                ownerProfile = profile,
                activeMode = UserRole.OWNER
            )
        }
    }

    fun submitGuideApplication(application: GuideApplication): Boolean {
        return AuthManager.updateCurrentUser {
            it.copy(
                guideCapability = GuideCapabilityStatus.SUBMITTED,
                guideApplication = application
            )
        }
    }

    fun adminApproveGuide(phoneOrId: String? = null): Boolean {
        return AuthManager.updateCurrentUser { current ->
            current.copy(
                guideCapability = GuideCapabilityStatus.APPROVED,
                guideApplication = current.guideApplication?.copy(status = GuideCapabilityStatus.APPROVED)
                    ?: GuideApplication(
                        fullName = current.name,
                        phone = current.phone,
                        status = GuideCapabilityStatus.APPROVED
                    ),
                guideProfile = GuideProfile()
            )
        }
    }

    fun adminSuspendGuide(phoneOrId: String? = null): Boolean {
        return AuthManager.updateCurrentUser { current ->
            current.copy(
                guideCapability = GuideCapabilityStatus.SUSPENDED,
                activeMode = if (current.activeMode == UserRole.GUIDE) UserRole.CUSTOMER else current.activeMode,
                guideApplication = current.guideApplication?.copy(status = GuideCapabilityStatus.SUSPENDED)
            )
        }
    }

    fun adminRejectGuide(reason: String = "Taarifa hazijakamilika"): Boolean {
        return AuthManager.updateCurrentUser { current ->
            current.copy(
                guideCapability = GuideCapabilityStatus.REJECTED,
                activeMode = if (current.activeMode == UserRole.GUIDE) UserRole.CUSTOMER else current.activeMode,
                guideApplication = current.guideApplication?.copy(status = GuideCapabilityStatus.REJECTED, reviewNotes = reason)
            )
        }
    }
}
