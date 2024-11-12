package com.crocoby.animeplayerua.utils

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.focusBorder(
    enabled: Boolean = true,
    shape: Shape = RectangleShape
): Modifier {
    if (!enabled) {
        return this
    }

    var focusState = remember { MutableInteractionSource() }
    val focused = focusState.collectIsFocusedAsState().value

    var modifier = this then Modifier.focusable(true, focusState)
    if (focused) {
        modifier = modifier.border(1.dp, Color.White, shape)
    }

    return modifier
}