package com.crocoby.animeplayerua.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.crocoby.animeplayerua.UiConstants

@Composable
fun HorizontalPadding(
    content: @Composable () -> Unit
) {
    Box(Modifier.padding(horizontal = UiConstants.horizontalScreenPadding)) {
        content()
    }
}

@Composable
fun VerticalPadding(
    content: @Composable () -> Unit
) {
    Box(Modifier.padding(vertical = UiConstants.verticalScreenPadding),) {
        content()
    }
}

@Composable
fun TopPadding(
    content: @Composable () -> Unit
) {
    Box(Modifier.padding(top = UiConstants.verticalScreenPadding)) {
        content()
    }
}