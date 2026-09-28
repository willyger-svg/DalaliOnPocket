import sys

with open("app/src/main/java/com/example/core/auth/AuthManager.kt", "r") as f:
    content = f.read()

target = """            _currentUser.value = foundUser
            _authResult.value = AuthResult.Success(foundUser)
            _authState.value = AppAuthState.AUTHENTICATED"""

replacement = """            // Validate active mode against current capabilities
            var safeUser = foundUser
            if (!safeUser.availableModes.contains(safeUser.activeMode)) {
                safeUser = safeUser.copy(activeMode = UserRole.CUSTOMER)
                devUserDirectory[lookupKey] = safeUser
            }

            _currentUser.value = safeUser
            _authResult.value = AuthResult.Success(safeUser)
            _authState.value = AppAuthState.AUTHENTICATED"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/core/auth/AuthManager.kt", "w") as f:
    f.write(content)

print("Patched AuthManager login validation")
