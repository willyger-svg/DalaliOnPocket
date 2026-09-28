package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.AppLanguage
import com.example.core.localization.DoPStrings
import com.example.ui.theme.*

/**
 * Customer Capability Navigation Destinations
 * 1. HOME
 * 2. EXPLORE
 * 3. SHORTS (First-class content & property discovery feed)
 * 4. VISITS
 * 5. ACCOUNT
 */
enum class CustomerTab(val route: String, val labelSwahili: String, val labelEnglish: String) {
    HOME("customer_home", "Mwanzo", "Home"),
    EXPLORE("customer_explore", "Tafuta", "Explore"),
    SHORTS("customer_shorts", "Video", "Shorts"),
    VISITS("customer_visits", "Ziara", "Visits"),
    ACCOUNT("customer_account", "Akaunti", "Account")
}

/**
 * Owner Capability Navigation Destinations
 * 1. DASHBOARD
 * 2. PROPERTIES
 * 3. ADD
 * 4. VIEWINGS
 * 5. ACCOUNT
 */
enum class OwnerTab(val route: String, val labelSwahili: String, val labelEnglish: String) {
    DASHBOARD("owner_dashboard", "Dashibodi", "Dashboard"),
    PROPERTIES("owner_properties", "Mali", "Properties"),
    ADD("owner_add", "Weka Mali", "Add Property"),
    VIEWINGS("owner_viewings", "Miadi", "Viewings"),
    ACCOUNT("owner_account", "Akaunti", "Account")
}

/**
 * Guide Capability Navigation Destinations
 * 1. DASHBOARD
 * 2. JOBS
 * 3. SCHEDULE
 * 4. EARNINGS
 * 5. ACCOUNT
 */
enum class GuideTab(val route: String, val labelSwahili: String, val labelEnglish: String) {
    DASHBOARD("guide_dashboard", "Dashibodi", "Dashboard"),
    JOBS("guide_jobs", "Kazi", "Jobs"),
    SCHEDULE("guide_schedule", "Ratiba", "Schedule"),
    EARNINGS("guide_earnings", "Mapato", "Earnings"),
    ACCOUNT("guide_account", "Akaunti", "Account")
}

/**
 * Dedicated Customer Workspace Navigation Bar
 * Color palette: Blue accents (DopCustomerBlue / DopNavyPrimary)
 */
@Composable
fun CustomerBottomNavigation(
    currentTab: CustomerTab,
    onTabSelected: (CustomerTab) -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("customer_navigation_bar"),
        containerColor = DopSurfaceCard,
        tonalElevation = 8.dp
    ) {
        CustomerTab.values().forEach { tab ->
            val icon: ImageVector = when (tab) {
                CustomerTab.HOME -> Icons.Default.Home
                CustomerTab.EXPLORE -> Icons.Default.Explore
                CustomerTab.SHORTS -> Icons.Default.VideoLibrary
                CustomerTab.VISITS -> Icons.Default.DirectionsWalk
                CustomerTab.ACCOUNT -> Icons.Default.Person
            }
            val label = when (tab) {
                CustomerTab.HOME -> DoPStrings.navHome(lang)
                CustomerTab.EXPLORE -> DoPStrings.navExplore(lang)
                CustomerTab.SHORTS -> DoPStrings.navShorts(lang)
                CustomerTab.VISITS -> DoPStrings.navVisits(lang)
                CustomerTab.ACCOUNT -> DoPStrings.navAccount(lang)
            }
            val testTag = when (tab) {
                CustomerTab.HOME -> "tab_home"
                CustomerTab.EXPLORE -> "tab_explore"
                CustomerTab.SHORTS -> "tab_shorts"
                CustomerTab.VISITS -> "tab_visits"
                CustomerTab.ACCOUNT -> "tab_account"
            }

            NavigationBarItem(
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DopCustomerBlueDark,
                    selectedTextColor = DopCustomerBlueDark,
                    indicatorColor = DopCustomerBlueContainer,
                    unselectedIconColor = DopTextSecondary,
                    unselectedTextColor = DopTextSecondary
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}

/**
 * Dedicated Owner Workspace Navigation Bar
 * Color palette: Green accents (DopOwnerGreen / DopTrustGreen)
 */
@Composable
fun OwnerBottomNavigation(
    currentTab: OwnerTab,
    onTabSelected: (OwnerTab) -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("owner_navigation_bar"),
        containerColor = DopSurfaceCard,
        tonalElevation = 8.dp
    ) {
        OwnerTab.values().forEach { tab ->
            val icon: ImageVector = when (tab) {
                OwnerTab.DASHBOARD -> Icons.Default.Dashboard
                OwnerTab.PROPERTIES -> Icons.Default.HomeWork
                OwnerTab.ADD -> Icons.Default.AddCircle
                OwnerTab.VIEWINGS -> Icons.Default.Visibility
                OwnerTab.ACCOUNT -> Icons.Default.Person
            }
            val label = if (lang == AppLanguage.SWAHILI) tab.labelSwahili else tab.labelEnglish
            val testTag = when (tab) {
                OwnerTab.DASHBOARD -> "owner_tab_dashboard"
                OwnerTab.PROPERTIES -> "owner_tab_properties"
                OwnerTab.ADD -> "owner_tab_add"
                OwnerTab.VIEWINGS -> "owner_tab_viewings"
                OwnerTab.ACCOUNT -> "owner_tab_account"
            }

            NavigationBarItem(
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DopOwnerGreenDark,
                    selectedTextColor = DopOwnerGreenDark,
                    indicatorColor = DopOwnerGreenContainer,
                    unselectedIconColor = DopTextSecondary,
                    unselectedTextColor = DopTextSecondary
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}

/**
 * Dedicated Guide Workspace Navigation Bar
 * Color palette: Yellow / Amber accents (DopGuideAmberDark / DopGuideAmberContainer)
 */
@Composable
fun GuideBottomNavigation(
    currentTab: GuideTab,
    onTabSelected: (GuideTab) -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("guide_navigation_bar"),
        containerColor = DopSurfaceCard,
        tonalElevation = 8.dp
    ) {
        GuideTab.values().forEach { tab ->
            val icon: ImageVector = when (tab) {
                GuideTab.DASHBOARD -> Icons.Default.Dashboard
                GuideTab.JOBS -> Icons.Default.Work
                GuideTab.SCHEDULE -> Icons.Default.EventNote
                GuideTab.EARNINGS -> Icons.Default.Payments
                GuideTab.ACCOUNT -> Icons.Default.Person
            }
            val label = if (lang == AppLanguage.SWAHILI) tab.labelSwahili else tab.labelEnglish
            val testTag = when (tab) {
                GuideTab.DASHBOARD -> "guide_tab_dashboard"
                GuideTab.JOBS -> "guide_tab_jobs"
                GuideTab.SCHEDULE -> "guide_tab_schedule"
                GuideTab.EARNINGS -> "guide_tab_earnings"
                GuideTab.ACCOUNT -> "guide_tab_account"
            }

            NavigationBarItem(
                selected = currentTab == tab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = DopGuideAmberDark,
                    selectedTextColor = DopGuideAmberDark,
                    indicatorColor = DopGuideAmberContainer,
                    unselectedIconColor = DopTextSecondary,
                    unselectedTextColor = DopTextSecondary
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}
