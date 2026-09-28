import sys

with open("app/src/main/java/com/example/core/auth/AuthManager.kt", "r") as f:
    content = f.read()

# Let's fix AuthManager.login in AuthManager.kt because we stripped switchMobileRole!
# We'll just put switchRole back since login uses it
target = """    fun switchRole(role: UserRole) = com.example.core.account.AccountManager.switchMobileRole(role)"""

if target in content:
    # All good
    print("switchRole is present")
else:
    print("switchRole missing!")

