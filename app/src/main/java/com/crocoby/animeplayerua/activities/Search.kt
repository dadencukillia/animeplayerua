package com.crocoby.animeplayerua.activities

import android.content.Context
import android.content.Intent
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
import com.crocoby.animeplayerua.logic.CustomActivity
import com.crocoby.animeplayerua.logic.runParser
import com.crocoby.animeplayerua.noAnimation
import com.crocoby.animeplayerua.widgets.AnimeCategory
import com.crocoby.animeplayerua.widgets.AnimeCategoryLoading
import com.crocoby.animeplayerua.widgets.ApplicationScaffold
import com.crocoby.animeplayerua.widgets.HorizontalPadding
import com.crocoby.animeplayerua.widgets.SearchField
import com.crocoby.animeplayerua.widgets.TextBanner
import com.crocoby.animeplayerua.widgets.TopPadding


class SearchActivity: CustomActivity() {
    companion object {
        fun createIntent(context: Context, searchQuery: String): Intent {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra("searchQuery", searchQuery)

            return intent
        }
    }

    @Composable
    override fun Page() {
        val searchQuery = intent.getStringExtra("searchQuery")?:"Steins;gate"

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

        ApplicationScaffold(this::class.java) {
            TopPadding {
                Column(Modifier.fillMaxSize()) {
                    HorizontalPadding {
                        SearchField(searchQuery) {
                            if (it.isNotEmpty() && searchQuery != it) {
                                startActivity(createIntent(this@SearchActivity, it).noAnimation())
                                finish()
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    if (!loaded)
                        AnimeCategoryLoading(20)
                    else if (found.isEmpty())
                        TextBanner("Нічого не знайдено :(")
                    else
                        AnimeCategory("Знайдено (${found.count()}):", found) {
                            startActivity(InfoActivity.createIntent(this@SearchActivity, it.slug))
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
