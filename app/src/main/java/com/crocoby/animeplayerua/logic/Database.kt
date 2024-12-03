package com.crocoby.animeplayerua.logic

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.crocoby.animeplayerua.AnimeDBEntity

@Database(entities = [AnimeDBEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDao(): AnimeDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "app")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}


@Dao
interface AnimeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AnimeDBEntity)

    @Delete
    suspend fun delete(item: AnimeDBEntity)

    @Query("SELECT * FROM animes WHERE likedMark=1 ORDER BY likedTime DESC")
    suspend fun getLiked(): List<AnimeDBEntity>

    @Query("SELECT * FROM animes WHERE watchedMark=1 ORDER BY watchedTime DESC")
    suspend fun getWatched(): List<AnimeDBEntity>

    @Query("SELECT * FROM animes WHERE lastWatchedEpisode != \"\" ORDER BY lastWatchedTime DESC")
    suspend fun getEpisodeWatched(): List<AnimeDBEntity>

    @Query("SELECT * FROM animes WHERE slug=:slug")
    suspend fun getBySlug(slug: String): AnimeDBEntity?
}