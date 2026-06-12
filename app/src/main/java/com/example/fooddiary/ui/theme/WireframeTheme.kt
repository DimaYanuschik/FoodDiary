package com.example.fooddiary.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val WireframeColorScheme = lightColorScheme(
    primary = Color.Black,
    onPrimary = Color.White,
    primaryContainer = Color.LightGray,
    onPrimaryContainer = Color.Black,
    secondary = Color.DarkGray,
    onSecondary = Color.White,
    secondaryContainer = Color.LightGray,
    onSecondaryContainer = Color.Black,
    tertiary = Color.Gray,
    onTertiary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = Color.DarkGray,
    outline = Color.Gray,
    outlineVariant = Color.LightGray,
    error = Color.Black,
    onError = Color.White
)

@Composable
fun WireframeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WireframeColorScheme,
        content = content
    )
}