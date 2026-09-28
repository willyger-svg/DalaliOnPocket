#!/bin/bash
cat << 'INNER_EOF' > bottombar.patch
--- app/src/main/java/com/example/MainActivity.kt
+++ app/src/main/java/com/example/MainActivity.kt
@@ -308,8 +308,8 @@
             }
         },
         bottomBar = {
-            if (currentScreen == Screen.MAIN_TABS && currentUser.activeMode == UserRole.CUSTOMER) {
-                NavigationBar(
+            if (currentScreen == Screen.MAIN_TABS) {
+                when (currentUser.activeMode) {
+                    UserRole.CUSTOMER -> NavigationBar(
                     containerColor = DopSurfaceCard,
                     tonalElevation = 8.dp
                 ) {
@@ -402,6 +402,96 @@
                     )
                 }
+                UserRole.OWNER -> NavigationBar(
+                    containerColor = DopSurfaceCard,
+                    tonalElevation = 8.dp
+                ) {
+                    NavigationBarItem(
+                        selected = currentOwnerTab == OwnerTab.DASHBOARD,
+                        onClick = { currentOwnerTab = OwnerTab.DASHBOARD },
+                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
+                        label = { Text("Dashboard", fontSize = 10.sp) },
+                        colors = NavigationBarItemDefaults.colors(
+                            selectedIconColor = DopTrustGreen,
+                            selectedTextColor = DopTrustGreen,
+                            indicatorColor = DopTrustGreenContainer
+                        )
+                    )
+                    NavigationBarItem(
+                        selected = currentOwnerTab == OwnerTab.PROPERTIES,
+                        onClick = { currentOwnerTab = OwnerTab.PROPERTIES },
+                        icon = { Icon(Icons.Default.HomeWork, contentDescription = "Properties") },
+                        label = { Text("Mali", fontSize = 10.sp) },
+                        colors = NavigationBarItemDefaults.colors(
+                            selectedIconColor = DopTrustGreen,
+                            selectedTextColor = DopTrustGreen,
+                            indicatorColor = DopTrustGreenContainer
+                        )
+                    )
+                    NavigationBarItem(
+                        selected = currentOwnerTab == OwnerTab.ADD_PROPERTY,
+                        onClick = { currentOwnerTab = OwnerTab.ADD_PROPERTY },
+                        icon = { Icon(Icons.Default.AddCircle, contentDescription = "Add") },
+                        label = { Text("Weka", fontSize = 10.sp) },
+                        colors = NavigationBarItemDefaults.colors(
+                            selectedIconColor = DopTrustGreen,
+                            selectedTextColor = DopTrustGreen,
+                            indicatorColor = DopTrustGreenContainer
+                        )
+                    )
+                    NavigationBarItem(
+                        selected = currentOwnerTab == OwnerTab.VIEWINGS,
+                        onClick = { currentOwnerTab = OwnerTab.VIEWINGS },
+                        icon = { Icon(Icons.Default.Visibility, contentDescription = "Viewings") },
+                        label = { Text("Ziara", fontSize = 10.sp) },
+                        colors = NavigationBarItemDefaults.colors(
+                            selectedIconColor = DopTrustGreen,
+                            selectedTextColor = DopTrustGreen,
+                            indicatorColor = DopTrustGreenContainer
+                        )
+                    )
+                    NavigationBarItem(
+                        selected = currentOwnerTab == OwnerTab.ACCOUNT,
+                        onClick = { currentOwnerTab = OwnerTab.ACCOUNT },
+                        icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
+                        label = { Text("Akaunti", fontSize = 10.sp) },
+                        colors = NavigationBarItemDefaults.colors(
+                            selectedIconColor = DopTrustGreen,
+                            selectedTextColor = DopTrustGreen,
+                            indicatorColor = DopTrustGreenContainer
+                        )
+                    )
+                }
+                UserRole.GUIDE -> NavigationBar(
+                    containerColor = DopSurfaceCard,
+                    tonalElevation = 8.dp
+                ) {
+                    NavigationBarItem(
+                        selected = currentGuideTab == GuideTab.DASHBOARD,
+                        onClick = { currentGuideTab = GuideTab.DASHBOARD },
+                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
+                        label = { Text("Dashboard", fontSize = 10.sp) },
+                        colors = NavigationBarItemDefaults.colors(
+                            selectedIconColor = DopGuideAmberDark,
+                            selectedTextColor = DopGuideAmberDark,
+                            indicatorColor = DopGuideAmberContainer
+                        )
+                    )
+                    NavigationBarItem(
+                        selected = currentGuideTab == GuideTab.JOBS,
+                        onClick = { currentGuideTab = GuideTab.JOBS },
+                        icon = { Icon(Icons.Default.Work, contentDescription = "Jobs") },
+                        label = { Text("Kazi", fontSize = 10.sp) },
+                        colors = NavigationBarItemDefaults.colors(
+                            selectedIconColor = DopGuideAmberDark,
+                            selectedTextColor = DopGuideAmberDark,
+                            indicatorColor = DopGuideAmberContainer
+                        )
+                    )
+                    NavigationBarItem(
+                        selected = currentGuideTab == GuideTab.EARNINGS,
+                        onClick = { currentGuideTab = GuideTab.EARNINGS },
+                        icon = { Icon(Icons.Default.Payments, contentDescription = "Earnings") },
+                        label = { Text("Mapato", fontSize = 10.sp) },
+                        colors = NavigationBarItemDefaults.colors(
+                            selectedIconColor = DopGuideAmberDark,
+                            selectedTextColor = DopGuideAmberDark,
+                            indicatorColor = DopGuideAmberContainer
+                        )
+                    )
+                    NavigationBarItem(
+                        selected = currentGuideTab == GuideTab.ACCOUNT,
+                        onClick = { currentGuideTab = GuideTab.ACCOUNT },
+                        icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
+                        label = { Text("Akaunti", fontSize = 10.sp) },
+                        colors = NavigationBarItemDefaults.colors(
+                            selectedIconColor = DopGuideAmberDark,
+                            selectedTextColor = DopGuideAmberDark,
+                            indicatorColor = DopGuideAmberContainer
+                        )
+                    )
+                }
+                else -> {}
+                }
             }
         }
INNER_EOF
patch app/src/main/java/com/example/MainActivity.kt bottombar.patch
