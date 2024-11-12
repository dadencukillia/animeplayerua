package com.crocoby.animeplayerua.widgets

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
import androidx.navigation.compose.currentBackStackEntryAsState
import com.crocoby.animeplayerua.MenuItem
import com.crocoby.animeplayerua.Routes
import com.crocoby.animeplayerua.navController

@Composable
fun ApplicationScaffold(content: @Composable () -> Unit) {
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
                        listOf(Routes.HOME, Routes.SEARCH)
                    ),
                    MenuItem(
                        Icons.Filled.PlayArrow,
                        Icons.Outlined.PlayArrow,
                        "Списки",
                        listOf(Routes.PLAYLISTS)
                    )
                )) {
                    val currentRoute = Routes.clearParams(navController!!.currentBackStackEntryAsState().value?.destination?.route?:"")
                    val selected = item.routes.contains(currentRoute)
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
                                contentDescription = item.routes[0] + "Icon"
                            )
                        },
                        onClick = {
                            if (item.routes[0] != currentRoute) {
                                if (currentRoute == Routes.SEARCH && item.routes[0] == Routes.HOME) {
                                    navController!!.navigateUp()
                                } else {
                                    navController!!.navigate(item.routes[0]) {
                                        launchSingleTop = true
                                        restoreState = true

                                        popUpTo(0) {
                                            saveState = true
                                        }
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