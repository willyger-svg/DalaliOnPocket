package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.model.UserRole

val LocalRoleThemeTokens = compositionLocalOf { DopRoleColors.Customer }

private val DarkColorScheme =
  darkColorScheme(
    primary = DopOchreLight,
    onPrimary = DopNavyDark,
    primaryContainer = DopOchre,
    onPrimaryContainer = Color.White,
    secondary = DopTrustGreenLight,
    onSecondary = DopNavyDark,
    secondaryContainer = DopTrustGreen,
    onSecondaryContainer = Color.White,
    background = DopNavyDark,
    onBackground = DopNeutralPearl,
    surface = DopNavySurface,
    onSurface = DopNeutralPearl,
    surfaceVariant = DopNavyElevated,
    onSurfaceVariant = DopTextMuted,
    outline = DopNavyElevated,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = DopNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = DopOchreContainer,
    onPrimaryContainer = DopOchre,
    secondary = DopTrustGreen,
    onSecondary = Color.White,
    secondaryContainer = DopTrustGreenContainer,
    onSecondaryContainer = DopTrustGreen,
    tertiary = DopOchre,
    onTertiary = Color.White,
    background = DopNeutralPearl,
    onBackground = DopTextPrimary,
    surface = DopSurfaceCard,
    onSurface = DopTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = DopTextSecondary,
    outline = DopBorderSubtle,
    error = DopError,
    errorContainer = DopErrorContainer,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  activeRole: UserRole? = null,
  content: @Composable () -> Unit,
) {
  val currentPreset by ThemeManager.currentPreset.collectAsState()
  val colorScheme = ThemeManager.getColorScheme(currentPreset)
  val roleTokens = DopRoleColors.forRole(activeRole)

  CompositionLocalProvider(LocalRoleThemeTokens provides roleTokens) {
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
  }
}
