package com.crocoby.animeplayerua.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crocoby.animeplayerua.AnimeItem
import com.crocoby.animeplayerua.UiColors
import com.crocoby.animeplayerua.utils.focusBorder
import com.crocoby.animeplayerua.zeroCardElevation
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun AnimeCard(
    animeItem: AnimeItem,
    onClick: () -> Unit
) {
    val painterResource = asyncPainterResource(data = animeItem.imageUrl)
    val shape = RoundedCornerShape(24.dp)

    Card(
        modifier = Modifier
            .width(160.dp)
            .height(250.dp)
            .focusBorder(shape = shape)
            .clip(shape)
            .clickable {
                onClick()
            },
        shape = shape,
        elevation = CardDefaults.zeroCardElevation(),
        colors = CardDefaults.cardColors(
            containerColor = UiColors.greyBar
        )
    ) {
        Column(Modifier.padding(8.dp)) {
            KamelImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .weight(1f),
                resource = {
                    painterResource
                },
                contentDescription = "animePicture",
                contentScale = ContentScale.Crop,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                animeItem.name,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

@Composable
fun AnimeCardLoading() {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(250.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.zeroCardElevation(),
        colors = CardDefaults.cardColors(
            containerColor = UiColors.greyBar
        )
    ) {
        Column(Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .weight(1f)
                    .background(UiColors.background),
            )
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(UiColors.background),
            )
        }
    }
}