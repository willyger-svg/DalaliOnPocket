import sys

with open("app/src/main/java/com/example/ui/screens/GuideScreens.kt", "r") as f:
    content = f.read()

new_screen = """
@Composable
fun GuideScheduleScreen(lang: AppLanguage) {
    val context = LocalContext.current
    var isAvailable by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize().background(DopNeutralPearl).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ratiba (Schedule & Availability)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(20.dp))
        
        DopBentoCard {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Hali ya Upatikanaji", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(if (isAvailable) "Upo tayari kupokea kazi mpya" else "Hupokei kazi kwa sasa", fontSize = 12.sp, color = DopTextSecondary)
                }
                Switch(
                    checked = isAvailable,
                    onCheckedChange = { 
                        isAvailable = it 
                        Toast.makeText(context, if (it) "Sasa upo online!" else "Umejiweka offline.", Toast.LENGTH_SHORT).show()
                    },
                    colors = SwitchDefaults.colors(checkedThumbColor = DopOchre, checkedTrackColor = DopOchreContainer)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Mock upcoming
        Text("Kazi Zijazo (Upcoming)", modifier = Modifier.align(Alignment.Start), fontWeight = FontWeight.Bold, color = DopNavyPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
            Text("Hakuna kazi mpya zilizopangwa kwa sasa.", color = DopTextSecondary)
        }
    }
}
"""

if "fun GuideScheduleScreen" not in content:
    content += new_screen

with open("app/src/main/java/com/example/ui/screens/GuideScreens.kt", "w") as f:
    f.write(content)

print("Patched GuideScreens.kt")
