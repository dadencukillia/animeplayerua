package com.crocoby.animeplayerua.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crocoby.animeplayerua.AnimeItem
import com.crocoby.animeplayerua.UiColors
import com.crocoby.animeplayerua.UiConstants
import kotlinx.coroutines.launch

@Composable
fun AnimeCategory(
    title: String,
    animeList: List<AnimeItem>,
    onClick: (AnimeItem) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column {
        HorizontalPadding {
            Text(
                title,
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
        Spacer(Modifier.height(20.dp))
        LazyRow(
            modifier = Modifier
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = DraggableState { delta ->
                        coroutineScope.launch {
                            lazyListState.scrollBy(-delta)
                        }
                    }
                ),
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            contentPadding = PaddingValues(horizontal = UiConstants.horizontalScreenPadding)
        ) {
            items(animeList) { item -> AnimeCard(item) { onClick(item) } }
        }
    }
}

@Composable
fun AnimeCategoryLoading(
    count: Int
) {
    Column {
        HorizontalPadding {
            Box(
                modifier = Modifier
                    .width(240.dp)
                    .height(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(UiColors.greyBar),
            )
        }
        Spacer(Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .padding(start = UiConstants.horizontalScreenPadding)
                .wrapContentWidth(
                    unbounded = true,
                    align = Alignment.Start
                ),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            for (i in 1..count) {
                AnimeCardLoading()
            }
        }
    }
}