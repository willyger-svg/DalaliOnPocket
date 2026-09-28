package com.example

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.core.account.AccountManager
import com.example.core.auth.AuthManager
import com.example.data.model.*
import com.example.ui.navigation.CustomerTab
import com.example.ui.navigation.GuideTab
import com.example.ui.navigation.OwnerTab
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CapabilityNavigationTest {

    @Before
    fun setup() {
        AuthManager.logout()
    }

    @Test
    fun `Customer capability has 5 distinct tabs with first-class Shorts`() {
        val tabs = CustomerTab.values().toList()
        assertEquals(5, tabs.size)
        assertEquals(CustomerTab.HOME, tabs[0])
        assertEquals(CustomerTab.EXPLORE, tabs[1])
        assertEquals(CustomerTab.SHORTS, tabs[2])
        assertEquals(CustomerTab.VISITS, tabs[3])
        assertEquals(CustomerTab.ACCOUNT, tabs[4])

        // Verify route uniqueness and first-class status
        val routes = tabs.map { it.route }.toSet()
        assertEquals(5, routes.size)
        assertTrue(routes.contains("customer_shorts"))
    }

    @Test
    fun `Owner capability has 5 distinct tabs specific to property management`() {
        val tabs = OwnerTab.values().toList()
        assertEquals(5, tabs.size)
        assertEquals(OwnerTab.DASHBOARD, tabs[0])
        assertEquals(OwnerTab.PROPERTIES, tabs[1])
        assertEquals(OwnerTab.ADD, tabs[2])
        assertEquals(OwnerTab.VIEWINGS, tabs[3])
        assertEquals(OwnerTab.ACCOUNT, tabs[4])

        // Verify owner-specific routes
        val routes = tabs.map { it.route }.toSet()
        assertEquals(5, routes.size)
        assertTrue(routes.contains("owner_dashboard"))
        assertTrue(routes.contains("owner_properties"))
        assertTrue(routes.contains("owner_add"))
        assertTrue(routes.contains("owner_viewings"))
        assertTrue(routes.contains("owner_account"))

        // Must not contain customer-specific shorts or explore
        assertFalse(routes.contains("customer_shorts"))
        assertFalse(routes.contains("customer_explore"))
    }

    @Test
    fun `Guide capability has 5 distinct tabs specific to field missions and earnings`() {
        val tabs = GuideTab.values().toList()
        assertEquals(5, tabs.size)
        assertEquals(GuideTab.DASHBOARD, tabs[0])
        assertEquals(GuideTab.JOBS, tabs[1])
        assertEquals(GuideTab.SCHEDULE, tabs[2])
        assertEquals(GuideTab.EARNINGS, tabs[3])
        assertEquals(GuideTab.ACCOUNT, tabs[4])

        // Verify guide-specific routes
        val routes = tabs.map { it.route }.toSet()
        assertEquals(5, routes.size)
        assertTrue(routes.contains("guide_dashboard"))
        assertTrue(routes.contains("guide_jobs"))
        assertTrue(routes.contains("guide_schedule"))
        assertTrue(routes.contains("guide_earnings"))
        assertTrue(routes.contains("guide_account"))

        // Must not contain owner or customer specific tabs
        assertFalse(routes.contains("owner_properties"))
        assertFalse(routes.contains("customer_shorts"))
    }

    @Test
    fun `Three workspaces have completely distinct primary navigation destinations`() {
        val customerRoutes = CustomerTab.values().map { it.route }.toSet()
        val ownerRoutes = OwnerTab.values().map { it.route }.toSet()
        val guideRoutes = GuideTab.values().map { it.route }.toSet()

        // None of the workspace routes should overlap except the concept of account which is capability-scoped
        val customerOwnerOverlap = customerRoutes.intersect(ownerRoutes)
        val customerGuideOverlap = customerRoutes.intersect(guideRoutes)
        val ownerGuideOverlap = ownerRoutes.intersect(guideRoutes)

        assertTrue(customerOwnerOverlap.isEmpty())
        assertTrue(customerGuideOverlap.isEmpty())
        assertTrue(ownerGuideOverlap.isEmpty())
    }

    @Test
    fun `Single user account can switch between authorized workspaces seamlessly`() {
        AuthManager.login("0711122233", "pass")
        val initialUser = AuthManager.currentUser.value
        assertNotNull(initialUser)
        assertEquals(UserRole.CUSTOMER, initialUser?.activeMode)

        // Activate Owner
        AccountManager.activateOwnerCapability(OwnerProfile(displayName = "Mama Juma", businessName = "Juma Real Estate"))
        var currentUser = AuthManager.currentUser.value
        assertEquals(UserRole.OWNER, currentUser?.activeMode)
        assertTrue(currentUser?.canAccessOwner == true)

        // Activate Guide (Approved)
        AccountManager.submitGuideApplication(GuideApplication("Juma Guide", "0711122233"))
        AccountManager.adminApproveGuide(currentUser!!.id)
        currentUser = AuthManager.currentUser.value
        assertTrue(currentUser?.canAccessGuide == true)

        // Switch to Guide workspace
        val switchedToGuide = AccountManager.switchActiveMode(UserRole.GUIDE)
        assertTrue(switchedToGuide)
        assertEquals(UserRole.GUIDE, AuthManager.currentUser.value?.activeMode)

        // Switch back to Owner workspace
        val switchedToOwner = AccountManager.switchActiveMode(UserRole.OWNER)
        assertTrue(switchedToOwner)
        assertEquals(UserRole.OWNER, AuthManager.currentUser.value?.activeMode)

        // Switch back to Customer workspace
        val switchedToCustomer = AccountManager.switchActiveMode(UserRole.CUSTOMER)
        assertTrue(switchedToCustomer)
        assertEquals(UserRole.CUSTOMER, AuthManager.currentUser.value?.activeMode)
    }

    @Test
    fun `Unauthorized workspace switch is rejected and preserves current workspace`() {
        AuthManager.login("0799887766", "pass")
        val user = AuthManager.currentUser.value
        assertNotNull(user)
        assertEquals(UserRole.CUSTOMER, user?.activeMode)

        // Cannot switch to Owner without activating
        val ownerSwitch = AccountManager.switchActiveMode(UserRole.OWNER)
        assertFalse(ownerSwitch)
        assertEquals(UserRole.CUSTOMER, AuthManager.currentUser.value?.activeMode)

        // Cannot switch to Guide without approval
        val guideSwitch = AccountManager.switchActiveMode(UserRole.GUIDE)
        assertFalse(guideSwitch)
        assertEquals(UserRole.CUSTOMER, AuthManager.currentUser.value?.activeMode)
    }
}
