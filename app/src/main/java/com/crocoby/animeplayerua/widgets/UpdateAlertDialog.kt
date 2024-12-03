package com.crocoby.animeplayerua.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateAlertDialog(
    version: String,
    onClose: () -> Unit,
    onOpen: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = { onClose() },
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(16.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Доступна нова версія застосунку!",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    )
                    Text(
                        text = "Оновіть застосунок до версії ${version}, щоб отримати нові функції та користуватися стабільною версією програми.",
                    )
                    Button(
                        onClick = { onOpen() }
                    ) {
                        Text("Перейти")
                    }
                }
            }
        }
    }
}