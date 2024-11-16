package com.crocoby.animeplayerua.activities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.crocoby.animeplayerua.AnimeItem
import com.crocoby.animeplayerua.BuildConfig
import com.crocoby.animeplayerua.logic.CustomActivity
import com.crocoby.animeplayerua.logic.LatestAppVersionAndDownloadUrl
import com.crocoby.animeplayerua.logic.parser
import com.crocoby.animeplayerua.logic.runParser
import com.crocoby.animeplayerua.widgets.AnimeCategory
import com.crocoby.animeplayerua.widgets.AnimeCategoryLoading
import com.crocoby.animeplayerua.widgets.ApplicationScaffold
import com.crocoby.animeplayerua.widgets.HorizontalPadding
import com.crocoby.animeplayerua.widgets.SearchField
import com.crocoby.animeplayerua.widgets.TopPadding
import com.crocoby.animeplayerua.widgets.UpdateAlertDialog
import kotlinx.coroutines.launch

class HomeActivity : CustomActivity() {
    @Preview
    @Composable
    override fun Page() {
        var loaded by rememberSaveable { mutableStateOf(false) }
        val animeNew = rememberSaveable { mutableListOf<AnimeItem>() }
        val animeSeasonBest = rememberSaveable { mutableListOf<AnimeItem>() }
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

        runParser(
            function = {
                if (!loaded) {
                    val mainPage = getAnimeMainPage()
                    animeNew.addAll(mainPage.new)
                    animeSeasonBest.addAll(mainPage.bestSeason)
                    loaded = true
                }
            },
            onError = {
            },
        )

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

        ApplicationScaffold(this::class.java) {
            TopPadding {
                Column(Modifier.fillMaxSize()) {
                    HorizontalPadding {
                        SearchField {
                            if (it.isNotEmpty()) {
                                startActivity(SearchActivity.createIntent(this@HomeActivity, it))
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Column(
                        Modifier
                            .verticalScroll(rememberScrollState(0))
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        if (loaded) {
                            if (animeNew.isNotEmpty()) AnimeCategory("Новинки", animeNew) {
                                startActivity(InfoActivity.createIntent(this@HomeActivity, it.slug))
                            }
                            if (animeSeasonBest.isNotEmpty()) AnimeCategory("Найкраще за сезон", animeSeasonBest) {
                                startActivity(InfoActivity.createIntent(this@HomeActivity, it.slug))
                            }
                        } else {
                            AnimeCategoryLoading(20)
                            AnimeCategoryLoading(20)
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}