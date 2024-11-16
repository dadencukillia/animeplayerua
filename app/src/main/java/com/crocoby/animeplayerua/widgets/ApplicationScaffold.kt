package com.crocoby.animeplayerua.widgets

import android.content.Intent
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crocoby.animeplayerua.MenuItem
import com.crocoby.animeplayerua.activities.HomeActivity
import com.crocoby.animeplayerua.activities.PlaylistsActivity
import com.crocoby.animeplayerua.activities.SearchActivity
import com.crocoby.animeplayerua.logic.CustomActivity

@Composable
fun CustomActivity.ApplicationScaffold(activity: Class<out CustomActivity>, content: @Composable () -> Unit) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    keyboardController?.hide()
                    focusManager.clearFocus(true)
                })
            },
        bottomBar = {
            BottomAppBar(tonalElevation = 0.dp) {
                for (item in listOf<MenuItem>(
                    MenuItem(
                        Icons.Filled.Home,
                        Icons.Outlined.Home,
                        "Популярне",
                        listOf(
                            HomeActivity::class.java,
                            SearchActivity::class.java
                        )
                    ),
                    MenuItem(
                        Icons.Filled.PlayArrow,
                        Icons.Outlined.PlayArrow,
                        "Списки",
                        listOf(
                            PlaylistsActivity::class.java
                        )
                    )
                )) {
                    val selected = item.activities.contains(activity)
                    NavigationBarItem(
                        selected = selected,
                        label = {
                            Text(
                                item.title,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                )
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = "activity icon"
                            )
                        },
                        onClick = {
                            if (item.activities[0] != activity) {
                                if (item.activities.contains(activity)) {
                                    finish()
                                } else {
                                    if (item.activities[0] != HomeActivity::class.java) {
                                        val newIntent = Intent(this@ApplicationScaffold, item.activities[0])
                                        startActivity(newIntent)
                                    }
                                    if (this@ApplicationScaffold::class.java != HomeActivity::class.java && this@ApplicationScaffold::class.java != SearchActivity::class.java) {
                                        finish()
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            content()
        }
    }
}