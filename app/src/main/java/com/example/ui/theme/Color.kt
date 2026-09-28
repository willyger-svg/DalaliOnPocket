package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Dalalion Pocket (DoP) Tanzanian Brand Identity
val DopNavyPrimary = Color(0xFF0A192F)
val DopNavyDark = Color(0xFF020C1B)
val DopNavySurface = Color(0xFF112240)
val DopNavyElevated = Color(0xFF233554)

val DopOchre = Color(0xFFD97706)
val DopOchreLight = Color(0xFFF59E0B)
val DopOchreContainer = Color(0xFFFEF3C7)

val DopTrustGreen = Color(0xFF059669)
val DopTrustGreenLight = Color(0xFF10B981)
val DopTrustGreenContainer = Color(0xFFD1FAE5)

val DopNeutralPearl = Color(0xFFF8FAFC)
val DopSurfaceCard = Color(0xFFFFFFFF)
val DopBorderSubtle = Color(0xFFE2E8F0)
val DopTextPrimary = Color(0xFF0F172A)
val DopTextSecondary = Color(0xFF64748B)
val DopTextMuted = Color(0xFF94A3B8)
val DopError = Color(0xFFDC2626)
val DopErrorContainer = Color(0xFFFEE2E2)

// ============================================================
// CENTRALIZED ROLE COLOR TOKENS (Identity, Accent & Context)
// ============================================================

// Customer Role Accents (Blue)
val DopCustomerBlue = Color(0xFF1E88E5)
val DopCustomerBlueDark = Color(0xFF0D47A1)
val DopCustomerBlueLight = Color(0xFF64B5F6)
val DopCustomerBlueContainer = Color(0xFFEFF6FF)
val DopCustomerBlueBorder = Color(0xFFBFDBFE)

// Owner Role Accents (Green)
val DopOwnerGreen = Color(0xFF059669)
val DopOwnerGreenDark = Color(0xFF065F46)
val DopOwnerGreenLight = Color(0xFF10B981)
val DopOwnerGreenContainer = Color(0xFFECFDF5)
val DopOwnerGreenBorder = Color(0xFFA7F3D0)

// Guide Role Accents (Yellow / Warm Amber)
val DopGuideAmber = Color(0xFFD97706)
val DopGuideAmberDark = Color(0xFFB45309)
val DopGuideAmberLight = Color(0xFFF59E0B)
val DopGuideAmberContainer = Color(0xFFFFFBEB)
val DopGuideAmberBorder = Color(0xFFFDE68A)

// Admin Role Accents (Pink / Cyber Rose)
val DopAdminPink = Color(0xFFFF2A85)
val DopAdminPinkDark = Color(0xFFC2185B)
val DopAdminPinkLight = Color(0xFFFF80AB)
val DopAdminPinkContainer = Color(0xFF2B0A22)
val DopAdminPinkBorder = Color(0xFFF472B6)

data class RoleThemeTokens(
    val primaryAccent: Color,
    val darkAccent: Color,
    val lightAccent: Color,
    val containerColor: Color,
    val borderColor: Color,
    val onContainerColor: Color,
    val roleBadgeTitleEn: String,
    val roleBadgeTitleSw: String
) {
    val roleAccent: Color get() = primaryAccent
    val roleBadgeBackground: Color get() = containerColor
    val roleBorder: Color get() = borderColor
    val roleAccentContainer: Color get() = containerColor
}

object DopRoleColors {
    val Customer = RoleThemeTokens(
        primaryAccent = DopCustomerBlue,
        darkAccent = DopCustomerBlueDark,
        lightAccent = DopCustomerBlueLight,
        containerColor = DopCustomerBlueContainer,
        borderColor = DopCustomerBlueBorder,
        onContainerColor = DopCustomerBlueDark,
        roleBadgeTitleEn = "CUSTOMER",
        roleBadgeTitleSw = "MTEJA"
    )

    val Owner = RoleThemeTokens(
        primaryAccent = DopOwnerGreen,
        darkAccent = DopOwnerGreenDark,
        lightAccent = DopOwnerGreenLight,
        containerColor = DopOwnerGreenContainer,
        borderColor = DopOwnerGreenBorder,
        onContainerColor = DopOwnerGreenDark,
        roleBadgeTitleEn = "OWNER",
        roleBadgeTitleSw = "MWENYE MALI"
    )

    val Guide = RoleThemeTokens(
        primaryAccent = DopGuideAmber,
        darkAccent = DopGuideAmberDark,
        lightAccent = DopGuideAmberLight,
        containerColor = DopGuideAmberContainer,
        borderColor = DopGuideAmberBorder,
        onContainerColor = DopGuideAmberDark,
        roleBadgeTitleEn = "DoP GUIDE",
        roleBadgeTitleSw = "DALALI RASMI"
    )

    val Admin = RoleThemeTokens(
        primaryAccent = DopAdminPink,
        darkAccent = DopAdminPinkDark,
        lightAccent = DopAdminPinkLight,
        containerColor = DopAdminPinkContainer,
        borderColor = DopAdminPinkBorder,
        onContainerColor = DopAdminPinkLight,
        roleBadgeTitleEn = "ADMIN",
        roleBadgeTitleSw = "MSIMAMIZI"
    )

    fun forRole(role: com.example.data.model.UserRole?): RoleThemeTokens = when (role) {
        com.example.data.model.UserRole.OWNER -> Owner
        com.example.data.model.UserRole.GUIDE -> Guide
        com.example.data.model.UserRole.ADMIN -> Admin
        else -> Customer
    }
}

