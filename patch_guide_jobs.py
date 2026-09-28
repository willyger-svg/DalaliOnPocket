import sys

with open("app/src/main/java/com/example/ui/screens/GuideScreens.kt", "r") as f:
    content = f.read()

target = """        // Guide Header & Wallet Card
        item {
            DopBentoCard(backgroundColor = DopNavyPrimary) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Dashibodi ya DoP Guide",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${user?.name ?: "Mwongoza Ziara"} • Level: $levelName",
                            fontSize = 12.sp,
                            color = DopGuideAmber
                        )
                    }
                    DopBadge("ACTIVE", Icons.Default.DirectionsWalk, DopTrustGreenLight, DopNavyElevated)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Wallet / Earnings Breakdown (TSh 2,500 split)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Mapato Yaliyolipwa", fontSize = 11.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                        Text(DoPStrings.tzs(earnings), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DopTrustGreenLight)
                    }
                    Column {
                        Text("Ada kwa Kila Ziara", fontSize = 11.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                        Text("TSh 2,500", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DopGuideAmber)
                    }
                    Column {
                        Text("M-Pesa Namba", fontSize = 11.sp, color = DopNeutralPearl.copy(alpha = 0.7f))
                        Text(user?.phone ?: "Not set", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color.White)
                    }
                }
            }
        }"""

replacement = """        item {
            Text("Guide Jobs", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DopNavyPrimary)
        }"""

if target in content:
    content = content.replace(target, replacement, 1)
    with open("app/src/main/java/com/example/ui/screens/GuideScreens.kt", "w") as f:
        f.write(content)
    print("Patched GuideJobsScreen successfully")
else:
    print("Target not found")
