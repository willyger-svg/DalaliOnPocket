import sys

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

target = """                                GuideTab.SCHEDULE -> {
                                    // Placeholder for Schedule
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Schedule Coming Soon", color = DopTextSecondary)
                                    }
                                }"""

replacement = """                                GuideTab.SCHEDULE -> {
                                    com.example.ui.screens.GuideScheduleScreen(lang = appLanguage)
                                }"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)

print("Patched Guide navigation")
