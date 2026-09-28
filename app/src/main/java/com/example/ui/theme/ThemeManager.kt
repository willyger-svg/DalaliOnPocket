package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemePreset(
    val id: String,
    val nameSwahili: String,
    val nameEnglish: String,
    val descriptionSwahili: String,
    val emoji: String,
    val primaryColor: Color,
    val onPrimaryColor: Color,
    val secondaryColor: Color,
    val onSecondaryColor: Color,
    val accentColor: Color,
    val backgroundColor: Color,
    val onBackgroundColor: Color,
    val surfaceColor: Color,
    val onSurfaceColor: Color,
    val surfaceVariantColor: Color,
    val isDark: Boolean = false
) {
    DOP_CLASSIC_NAVY(
        id = "dop_navy",
        nameSwahili = "DoP Navy & Dhahabu (Klasiki)",
        nameEnglish = "DoP Classic Navy & Ochre",
        descriptionSwahili = "Rangi rasmi za kifalme za Dalalion Pocket zenye heshima na umaridadi wa Kitanzania.",
        emoji = "🏛️",
        primaryColor = Color(0xFF0A192F),
        onPrimaryColor = Color.White,
        secondaryColor = Color(0xFF059669),
        onSecondaryColor = Color.White,
        accentColor = Color(0xFFD97706),
        backgroundColor = Color(0xFFF8FAFC),
        onBackgroundColor = Color(0xFF0F172A),
        surfaceColor = Color(0xFFFFFFFF),
        onSurfaceColor = Color(0xFF0F172A),
        surfaceVariantColor = Color(0xFFF1F5F9),
        isDark = false
    ),
    SERENGETI_SUNSET(
        id = "serengeti_sunset",
        nameSwahili = "Machweo ya Serengeti",
        nameEnglish = "Serengeti Sunset (Amber & Terracotta)",
        descriptionSwahili = "Rangi motomoto za dhahabu, machweo ya jua na mchanga wa bonde la Serengeti.",
        emoji = "🌅",
        primaryColor = Color(0xFF9A3412),
        onPrimaryColor = Color.White,
        secondaryColor = Color(0xFFD97706),
        onSecondaryColor = Color.White,
        accentColor = Color(0xFFF59E0B),
        backgroundColor = Color(0xFFFFFBEB),
        onBackgroundColor = Color(0xFF451A03),
        surfaceColor = Color(0xFFFFFFFF),
        onSurfaceColor = Color(0xFF451A03),
        surfaceVariantColor = Color(0xFFFEF3C7),
        isDark = false
    ),
    ZANZIBAR_OCEAN_TEAL(
        id = "zanzibar_teal",
        nameSwahili = "Zanzibar Bahari na Viungo",
        nameEnglish = "Zanzibar Ocean Teal & Mint",
        descriptionSwahili = "Mawimbi tulivu ya bahari ya Hindi, upepo wa fukwe za Nungwi na mikarafuu ya Pemba.",
        emoji = "🏝️",
        primaryColor = Color(0xFF0F766E),
        onPrimaryColor = Color.White,
        secondaryColor = Color(0xFF0284C7),
        onSecondaryColor = Color.White,
        accentColor = Color(0xFF06B6D4),
        backgroundColor = Color(0xFFF0FDFA),
        onBackgroundColor = Color(0xFF134E4A),
        surfaceColor = Color(0xFFFFFFFF),
        onSurfaceColor = Color(0xFF134E4A),
        surfaceVariantColor = Color(0xFFCCFBF1),
        isDark = false
    ),
    KILIMANJARO_SNOW(
        id = "kilimanjaro_snow",
        nameSwahili = "Theluji ya Kilimanjaro",
        nameEnglish = "Kilimanjaro Minimal Crisp",
        descriptionSwahili = "Mwonekano safi kama barafu ya Kibo, minimalist yenye utulivu na uwazi wa hali ya juu.",
        emoji = "🏔️",
        primaryColor = Color(0xFF1E293B),
        onPrimaryColor = Color.White,
        secondaryColor = Color(0xFF0284C7),
        onSecondaryColor = Color.White,
        accentColor = Color(0xFF38BDF8),
        backgroundColor = Color(0xFFF8FAFC),
        onBackgroundColor = Color(0xFF0F172A),
        surfaceColor = Color(0xFFFFFFFF),
        onSurfaceColor = Color(0xFF0F172A),
        surfaceVariantColor = Color(0xFFE2E8F0),
        isDark = false
    ),
    NGORONGORO_SAFARI(
        id = "ngorongoro_safari",
        nameSwahili = "Mbuga ya Ngorongoro",
        nameEnglish = "Ngorongoro Forest & Safari",
        descriptionSwahili = "Kijani asili cha misitu, mimea ya kreta na harufu ya ardhi yenye amani tele.",
        emoji = "🌿",
        primaryColor = Color(0xFF14532D),
        onPrimaryColor = Color.White,
        secondaryColor = Color(0xFF16A34A),
        onSecondaryColor = Color.White,
        accentColor = Color(0xFF84CC16),
        backgroundColor = Color(0xFFF7FEE7),
        onBackgroundColor = Color(0xFF14532D),
        surfaceColor = Color(0xFFFFFFFF),
        onSurfaceColor = Color(0xFF14532D),
        surfaceVariantColor = Color(0xFFECFCCB),
        isDark = false
    ),
    DAR_CYBERPUNK(
        id = "dar_cyberpunk",
        nameSwahili = "Dar Usiku (Neon & Violet)",
        nameEnglish = "Dar Neon Dusk",
        descriptionSwahili = "Vibe ya usiku wa jiji lenye taa za neon, muziki, daraja la Tanzanite na msisimko.",
        emoji = "🌃",
        primaryColor = Color(0xFF4338CA),
        onPrimaryColor = Color.White,
        secondaryColor = Color(0xFF7C3AED),
        onSecondaryColor = Color.White,
        accentColor = Color(0xFFF43F5E),
        backgroundColor = Color(0xFFFAF5FF),
        onBackgroundColor = Color(0xFF312E81),
        surfaceColor = Color(0xFFFFFFFF),
        onSurfaceColor = Color(0xFF312E81),
        surfaceVariantColor = Color(0xFFF3E8FF),
        isDark = false
    ),
    ONYX_MIDNIGHT_LUXE(
        id = "onyx_midnight",
        nameSwahili = "Onyx ya Kifahari (Dark AMOLED)",
        nameEnglish = "Onyx Midnight Luxe Gold",
        descriptionSwahili = "Mandhari meusi ya giza totoro yasiyoumiza macho, yanaokoa betri na kung'arisha dhahabu safi.",
        emoji = "✨",
        primaryColor = Color(0xFFEAB308), // Gold primary
        onPrimaryColor = Color(0xFF09090B),
        secondaryColor = Color(0xFF10B981),
        onSecondaryColor = Color(0xFF09090B),
        accentColor = Color(0xFFF59E0B),
        backgroundColor = Color(0xFF09090B),
        onBackgroundColor = Color(0xFFF4F4F5),
        surfaceColor = Color(0xFF18181B),
        onSurfaceColor = Color(0xFFF4F4F5),
        surfaceVariantColor = Color(0xFF27272A),
        isDark = true
    ),
    ROYAL_AMETHYST(
        id = "royal_amethyst",
        nameSwahili = "Zambarau ya Kifalme",
        nameEnglish = "Royal Amethyst & Rose",
        descriptionSwahili = "Hadhi ya juu ya kifalme, muonekano maridadi unaovutia jicho na kuacha kumbukumbu nzuri.",
        emoji = "👑",
        primaryColor = Color(0xFF581C87),
        onPrimaryColor = Color.White,
        secondaryColor = Color(0xFF9333EA),
        onSecondaryColor = Color.White,
        accentColor = Color(0xFFE879F9),
        backgroundColor = Color(0xFFFAF5FF),
        onBackgroundColor = Color(0xFF3B0764),
        surfaceColor = Color(0xFFFFFFFF),
        onSurfaceColor = Color(0xFF3B0764),
        surfaceVariantColor = Color(0xFFF3E8FF),
        isDark = false
    ),
    SAHARA_GOLD(
        id = "sahara_gold",
        nameSwahili = "Dhahabu ya Afrika (Warm Sand)",
        nameEnglish = "African Gold & Sand",
        descriptionSwahili = "Rangi ya utajiri na madini ya dhahabu, mchanga wa dhahabu na mwanga angavu.",
        emoji = "🏺",
        primaryColor = Color(0xFFB45309),
        onPrimaryColor = Color.White,
        secondaryColor = Color(0xFFCA8A04),
        onSecondaryColor = Color.White,
        accentColor = Color(0xFFEAB308),
        backgroundColor = Color(0xFFFFFDF5),
        onBackgroundColor = Color(0xFF451A03),
        surfaceColor = Color(0xFFFFFFFF),
        onSurfaceColor = Color(0xFF451A03),
        surfaceVariantColor = Color(0xFFFEF9C3),
        isDark = false
    ),
    CORAL_PEACH(
        id = "coral_peach",
        nameSwahili = "Matumbawe ya Bahari (Coral & Peach)",
        nameEnglish = "Ocean Coral & Warm Peach",
        descriptionSwahili = "Rangi nyororo zenye mapenzi na ukarimu wa Kitanzania, laini na rafiki sana kwa macho.",
        emoji = "🪸",
        primaryColor = Color(0xFFBE123C),
        onPrimaryColor = Color.White,
        secondaryColor = Color(0xFFFB7185),
        onSecondaryColor = Color.White,
        accentColor = Color(0xFFF43F5E),
        backgroundColor = Color(0xFFFFF1F2),
        onBackgroundColor = Color(0xFF4C0519),
        surfaceColor = Color(0xFFFFFFFF),
        onSurfaceColor = Color(0xFF4C0519),
        surfaceVariantColor = Color(0xFFFFE4E6),
        isDark = false
    )
}

object ThemeManager {
    private val _currentPreset = MutableStateFlow(AppThemePreset.DOP_CLASSIC_NAVY)
    val currentPreset: StateFlow<AppThemePreset> = _currentPreset.asStateFlow()

    fun setPreset(preset: AppThemePreset) {
        _currentPreset.value = preset
    }

    fun getColorScheme(preset: AppThemePreset): ColorScheme {
        return if (preset.isDark) {
            darkColorScheme(
                primary = preset.primaryColor,
                onPrimary = preset.onPrimaryColor,
                primaryContainer = preset.surfaceVariantColor,
                onPrimaryContainer = preset.primaryColor,
                secondary = preset.secondaryColor,
                onSecondary = preset.onSecondaryColor,
                secondaryContainer = preset.surfaceVariantColor,
                onSecondaryContainer = preset.secondaryColor,
                tertiary = preset.accentColor,
                onTertiary = Color.White,
                background = preset.backgroundColor,
                onBackground = preset.onBackgroundColor,
                surface = preset.surfaceColor,
                onSurface = preset.onSurfaceColor,
                surfaceVariant = preset.surfaceVariantColor,
                onSurfaceVariant = preset.onSurfaceColor.copy(alpha = 0.7f),
                outline = Color(0xFF3F3F46),
                error = Color(0xFFEF4444)
            )
        } else {
            lightColorScheme(
                primary = preset.primaryColor,
                onPrimary = preset.onPrimaryColor,
                primaryContainer = preset.surfaceVariantColor,
                onPrimaryContainer = preset.primaryColor,
                secondary = preset.secondaryColor,
                onSecondary = preset.onSecondaryColor,
                secondaryContainer = preset.surfaceVariantColor,
                onSecondaryContainer = preset.secondaryColor,
                tertiary = preset.accentColor,
                onTertiary = Color.White,
                background = preset.backgroundColor,
                onBackground = preset.onBackgroundColor,
                surface = preset.surfaceColor,
                onSurface = preset.onSurfaceColor,
                surfaceVariant = preset.surfaceVariantColor,
                onSurfaceVariant = preset.onBackgroundColor.copy(alpha = 0.7f),
                outline = Color(0xFFCBD5E1),
                error = Color(0xFFDC2626)
            )
        }
    }
}
