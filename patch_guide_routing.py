import sys

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

target = """                        UserRole.GUIDE -> {
                            when (currentGuideTab) {
                                GuideTab.DASHBOARD, GuideTab.JOBS, GuideTab.SCHEDULE, GuideTab.EARNINGS -> {
                                    GuideDashboardScreen(
                                        lang = appLanguage,
                                        onOpenChat = { booking ->
                                            chatBookingCode = booking.bookingCode
                                            currentScreen = Screen.GUIDE_CHAT
                                        }
                                    )
                                }"""

replacement = """                        UserRole.GUIDE -> {
                            when (currentGuideTab) {
                                GuideTab.DASHBOARD -> {
                                    com.example.ui.screens.GuideMainDashboardScreen(
                                        lang = appLanguage,
                                        onOpenChat = { booking ->
                                            chatBookingCode = booking.bookingCode
                                            currentScreen = Screen.GUIDE_CHAT
                                        }
                                    )
                                }
                                GuideTab.JOBS -> {
                                    com.example.ui.screens.GuideJobsScreen(
                                        lang = appLanguage,
                                        onOpenChat = { booking ->
                                            chatBookingCode = booking.bookingCode
                                            currentScreen = Screen.GUIDE_CHAT
                                        }
                                    )
                                }
                                GuideTab.SCHEDULE -> {
                                    // Placeholder for Schedule
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Schedule Coming Soon", color = DopTextSecondary)
                                    }
                                }
                                GuideTab.EARNINGS -> {
                                    com.example.ui.screens.GuideEarningsScreen()
                                }"""

if target in content:
    content = content.replace(target, replacement, 1)
    with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
        f.write(content)
    print("Patched Guide routing successfully")
else:
    print("Target not found")
