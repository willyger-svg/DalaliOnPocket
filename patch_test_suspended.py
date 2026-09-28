import sys

with open("app/src/test/java/com/example/AccountAndRolesTest.kt", "r") as f:
    content = f.read()

target = """    @Test
    fun `Customer Owner Guide all work on one account without duplicating`() {"""

replacement = """    @Test
    fun `Suspended Guide cannot switch to Guide`() {
        AuthManager.login("0711122233", "pass")
        val app = GuideApplication("Test Guide", "0711122233")
        AuthManager.submitGuideApplication(app)
        
        val userAfterSubmit = AuthManager.currentUser.value
        AuthManager.adminApproveGuide(userAfterSubmit!!.id)
        
        // Manually suspend for test
        val suspendedUser = AuthManager.currentUser.value!!.copy(guideCapability = GuideCapabilityStatus.SUSPENDED)
        AuthManager.javaClass.getDeclaredField("_currentUser").apply { isAccessible = true }.set(AuthManager, kotlinx.coroutines.flow.MutableStateFlow(suspendedUser))
        
        val success = AuthManager.switchActiveMode(UserRole.GUIDE)
        assertFalse(success)
        assertEquals(UserRole.CUSTOMER, AuthManager.currentUser.value?.activeMode)
    }

    @Test
    fun `Customer Owner Guide all work on one account without duplicating`() {"""

if target in content:
    content = content.replace(target, replacement, 1)
    with open("app/src/test/java/com/example/AccountAndRolesTest.kt", "w") as f:
        f.write(content)
    print("Patched test with suspended check")
else:
    print("Target not found")
