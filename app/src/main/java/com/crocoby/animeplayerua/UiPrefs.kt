package com.crocoby.animeplayerua

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object UiColors {
    val text = Color(0xFFFFFFFF)
    val error = Color(0xFFBA1A1A)
    val buttons = Color(0xFF8104F4)
    val greyBar = Color(0xFF323232)
    val background = Color(0xFF161616)
    val yellow = Color(0xFFFACC15)
}

object UiConstants {
    val horizontalScreenPadding = 24.dp
    val verticalScreenPadding = 32.dp
}

val darkScheme = darkColorScheme(
    primary = UiColors.buttons,
    secondary = UiColors.greyBar,
    surface = UiColors.greyBar,
    background = UiColors.background,

    onSurface = UiColors.text,
    onBackground = UiColors.text,
    onPrimary = UiColors.text,
    onSecondary = UiColors.text,
    onError = UiColors.error
)

@Composable
fun CardDefaults.zeroCardElevation() = cardElevation(0.dp, 0.dp, 0.dp, 0.dp, 0.dp, 0.dp)