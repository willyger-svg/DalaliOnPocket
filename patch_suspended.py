import sys

with open("app/src/main/java/com/example/ui/screens/AccountScreens.kt", "r") as f:
    content = f.read()

target = """                } else {
                    // Applied but not approved
                    DopBentoCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = DopTextMuted)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("DoP Guide Application", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Status: ${guideStatus.name}", fontSize = 12.sp, color = DopGuideAmberDark)
                            }
                        }
                    }
                }"""

replacement = """                } else if (guideStatus == GuideCapabilityStatus.SUSPENDED) {
                    DopBentoCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = DopError)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("DoP Guide Suspended", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopError)
                                Text("Your Guide capability is currently suspended. Please contact DoP Support.", fontSize = 12.sp, color = DopTextSecondary)
                            }
                        }
                    }
                } else {
                    // Applied but not approved
                    DopBentoCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DirectionsWalk, contentDescription = null, tint = DopTextMuted)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("DoP Guide Application", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Status: ${guideStatus.name}", fontSize = 12.sp, color = DopGuideAmberDark)
                            }
                        }
                    }
                }"""

if target in content:
    content = content.replace(target, replacement, 1)
    with open("app/src/main/java/com/example/ui/screens/AccountScreens.kt", "w") as f:
        f.write(content)
    print("Patched suspended successfully")
else:
    print("Target not found")
