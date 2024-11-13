package com.crocoby.animeplayerua.widgets

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.crocoby.animeplayerua.navController
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun hideBars(systemUiController: SystemUiController) {
    systemUiController.isSystemBarsVisible = false
    systemUiController.isStatusBarVisible = false
    systemUiController.isNavigationBarVisible = false
}

fun showBars(systemUiController: SystemUiController) {
    systemUiController.isSystemBarsVisible = true
    systemUiController.isStatusBarVisible = true
    systemUiController.isNavigationBarVisible = true
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(modifier: Modifier, url: String) {
    val context = LocalContext.current.applicationContext
    val systemUiController = rememberSystemUiController()
    val focusRequester = remember { FocusRequester() }
    var lastBarVisibleState by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val exoPlayer: ExoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            repeatMode = ExoPlayer.REPEAT_MODE_OFF
            setWakeMode(C.WAKE_MODE_LOCAL)
            playWhenReady = true
            prepare()

            hideBars(systemUiController)

            play()
        }
    }
    val playerView = remember {
        val t = PlayerView(context).apply {
            player = exoPlayer
            useController = true
            FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        t.setControllerVisibilityListener(PlayerView.ControllerVisibilityListener {
            when (it) {
                PlayerView.VISIBLE -> showBars(systemUiController)
                PlayerView.INVISIBLE, PlayerView.GONE -> hideBars(systemUiController)
            }
        })

        t
    }

    LaunchedEffect(true) {
        launch {
            while (true) {
                if (systemUiController.isSystemBarsVisible || systemUiController.isStatusBarVisible || systemUiController.isNavigationBarVisible) {
                    if (!lastBarVisibleState) {
                        lastBarVisibleState = true
                        playerView.showController()
                    }
                } else {
                    lastBarVisibleState = false
                }
                delay(500)
            }
        }
    }

    AndroidView(
        modifier = modifier
            .safeDrawingPadding()
            .focusable()
            .focusRequester(focusRequester)
            .onKeyEvent {
                if (it.type != KeyEventType.KeyUp) {
                    return@onKeyEvent false
                }
                when (it.key) {
                    Key.DirectionCenter -> {
                        if (playerView.isControllerFullyVisible) {
                            if (exoPlayer.isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.play()
                            }
                        } else {
                            playerView.showController()
                        }
                    }
                    Key.DirectionLeft -> {
                        exoPlayer.seekBack()
                    }
                    Key.DirectionRight -> {
                        exoPlayer.seekForward()
                    }
                    Key.Back -> {
                        if (playerView.isControllerFullyVisible) {
                            playerView.hideController()
                        } else {
                            navController!!.navigateUp()
                        }
                    }
                }
                true
        },
        factory = {
            playerView
        },
        onRelease = {},
    )

    DisposableEffect(Unit) {
        focusRequester.requestFocus()
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) {
                exoPlayer.pause()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            exoPlayer.release()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}