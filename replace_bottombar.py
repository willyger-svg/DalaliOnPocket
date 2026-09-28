import sys

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

start_str = "        bottomBar = {\n            if (currentScreen == Screen.MAIN_TABS && currentUser.activeMode == UserRole.CUSTOMER) {\n                NavigationBar("
end_str = "                        modifier = Modifier.testTag(\"tab_account\")\n                    )\n                }\n            }\n        }\n    ) { padding ->"

new_bottombar = """        bottomBar = {
            if (currentScreen == Screen.MAIN_TABS) {
                when (currentUser.activeMode) {
                    UserRole.CUSTOMER -> NavigationBar(
                        containerColor = DopSurfaceCard,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = currentTab == CustomerTab.HOME,
                            onClick = { currentTab = CustomerTab.HOME },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text(DoPStrings.navHome(appLanguage), fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DopNavyPrimary,
                                selectedTextColor = DopNavyPrimary,
                                indicatorColor = DopOchreContainer
                            ),
                            modifier = Modifier.testTag("tab_home")
                        )

                        NavigationBarItem(
                            selected = currentTab == CustomerTab.SHORTS,
                            onClick = { currentTab = CustomerTab.SHORTS },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = Color(0xFFE53935)) {
                                            Text("🔥", fontSize = 8.sp)
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.PlayCircle, contentDescription = "Shorts")
                                }
                            },
                            label = { Text("Shorts", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFE53935),
                                selectedTextColor = Color(0xFFE53935),
                                indicatorColor = Color(0xFFFFCDD2)
                            ),
                            modifier = Modifier.testTag("tab_shorts")
                        )

                        NavigationBarItem(
                            selected = currentTab == CustomerTab.EXPLORE,
                            onClick = { currentTab = CustomerTab.EXPLORE },
                            icon = { Icon(Icons.Default.Explore, contentDescription = "Explore") },
                            label = { Text(DoPStrings.navExplore(appLanguage), fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DopNavyPrimary,
                                selectedTextColor = DopNavyPrimary,
                                indicatorColor = DopOchreContainer
                            ),
                            modifier = Modifier.testTag("tab_explore")
                        )

                        NavigationBarItem(
                            selected = currentTab == CustomerTab.SAVED,
                            onClick = { currentTab = CustomerTab.SAVED },
                            icon = { Icon(Icons.Default.Favorite, contentDescription = "Saved") },
                            label = { Text(DoPStrings.navSaved(appLanguage), fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DopNavyPrimary,
                                selectedTextColor = DopNavyPrimary,
                                indicatorColor = DopOchreContainer
                            ),
                            modifier = Modifier.testTag("tab_saved")
                        )

                        NavigationBarItem(
                            selected = currentTab == CustomerTab.VISITS,
                            onClick = { currentTab = CustomerTab.VISITS },
                            icon = { Icon(Icons.AutoMirrored.Filled.DirectionsWalk, contentDescription = "Visits") },
                            label = { Text(DoPStrings.navVisits(appLanguage), fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DopNavyPrimary,
                                selectedTextColor = DopNavyPrimary,
                                indicatorColor = DopOchreContainer
                            ),
                            modifier = Modifier.testTag("tab_visits")
                        )

                        NavigationBarItem(
                            selected = currentTab == CustomerTab.ACCOUNT,
                            onClick = { currentTab = CustomerTab.ACCOUNT },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
                            label = { Text(DoPStrings.navAccount(appLanguage), fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DopNavyPrimary,
                                selectedTextColor = DopNavyPrimary,
                                indicatorColor = DopOchreContainer
                            ),
                            modifier = Modifier.testTag("tab_account")
                        )
                    }
                    UserRole.OWNER -> NavigationBar(
                        containerColor = DopSurfaceCard,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = currentOwnerTab == OwnerTab.DASHBOARD,
                            onClick = { currentOwnerTab = OwnerTab.DASHBOARD },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                            label = { Text("Dashboard", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DopTrustGreen,
                                selectedTextColor = DopTrustGreen,
                                indicatorColor = DopTrustGreenContainer
                            )
                        )
                        NavigationBarItem(
                            selected = currentOwnerTab == OwnerTab.PROPERTIES,
                            onClick = { currentOwnerTab = OwnerTab.PROPERTIES },
                            icon = { Icon(Icons.Default.HomeWork, contentDescription = "Properties") },
                            label = { Text("Mali", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DopTrustGreen,
                                selectedTextColor = DopTrustGreen,
                                indicatorColor = DopTrustGreenContainer
                            )
                        )
                        NavigationBarItem(
                            selected = currentOwnerTab == OwnerTab.ADD_PROPERTY,
                            onClick = { currentOwnerTab = OwnerTab.ADD_PROPERTY },
                            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Add") },
                            label = { Text("Weka", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DopTrustGreen,
                                selectedTextColor = DopTrustGreen,
                                indicatorColor = DopTrustGreenContainer
                            )
                        )
                        NavigationBarItem(
                            selected = currentOwnerTab == OwnerTab.VIEWINGS,
                            onClick = { currentOwnerTab = OwnerTab.VIEWINGS },
                            icon = { Icon(Icons.Default.Visibility, contentDescription = "Viewings") },
                            label = { Text("Ziara", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DopTrustGreen,
                                selectedTextColor = DopTrustGreen,
                                indicatorColor = DopTrustGreenContainer
                            )
                        )
                        NavigationBarItem(
                            selected = currentOwnerTab == OwnerTab.ACCOUNT,
                            onClick = { currentOwnerTab = OwnerTab.ACCOUNT },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
                            label = { Text("Akaunti", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DopTrustGreen,
                                selectedTextColor = DopTrustGreen,
                                indicatorColor = DopTrustGreenContainer
                            )
                        )
                    }
                    UserRole.GUIDE -> NavigationBar(
                        containerColor = DopSurfaceCard,
                        tonalElevation = 8.dp
                    ) {
                        NavigationBarItem(
                            selected = currentGuideTab == GuideTab.DASHBOARD,
                            onClick = { currentGuideTab = GuideTab.DASHBOARD },
                            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                            label = { Text("Dashboard", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DopGuideAmberDark,
                                selectedTextColor = DopGuideAmberDark,
                                indicatorColor = DopGuideAmberContainer
                            )
                        )
                        NavigationBarItem(
                            selected = currentGuideTab == GuideTab.JOBS,
                            onClick = { currentGuideTab = GuideTab.JOBS },
                            icon = { Icon(Icons.Default.Work, contentDescription = "Jobs") },
                            label = { Text("Kazi", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DopGuideAmberDark,
                                selectedTextColor = DopGuideAmberDark,
                                indicatorColor = DopGuideAmberContainer
                            )
                        )
                        NavigationBarItem(
                            selected = currentGuideTab == GuideTab.EARNINGS,
                            onClick = { currentGuideTab = GuideTab.EARNINGS },
                            icon = { Icon(Icons.Default.Payments, contentDescription = "Earnings") },
                            label = { Text("Mapato", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DopGuideAmberDark,
                                selectedTextColor = DopGuideAmberDark,
                                indicatorColor = DopGuideAmberContainer
                            )
                        )
                        NavigationBarItem(
                            selected = currentGuideTab == GuideTab.ACCOUNT,
                            onClick = { currentGuideTab = GuideTab.ACCOUNT },
                            icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
                            label = { Text("Akaunti", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DopGuideAmberDark,
                                selectedTextColor = DopGuideAmberDark,
                                indicatorColor = DopGuideAmberContainer
                            )
                        )
                    }
                    else -> {}
                }
            }
        }
    } { padding ->"""

idx_start = content.find(start_str)
idx_end = content.find(end_str)

if idx_start != -1 and idx_end != -1:
    new_content = content[:idx_start] + new_bottombar + content[idx_end + len(end_str):]
    with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
        f.write(new_content)
    print("Replaced successfully")
else:
    print(f"Failed to find start or end string: {idx_start} - {idx_end}")

