import sys

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

target = """                                CustomerTab.ACCOUNT -> {
                                    CustomerAccountScreen(
                                        lang = appLanguage,
                                        onLanguageChange = onLanguageChange,
                                        onRoleChange = { role -> AuthManager.switchActiveMode(role) },
                                        onViewAgreements = {
                                            selectedAgreement = PropertyRepository.tenancyAgreements.value.firstOrNull()
                                            if (selectedAgreement != null) {
                                                currentScreen = Screen.TENANCY_AGREEMENT
                                            }
                                        }
                                    )
                                }"""

replacement = """                                CustomerTab.ACCOUNT -> {
                                    com.example.ui.screens.UnifiedAccountCenterScreen(
                                        lang = appLanguage,
                                        onNavigateToEditProfile = { currentScreen = Screen.EDIT_PROFILE },
                                        onNavigateToAccountRoles = { currentScreen = Screen.ACCOUNT_ROLES },
                                        onNavigateToSettings = {}
                                    )
                                }"""

if target in content:
    content = content.replace(target, replacement, 1)
    with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
        f.write(content)
    print("Patched CustomerAccountScreen successfully")
else:
    print("Target not found")
