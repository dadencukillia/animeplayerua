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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.crocoby.animeplayerua.AnimeItem
import com.crocoby.animeplayerua.Routes
import com.crocoby.animeplayerua.logic.runParser
import com.crocoby.animeplayerua.navController
import com.crocoby.animeplayerua.widgets.AnimeCategory
import com.crocoby.animeplayerua.widgets.AnimeCategoryLoading
import com.crocoby.animeplayerua.widgets.ApplicationScaffold
import com.crocoby.animeplayerua.widgets.HorizontalPadding
import com.crocoby.animeplayerua.widgets.SearchField
import com.crocoby.animeplayerua.widgets.TopPadding

@Composable
fun HomeActivity() {
    var loaded by rememberSaveable { mutableStateOf(false) }
    val animeNew = rememberSaveable { mutableListOf<AnimeItem>() }
    val animeSeasonBest = rememberSaveable { mutableListOf<AnimeItem>() }

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

    ApplicationScaffold() {
        TopPadding {
            Column(Modifier.fillMaxSize()) {
                HorizontalPadding {
                    SearchField {
                        if (it.isNotEmpty()) {
                            navController!!.navigate(Routes.paramsConcat(Routes.SEARCH, it))
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
                        if (animeNew.isNotEmpty()) AnimeCategory("Новинки", animeNew) { openInfoActivity(it) }
                        if (animeSeasonBest.isNotEmpty()) AnimeCategory("Найкраще за сезон", animeSeasonBest) { openInfoActivity(it) }
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