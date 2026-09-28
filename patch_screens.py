import sys

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

target = """                Screen.GUIDE_CHAT -> {
                    GuideCustomerChatScreen(
                        lang = appLanguage,
                        bookingCode = chatBookingCode,
                        onBack = { currentScreen = Screen.MAIN_TABS }
                    )
                }"""

replacement = """                Screen.GUIDE_CHAT -> {
                    GuideCustomerChatScreen(
                        lang = appLanguage,
                        bookingCode = chatBookingCode,
                        onBack = { currentScreen = Screen.MAIN_TABS }
                    )
                }
                
                Screen.EDIT_PROFILE -> {
                    com.example.ui.screens.EditProfileScreen(
                        onBack = { currentScreen = Screen.MAIN_TABS }
                    )
                }
                
                Screen.ACCOUNT_ROLES -> {
                    com.example.ui.screens.AccountAndRolesScreen(
                        onBack = { currentScreen = Screen.MAIN_TABS },
                        onOpenOwnerOnboarding = { showOwnerOnboarding = true },
                        onOpenGuideApplication = { showGuideApplication = true }
                    )
                }"""

if target in content:
    content = content.replace(target, replacement, 1)
    with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
        f.write(content)
    print("Patched screens successfully")
else:
    print("Target not found")
