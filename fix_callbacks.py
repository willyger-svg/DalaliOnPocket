import sys

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

target = """                Screen.ACCOUNT_ROLES -> {
                    com.example.ui.screens.AccountAndRolesScreen(
                        onBack = { currentScreen = Screen.MAIN_TABS },
                        onOpenOwnerOnboarding = { showOwnerOnboarding = true },
                        onOpenGuideApplication = { showGuideApplication = true }
                    )
                }"""

replacement = """                Screen.ACCOUNT_ROLES -> {
                    com.example.ui.screens.AccountAndRolesScreen(
                        onBack = { currentScreen = Screen.MAIN_TABS },
                        onOpenOwnerOnboarding = { 
                            showOwnerOnboarding = true
                        },
                        onOpenGuideApplication = { 
                            showGuideApplication = true
                        }
                    )
                }"""

if target in content:
    content = content.replace(target, replacement, 1)
    with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
        f.write(content)
    print("Patched callbacks")
