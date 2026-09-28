package com.example

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.core.auth.AuthManager
import com.example.data.model.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountAndRolesTest {

    @Before
    fun setup() {
        AuthManager.logout()
    }

    @Test
    fun `New user is Customer only`() {
        AuthManager.login("0711122233", "pass")
        val user = AuthManager.currentUser.value
        assertNotNull(user)
        assertEquals(UserRole.CUSTOMER, user?.activeMode)
        assertFalse(user?.canAccessOwner == true)
        assertEquals(GuideCapabilityStatus.NOT_APPLIED, user?.guideCapability)
    }

    @Test
    fun `Customer can activate Owner capability and switch back`() {
        AuthManager.login("0711122233", "pass")
        com.example.core.account.AccountManager.activateOwnerCapability(OwnerProfile(displayName = "Test Owner", businessName = "Test Business"))
        var user = AuthManager.currentUser.value
        assertTrue(user?.canAccessOwner == true)
        assertEquals(UserRole.OWNER, user?.activeMode)
        
        com.example.core.account.AccountManager.switchActiveMode(UserRole.CUSTOMER)
        user = AuthManager.currentUser.value
        assertEquals(UserRole.CUSTOMER, user?.activeMode)
        assertTrue(user?.canAccessOwner == true)
    }

    @Test
    fun `Unapproved Guide cannot switch to Guide`() {
        AuthManager.login("0711122233", "pass")
        val app = GuideApplication("Test Guide", "0711122233")
        com.example.core.account.AccountManager.submitGuideApplication(app)
        
        // At this point status is SUBMITTED, not APPROVED
        val success = com.example.core.account.AccountManager.switchActiveMode(UserRole.GUIDE)
        assertFalse(success)
        
        val user = AuthManager.currentUser.value
        assertEquals(UserRole.CUSTOMER, user?.activeMode)
    }
    
    @Test
    fun `Approved Guide can activate Guide capability`() {
        AuthManager.login("0711122233", "pass")
        val app = GuideApplication("Test Guide", "0711122233")
        com.example.core.account.AccountManager.submitGuideApplication(app)
        
        val userAfterSubmit = AuthManager.currentUser.value
        assertNotNull(userAfterSubmit)
        com.example.core.account.AccountManager.adminApproveGuide(userAfterSubmit!!.id)
        
        val success = com.example.core.account.AccountManager.switchActiveMode(UserRole.GUIDE)
        assertTrue(success)
        
        val user = AuthManager.currentUser.value
        assertEquals(UserRole.GUIDE, user?.activeMode)
    }
    
    @Test
    fun `Suspended Guide cannot switch to Guide`() {
        AuthManager.login("0711122233", "pass")
        val app = GuideApplication("Test Guide", "0711122233")
        com.example.core.account.AccountManager.submitGuideApplication(app)
        
        val userAfterSubmit = AuthManager.currentUser.value
        com.example.core.account.AccountManager.adminApproveGuide(userAfterSubmit!!.id)
        
        // Manually suspend for test
        com.example.core.account.AccountManager.adminSuspendGuide(userAfterSubmit.id)
        
        val success = com.example.core.account.AccountManager.switchActiveMode(UserRole.GUIDE)
        assertFalse(success)
        assertEquals(UserRole.CUSTOMER, AuthManager.currentUser.value?.activeMode)
    }

    @Test
    fun `Customer Owner Guide all work on one account without duplicating`() {
        AuthManager.login("0711122233", "pass")
        val userId = AuthManager.currentUser.value?.id
        
        com.example.core.account.AccountManager.activateOwnerCapability(OwnerProfile(displayName = "Test Owner", businessName = "Test Business"))
        val app = GuideApplication("Test Guide", "0711122233")
        com.example.core.account.AccountManager.submitGuideApplication(app)
        com.example.core.account.AccountManager.adminApproveGuide(userId!!)
        
        val user = AuthManager.currentUser.value
        assertEquals(userId, user?.id)
        assertTrue(user?.canAccessCustomer == true)
        assertTrue(user?.canAccessOwner == true)
        assertTrue(user?.canAccessGuide == true)
        
        assertEquals(3, user?.availableModes?.size)
    }
}
