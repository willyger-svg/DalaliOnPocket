import sys

with open("app/src/main/java/com/example/ui/screens/GuideScreens.kt", "r") as f:
    content = f.read()

target = """        if (availableJobs.isNotEmpty()) {"""
replacement = """        item {
            Text("Live Virtual Viewings", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
        }
        item {
            DopBentoCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Videocam, contentDescription = null, tint = DopGuideAmberDark)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Live Virtual Viewing", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Service not yet connected", fontSize = 12.sp, color = DopTextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Live Virtual Viewing will be available when the video service is connected.", fontSize = 11.sp, color = DopTextMuted)
            }
        }
        
        if (availableJobs.isNotEmpty()) {"""

if target in content:
    content = content.replace(target, replacement, 1)
    with open("app/src/main/java/com/example/ui/screens/GuideScreens.kt", "w") as f:
        f.write(content)
    print("Patched GuideJobsScreen with WebRTC placeholder")
else:
    print("Target not found")
