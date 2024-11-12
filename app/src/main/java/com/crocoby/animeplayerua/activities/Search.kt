package com.crocoby.animeplayerua.activities

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.crocoby.animeplayerua.widgets.TextBanner
import com.crocoby.animeplayerua.widgets.TopPadding

@Composable
fun SearchActivity(
    searchQuery: String
) {
    var loaded by rememberSaveable { mutableStateOf(false) }
    val found = rememberSaveable { mutableListOf<AnimeItem>() }

    runParser(
        function = {
            if (!loaded) {
                found.addAll(searchAnimeByName(searchQuery))
                loaded = true
            }
        },
        onError = {}
    )

    ApplicationScaffold() {
        TopPadding {
            Column(Modifier.fillMaxSize()) {
                HorizontalPadding {
                    SearchField(searchQuery) {
                        if (it.isNotEmpty() && searchQuery != it) {
                            navController!!.navigate(Routes.paramsConcat(Routes.SEARCH, it)) {
                                navController!!.navigateUp()
                            }
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                if (!loaded)
                    AnimeCategoryLoading(20)
                else if (found.isEmpty())
                    TextBanner("Нічого не знайдено :(")
                else
                    AnimeCategory("Знайдено (${found.count()}):", found) { openInfoActivity(it) }
            }
        }
    }
}