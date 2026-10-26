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
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

data class AppThemeColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val topBarBackground: Color,
    val bottomBarBackground: Color,
    val navItemUnselected: Color,
    val goldAccent: Color,
    val goldAccentLight: Color,
    val chipBackground: Color,
    val chipBorder: Color,
    val inputBackground: Color,
    val inputBorder: Color,
    val sectionHeader: Color,
    val dividerColor: Color
)

val lightThemeColors = AppThemeColors(
    isDark = false,
    background = Color(0xFFF8FAFC),      // Crisp clean light slate background
    surface = Color(0xFFFFFFFF),         // Pure white cards
    surfaceVariant = Color(0xFFF1F5F9),  // Light slate subtle accent container
    cardBackground = Color(0xFFFFFFFF),
    cardBorder = Color(0xFFE2E8F0),      // Crisp modern subtle card border
    textPrimary = Color(0xFF0F172A),      // Deep dark slate/navy for maximum readability
    textSecondary = Color(0xFF475569),    // Slate 600
    textMuted = Color(0xFF64748B),        // Slate 500
    topBarBackground = Color(0xFFFFFFFF), // Clean white top bar
    bottomBarBackground = Color(0xFFFFFFFF), // Clean white bottom nav
    navItemUnselected = Color(0xFF64748B),
    goldAccent = Color(0xFFB45309),       // Rich Amber Gold (superb contrast on white)
    goldAccentLight = Color(0xFFD97706),
    chipBackground = Color(0xFFF1F5F9),
    chipBorder = Color(0xFFCBD5E1),
    inputBackground = Color(0xFFF8FAFC),
    inputBorder = Color(0xFFCBD5E1),
    sectionHeader = Color(0xFF475569),
    dividerColor = Color(0xFFE2E8F0)
)

val darkThemeColors = AppThemeColors(
    isDark = true,
    background = Color(0xFF0B1220),      // MidnightNavy
    surface = Color(0xFF101827),         // DarkSurface
    surfaceVariant = Color(0xFF162033),
    cardBackground = Color(0xFF101827),
    cardBorder = Color(0xFF1F2E47),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFFA8B3C2),
    textMuted = Color(0xFF64748B),
    topBarBackground = Color(0xFF0B1220),
    bottomBarBackground = Color(0xFF0B1220),
    navItemUnselected = Color(0xFF94A3B8),
    goldAccent = Color(0xFFD6B36A),       // ChampagneGold
    goldAccentLight = Color(0xFFFDE68A),
    chipBackground = Color(0xFF162035),
    chipBorder = Color(0xFF2A3A5A),
    inputBackground = Color(0xFF17243B),
    inputBorder = Color(0xFF273854),
    sectionHeader = Color(0xFF94A3B8),
    dividerColor = Color(0xFF1F2E47)
)

val LocalAppThemeColors = compositionLocalOf { lightThemeColors }

object AppColors {
    val isDark: Boolean @Composable get() = LocalAppThemeColors.current.isDark
    val background: Color @Composable get() = LocalAppThemeColors.current.background
    val surface: Color @Composable get() = LocalAppThemeColors.current.surface
    val surfaceVariant: Color @Composable get() = LocalAppThemeColors.current.surfaceVariant
    val cardBackground: Color @Composable get() = LocalAppThemeColors.current.cardBackground
    val cardBorder: Color @Composable get() = LocalAppThemeColors.current.cardBorder
    val textPrimary: Color @Composable get() = LocalAppThemeColors.current.textPrimary
    val textSecondary: Color @Composable get() = LocalAppThemeColors.current.textSecondary
    val textMuted: Color @Composable get() = LocalAppThemeColors.current.textMuted
    val topBarBackground: Color @Composable get() = LocalAppThemeColors.current.topBarBackground
    val bottomBarBackground: Color @Composable get() = LocalAppThemeColors.current.bottomBarBackground
    val navItemUnselected: Color @Composable get() = LocalAppThemeColors.current.navItemUnselected
    val goldAccent: Color @Composable get() = LocalAppThemeColors.current.goldAccent
    val goldAccentLight: Color @Composable get() = LocalAppThemeColors.current.goldAccentLight
    val chipBackground: Color @Composable get() = LocalAppThemeColors.current.chipBackground
    val chipBorder: Color @Composable get() = LocalAppThemeColors.current.chipBorder
    val inputBackground: Color @Composable get() = LocalAppThemeColors.current.inputBackground
    val inputBorder: Color @Composable get() = LocalAppThemeColors.current.inputBorder
    val sectionHeader: Color @Composable get() = LocalAppThemeColors.current.sectionHeader
    val dividerColor: Color @Composable get() = LocalAppThemeColors.current.dividerColor
}

private val DarkColorScheme = darkColorScheme(
    primary = RoyalGold,
    onPrimary = Color(0xFF1A1300),
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = RoyalBlueLight,
    onSecondary = Color(0xFF001D4D),
    secondaryContainer = RoyalBlueContainer,
    onSecondaryContainer = Color(0xFFD8E2FF),
    tertiary = EmeraldGreenLight,
    onTertiary = Color(0xFF003924),
    tertiaryContainer = EmeraldContainer,
    onTertiaryContainer = Color(0xFFA7F3D0),
    background = MidnightNavy,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkCardBorder,
    error = RubyDanger,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFB45309),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFEF3C7),
    onPrimaryContainer = Color(0xFF78350F),
    secondary = Color(0xFF1D4ED8),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDBEAFE),
    onSecondaryContainer = Color(0xFF1E3A8A),
    tertiary = Color(0xFF047857),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF064E3B),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFE2E8F0),
    error = RubyDanger,
    onError = Color.White
)

@Composable
fun TaxmitraTheme(
    darkTheme: Boolean = false, // Normal Mode (Light) by default per user request
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val appThemeColors = if (darkTheme) darkThemeColors else lightThemeColors

    CompositionLocalProvider(LocalAppThemeColors provides appThemeColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

