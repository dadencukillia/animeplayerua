package com.crocoby.animeplayerua.widgets

import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crocoby.animeplayerua.UiColors
import com.crocoby.animeplayerua.UiConstants
import com.crocoby.animeplayerua.utils.focusBorder
import kotlinx.coroutines.launch

@Composable
fun AnimePlaylistRow(
    playlistItems: List<String>,
    selected: Int = 0,
    onToggle: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState(0)
    val buttonShape = RoundedCornerShape(24.dp)

    Box(
        modifier = Modifier
            .draggable(
                orientation = Orientation.Horizontal,
                state = DraggableState { delta ->
                    coroutineScope.launch {
                        scrollState.scrollBy(-delta)
                    }
                }
            )
            .horizontalScroll(scrollState)
            .fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(horizontal = UiConstants.horizontalScreenPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            for ((index, item) in playlistItems.withIndex()) {
                Button(
                    modifier = Modifier.focusBorder(shape = buttonShape),
                    onClick = {
                        if (selected != index) {
                            onToggle(index)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selected == index) UiColors.buttons else UiColors.greyBar
                    ),
                    shape = buttonShape,
                    contentPadding = PaddingValues(8.dp)
                ) {
                    Text(
                        text = item,
                        maxLines = 1,
                        style = TextStyle(
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    }
}