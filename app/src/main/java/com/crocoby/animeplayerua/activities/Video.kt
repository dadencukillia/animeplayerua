package com.crocoby.animeplayerua.activities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.crocoby.animeplayerua.logic.runParser
import com.crocoby.animeplayerua.navController
import com.crocoby.animeplayerua.widgets.VideoPlayer

@Composable
fun VideoActivity(iframeUrl: String) {
    var videoUrl by remember { mutableStateOf("") }

    runParser(
        function = {
            videoUrl = getDirectUrlFromIFrame(iframeUrl)
        },
        onError = {
            navController!!.navigateUp()
        }
    )

    if (videoUrl.isEmpty()) {
        Box(Modifier.fillMaxSize().background(Color.Black))
    } else {
        VideoPlayer(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            videoUrl
        )
    }
}