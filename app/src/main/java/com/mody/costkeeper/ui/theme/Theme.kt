package com.mody.costkeeper.ui.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ==================== DARK COLOR SCHEME (v2.0 Bold) ====================

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = TextOnAccent,
    primaryContainer = PrimaryBlueContainer,
    onPrimaryContainer = PrimaryBlueVivid,

    secondary = IncomeGreen,
    onSecondary = TextOnAccent,
    secondaryContainer = IncomeGreenContainer,
    onSecondaryContainer = IncomeGreen,

    tertiary = ExpenseRed,
    onTertiary = TextOnAccent,
    tertiaryContainer = ExpenseRedContainer,
    onTertiaryContainer = ExpenseRed,

    error = ExpenseRed,
    onError = TextOnAccent,
    errorContainer = ExpenseRedContainer,
    onErrorContainer = ExpenseRed,

    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkSurfaceElevated,
    surfaceContainerHighest = DarkSurfaceHighest,

    outline = BorderDark,
    outlineVariant = DividerDark,

    inverseSurface = TextPrimary,
    inverseOnSurface = DarkBackground,
    inversePrimary = PrimaryBlueVivid
)

// ==================== LIGHT COLOR SCHEME (v2.0 Bold) ====================

private val LightColorScheme = lightColorScheme(
    primary = LightPrimaryBlue,
    onPrimary = LightTextOnAccent,
    primaryContainer = LightPrimaryBlueContainer,
    onPrimaryContainer = LightPrimaryBlue,

    secondary = LightIncomeGreen,
    onSecondary = LightTextOnAccent,
    secondaryContainer = LightIncomeGreenContainer,
    onSecondaryContainer = LightIncomeGreen,

    tertiary = LightExpenseRed,
    onTertiary = LightTextOnAccent,
    tertiaryContainer = LightExpenseRedContainer,
    onTertiaryContainer = LightExpenseRed,

    error = LightExpenseRed,
    onError = LightTextOnAccent,
    errorContainer = LightExpenseRedContainer,
    onErrorContainer = LightExpenseRed,

    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceElevated,
    onSurfaceVariant = LightTextSecondary,
    surfaceContainer = LightSurface,
    surfaceContainerHigh = LightSurfaceElevated,
    surfaceContainerHighest = LightSurfaceHighest,

    outline = LightBorder,
    outlineVariant = LightDivider,

    inverseSurface = LightTextPrimary,
    inverseOnSurface = LightBackground,
    inversePrimary = LightPrimaryBlue
)

// ==================== THEME COMPOSABLE ====================

@Composable
fun CostKeeperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as ComponentActivity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
