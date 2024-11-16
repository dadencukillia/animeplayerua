package com.crocoby.animeplayerua

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.crocoby.animeplayerua.logic.CustomActivity
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
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
            parcel.writeString(slug + "\n\t" + imageUrl + "\n\t" + name)
        }

        override fun create(parcel: Parcel): AnimeItem {
            val string = parcel.readString()!!
            val split = string.split("\n\t", limit = 3)
            return AnimeItem(
                split[0], split[2], split[1]
            )
        }
    }
}

@Parcelize
data class AnimeEpisode(
    val name: String,
    val url: String,
    val playlistsId: String
) : Parcelable {
    companion object : Parceler<AnimeEpisode> {
        override fun AnimeEpisode.write(parcel: Parcel, flags: Int) {
            parcel.writeString(name + "\n\t" + url + "\n\t" + playlistsId)
        }

        override fun create(parcel: Parcel): AnimeEpisode {
            val string = parcel.readString()!!
            val split = string.split("\n\t")
            return AnimeEpisode(
                split[0], split[1], split[2]
            )
        }
    }
}

data class AnimeInfo(
    val slug: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val rate: Int,
    val playlists: Map<String, String>,
    val episodes: List<AnimeEpisode>,
) {
    fun toAnimeItem(): AnimeItem {
        return AnimeItem(this.slug, this.name, this.imageUrl)
    }
}

data class MenuItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val title: String,
    val activities: List<Class<out CustomActivity>>
)

// Rooms
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
) {
    fun toAnimeItem(): AnimeItem {
        return AnimeItem(
            slug, name, imageUrl
        )
    }
}