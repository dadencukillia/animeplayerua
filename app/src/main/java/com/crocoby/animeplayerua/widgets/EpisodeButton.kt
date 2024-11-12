package com.crocoby.animeplayerua.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crocoby.animeplayerua.UiColors
import com.crocoby.animeplayerua.utils.focusBorder
import com.crocoby.animeplayerua.zeroCardElevation

@Composable
fun EpisodeButton(
    name: String,
    continueWatching: Boolean = false,
    onWatch: () -> Unit
) {
    val cardShape = RoundedCornerShape(24.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .focusBorder(shape = cardShape)
            .clip(cardShape)
            .clickable { onWatch() },
        colors = CardDefaults.cardColors(
            containerColor = if (continueWatching) UiColors.buttons else UiColors.greyBar
        ),
        shape = cardShape,
        elevation = CardDefaults.zeroCardElevation(),
    ) {
        Row(
            modifier = Modifier.padding(8.dp).height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(align = Alignment.CenterVertically),
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                ),
            )
            Spacer(Modifier.width(8.dp).weight(1f))
            if (continueWatching) {
                Text(
                    text = "Продовжити",
                    style = TextStyle(
                        fontSize = 12.sp
                    )
                )
            } else {
                IconButton(
                    onClick = {
                        onWatch()
                    }
                ) {
                    Icon(
                        modifier = Modifier.height(24.dp),
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "playIcon"
                    )
                }
            }
        }
    }
}