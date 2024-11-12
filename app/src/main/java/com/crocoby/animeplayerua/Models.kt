package com.crocoby.animeplayerua

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.util.fastJoinToString
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.crocoby.animeplayerua.utils.UrlEncoderUtil
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant

@Parcelize
@Serializable
data class AnimeItem(
    val slug: String,
    val name: String,
    val imageUrl: String
) : Parcelable {
    fun toAnimeInfo(
        description: String,
        rate: Int,
        playlists: Map<String, String>,
        episodes: List<AnimeEpisode>
    ): AnimeInfo {
        return AnimeInfo(slug, name, imageUrl, description, rate, playlists, episodes)
    }

    companion object : Parceler<AnimeItem> {
        override fun AnimeItem.write(parcel: Parcel, flags: Int) {
            val json = Json.encodeToString(this)
            parcel.writeString(json)
        }

        override fun create(parcel: Parcel): AnimeItem {
            val string = parcel.readString()!!
            val json = Json.decodeFromString<AnimeItem>(string)
            return json
        }
    }
}

@Parcelize
@Serializable
data class AnimeEpisode(
    val name: String,
    val url: String,
    val playlistsId: String
) : Parcelable {
    companion object : Parceler<AnimeEpisode> {
        override fun AnimeEpisode.write(parcel: Parcel, flags: Int) {
            val json = Json.encodeToString(this)
            parcel.writeString(json)
        }

        override fun create(parcel: Parcel): AnimeEpisode {
            val string = parcel.readString()!!
            val json = Json.decodeFromString<AnimeEpisode>(string)
            return json
        }
    }
}

@Parcelize
@Serializable
data class AnimeInfo(
    val slug: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val rate: Int,
    val playlists: Map<String, String>,
    val episodes: List<AnimeEpisode>,
) : Parcelable {
    fun toAnimeItem(): AnimeItem {
        return AnimeItem(this.slug, this.name, this.imageUrl)
    }

    companion object : Parceler<AnimeInfo> {
        override fun AnimeInfo.write(parcel: Parcel, flags: Int) {
            val json = Json.encodeToString(this)
            parcel.writeString(json)
        }

        override fun create(parcel: Parcel): AnimeInfo {
            val string = parcel.readString()!!
            val json = Json.decodeFromString<AnimeInfo>(string)
            return json
        }
    }
}

object Routes {
    const val HOME = "home"
    const val PLAYLISTS = "playlists"
    const val SEARCH = "search"
    const val ANIMEINFO = "anime"
    const val VIDEO = "video"

    fun paramsConcat(route: String, vararg param: String): String {
        val trimmed = route.trim('/')
        val encoded: List<String> = param.map {
            UrlEncoderUtil.encode(it)
        }
        val joined = encoded.fastJoinToString("/")
        val res = "$trimmed/$joined"

        return res
    }

    fun clearParams(path: String): String {
        val trimmed = path.trimStart('/')
        val split = trimmed.split('/', limit = 2)
        return split[0]
    }
}

data class MenuItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val title: String,
    val routes: List<String>
)

// Rooms
@Parcelize
@Serializable
@Entity(tableName = "animes")
data class AnimeDBEntity(
    @PrimaryKey val slug: String = "",
    var watchedMark: Boolean = false,
    var watchedTime: Long = Instant.now().toEpochMilli(),
    var likedMark: Boolean = false,
    var likedTime: Long = Instant.now().toEpochMilli(),
    var lastWatchedEpisode: String = "",
    var lastWatchedTime: Long = Instant.now().toEpochMilli(),
    var name: String = "",
    var imageUrl: String = ""
) : Parcelable {
    fun toAnimeItem(): AnimeItem {
        return AnimeItem(
            slug, name, imageUrl
        )
    }

    companion object : Parceler<AnimeDBEntity> {
        override fun AnimeDBEntity.write(parcel: Parcel, flags: Int) {
            val json = Json.encodeToString(this)
            parcel.writeString(json)
        }

        override fun create(parcel: Parcel): AnimeDBEntity {
            val string = parcel.readString()!!
            val json = Json.decodeFromString<AnimeDBEntity>(string)
            return json
        }
    }
}