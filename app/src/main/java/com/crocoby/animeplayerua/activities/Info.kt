package com.crocoby.animeplayerua.activities

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crocoby.animeplayerua.AnimeDBEntity
import com.crocoby.animeplayerua.AnimeInfo
import com.crocoby.animeplayerua.R
import com.crocoby.animeplayerua.UiColors
import com.crocoby.animeplayerua.UiConstants
import com.crocoby.animeplayerua.logic.CustomActivity
import com.crocoby.animeplayerua.logic.parser
import com.crocoby.animeplayerua.utils.focusBorder
import com.crocoby.animeplayerua.widgets.AnimePlaylistRow
import com.crocoby.animeplayerua.widgets.EpisodeButton
import com.crocoby.animeplayerua.widgets.ErrorAlertDialog
import com.crocoby.animeplayerua.widgets.HorizontalPadding
import com.crocoby.animeplayerua.widgets.TopPadding
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.Instant

class InfoActivity : CustomActivity() {
    companion object {
        fun createIntent(context: Context, animeSlug: String): Intent {
            val intent = Intent(context, InfoActivity::class.java)
            intent.putExtra("animeSlug", animeSlug)

            return intent
        }
    }

    @Composable
    override fun Page() {
        val animeSlug = intent.getStringExtra("animeSlug")?:""

        var animeInfo by remember { mutableStateOf(AnimeInfo(animeSlug, "", "", "", 0, mapOf(), listOf())) }
        var dbEntity by remember { mutableStateOf(AnimeDBEntity()) }
        var loaded by rememberSaveable { mutableStateOf(false) }

        var loadError by rememberSaveable { mutableStateOf(false) }
        val coroutine = rememberCoroutineScope()

        val load = {
            loadError = false
            coroutine.launch {
                try {
                    if (!loaded) {
                        animeInfo = parser.getAnimeInfoBySlug(animeSlug)
                        dbEntity = database.getBySlug(animeSlug)?:AnimeDBEntity(
                            slug = animeInfo.slug,
                            name = animeInfo.name,
                            imageUrl = animeInfo.imageUrl
                        )

                        loaded = true
                    }
                } catch (_: Exception) {
                    loadError = true
                }
            }
        }

        LaunchedEffect(true) {
            load()
        }

        if (loadError) {
            ErrorAlertDialog(
                text = "Помилка завантаження, перевірте з'єднання з інтернетом",
                button = "Перезапустити"
            ) {
                load()
            }
        }

        if (loaded)
            InfoLoaded(animeInfo, dbEntity)
        else
            InfoLoading()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun InfoLoaded(
        animeInfo: AnimeInfo,
        dbEntity: AnimeDBEntity
    ) {
        val slug = rememberSaveable { animeInfo.slug }
        val name = rememberSaveable { animeInfo.name }
        val imageUrl = rememberSaveable { animeInfo.imageUrl }
        val description = rememberSaveable { animeInfo.description }
        val rate = rememberSaveable { animeInfo.rate }
        val playlists = rememberSaveable { animeInfo.playlists }
        val episodes = rememberSaveable { animeInfo.episodes }

        var liked by rememberSaveable { mutableStateOf(dbEntity.likedMark) }
        var likedTime by rememberSaveable { mutableLongStateOf(dbEntity.likedTime) }
        var watched by rememberSaveable { mutableStateOf(dbEntity.watchedMark) }
        var watchedTime by rememberSaveable { mutableLongStateOf(dbEntity.watchedTime) }
        var lastWatchedEpisode by rememberSaveable { mutableStateOf(dbEntity.lastWatchedEpisode) }
        var lastWatchedTime by rememberSaveable { mutableLongStateOf(dbEntity.lastWatchedTime) }

        val keys = playlists.keys.map {
            it.split("_")
        }
        val rows = (keys.maxOfOrNull {
            it.count()
        }?:1) - 1

        var foldDescription by rememberSaveable { mutableStateOf(true) }
        val currentPlaylist = remember<MutableList<Int>> {
            if (lastWatchedEpisode.isNotEmpty() && lastWatchedEpisode.split("_").count() == rows + 2 && playlists.keys.contains(lastWatchedEpisode.split("_").subList(0, rows + 1).joinToString("_"))) {
                lastWatchedEpisode.split("_").subList(0, rows + 1).map{it.toInt()}
            } else {
                (0..rows).map { 0 }
            }.toMutableStateList()
        }

        val joinedCurrentPlaylist = currentPlaylist.joinToString("_")
        val activeEpisodes = episodes.filter {
            it.playlistsId == joinedCurrentPlaylist
        }

        var showNameAlert by remember { mutableStateOf(false) }

        val editableAnimeDBEntity = AnimeDBEntity(
            slug, watched, watchedTime, liked, likedTime, lastWatchedEpisode, lastWatchedTime, name, imageUrl
        )

        LaunchedEffect(editableAnimeDBEntity) {
            runBlocking {
                if (!editableAnimeDBEntity.likedMark && !editableAnimeDBEntity.watchedMark && editableAnimeDBEntity.lastWatchedEpisode == "") {
                    database.delete(editableAnimeDBEntity)
                } else {
                    database.insert(editableAnimeDBEntity)
                }
            }
        }

        if (showNameAlert) {
            BasicAlertDialog(
                onDismissRequest = {
                    showNameAlert = false
                }
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(16.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = name,
                    )
                }
            }
        }

        Scaffold { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                TopPadding {
                    Column(Modifier.fillMaxSize()) {
                        HorizontalPadding {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        finish()
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.width(24.dp),
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "backIcon"
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            showNameAlert = true
                                        },
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text(
                                        text = name,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        style = TextStyle(
                                            fontSize = 24.sp,
                                        )
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(UiConstants.verticalScreenPadding))
                        Column(
                            Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState(0))
                        ) {
                            HorizontalPadding {
                                Column {
                                    Box(Modifier.fillMaxWidth().height(210.dp)) {
                                        KamelImage(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(24.dp)),
                                            resource = { asyncPainterResource(data = imageUrl) },
                                            contentDescription = "animeBanner",
                                            contentScale = ContentScale.Crop
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxSize(),
                                            verticalAlignment = Alignment.Bottom,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            IconButton(
                                                modifier = Modifier.focusBorder(shape = CircleShape),
                                                onClick = {
                                                    liked = !liked
                                                    likedTime = Instant.now().toEpochMilli()
                                                },
                                                colors = IconButtonDefaults.iconButtonColors(
                                                    containerColor = Color(0x22FFFFFF)
                                                )
                                            ) {
                                                Icon(
                                                    modifier = Modifier.width(24.dp),
                                                    imageVector = Icons.Filled.Favorite,
                                                    contentDescription = "heartIcon",
                                                    tint = if (liked) UiColors.error else UiColors.background
                                                )
                                            }
                                            IconButton(
                                                modifier = Modifier.focusBorder(shape = CircleShape),
                                                onClick = {
                                                    watched = !watched
                                                    watchedTime = Instant.now().toEpochMilli()
                                                },
                                                colors = IconButtonDefaults.iconButtonColors(
                                                    containerColor = Color(0x22FFFFFF)
                                                )
                                            ) {
                                                if (watched)
                                                    Icon(
                                                        modifier = Modifier.width(24.dp),
                                                        painter = painterResource(R.drawable.baseline_done_all),
                                                        contentDescription = "markIcon",
                                                        tint = Color.White
                                                    )
                                                else
                                                    Icon(
                                                        modifier = Modifier.width(24.dp),
                                                        imageVector = Icons.Filled.Done,
                                                        contentDescription = "markIcon",
                                                        tint = UiColors.background
                                                    )
                                            }
                                        }
                                    }
                                    Spacer(Modifier.height(16.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "★".repeat(rate) + "☆".repeat(10 - rate),
                                            style = TextStyle(
                                                color = UiColors.yellow,
                                                fontSize = 20.sp,
                                            )
                                        )
                                        Text(
                                            text = "(${rate}/10)",
                                            style = TextStyle(
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                foldDescription = !foldDescription
                                            },
                                        text = description,
                                        maxLines = if (foldDescription) 5 else Int.MAX_VALUE,
                                        overflow = TextOverflow.Ellipsis,
                                        style = TextStyle(
                                            fontSize = 16.sp
                                        )
                                    )
                                    Spacer(Modifier.height(16.dp))
                                    Text(
                                        "Серії",
                                        style = TextStyle(
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Column {
                                for (row in 1..rows) {
                                    val str = currentPlaylist.subList(0, row).joinToString("_") + "_"
                                    val names = playlists.filter { (k, v) ->
                                        k.split("_").count() == row + 1 && k.startsWith(str)
                                    }.values
                                    AnimePlaylistRow(
                                        names.toList(),
                                        currentPlaylist[row]
                                    ) {
                                        currentPlaylist[row] = it
                                        for (nr in (row + 1)..rows) {
                                            currentPlaylist[nr] = 0
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            HorizontalPadding {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (lastWatchedEpisode.isNotEmpty() && activeEpisodes.any { lastWatchedEpisode.startsWith(it.playlistsId) }) {
                                        val ep = activeEpisodes[lastWatchedEpisode.split("_").last().toInt()]
                                        EpisodeButton(ep.name, continueWatching = true) {
                                            lastWatchedTime = Instant.now().toEpochMilli()

                                            startActivity(VideoActivity.createIntent(this@InfoActivity, ep.url))
                                        }
                                    }
                                    for ((index, episode) in activeEpisodes.withIndex()) {
                                        EpisodeButton(episode.name) {
                                            lastWatchedEpisode = "${episode.playlistsId}_$index"
                                            lastWatchedTime = Instant.now().toEpochMilli()

                                            startActivity(VideoActivity.createIntent(this@InfoActivity, episode.url))
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(UiConstants.verticalScreenPadding))
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun InfoLoading() {
        Scaffold { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                TopPadding {
                    HorizontalPadding {
                        Column(Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(100))
                                        .width(48.dp)
                                        .height(48.dp)
                                        .background(UiColors.greyBar),
                                ) {}
                                Spacer(Modifier.width(8.dp))
                                Box(
                                    Modifier
                                        .clip(RoundedCornerShape(24.dp))
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .background(UiColors.greyBar),
                                ) {}
                            }
                            Spacer(Modifier.height(UiConstants.verticalScreenPadding))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .fillMaxWidth()
                                    .height(210.dp)
                                    .background(UiColors.greyBar),
                            ) {}
                            Spacer(Modifier.height(16.dp))
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .width(220.dp)
                                    .height(32.dp)
                                    .background(UiColors.greyBar),
                            ) {}
                            Spacer(Modifier.height(8.dp))
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .background(UiColors.greyBar),
                            ) {}
                            Spacer(Modifier.height(16.dp))
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .width(100.dp)
                                    .height(32.dp)
                                    .background(UiColors.greyBar),
                            ) {}
                            Spacer(Modifier.height(8.dp))
                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(UiColors.greyBar)
                                    .fillMaxWidth()
                                    .weight(1f),
                            ) {}
                            Spacer(Modifier.height(UiConstants.verticalScreenPadding))
                        }
                    }
                }
            }
        }
    }
}