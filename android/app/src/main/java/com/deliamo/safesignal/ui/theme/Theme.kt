package com.deliamo.safesignal.ui.theme

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
  primary = PrimaryDark,
  secondary = SecondaryDark,
  tertiary = TertiaryDark,

  background = BgDark,
  surface = SurfaceDark,

  onPrimary = OnDark,
  onSecondary = OnDark,
  onTertiary = OnDark,

  onBackground = OnLight,
  onSurface = OnLight,

  outline = Color(0xFF334155),
  outlineVariant = OutlineVariantDark,

  surfaceVariant = SurfaceVariantDark,
  onSurfaceVariant = OnSurfaceVariantDark,

  secondaryContainer = SecondaryContainerDark,
  onSecondaryContainer = OnSecondaryContainerDark,

  error = Error,

  surfaceTint = SurfaceVariantDark
)


private val LightColorScheme = lightColorScheme(
  primary = PrimaryLight,
  secondary = SecondaryLight,
  tertiary = TertiaryLight,

  background = BgLight,
  surface = SurfaceLight,

  onPrimary = Color.White,
  onSecondary = Color.White,
  onTertiary = Color.White,

  onBackground = OnDark,
  onSurface = OnDark,

  outline = OutlineLight,
  outlineVariant = OutlineVariantLight,

  surfaceTint = SurfaceTint,
  surfaceVariant = SurfaceVariantLight,
  onSurfaceVariant = OnSurfaceVariantLight,

  secondaryContainer = SecondaryContainerLight,
  onSecondaryContainer = OnSecondaryContainerLight,

  error = Error
)

@Composable
fun SpywareCheckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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

    MaterialTheme(colorScheme = colorScheme, typography = AppTypography, content = content)
  }
