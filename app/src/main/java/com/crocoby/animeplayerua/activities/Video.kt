package com.crocoby.animeplayerua.activities

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.crocoby.animeplayerua.logic.CustomActivity
import com.crocoby.animeplayerua.logic.parser
import com.crocoby.animeplayerua.widgets.VideoPlayer
import kotlinx.coroutines.launch

class VideoActivity : CustomActivity() {
    companion object {
        fun createIntent(context: Context, iframeUrl: String): Intent {
            val intent = Intent(context, VideoActivity::class.java)
            intent.putExtra("iframeUrl", iframeUrl)

            return intent
        }
    }

    @Composable
    override fun Page() {
        val iframeUrl = intent.getStringExtra("iframeUrl")!!

        var videoUrl by remember { mutableStateOf("") }

        LaunchedEffect(true) {
            launch {
                try {
                    videoUrl = parser.getDirectUrlFromIFrame(iframeUrl)
                } catch (_: Exception) {
                    finish()
                }
            }
        }

        if (videoUrl.isEmpty()) {
            Box(Modifier.fillMaxSize().background(Color.Black))
        } else {
            VideoPlayer(
                modifier = Modifier.fillMaxSize().background(Color.Black),
                videoUrl
            )
        }
    }
}