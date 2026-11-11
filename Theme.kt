package com.zyranto.app.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Gold = Color(0xFFF2B93B)
val GoldDark = Color(0xFFC8901A)
val Ink = Color(0xFF0B0B0B)

private val scheme = lightColorScheme(
    primary = Gold, onPrimary = Ink, secondary = Ink, onSecondary = Color.White,
    background = Color(0xFFF7F7F7), onBackground = Ink, surface = Color.White, onSurface = Ink
)

@Composable
fun ZyrantoTheme(content: @Composable () -> Unit) = MaterialTheme(colorScheme = scheme, content = content)
