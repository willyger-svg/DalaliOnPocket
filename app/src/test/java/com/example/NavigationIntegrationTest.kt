package com.example

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.core.auth.AuthManager
import com.example.core.market.MarketConfig
import com.example.core.market.MarketService
import com.example.core.market.Region
import com.example.data.model.UserRole
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationIntegrationTest {

    @Before
    fun setup() {
        AuthManager.logout()
    }

    @Test
    fun `Dar es Salaam is the initial active market`() {
        val currentMarket = MarketService.currentMarket.value
        assertEquals(Region.DAR_ES_SALAAM, currentMarket)
        assertTrue(MarketConfig.isMarketActive("Dar es Salaam"))
        assertFalse(MarketConfig.isMarketActive("Mbeya"))
    }

    @Test
    fun `App restart restores valid active capability`() {
        // Mock a user with suspended guide capability logging in
        val phone = "0733445566"
        AuthManager.login(phone, "pass")
        val user = AuthManager.currentUser.value
        assertNotNull(user)
        
        // Let's modify the mock directory manually and login again to simulate restart
        var suspendedUser = AuthManager.currentUser.value!!.copy(
            activeMode = UserRole.GUIDE,
            guideCapability = com.example.data.model.GuideCapabilityStatus.SUSPENDED
        )
        
        val lookupKey = phone.replace(" ", "")
        // Access devUserDirectory via reflection to mock the state
        val devDirField = AuthManager::class.java.getDeclaredField("devUserDirectory")
        devDirField.isAccessible = true
        val devDir = devDirField.get(AuthManager) as MutableMap<String, com.example.data.model.UserSession>
        devDir[lookupKey] = suspendedUser
        
        AuthManager.logout()
        
        // "Restart" - Login again
        AuthManager.login(phone, "pass")
        val loggedInUser = AuthManager.currentUser.value
        
        // Active mode should fallback to CUSTOMER
        assertEquals(UserRole.CUSTOMER, loggedInUser?.activeMode)
    }
}
