package com.example.simpleimageeditor.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color // Added this import
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.material3.ColorScheme // Added this import

// Extension property to check if the ColorScheme is light
import androidx.compose.ui.graphics.luminance // Added this import

val ColorScheme.isLight: Boolean
    get() = primary.luminance() > 0.5f // A simple heuristic, adjust as needed

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    secondary = SecondaryDark,
    tertiary = SecondaryDark, // Using secondary for tertiary as well for simplicity
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = TextLight,
    onSecondary = TextLight,
    onTertiary = TextLight,
    onBackground = TextLight,
    onSurface = TextLight
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    secondary = SecondaryLight,
    tertiary = SecondaryLight, // Using secondary for tertiary as well for simplicity
    background = Color(0xFFFFFFFF), // White background for light theme
    surface = Color(0xFFF5F5F5), // Light gray surface for light theme
    onPrimary = TextDark,
    onSecondary = TextDark,
    onTertiary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun SimpleImageEditorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
        // Removed SideEffect for status bar color as it's handled by SystemUiController in MainActivity
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
