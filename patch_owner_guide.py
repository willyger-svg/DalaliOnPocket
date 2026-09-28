import sys

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

target = """                        UserRole.OWNER -> {
                            OwnerDashboardScreen(
                                lang = appLanguage,
                                onOpenWizard = { currentScreen = Screen.OWNER_WIZARD },
                                onViewAgreement = { agr ->
                                    selectedAgreement = agr
                                    currentScreen = Screen.TENANCY_AGREEMENT
                                }
                            )
                        }
                        UserRole.GUIDE -> {
                            GuideDashboardScreen(
                                lang = appLanguage,
                                onOpenChat = { booking ->
                                    chatBookingCode = booking.bookingCode
                                    currentScreen = Screen.GUIDE_CHAT
                                }
                            )
                        }"""

replacement = """                        UserRole.OWNER -> {
                            when (currentOwnerTab) {
                                OwnerTab.DASHBOARD, OwnerTab.PROPERTIES, OwnerTab.VIEWINGS, OwnerTab.ADD_PROPERTY -> {
                                    OwnerDashboardScreen(
                                        lang = appLanguage,
                                        onOpenWizard = { currentScreen = Screen.OWNER_WIZARD },
                                        onViewAgreement = { agr ->
                                            selectedAgreement = agr
                                            currentScreen = Screen.TENANCY_AGREEMENT
                                        }
                                    )
                                }
                                OwnerTab.ACCOUNT -> {
                                    com.example.ui.screens.UnifiedAccountCenterScreen(
                                        lang = appLanguage,
                                        onNavigateToEditProfile = { currentScreen = Screen.EDIT_PROFILE },
                                        onNavigateToAccountRoles = { currentScreen = Screen.ACCOUNT_ROLES },
                                        onNavigateToSettings = {}
                                    )
                                }
                            }
                        }
                        UserRole.GUIDE -> {
                            when (currentGuideTab) {
                                GuideTab.DASHBOARD, GuideTab.JOBS, GuideTab.SCHEDULE, GuideTab.EARNINGS -> {
                                    GuideDashboardScreen(
                                        lang = appLanguage,
                                        onOpenChat = { booking ->
                                            chatBookingCode = booking.bookingCode
                                            currentScreen = Screen.GUIDE_CHAT
                                        }
                                    )
                                }
                                GuideTab.ACCOUNT -> {
                                    com.example.ui.screens.UnifiedAccountCenterScreen(
                                        lang = appLanguage,
                                        onNavigateToEditProfile = { currentScreen = Screen.EDIT_PROFILE },
                                        onNavigateToAccountRoles = { currentScreen = Screen.ACCOUNT_ROLES },
                                        onNavigateToSettings = {}
                                    )
                                }
                            }
                        }"""

if target in content:
    content = content.replace(target, replacement, 1)
    with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
        f.write(content)
    print("Patched owner guide successfully")
else:
    print("Target not found")
