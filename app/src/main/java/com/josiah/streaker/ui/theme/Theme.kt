package com.josiah.streaker.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

object DarkColors {
    val Background    = Color(0xFF0F0F12)
    val Surface1      = Color(0xFF1A1A22)
    val Surface2      = Color(0xFF242430)
    val Ember         = Color(0xFFFF6B2B)
    val EmberLight    = Color(0xFFFF9A5C)
    val Gold          = Color(0xFFFFCC44)
    val TextPrimary   = Color(0xFFF2F2F4)
    val TextSecondary = Color(0xFF8888A0)
    val Destructive   = Color(0xFFFF4466)
    val Success       = Color(0xFF44DD88)
}

object LightColors {
    val Background    = Color(0xFFF5F5F7)
    val Surface1      = Color(0xFFFFFFFF)
    val Surface2      = Color(0xFFEEEEF2)
    val Ember         = Color(0xFFFF6B2B)
    val EmberLight    = Color(0xFFFF9A5C)
    val Gold          = Color(0xFFFFAA00)
    val TextPrimary   = Color(0xFF111114)
    val TextSecondary = Color(0xFF777788)
    val Destructive   = Color(0xFFDD2244)
    val Success       = Color(0xFF22AA66)
}

// Global theme colors that switch based on mode
object AppColors {
    var Background    = DarkColors.Background
    var Surface1      = DarkColors.Surface1
    var Surface2      = DarkColors.Surface2
    var Ember         = DarkColors.Ember
    var EmberLight    = DarkColors.EmberLight
    var Gold          = DarkColors.Gold
    var TextPrimary   = DarkColors.TextPrimary
    var TextSecondary = DarkColors.TextSecondary
    var Destructive   = DarkColors.Destructive
    var Success       = DarkColors.Success

    fun applyDark() {
        Background    = DarkColors.Background
        Surface1      = DarkColors.Surface1
        Surface2      = DarkColors.Surface2
        Ember         = DarkColors.Ember
        EmberLight    = DarkColors.EmberLight
        Gold          = DarkColors.Gold
        TextPrimary   = DarkColors.TextPrimary
        TextSecondary = DarkColors.TextSecondary
        Destructive   = DarkColors.Destructive
        Success       = DarkColors.Success
    }

    fun applyLight() {
        Background    = LightColors.Background
        Surface1      = LightColors.Surface1
        Surface2      = LightColors.Surface2
        Ember         = LightColors.Ember
        EmberLight    = LightColors.EmberLight
        Gold          = LightColors.Gold
        TextPrimary   = LightColors.TextPrimary
        TextSecondary = LightColors.TextSecondary
        Destructive   = LightColors.Destructive
        Success       = LightColors.Success
    }
}

val LocalIsDarkTheme = compositionLocalOf { true }

@Composable
fun StreakerTheme(
    isDarkTheme: Boolean = true,
    content:     @Composable () -> Unit
) {
    // Apply correct colors
    if (isDarkTheme) AppColors.applyDark() else AppColors.applyLight()

    val colorScheme = if (isDarkTheme) {
        darkColorScheme(
            background   = DarkColors.Background,
            surface      = DarkColors.Surface1,
            primary      = DarkColors.Ember,
            onPrimary    = Color.White,
            onBackground = DarkColors.TextPrimary,
            onSurface    = DarkColors.TextPrimary,
        )
    } else {
        lightColorScheme(
            background   = LightColors.Background,
            surface      = LightColors.Surface1,
            primary      = LightColors.Ember,
            onPrimary    = Color.White,
            onBackground = LightColors.TextPrimary,
            onSurface    = LightColors.TextPrimary,
        )
    }

    CompositionLocalProvider(LocalIsDarkTheme provides isDarkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            content     = content
        )
    }
}