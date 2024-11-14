package com.crocoby.animeplayerua

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.crocoby.animeplayerua.activities.HomeActivity
import com.crocoby.animeplayerua.activities.InfoActivity
import com.crocoby.animeplayerua.activities.PlaylistsActivity
import com.crocoby.animeplayerua.activities.SearchActivity
import com.crocoby.animeplayerua.activities.VideoActivity
import com.crocoby.animeplayerua.logic.AnimeDao
import com.crocoby.animeplayerua.logic.AppDatabase
import com.crocoby.animeplayerua.logic.LatestAppVersionAndDownloadUrl
import com.crocoby.animeplayerua.logic.parser
import com.crocoby.animeplayerua.widgets.UpdateAlertDialog
import kotlinx.coroutines.launch

var database: AnimeDao? = null
@SuppressLint("StaticFieldLeak")
var navController: NavHostController? = null

@Composable
fun App(context: Context) {
    navController = rememberNavController()
    database = remember { AppDatabase.getDatabase(context).getDao() }

    val coroutine = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current
    val appVersion = BuildConfig.VERSION_NAME
    var updatePopupInfo by remember { mutableStateOf<LatestAppVersionAndDownloadUrl?>(null) }
    var firstStart by rememberSaveable { mutableStateOf(true) }

    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        if (!firstStart) {
            return@LifecycleEventEffect
        }
        firstStart = false

        coroutine.launch {
            try {
                val resp = parser.getLatestAppVersionAndDownloadUrl()
                if (resp.version != appVersion) {
                    updatePopupInfo = resp
                }
            } catch (_: Exception) { }
        }
    }

    MaterialTheme(colorScheme = darkScheme) {
        updatePopupInfo?.let {
            UpdateAlertDialog(
                version = it.version,
                onClose = { updatePopupInfo = null },
                onOpen = {
                    uriHandler.openUri(it.downloadUrl)
                    updatePopupInfo = null
                }
            )
        }

        NavHost(
            navController!!,
            startDestination = Routes.HOME,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            builder = {
                composable(Routes.HOME) {
                    HomeActivity()
                }
                composable(Routes.PLAYLISTS) {
                    PlaylistsActivity()
                }
                composable(Routes.SEARCH + "/{query}") {
                    val query = it.arguments?.getString("query") ?: "Steins;Gate"
                    SearchActivity(query)
                }
                composable(Routes.ANIMEINFO + "/{slug}") {
                    val slug = it.arguments?.getString("slug") ?: "1886-shteynova-brama-steinsgate-steins-gate"
                    InfoActivity(slug)
                }
                composable(Routes.VIDEO + "/{url}") {
                    val iframeUrl = it.arguments?.getString("url")!!
                    VideoActivity(iframeUrl)
                }
            }
        )
    }
}