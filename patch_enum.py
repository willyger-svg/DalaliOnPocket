import sys

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

target = """enum class Screen {
    MAIN_TABS,
    PROPERTY_DETAILS,"""

replacement = """enum class Screen {
    MAIN_TABS,
    PROPERTY_DETAILS,
    EDIT_PROFILE,
    ACCOUNT_ROLES,"""

if target in content:
    content = content.replace(target, replacement, 1)
    with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
        f.write(content)
    print("Patched enum successfully")
else:
    print("Target not found")
