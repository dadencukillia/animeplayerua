package com.crocoby.animeplayerua.logic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.crocoby.animeplayerua.darkScheme
import com.crocoby.animeplayerua.noAnimation

open class CustomActivity : ComponentActivity() {
    private var databaseRef: AnimeDao? = null
    protected val database: AnimeDao
        get() {
            return databaseRef!!
        }

    @Composable
    open fun Page() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseRef = AppDatabase.getDatabase(this).getDao()
        enableEdgeToEdge()

        setContent {
            MaterialTheme(colorScheme = darkScheme) {
                this.Page()
            }
        }
    }

    fun restartActivity() {
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent.noAnimation())
        overridePendingTransition(0, 0)
    }
}