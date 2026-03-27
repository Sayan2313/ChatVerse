package com.example.chatverse.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary             = VioletPrimary,
    onPrimary           = TextWhite,
    primaryContainer    = VioletContainer,
    onPrimaryContainer  = VioletLight,
    secondary           = CyanAccent,
    onSecondary         = DarkNavy,
    secondaryContainer  = CyanContainer,
    onSecondaryContainer= CyanAccent,
    tertiary            = MagentaAccent,
    onTertiary          = DarkNavy,
    tertiaryContainer   = MagentaContainer,
    onTertiaryContainer = MagentaAccent,
    background          = DarkNavy,
    onBackground        = TextWhite,
    surface             = DarkSurface,
    onSurface           = TextWhite,
    surfaceVariant      = DarkCard,
    onSurfaceVariant    = TextSilver,
    error               = Color(0xFFFF5370),
    onError             = TextWhite,
    outline             = GlassBorder
)

private val LightColorScheme = lightColorScheme(
    primary             = VioletPrimary,
    onPrimary           = TextWhite,
    primaryContainer    = Color(0xFFEDE7FF),
    onPrimaryContainer  = Color(0xFF3700B3),
    secondary           = Color(0xFF0097A7),
    onSecondary         = TextWhite,
    tertiary            = Color(0xFFAD1457),
    onTertiary          = TextWhite,
    background          = Color(0xFFF5F5FF),
    onBackground        = Color(0xFF0D0D1A),
    surface             = Color(0xFFFFFFFF),
    onSurface           = Color(0xFF0D0D1A),
    surfaceVariant      = Color(0xFFEEEEF7),
    onSurfaceVariant    = Color(0xFF44475A),
    error               = Color(0xFFD32F2F),
    outline             = Color(0xFF9090B0)
)

@Composable
fun ChatVerseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,   // disabled → use our custom palette
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else      -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,
        content     = content
    )
}