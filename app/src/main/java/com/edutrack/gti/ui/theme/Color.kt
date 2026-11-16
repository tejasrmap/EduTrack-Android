package com.edutrack.gti.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Dark Aurora + Selective Liquid Glass Palette
val DarkAuroraBg = Color(0xFF070912)            // Deepest midnight space background
val MidnightBackground = DarkAuroraBg           // Alias for backward compatibility
val MidnightSurface = Color(0xFF0F172A)         // Slate 900
val MidnightSurfaceVariant = Color(0xFF1E293B)  // Slate 800
val MidnightBorder = Color(0xFF334155)          // Slate 700

// Liquid Glass Overlay Color Tokens
val LiquidGlassFill = Color(0x331E293B)
val LiquidGlassBorderColor = Color(0x40FFFFFF)
val LiquidGlassHighlight = Color(0x1A6366F1)

// Accent Palette
val ElectricIndigo = Color(0xFF6366F1)
val NeonCyan = Color(0xFF06B6D4)
val RadiantViolet = Color(0xFF8B5CF6)
val EmeraldGreen = Color(0xFF10B981)
val StreakOrange = Color(0xFFF97316)
val AmberGold = Color(0xFFF59E0B)
val GlowingAmber = AmberGold                   // Alias for backward compatibility
val CrimsonRose = Color(0xFFEF4444)

// Preset Subject Color Tag Hexes
val PresetSubjectColors = listOf(
    "#6366F1", // Indigo
    "#06B6D4", // Neon Cyan
    "#10B981", // Emerald Green
    "#F97316", // Streak Orange
    "#8B5CF6", // Radiant Violet
    "#EF4444", // Crimson Rose
    "#38BDF8", // Sky Blue
    "#EC4899"  // Pink
)

// Aurora Gradients
val HeroAuroraGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6), Color(0xFF06B6D4))
)

val StreakFlameGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFFF97316), Color(0xFFEF4444))
)

val DarkAuroraBgGradient = Brush.radialGradient(
    colors = listOf(Color(0xFF1B1947), Color(0xFF0F172A), Color(0xFF070912)),
    radius = 2200f
)

fun parseHexColor(colorHex: String): Color {
    return try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        ElectricIndigo
    }
}
