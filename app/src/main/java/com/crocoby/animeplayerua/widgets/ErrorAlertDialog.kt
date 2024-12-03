package com.crocoby.animeplayerua.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.crocoby.animeplayerua.UiColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorAlertDialog(
    text: String,
    button: String,
    onClick: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = {
            onClick()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Column(Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(16.dp),
                shape = MaterialTheme.shapes.large,
                color = UiColors.error
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = text,
                            style = TextStyle(
                                color = Color.White
                            )
                        )
                        Button(
                            onClick = { onClick() }
                        ) {
                            Text(button)
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
        }
    }
}