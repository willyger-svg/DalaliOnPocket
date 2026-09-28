import sys

with open("app/src/test/java/com/example/AccountAndRolesTest.kt", "r") as f:
    content = f.read()

content = content.replace("AuthManager.activateOwnerCapability()", 'AuthManager.activateOwnerCapability(OwnerProfile(fullName = "Test Owner", businessName = "Test Business"))')

with open("app/src/test/java/com/example/AccountAndRolesTest.kt", "w") as f:
    f.write(content)
print("Patched test")
