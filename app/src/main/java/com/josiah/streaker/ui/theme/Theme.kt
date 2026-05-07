package com.josiah.streaker.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppColors {
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

@Composable
fun StreakerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            background   = AppColors.Background,
            surface      = AppColors.Surface1,
            primary      = AppColors.Ember,
            onPrimary    = Color.White,
            onBackground = AppColors.TextPrimary,
            onSurface    = AppColors.TextPrimary,
        ),
        content = content
    )
}