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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.crocoby.animeplayerua.AnimeItem
import com.crocoby.animeplayerua.logic.CustomActivity
import com.crocoby.animeplayerua.widgets.AnimeCategory
import com.crocoby.animeplayerua.widgets.AnimeCategoryLoading
import com.crocoby.animeplayerua.widgets.ApplicationScaffold
import com.crocoby.animeplayerua.widgets.TextBanner
import kotlinx.coroutines.launch

class PlaylistsActivity: CustomActivity() {
    @Composable
    override fun Page() {
        var animeContinueWatching by remember { mutableStateOf(listOf<AnimeItem>()) }
        var animeLiked by remember { mutableStateOf(listOf<AnimeItem>()) }
        var animeWatched by remember { mutableStateOf(listOf<AnimeItem>()) }
        var loaded by remember { mutableStateOf(false) }
        val coroutine = rememberCoroutineScope()

        LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
            coroutine.launch {
                val newContinueWatching = database.getEpisodeWatched().map { it.toAnimeItem() }
                val newLiked = database.getLiked().map { it.toAnimeItem() }
                val newWatched = database.getWatched().map { it.toAnimeItem() }

                if (newContinueWatching != animeContinueWatching) {
                    animeContinueWatching = newContinueWatching.toMutableList()
                }
                if (newLiked != animeLiked) {
                    animeLiked = newLiked.toMutableList()
                }
                if (newWatched != animeWatched) {
                    animeWatched = newWatched.toMutableList()
                }

                loaded = true
            }
        }

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

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0,0)
    }
}