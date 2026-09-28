package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.auth.AuthManager
import com.example.core.localization.AppLanguage
import com.example.core.localization.strings
import com.example.core.sync.SyncManager
import com.example.data.model.GuideCapabilityStatus
import com.example.data.model.OwnerCapabilityStatus
import com.example.data.model.UserRole
import com.example.ui.components.DopBentoCard
import com.example.ui.theme.*

@Composable
fun UnifiedAccountCenterScreen(
    lang: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit = {},
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAccountRoles: () -> Unit,
    onOpenRoleSwitcher: () -> Unit = {},
    onOpenOwnerOnboarding: () -> Unit = {},
    onOpenGuideApplication: () -> Unit = {},
    onNavigateToSettings: (String) -> Unit = {}
) {
    val s = strings(lang)
    val currentUser by AuthManager.currentUser.collectAsState()
    val isConnected by SyncManager.isConnected.collectAsState()
    val user = currentUser ?: return
    val currentMode = user.activeMode
    val roleTokens = DopRoleColors.forRole(currentMode)

    var showInternalRoleModal by remember { mutableStateOf(false) }
    var showInternalOwnerOnboarding by remember { mutableStateOf(false) }
    var showInternalGuideApplication by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showVerificationDialog by remember { mutableStateOf(false) }
    var showSupportDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }

    val roleTitle = when (currentMode) {
        UserRole.CUSTOMER -> s.roleCustomerTitle
        UserRole.OWNER -> s.roleOwnerTitle
        UserRole.GUIDE -> s.roleGuideTitle
        UserRole.ADMIN -> s.roleAdminTitle
    }

    val roleDescription = when (currentMode) {
        UserRole.CUSTOMER -> s.roleCustomerDesc
        UserRole.OWNER -> s.roleOwnerDesc
        UserRole.GUIDE -> s.roleGuideDesc
        UserRole.ADMIN -> s.roleAdminDesc
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Profile Summary Card
        item {
            Text(s.profileSectionHeader, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopNavyPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            DopBentoCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(roleTokens.roleAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(2).uppercase(),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = user.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = DopNavyPrimary)
                            if (user.isVerified) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = s.roleVerifiedBadge,
                                    tint = DopTrustGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(text = user.phone, fontSize = 12.sp, color = DopTextSecondary)
                        Text(text = user.email, fontSize = 12.sp, color = DopTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        // Active Role Pill
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = roleTokens.roleBadgeBackground,
                            border = BorderStroke(1.dp, roleTokens.roleBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(roleTokens.roleAccent)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = roleTitle,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = roleTokens.roleAccent
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedButton(
                    onClick = onNavigateToEditProfile,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_profile_btn"),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = DopNavyPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (lang == AppLanguage.SWAHILI) "Hariri Wasifu Wako" else "Edit Your Profile", color = DopNavyPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                }
            }
        }

        // 2. Dedicated Role Switcher in Settings/Account
        item {
            Text(s.rolesSectionHeader, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopNavyPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            DopBentoCard {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = roleTokens.roleBadgeBackground,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = when (currentMode) {
                                        UserRole.CUSTOMER -> Icons.Default.Person
                                        UserRole.OWNER -> Icons.Default.HomeWork
                                        UserRole.GUIDE -> Icons.Default.DirectionsWalk
                                        UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                    },
                                    contentDescription = null,
                                    tint = roleTokens.roleAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = roleTitle,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = DopNavyPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = DopTrustGreenContainer
                                ) {
                                    Text(
                                        text = s.languageActiveBadge.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DopOwnerGreenDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = roleDescription,
                                fontSize = 11.sp,
                                color = DopTextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = DopBorderSubtle)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (lang == AppLanguage.SWAHILI) "Badilisha Nafasi ya Kazi Papo Hapo:" else "Quick Switch Active Workspace:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DopTextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 3 One-Tap Role Switch Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Customer Button
                        val isCustomer = currentMode == UserRole.CUSTOMER
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isCustomer) DopCustomerBlueContainer else Color.White,
                            border = BorderStroke(if (isCustomer) 2.dp else 1.dp, if (isCustomer) DopCustomerBlue else DopBorderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    com.example.core.account.AccountManager.switchMobileRole(UserRole.CUSTOMER)
                                }
                                .testTag("account_switch_customer")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = if (isCustomer) DopCustomerBlue else DopTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (lang == AppLanguage.SWAHILI) "Mteja" else "Tenant",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCustomer) DopCustomerBlueDark else DopNavyPrimary
                                )
                                Text(
                                    text = if (isCustomer) (if (lang == AppLanguage.SWAHILI) "Inatumika" else "Active") else (if (lang == AppLanguage.SWAHILI) "Gusa" else "Tap"),
                                    fontSize = 9.sp,
                                    color = if (isCustomer) DopCustomerBlue else DopTextMuted
                                )
                            }
                        }

                        // Owner Button
                        val isOwner = currentMode == UserRole.OWNER
                        val hasOwnerCapability = user.canAccessOwner
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isOwner) DopOwnerGreenContainer else Color.White,
                            border = BorderStroke(if (isOwner) 2.dp else 1.dp, if (isOwner) DopOwnerGreen else DopBorderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (hasOwnerCapability) {
                                        com.example.core.account.AccountManager.switchMobileRole(UserRole.OWNER)
                                    } else {
                                        showInternalOwnerOnboarding = true
                                    }
                                }
                                .testTag("account_switch_owner")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.HomeWork,
                                    contentDescription = null,
                                    tint = if (isOwner) DopOwnerGreen else DopTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (lang == AppLanguage.SWAHILI) "Mmiliki" else "Owner",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOwner) DopOwnerGreenDark else DopNavyPrimary
                                )
                                Text(
                                    text = if (isOwner) (if (lang == AppLanguage.SWAHILI) "Inatumika" else "Active") else if (hasOwnerCapability) (if (lang == AppLanguage.SWAHILI) "Gusa" else "Tap") else (if (lang == AppLanguage.SWAHILI) "+ Washa" else "+ Enable"),
                                    fontSize = 9.sp,
                                    color = if (isOwner) DopOwnerGreen else DopTextMuted
                                )
                            }
                        }

                        // Guide Button
                        val isGuide = currentMode == UserRole.GUIDE
                        val hasGuideCapability = user.canAccessGuide
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isGuide) DopGuideAmberContainer else Color.White,
                            border = BorderStroke(if (isGuide) 2.dp else 1.dp, if (isGuide) DopGuideAmber else DopBorderSubtle),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (hasGuideCapability) {
                                        com.example.core.account.AccountManager.switchMobileRole(UserRole.GUIDE)
                                    } else {
                                        showInternalGuideApplication = true
                                    }
                                }
                                .testTag("account_switch_guide")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.DirectionsWalk,
                                    contentDescription = null,
                                    tint = if (isGuide) DopGuideAmberDark else DopTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (lang == AppLanguage.SWAHILI) "Dalali" else "Guide",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isGuide) DopGuideAmberDark else DopNavyPrimary
                                )
                                Text(
                                    text = if (isGuide) (if (lang == AppLanguage.SWAHILI) "Inatumika" else "Active") else if (hasGuideCapability) (if (lang == AppLanguage.SWAHILI) "Gusa" else "Tap") else (if (lang == AppLanguage.SWAHILI) "+ Omba" else "+ Apply"),
                                    fontSize = 9.sp,
                                    color = if (isGuide) DopGuideAmberDark else DopTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (onOpenRoleSwitcher != {}) onOpenRoleSwitcher() else showInternalRoleModal = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("account_open_role_modal_btn")
                    ) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(s.roleSwitchButton, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // 3. Account & Capabilities
        item {
            Text(s.capabilitiesMenuTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopNavyPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            DopBentoCard {
                AccountMenuItem(
                    icon = Icons.Default.SwitchAccount,
                    title = s.switchRoleMenuTitle,
                    subtitle = s.switchRoleMenuSubtitle,
                    onClick = {
                        if (onOpenRoleSwitcher != {}) onOpenRoleSwitcher() else showInternalRoleModal = true
                    }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DopTextMuted.copy(alpha = 0.2f))
                AccountMenuItem(
                    icon = Icons.Default.Badge,
                    title = s.capabilitiesMenuTitle,
                    subtitle = s.capabilitiesMenuSubtitle,
                    onClick = onNavigateToAccountRoles
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DopTextMuted.copy(alpha = 0.2f))
                AccountMenuItem(
                    icon = Icons.Default.VerifiedUser,
                    title = s.verificationMenuTitle,
                    subtitle = if (user.isVerified) s.verificationMenuVerifiedSubtitle else s.verificationMenuUnverifiedSubtitle,
                    onClick = { showVerificationDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DopTextMuted.copy(alpha = 0.2f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.CloudSync, contentDescription = null, tint = DopNavyPrimary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(s.liveSyncMenuTitle, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DopNavyPrimary)
                            Text(if (isConnected) s.liveSyncActiveSubtitle else s.liveSyncOfflineSubtitle, fontSize = 12.sp, color = DopTextSecondary)
                        }
                    }
                    Switch(
                        checked = isConnected,
                        onCheckedChange = { SyncManager.toggleConnectionState() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = DopTrustGreen
                        )
                    )
                }
            }
        }

        // 4. System Settings & Preferences
        item {
            Text(s.systemSettingsHeader, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopNavyPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            DopBentoCard {
                AccountMenuItem(
                    icon = Icons.Default.Language,
                    title = s.languageMenuTitle,
                    subtitle = if (lang == AppLanguage.SWAHILI) "Kiswahili Fasaha (${s.languageActiveBadge})" else "Standard English (${s.languageActiveBadge})",
                    onClick = { showLanguageDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DopTextMuted.copy(alpha = 0.2f))
                AccountMenuItem(
                    icon = Icons.Default.Notifications,
                    title = s.notificationsMenuTitle,
                    subtitle = s.notificationsMenuSubtitle,
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DopTextMuted.copy(alpha = 0.2f))
                AccountMenuItem(
                    icon = Icons.Default.Security,
                    title = s.securityMenuTitle,
                    subtitle = s.securityMenuSubtitle,
                    onClick = { }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DopTextMuted.copy(alpha = 0.2f))
                AccountMenuItem(
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    title = s.supportMenuTitle,
                    subtitle = s.supportMenuSubtitle,
                    onClick = { showSupportDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DopTextMuted.copy(alpha = 0.2f))
                AccountMenuItem(
                    icon = Icons.Default.Info,
                    title = s.aboutMenuTitle,
                    subtitle = s.aboutMenuSubtitle,
                    onClick = { showAboutDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DopTextMuted.copy(alpha = 0.2f))
                AccountMenuItem(
                    icon = Icons.AutoMirrored.Filled.Article,
                    title = s.termsMenuTitle,
                    subtitle = s.termsMenuSubtitle,
                    onClick = { showTermsDialog = true }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = DopTextMuted.copy(alpha = 0.2f))
                AccountMenuItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = s.logoutMenuTitle,
                    titleColor = DopError,
                    onClick = { showLogoutDialog = true }
                )
            }
        }
    }

    // Role Switcher Modal triggered from Account Settings
    if (showInternalRoleModal) {
        CapabilityRoleSwitcherDialog(
            currentUser = user,
            onDismiss = { showInternalRoleModal = false },
            onSelectMode = { newMode ->
                com.example.core.account.AccountManager.switchMobileRole(newMode)
                showInternalRoleModal = false
            },
            onOpenOwnerOnboarding = {
                showInternalRoleModal = false
                showInternalOwnerOnboarding = true
            },
            onOpenGuideApplication = {
                showInternalRoleModal = false
                showInternalGuideApplication = true
            }
        )
    }

    if (showInternalOwnerOnboarding) {
        OwnerOnboardingDialog(
            onDismiss = { showInternalOwnerOnboarding = false },
            onSuccessActivated = { showInternalOwnerOnboarding = false }
        )
    }

    if (showInternalGuideApplication) {
        GuideApplicationDialog(
            onDismiss = { showInternalGuideApplication = false },
            onSuccessSubmitted = { showInternalGuideApplication = false }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text(s.logoutDialogTitle, fontWeight = FontWeight.Bold) },
            text = { Text(s.logoutDialogMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        AuthManager.logout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DopError)
                ) {
                    Text(s.logoutConfirmButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(s.logoutCancelButton, color = DopTextSecondary)
                }
            }
        )
    }

    // Dedicated Fluent Language Selector Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = DopNavyPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(s.languageDialogTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
                        Text(s.languageDialogSubtitle, fontSize = 11.sp, color = DopTextSecondary)
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Swahili Option Card
                    val isSwahili = lang == AppLanguage.SWAHILI
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSwahili) DopOchreContainer else Color.White,
                        border = BorderStroke(if (isSwahili) 2.dp else 1.dp, if (isSwahili) DopOchre else DopBorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLanguageChange(AppLanguage.SWAHILI)
                            }
                            .testTag("lang_select_swahili")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🇹🇿", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = s.languageSwahiliTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = DopNavyPrimary
                                    )
                                    if (isSwahili) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = DopTrustGreenContainer
                                        ) {
                                            Text(
                                                text = s.languageActiveBadge,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DopOwnerGreenDark,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = s.languageSwahiliDesc,
                                    fontSize = 11.sp,
                                    color = DopTextSecondary
                                )
                            }
                            RadioButton(
                                selected = isSwahili,
                                onClick = { onLanguageChange(AppLanguage.SWAHILI) }
                            )
                        }
                    }

                    // English Option Card
                    val isEnglish = lang == AppLanguage.ENGLISH
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isEnglish) DopCustomerBlueContainer else Color.White,
                        border = BorderStroke(if (isEnglish) 2.dp else 1.dp, if (isEnglish) DopCustomerBlue else DopBorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onLanguageChange(AppLanguage.ENGLISH)
                            }
                            .testTag("lang_select_english")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🌍", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = s.languageEnglishTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = DopNavyPrimary
                                    )
                                    if (isEnglish) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = DopTrustGreenContainer
                                        ) {
                                            Text(
                                                text = s.languageActiveBadge,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = DopOwnerGreenDark,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = s.languageEnglishDesc,
                                    fontSize = 11.sp,
                                    color = DopTextSecondary
                                )
                            }
                            RadioButton(
                                selected = isEnglish,
                                onClick = { onLanguageChange(AppLanguage.ENGLISH) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showLanguageDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary)
                ) {
                    Text(s.languageConfirmButton)
                }
            }
        )
    }

    // Verification Dialog
    if (showVerificationDialog) {
        AlertDialog(
            onDismissRequest = { showVerificationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = DopTrustGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(s.verificationDialogTitle, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(s.verificationDialogDesc, fontSize = 12.sp, color = DopTextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("• NIDA NIN: 19900815-11234-00001-28", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = DopNavyPrimary)
                    Text("• ${if (lang == AppLanguage.SWAHILI) "Hali" else "Status"}: ${s.roleVerifiedBadge}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = DopOwnerGreenDark)
                    Text("• ${if (lang == AppLanguage.SWAHILI) "Jina Kamili" else "Full Name"}: ${user.name}", fontSize = 12.sp)
                    Text("• ${if (lang == AppLanguage.SWAHILI) "Namba ya Simu" else "Phone Number"}: ${user.phone}", fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showVerificationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary)
                ) {
                    Text(s.dialogCloseButton)
                }
            }
        )
    }

    // Support Dialog
    if (showSupportDialog) {
        AlertDialog(
            onDismissRequest = { showSupportDialog = false },
            title = { Text(s.supportDialogTitle, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(s.supportDialogDesc, fontSize = 12.sp, color = DopTextSecondary)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = DopNavyPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("+255 712 345 678", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = DopTrustGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("WhatsApp: +255 655 789 012", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = DopNavyPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("huduma@dalalionpocket.co.tz", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showSupportDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary)) {
                    Text(s.dialogCloseButton)
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text(s.aboutDialogTitle, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Dalalion Pocket (DoP) Tanzania", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DopNavyPrimary)
                    Text(s.aboutMenuSubtitle, fontSize = 11.sp, color = DopTextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        s.aboutDialogDesc,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showAboutDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary)) {
                    Text(s.dialogCloseButton)
                }
            }
        )
    }

    // Terms Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text(s.termsDialogTitle, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        if (lang == AppLanguage.SWAHILI)
                            "1. Uhakiki wa Mali: Mali zote zinakaguliwa na Madalali walioidhinishwa wa DoP."
                        else
                            "1. Listing Verification: All listed properties are inspected and verified by licensed DoP Field Guides.",
                        fontSize = 11.sp
                    )
                    Text(
                        if (lang == AppLanguage.SWAHILI)
                            "2. Usalama wa Malipo: Malipo yote hufanywa kupitia mfumo rasmi wa Escrow unaolinda pande zote mbili."
                        else
                            "2. Payment Escrow: All transactions are processed through a regulated escrow protocol protecting both parties.",
                        fontSize = 11.sp
                    )
                    Text(
                        if (lang == AppLanguage.SWAHILI)
                            "3. Mikataba ya Kisheria: Mikataba yote ya upangishaji inafuata sheria za ardhi na nyumba za Tanzania."
                        else
                            "3. Statutory Leases: All digital rental agreements comply with the statutory land laws of the United Republic of Tanzania.",
                        fontSize = 11.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showTermsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary)) {
                    Text(s.dialogCloseButton)
                }
            }
        )
    }
}

@Composable
fun AccountMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    titleColor: Color = DopNavyPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = DopTextSecondary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = titleColor)
            if (subtitle != null) {
                Text(text = subtitle, fontSize = 12.sp, color = DopTextSecondary)
            }
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = DopTextMuted)
    }
}

@Composable
fun EditProfileScreen(
    lang: AppLanguage = AppLanguage.SWAHILI,
    onBack: () -> Unit
) {
    val s = strings(lang)
    val currentUser by AuthManager.currentUser.collectAsState()
    val user = currentUser ?: return
    
    var name by remember { mutableStateOf(user.name) }
    var email by remember { mutableStateOf(user.email) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
    ) {
        Surface(
            color = DopSurfaceCard,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.dialogCloseButton, tint = DopNavyPrimary)
                }
                Text(
                    text = if (lang == AppLanguage.SWAHILI) "Hariri Wasifu" else "Edit Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DopNavyPrimary
                )
            }
        }
        
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (lang == AppLanguage.SWAHILI) "Picha ya Wasifu" else "Profile Photo",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(DopCustomerBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(name.take(2).uppercase(), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(if (lang == AppLanguage.SWAHILI) "Jina Kamili" else "Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(if (lang == AppLanguage.SWAHILI) "Barua Pepe" else "Email Address") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = user.phone,
                onValueChange = { },
                label = { Text(if (lang == AppLanguage.SWAHILI) "Namba ya Simu" else "Phone Number") },
                enabled = false, // Phone number change requires re-verification
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = if (lang == AppLanguage.SWAHILI)
                    "Kubadilisha namba ya simu kunahitaji uhakiki upya wa NIDA."
                else
                    "To change your phone number, you must complete re-verification.",
                fontSize = 10.sp,
                color = DopTextMuted
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = {
                    AuthManager.javaClass.getDeclaredField("_currentUser").apply { isAccessible = true }.set(AuthManager, kotlinx.coroutines.flow.MutableStateFlow(user.copy(name = name, email = email)))
                    onBack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DopNavyPrimary),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (lang == AppLanguage.SWAHILI) "Hifadhi Mabadiliko" else "Save Changes")
            }
        }
    }
}

@Composable
fun AccountAndRolesScreen(
    lang: AppLanguage = AppLanguage.SWAHILI,
    onBack: () -> Unit,
    onOpenOwnerOnboarding: () -> Unit,
    onOpenGuideApplication: () -> Unit
) {
    val s = strings(lang)
    val currentUser by AuthManager.currentUser.collectAsState()
    val user = currentUser ?: return
    
    var showSwitchConfirmation by remember { mutableStateOf<UserRole?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DopNeutralPearl)
    ) {
        Surface(
            color = DopSurfaceCard,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.dialogCloseButton, tint = DopNavyPrimary)
                }
                Text(
                    text = if (lang == AppLanguage.SWAHILI) "Akaunti na Majukumu" else "Account & Roles",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DopNavyPrimary
                )
            }
        }
        
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = if (lang == AppLanguage.SWAHILI) "NAFASI INAYOTUMIKA SASA" else "ACTIVE ACCOUNT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DopTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                when (user.activeMode) {
                    UserRole.CUSTOMER -> ActiveRoleCard(
                        title = s.roleCustomerTitle,
                        description = s.roleCustomerDesc,
                        color = DopCustomerBlueDark,
                        containerColor = DopCustomerBlueContainer,
                        badgeText = s.languageActiveBadge
                    )
                    UserRole.OWNER -> ActiveRoleCard(
                        title = s.roleOwnerTitle,
                        description = s.roleOwnerDesc,
                        color = DopOwnerGreenDark,
                        containerColor = DopOwnerGreenContainer,
                        badgeText = s.languageActiveBadge
                    )
                    UserRole.GUIDE -> ActiveRoleCard(
                        title = s.roleGuideTitle,
                        description = s.roleGuideDesc,
                        color = DopGuideAmberDark,
                        containerColor = DopGuideAmberContainer,
                        badgeText = s.languageActiveBadge
                    )
                    else -> {}
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (lang == AppLanguage.SWAHILI) "MAJUKUMU NA UWEZO UNAOPATIKANA" else "AVAILABLE CAPABILITIES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DopTextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Customer is always available
            if (user.activeMode != UserRole.CUSTOMER) {
                item {
                    AvailableRoleCard(
                        title = s.roleCustomerTitle,
                        description = if (lang == AppLanguage.SWAHILI) "Rudi kwenye akaunti yako ya mteja." else "Switch back to your customer account.",
                        accentColor = DopCustomerBlue,
                        switchText = if (lang == AppLanguage.SWAHILI) "Badili" else "Switch",
                        onClick = { showSwitchConfirmation = UserRole.CUSTOMER }
                    )
                }
            }
            
            // Owner capability
            item {
                if (user.canAccessOwner) {
                    if (user.activeMode != UserRole.OWNER) {
                        AvailableRoleCard(
                            title = s.roleOwnerTitle,
                            description = if (lang == AppLanguage.SWAHILI) "Badili kwenda kwenye dawati la mmiliki." else "Switch to your Owner workspace.",
                            accentColor = DopOwnerGreen,
                            switchText = if (lang == AppLanguage.SWAHILI) "Badili" else "Switch",
                            onClick = { showSwitchConfirmation = UserRole.OWNER }
                        )
                    }
                } else {
                    InactiveRoleCard(
                        title = s.roleOwnerTitle,
                        description = if (lang == AppLanguage.SWAHILI) "Sajili na simamia nyumba zako kwa urahisi." else "List and manage your properties.",
                        actionText = if (lang == AppLanguage.SWAHILI) "Washa Nafasi ya Mmiliki" else "List Your Property",
                        onClick = onOpenOwnerOnboarding
                    )
                }
            }
            
            // Guide capability
            item {
                val guideStatus = user.guideCapability
                if (guideStatus == GuideCapabilityStatus.APPROVED || guideStatus == GuideCapabilityStatus.ACTIVE) {
                    if (user.activeMode != UserRole.GUIDE) {
                        AvailableRoleCard(
                            title = s.roleGuideTitle,
                            description = if (lang == AppLanguage.SWAHILI) "Ingia kwenye dawati la mwongoza ziara za DoP." else "Switch to your DoP Guide workspace.",
                            accentColor = DopGuideAmberDark,
                            switchText = if (lang == AppLanguage.SWAHILI) "Badili" else "Switch",
                            onClick = { showSwitchConfirmation = UserRole.GUIDE }
                        )
                    }
                } else if (guideStatus == GuideCapabilityStatus.NOT_APPLIED) {
                    InactiveRoleCard(
                        title = s.roleGuideTitle,
                        description = if (lang == AppLanguage.SWAHILI) "Pata mapato ya uhakika kwa kuongoza wateja kutembelea nyumba." else "Earn money by guiding property viewings.",
                        actionText = if (lang == AppLanguage.SWAHILI) "Omba Kuwa Dalali wa DoP" else "Become a DoP Guide",
                        onClick = onOpenGuideApplication
                    )
                } else if (guideStatus == GuideCapabilityStatus.SUSPENDED) {
                    DopBentoCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = DopError)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.SWAHILI) "Udalali wa DoP Umesitishwa" else "DoP Guide Suspended",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = DopError
                                )
                                Text(
                                    text = if (lang == AppLanguage.SWAHILI) "Uwezo wako wa Dalali umesitishwa. Tafadhali wasiliana na Huduma kwa Wateja." else "Your Guide capability is currently suspended. Please contact DoP Support.",
                                    fontSize = 12.sp,
                                    color = DopTextSecondary
                                )
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
                                Text(
                                    text = if (lang == AppLanguage.SWAHILI) "Maombi ya Dalali wa DoP" else "DoP Guide Application",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "${if (lang == AppLanguage.SWAHILI) "Hali ya Maombi" else "Status"}: ${guideStatus.name}",
                                    fontSize = 12.sp,
                                    color = DopGuideAmberDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Switch Confirmation Dialog
    showSwitchConfirmation?.let { modeToSwitchTo ->
        AlertDialog(
            onDismissRequest = { showSwitchConfirmation = null },
            title = {
                val targetName = when (modeToSwitchTo) {
                    UserRole.CUSTOMER -> s.roleCustomerTitle
                    UserRole.OWNER -> s.roleOwnerTitle
                    UserRole.GUIDE -> s.roleGuideTitle
                    else -> modeToSwitchTo.name
                }
                Text(
                    text = if (lang == AppLanguage.SWAHILI) "Badili kwenda nafasi ya $targetName?" else "Switch to $targetName?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                val msg = when (modeToSwitchTo) {
                    UserRole.CUSTOMER -> if (lang == AppLanguage.SWAHILI) "Unabadili kurudi kwenye dawati la Mteja." else "You are switching back to Customer."
                    UserRole.OWNER -> if (lang == AppLanguage.SWAHILI) "Unabadili kwenda kwenye dawati la Mmiliki wa Nyumba." else "You are switching to your Owner workspace."
                    UserRole.GUIDE -> if (lang == AppLanguage.SWAHILI) "Unakaribia kuingia kwenye dawati la Dalali wa DoP." else "You are about to enter your DoP Guide workspace."
                    else -> ""
                }
                Text(msg)
            },
            confirmButton = {
                Button(
                    onClick = {
                        com.example.core.account.AccountManager.switchActiveMode(modeToSwitchTo)
                        showSwitchConfirmation = null
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when(modeToSwitchTo) {
                            UserRole.CUSTOMER -> DopCustomerBlue
                            UserRole.OWNER -> DopOwnerGreen
                            UserRole.GUIDE -> DopGuideAmber
                            else -> DopNavyPrimary
                        }
                    )
                ) {
                    Text(if (lang == AppLanguage.SWAHILI) "Endelea" else "Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSwitchConfirmation = null }) {
                    Text(s.dialogCancelButton, color = DopTextSecondary)
                }
            }
        )
    }
}

@Composable
fun ActiveRoleCard(title: String, description: String, color: Color, containerColor: Color, badgeText: String = "Inatumika") {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(10.dp).clip(CircleShape).background(color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = color)
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = color.copy(alpha = 0.2f)
                ) {
                    Text(badgeText, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(description, fontSize = 13.sp, color = DopNavyPrimary.copy(alpha = 0.8f))
        }
    }
}

@Composable
fun AvailableRoleCard(title: String, description: String, accentColor: Color, switchText: String = "Switch", onClick: () -> Unit) {
    DopBentoCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(10.dp).clip(CircleShape).background(accentColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, fontSize = 13.sp, color = DopTextSecondary)
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(switchText)
            }
        }
    }
}

@Composable
fun InactiveRoleCard(title: String, description: String, actionText: String, onClick: () -> Unit) {
    DopBentoCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DopNavyPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, fontSize = 13.sp, color = DopTextSecondary)
            }
            OutlinedButton(
                onClick = onClick,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(actionText, fontSize = 12.sp)
            }
        }
    }
}
