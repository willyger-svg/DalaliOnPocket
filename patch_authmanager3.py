import sys

with open("app/src/main/java/com/example/core/auth/AuthManager.kt", "r") as f:
    content = f.read()

# I see the problem. devUserDirectory lookup might be caching old values between tests.
# And AuthManager.logout() just sets _currentUser = null but doesn't clear devUserDirectory.
# That's why when it looks up "0711122233" again in the second test, it retrieves the user that already had Owner activated from the first test.
# Let's fix logout to clear devUserDirectory for tests, OR just register fresh mock users in logout.

replacement = """    fun logout() {
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
    }"""

import re
content = re.sub(r'fun logout\(\) \{[\s\S]*?\}', replacement, content, count=1)

with open("app/src/main/java/com/example/core/auth/AuthManager.kt", "w") as f:
    f.write(content)
print("AuthManager patched logout")
