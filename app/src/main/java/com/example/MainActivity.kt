package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.auth.AppAuthState
import com.example.core.auth.AuthManager
import com.example.core.localization.AppLanguage
import com.example.core.localization.DoPStrings
import com.example.core.localization.strings
import com.example.core.sync.SyncManager
import com.example.data.model.Property
import com.example.data.model.TenancyAgreement
import com.example.data.model.UserRole
import com.example.data.model.ViewingBooking
import com.example.data.repository.PropertyRepository
import com.example.ui.components.DopBadge
import com.example.ui.components.SyncStatusChip
import com.example.ui.navigation.*
import com.example.ui.screens.*
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentUserSession by AuthManager.currentUser.collectAsState()
            MyApplicationTheme(activeRole = currentUserSession?.activeMode) {
                DalalionPocketApp()
            }
        }
    }
}

enum class Screen {
    MAIN_TABS,
    PROPERTY_DETAILS,
    EDIT_PROFILE,
    ACCOUNT_ROLES,
    VIRTUAL_TOUR_360,
    LIVE_VIRTUAL_VIEWING,
    TENANCY_AGREEMENT,
    OWNER_WIZARD,
    PROPERTY_SHORTS,
    GUIDE_CHAT
}

@Composable
fun DalalionPocketApp() {
    val authState by AuthManager.authState.collectAsState()
    var appLanguage by remember { mutableStateOf(AppLanguage.SWAHILI) }

    when (authState) {
        AppAuthState.SPLASH -> {
            SplashScreen(
                onContinue = { AuthManager.completeSplash() }
            )
        }
        AppAuthState.ONBOARDING -> {
            OnboardingScreen(
                lang = appLanguage,
                onFinished = { AuthManager.completeOnboarding() },
                onLoginClick = { AuthManager.navigateToLogin() }
            )
        }
        AppAuthState.LOGIN -> {
            LoginScreen(
                lang = appLanguage,
                onLanguageChange = { appLanguage = it },
                onNavigateToSignUp = { AuthManager.navigateToSignUp() },
                onNavigateToAdminLogin = { AuthManager.navigateToAdminLogin() }
            )
        }
        AppAuthState.SIGN_UP -> {
            SignUpScreen(
                lang = appLanguage,
                onNavigateToLogin = { AuthManager.navigateToLogin() }
            )
        }
        AppAuthState.ADMIN_BLOCKED -> {
            AdminBlockedScreen(
                onReturnToLogin = { AuthManager.navigateToLogin() }
            )
        }
        AppAuthState.ADMIN_ACCESS -> {
            AdminAccessScreen(
                onCancel = { AuthManager.exitAdminAccess() },
                onAuthenticationSuccess = { /* authState updates to ADMIN_HANDOFF in AuthManager */ }
            )
        }
        AppAuthState.ADMIN_HANDOFF -> {
            AdminHandoffScreen(
                onExitHandoff = { AuthManager.exitAdminHandoff() }
            )
        }
        AppAuthState.AUTHENTICATED -> {
            MainAppScaffold(
                appLanguage = appLanguage,
                onLanguageChange = { appLanguage = it }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScaffold(
    appLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    val s = strings(appLanguage)
    val currentUserSession by AuthManager.currentUser.collectAsState()
    val isConnected by SyncManager.isConnected.collectAsState()
    val notifications by SyncManager.notifications.collectAsState()

    val currentUser = currentUserSession ?: AuthManager.mockUsers[UserRole.CUSTOMER]!!

    var currentScreen by remember { mutableStateOf(Screen.MAIN_TABS) }
    var currentTab by remember { mutableStateOf(CustomerTab.HOME) }
    var currentOwnerTab by remember { mutableStateOf(OwnerTab.DASHBOARD) }
    var currentGuideTab by remember { mutableStateOf(GuideTab.DASHBOARD) }
    var selectedProperty by remember { mutableStateOf<Property?>(null) }
    var selectedAgreement by remember { mutableStateOf<TenancyAgreement?>(null) }
    var showNotificationDialog by remember { mutableStateOf(false) }
    var chatBookingCode by remember { mutableStateOf("DOP-BK-9182") }

    // Role Quick Switcher & Capability Dialog state
    var showRoleModal by remember { mutableStateOf(false) }
    var showOwnerOnboarding by remember { mutableStateOf(false) }
    var showGuideApplication by remember { mutableStateOf(false) }

    // Active capability authorization guard
    LaunchedEffect(currentUser.activeMode, currentUser.canAccessOwner, currentUser.canAccessGuide) {
        if (currentUser.activeMode == UserRole.OWNER && !currentUser.canAccessOwner) {
            com.example.core.account.AccountManager.switchActiveMode(UserRole.CUSTOMER)
        } else if (currentUser.activeMode == UserRole.GUIDE && !currentUser.canAccessGuide) {
            com.example.core.account.AccountManager.switchActiveMode(UserRole.CUSTOMER)
        }
    }

    val roleTokens = LocalRoleThemeTokens.current

    Scaffold(
        topBar = {
            if (currentScreen == Screen.MAIN_TABS ) {
                Column {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DopOchre),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HomeWork,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = s.appTitle,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = s.appTagline,
                                        fontSize = 10.sp,
                                        color = DopOchreLight
                                    )
                                }
                            }
                        },
                        actions = {
                            // Live Sync chip
                            SyncStatusChip(
                                isConnected = isConnected,
                                lang = appLanguage,
                                onClick = { SyncManager.toggleConnectionState() }
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            // Quick Chat Icon with Guide/Customer
                            IconButton(
                                onClick = {
                                    chatBookingCode = "DOP-BK-9182"
                                    currentScreen = Screen.GUIDE_CHAT
                                },
                                modifier = Modifier.testTag("top_chat_btn")
                            ) {
                                BadgedBox(
                                    badge = {
                                        Badge(containerColor = DopTrustGreen) {
                                            Text("1", fontSize = 9.sp)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Chat,
                                        contentDescription = "Chat",
                                        tint = Color.White
                                    )
                                }
                            }

                            // Notification Icon with badge count
                            IconButton(
                                onClick = { showNotificationDialog = true },
                                modifier = Modifier.testTag("notifications_btn")
                            ) {
                                BadgedBox(
                                    badge = {
                                        if (notifications.isNotEmpty()) {
                                            Badge(containerColor = DopOchre) {
                                                Text("${notifications.size}", fontSize = 9.sp)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = Color.White
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = DopNavyPrimary)
                    )
                    // Identity / Context Accent Line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .background(roleTokens.roleAccent)
                    )
                }
            }
        },
        bottomBar = {
            if (currentScreen == Screen.MAIN_TABS) {
                when (currentUser.activeMode) {
                    UserRole.CUSTOMER -> CustomerBottomNavigation(
                        currentTab = currentTab,
                        onTabSelected = { currentTab = it },
                        lang = appLanguage
                    )
                    UserRole.OWNER -> OwnerBottomNavigation(
                        currentTab = currentOwnerTab,
                        onTabSelected = { currentOwnerTab = it },
                        lang = appLanguage
                    )
                    UserRole.GUIDE -> GuideBottomNavigation(
                        currentTab = currentGuideTab,
                        onTabSelected = { currentGuideTab = it },
                        lang = appLanguage
                    )
                    else -> {}
                }
            }
        }
        ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentScreen) {
                Screen.MAIN_TABS -> {
                    when (currentUser.activeMode) {
                        UserRole.CUSTOMER -> {
                            when (currentTab) {
                                CustomerTab.HOME -> {
                                    CustomerHomeScreen(
                                        lang = appLanguage,
                                        onSelectProperty = { prop ->
                                            selectedProperty = prop
                                            currentScreen = Screen.PROPERTY_DETAILS
                                        },
                                        onOpen360Tour = { prop ->
                                            selectedProperty = prop
                                            currentScreen = Screen.VIRTUAL_TOUR_360
                                        },
                                        onOpenLiveTour = { prop ->
                                            selectedProperty = prop
                                            currentScreen = Screen.LIVE_VIRTUAL_VIEWING
                                        },
                                        onOpenShorts = {
                                            currentTab = CustomerTab.SHORTS
                                        }
                                    )
                                }
                                CustomerTab.EXPLORE -> {
                                    CustomerExploreScreen(
                                        lang = appLanguage,
                                        onSelectProperty = { prop ->
                                            selectedProperty = prop
                                            currentScreen = Screen.PROPERTY_DETAILS
                                        }
                                    )
                                }
                                CustomerTab.SHORTS -> {
                                    PropertyShortsScreen(
                                        lang = appLanguage,
                                        onBack = { currentTab = CustomerTab.HOME },
                                        onSelectProperty = { prop ->
                                            selectedProperty = prop
                                            currentScreen = Screen.PROPERTY_DETAILS
                                        },
                                        onBookTour = { short ->
                                            val matchedProp = PropertyRepository.properties.value.find { it.id == short.propertyId } ?: PropertyRepository.properties.value.firstOrNull()
                                            if (matchedProp != null) {
                                                selectedProperty = matchedProp
                                                currentScreen = Screen.LIVE_VIRTUAL_VIEWING
                                            }
                                        }
                                    )
                                }
                                CustomerTab.VISITS -> {
                                    CustomerVisitsScreen(
                                        lang = appLanguage,
                                        onOpenLiveTour = { prop ->
                                            selectedProperty = prop
                                            currentScreen = Screen.LIVE_VIRTUAL_VIEWING
                                        },
                                        onOpenChat = { booking ->
                                            chatBookingCode = booking.bookingCode
                                            currentScreen = Screen.GUIDE_CHAT
                                        }
                                    )
                                }
                                CustomerTab.ACCOUNT -> {
                                    com.example.ui.screens.UnifiedAccountCenterScreen(
                                        lang = appLanguage,
                                        onLanguageChange = onLanguageChange,
                                        onNavigateToEditProfile = { currentScreen = Screen.EDIT_PROFILE },
                                        onNavigateToAccountRoles = { currentScreen = Screen.ACCOUNT_ROLES },
                                        onOpenRoleSwitcher = { showRoleModal = true },
                                        onOpenOwnerOnboarding = { showOwnerOnboarding = true },
                                        onOpenGuideApplication = { showGuideApplication = true },
                                        onNavigateToSettings = {}
                                    )
                                }
                            }
                        }
                        UserRole.OWNER -> {
                            when (currentOwnerTab) {
                                OwnerTab.DASHBOARD -> {
                                    OwnerDashboardScreen(
                                        lang = appLanguage,
                                        onOpenWizard = { currentOwnerTab = OwnerTab.ADD },
                                        onOpenProperties = { currentOwnerTab = OwnerTab.PROPERTIES },
                                        onOpenShorts = { currentScreen = Screen.PROPERTY_SHORTS },
                                        onOpenViewings = { currentOwnerTab = OwnerTab.VIEWINGS },
                                        onViewAgreement = { agr ->
                                            selectedAgreement = agr
                                            currentScreen = Screen.TENANCY_AGREEMENT
                                        }
                                    )
                                }
                                OwnerTab.PROPERTIES -> {
                                    com.example.ui.screens.OwnerPropertiesScreen(
                                        lang = appLanguage,
                                        onViewProperty = { prop ->
                                            selectedProperty = prop
                                            currentScreen = Screen.PROPERTY_DETAILS
                                        },
                                        onAddProperty = { currentOwnerTab = OwnerTab.ADD },
                                        onOpenShortsFeed = { currentScreen = Screen.PROPERTY_SHORTS }
                                    )
                                }
                                OwnerTab.ADD -> {
                                    com.example.ui.screens.OwnerPropertyWizardScreen(
                                        onDismiss = { currentOwnerTab = OwnerTab.PROPERTIES },
                                        onFinished = { currentOwnerTab = OwnerTab.PROPERTIES }
                                    )
                                }
                                OwnerTab.VIEWINGS -> {
                                    com.example.ui.screens.OwnerViewingsScreen(
                                        lang = appLanguage,
                                        onViewProperty = { prop ->
                                            selectedProperty = prop
                                            currentScreen = Screen.PROPERTY_DETAILS
                                        },
                                        onViewRequest = { }
                                    )
                                }
                                OwnerTab.ACCOUNT -> {
                                    com.example.ui.screens.UnifiedAccountCenterScreen(
                                        lang = appLanguage,
                                        onLanguageChange = onLanguageChange,
                                        onNavigateToEditProfile = { currentScreen = Screen.EDIT_PROFILE },
                                        onNavigateToAccountRoles = { currentScreen = Screen.ACCOUNT_ROLES },
                                        onOpenRoleSwitcher = { showRoleModal = true },
                                        onOpenOwnerOnboarding = { showOwnerOnboarding = true },
                                        onOpenGuideApplication = { showGuideApplication = true },
                                        onNavigateToSettings = {}
                                    )
                                }
                            }
                        }
                        UserRole.GUIDE -> {
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
                                    com.example.ui.screens.GuideScheduleScreen(lang = appLanguage)
                                }
                                GuideTab.EARNINGS -> {
                                    com.example.ui.screens.GuideEarningsScreen()
                                }
                                GuideTab.ACCOUNT -> {
                                    com.example.ui.screens.UnifiedAccountCenterScreen(
                                        lang = appLanguage,
                                        onLanguageChange = onLanguageChange,
                                        onNavigateToEditProfile = { currentScreen = Screen.EDIT_PROFILE },
                                        onNavigateToAccountRoles = { currentScreen = Screen.ACCOUNT_ROLES },
                                        onOpenRoleSwitcher = { showRoleModal = true },
                                        onOpenOwnerOnboarding = { showOwnerOnboarding = true },
                                        onOpenGuideApplication = { showGuideApplication = true },
                                        onNavigateToSettings = {}
                                    )
                                }
                            }
                        }
                        UserRole.ADMIN -> {
                            // If an admin role somehow arrives here, switch to AdminBlocked immediately
                            LaunchedEffect(Unit) {
                                AuthManager.switchRole(UserRole.ADMIN)
                            }
                        }
                    }
                }

                Screen.PROPERTY_DETAILS -> {
                    selectedProperty?.let { prop ->
                        PropertyDetailsScreen(
                            property = prop,
                            lang = appLanguage,
                            onBack = { currentScreen = Screen.MAIN_TABS },
                            onOpen360Tour = { currentScreen = Screen.VIRTUAL_TOUR_360 },
                            onOpenLiveViewing = { currentScreen = Screen.LIVE_VIRTUAL_VIEWING },
                            onAgreementView = { agr ->
                                selectedAgreement = agr
                                currentScreen = Screen.TENANCY_AGREEMENT
                            },
                            onOwnerManage = {
                                currentOwnerTab = OwnerTab.PROPERTIES
                                currentScreen = Screen.MAIN_TABS
                            },
                            onOwnerViewings = {
                                currentOwnerTab = OwnerTab.VIEWINGS
                                currentScreen = Screen.MAIN_TABS
                            },
                            onGuideStartViewing = {
                                currentGuideTab = GuideTab.JOBS
                                currentScreen = Screen.MAIN_TABS
                            }
                        )
                    } ?: run { currentScreen = Screen.MAIN_TABS }
                }

                Screen.VIRTUAL_TOUR_360 -> {
                    selectedProperty?.let { prop ->
                        VirtualTour360Screen(
                            property = prop,
                            lang = appLanguage,
                            onBack = { currentScreen = Screen.PROPERTY_DETAILS }
                        )
                    } ?: run { currentScreen = Screen.MAIN_TABS }
                }

                Screen.LIVE_VIRTUAL_VIEWING -> {
                    selectedProperty?.let { prop ->
                        LiveVirtualViewingScreen(
                            property = prop,
                            lang = appLanguage,
                            onEndSession = { currentScreen = Screen.PROPERTY_DETAILS }
                        )
                    } ?: run { currentScreen = Screen.MAIN_TABS }
                }

                Screen.TENANCY_AGREEMENT -> {
                    val fallbackAgreement = PropertyRepository.tenancyAgreements.value.first()
                    TenancyAgreementScreen(
                        agreement = selectedAgreement ?: fallbackAgreement,
                        currentUserRole = currentUser.activeMode,
                        lang = appLanguage,
                        onBack = { currentScreen = Screen.MAIN_TABS }
                    )
                }

                Screen.OWNER_WIZARD -> {
                    if (!currentUser.canAccessOwner) {
                        LaunchedEffect(Unit) {
                            currentScreen = Screen.MAIN_TABS
                            showOwnerOnboarding = true
                        }
                    } else {
                        OwnerPropertyWizardScreen(
                            onDismiss = { currentScreen = Screen.MAIN_TABS },
                            onFinished = { currentScreen = Screen.MAIN_TABS }
                        )
                    }
                }

                Screen.PROPERTY_SHORTS -> {
                    PropertyShortsScreen(
                        lang = appLanguage,
                        onBack = { currentScreen = Screen.MAIN_TABS },
                        onSelectProperty = { prop ->
                            selectedProperty = prop
                            currentScreen = Screen.PROPERTY_DETAILS
                        },
                        onBookTour = { short ->
                            val matchedProp = PropertyRepository.properties.value.find { it.id == short.propertyId } ?: PropertyRepository.properties.value.firstOrNull()
                            if (matchedProp != null) {
                                selectedProperty = matchedProp
                                currentScreen = Screen.LIVE_VIRTUAL_VIEWING
                            }
                        }
                    )
                }

                Screen.GUIDE_CHAT -> {
                    GuideCustomerChatScreen(
                        lang = appLanguage,
                        bookingCode = chatBookingCode,
                        onBack = { currentScreen = Screen.MAIN_TABS }
                    )
                }
                
                Screen.EDIT_PROFILE -> {
                    com.example.ui.screens.EditProfileScreen(
                        lang = appLanguage,
                        onBack = { currentScreen = Screen.MAIN_TABS }
                    )
                }
                
                Screen.ACCOUNT_ROLES -> {
                    com.example.ui.screens.AccountAndRolesScreen(
                        lang = appLanguage,
                        onBack = { currentScreen = Screen.MAIN_TABS },
                        onOpenOwnerOnboarding = { 
                            showOwnerOnboarding = true
                        },
                        onOpenGuideApplication = { 
                            showGuideApplication = true
                        }
                    )
                }
            }
        }
    }

    // Capability Mode Switcher Modal (Customer, Owner, Guide)
    if (showRoleModal) {
        CapabilityRoleSwitcherDialog(
            currentUser = currentUser,
            onDismiss = { showRoleModal = false },
            onSelectMode = { newMode ->
                com.example.core.account.AccountManager.switchActiveMode(newMode)
                showRoleModal = false
            },
            onOpenOwnerOnboarding = {
                showRoleModal = false
                showOwnerOnboarding = true
            },
            onOpenGuideApplication = {
                showRoleModal = false
                showGuideApplication = true
            }
        )
    }

    if (showOwnerOnboarding) {
        OwnerOnboardingDialog(
            onDismiss = { showOwnerOnboarding = false },
            onSuccessActivated = {
                showOwnerOnboarding = false
            }
        )
    }

    if (showGuideApplication) {
        GuideApplicationDialog(
            onDismiss = { showGuideApplication = false },
            onSuccessSubmitted = {
                showGuideApplication = false
            }
        )
    }

    // Notifications Dialog
    if (showNotificationDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationDialog = false },
            title = { Text(s.notificationsTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    notifications.forEach { notif ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(notif.title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DopNavyPrimary)
                                Text(notif.message, fontSize = 11.sp, color = DopTextSecondary)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationDialog = false }) {
                    Text(s.dialogCloseButton)
                }
            }
        )
    }
}
