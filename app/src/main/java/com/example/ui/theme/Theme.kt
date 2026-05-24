package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = SleekIndigo,
    onPrimary = Color.White,
    primaryContainer = SleekDarkBorder,
    onPrimaryContainer = SleekIndigo,
    secondary = SleekDarkSecondary,
    onSecondary = Color.White,
    tertiary = SleekPink,
    background = SleekDarkBg,
    onBackground = SleekDarkOnSurface,
    surface = SleekDarkSurface,
    onSurface = SleekDarkOnSurface,
    surfaceVariant = SleekDarkBorder,
    onSurfaceVariant = SleekDarkSecondary,
    outline = SleekDarkBorder,
    outlineVariant = SleekDarkBorder
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SleekIndigo,
    onPrimary = Color.White,
    primaryContainer = SleekIndigoContainer,
    onPrimaryContainer = SleekIndigo,
    secondary = SleekSecondary,
    onSecondary = Color.White,
    tertiary = SleekPink,
    background = SleekBackground,
    onBackground = SleekOnSurface,
    surface = SleekSurface,
    onSurface = SleekOnSurface,
    surfaceVariant = SleekSurfaceVariant,
    onSurfaceVariant = SleekSecondary,
    outline = SleekOutline,
    outlineVariant = SleekBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disabling dynamic colors by default so the highly-tailored "Sleek Interface" Indigo theme displays beautifully
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
