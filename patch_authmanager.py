import sys

with open("app/src/main/java/com/example/core/auth/AuthManager.kt", "r") as f:
    content = f.read()

# Add updateCurrentUser
update_func = """
    fun updateCurrentUser(transform: (UserSession) -> UserSession): Boolean {
        val current = _currentUser.value ?: return false
        val updated = transform(current)
        _currentUser.value = updated
        val key = updated.phone.replace(" ", "")
        devUserDirectory[key] = updated
        return true
    }
"""

# We'll just append it to the object
idx = content.rfind("}")
if idx != -1:
    content = content[:idx] + update_func + content[idx:]

# Remove old capability methods using regex or simple slicing
import re
content = re.sub(r'fun activateOwnerCapability[\s\S]*?fun switchActiveMode\([^)]*\)\s*:\s*Boolean\s*\{[\s\S]*?return true\n    \}', '', content)
content = re.sub(r'fun switchMobileRole[\s\S]*?fun logout\(\)', 'fun logout()', content)
content = re.sub(r'fun switchRole\(role: UserRole\) = switchMobileRole\(role\)', 'fun switchRole(role: UserRole) = com.example.core.account.AccountManager.switchMobileRole(role)', content)

with open("app/src/main/java/com/example/core/auth/AuthManager.kt", "w") as f:
    f.write(content)
print("AuthManager patched")
