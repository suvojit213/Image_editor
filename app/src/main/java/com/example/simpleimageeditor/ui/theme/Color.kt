package com.example.simpleimageeditor.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Colors
val PrimaryDark = Color(0xFF2196F3) // A vibrant blue
val PrimaryLight = Color(0xFF64B5F6) // A lighter shade of the primary blue

// Secondary Colors
val SecondaryDark = Color(0xFF00BCD4) // A teal/cyan for accents
val SecondaryLight = Color(0xFF4DD0E1) // A lighter shade of the secondary

// Backgrounds
val BackgroundDark = Color(0xFF121212) // Very dark for true black theme
val SurfaceDark = Color(0xFF1E1E1E) // Slightly lighter than background for cards/surfaces

// Text Colors
val TextLight = Color(0xFFFFFFFF) // White text on dark backgrounds
val TextDark = Color(0xFF000000) // Black text on light backgrounds

// Other useful colors
val ErrorRed = Color(0xFFB00020)
val SuccessGreen = Color(0xFF4CAF50)

// Keeping original DarkBackground and LightText for now, but will update their usage in Theme.kt
val DarkBackground = BackgroundDark
val LightText = TextLight