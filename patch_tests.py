import sys

with open("app/src/test/java/com/example/AccountAndRolesTest.kt", "r") as f:
    content = f.read()

# Fix IllegalAccessException: _currentUser is private val, use updateCurrentUser instead
target_suspend = """        // Manually suspend for test
        val suspendedUser = AuthManager.currentUser.value!!.copy(guideCapability = GuideCapabilityStatus.SUSPENDED)
        AuthManager.javaClass.getDeclaredField("_currentUser").apply { isAccessible = true }.set(AuthManager, kotlinx.coroutines.flow.MutableStateFlow(suspendedUser))"""

replacement_suspend = """        // Manually suspend for test
        com.example.core.account.AccountManager.adminSuspendGuide(userAfterSubmit.id)"""

if target_suspend in content:
    content = content.replace(target_suspend, replacement_suspend)
    with open("app/src/test/java/com/example/AccountAndRolesTest.kt", "w") as f:
        f.write(content)
    print("Patched AccountAndRolesTest.kt")
else:
    print("Could not find suspend code to patch")
