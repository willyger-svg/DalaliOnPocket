import sys

def patch_file(filename):
    with open(filename, "r") as f:
        content = f.read()

    # Add AccountManager import if not there
    if "com.example.core.account.AccountManager" not in content:
        content = content.replace("import com.example.core.auth.AuthManager", "import com.example.core.auth.AuthManager\nimport com.example.core.account.AccountManager")

    with open(filename, "w") as f:
        f.write(content)

patch_file("app/src/test/java/com/example/AccountAndRolesTest.kt")
patch_file("app/src/test/java/com/example/GuideDispatchTest.kt")
patch_file("app/src/test/java/com/example/PropertyStudioTest.kt")
