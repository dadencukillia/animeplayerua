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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.crocoby.animeplayerua.AnimeItem
import com.crocoby.animeplayerua.logic.CustomActivity
import com.crocoby.animeplayerua.logic.runParser
import com.crocoby.animeplayerua.widgets.AnimeCategory
import com.crocoby.animeplayerua.widgets.AnimeCategoryLoading
import com.crocoby.animeplayerua.widgets.ApplicationScaffold
import com.crocoby.animeplayerua.widgets.TextBanner

class PlaylistsActivity: CustomActivity() {
    @Composable
    override fun Page() {
        val animeContinueWatching = remember { mutableListOf<AnimeItem>() }
        val animeLiked = remember { mutableListOf<AnimeItem>() }
        val animeWatched = remember { mutableListOf<AnimeItem>() }
        var loaded by remember { mutableStateOf(false) }

        runParser(
            function = {
                if (!loaded) {
                    animeContinueWatching.addAll(database.getEpisodeWatched().map {it.toAnimeItem()})
                    animeLiked.addAll(database.getLiked().map {it.toAnimeItem()})
                    animeWatched.addAll(database.getWatched().map {it.toAnimeItem()})

                    loaded = true
                }
            },
            onError = {}
        )

        ApplicationScaffold(this::class.java) {
            if (animeContinueWatching.isEmpty() && animeLiked.isEmpty() && animeWatched.isEmpty() && loaded) {
                TextBanner("Тут поки-що нічого немає ;)")
            } else {
                Column(Modifier.fillMaxSize()) {
                    Column(
                        Modifier
                            .verticalScroll(rememberScrollState(0))
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(32.dp),
                    ) {
                        Spacer(Modifier)
                        if (loaded) {
                            if (animeContinueWatching.isNotEmpty()) AnimeCategory(
                                "Продовжити перегляд",
                                animeContinueWatching
                            ) {
                                startActivity(InfoActivity.createIntent(this@PlaylistsActivity, it.slug))
                            }
                            if (animeLiked.isNotEmpty()) AnimeCategory(
                                "Сподобалося",
                                animeLiked
                            ) {
                                startActivity(InfoActivity.createIntent(this@PlaylistsActivity, it.slug))
                            }
                            if (animeWatched.isNotEmpty()) AnimeCategory(
                                "Переглянуто",
                                animeWatched
                            ) {
                                startActivity(InfoActivity.createIntent(this@PlaylistsActivity, it.slug))
                            }
                        } else {
                            AnimeCategoryLoading(20)
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