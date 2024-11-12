package com.crocoby.animeplayerua.widgets

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.crocoby.animeplayerua.UiColors
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchField(
    query: String = "",
    onSubmition: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf(query) }

    BasicTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        value = TextFieldValue(
            searchQuery,
            selection = TextRange(searchQuery.length)
        ),
        onValueChange = {
            val text = it.text
            searchQuery = text.substring(0, min(text.length, 32))
        },
        singleLine = true,
        textStyle = TextStyle(
            color = UiColors.text,
            fontSize = 14.sp,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSubmition(searchQuery)
            }
        ),
        cursorBrush = SolidColor(Color.White)
    ) {
        TextFieldDefaults.DecorationBox(
            value = searchQuery,
            leadingIcon = {
                Icon(
                    modifier = Modifier.height(16.dp),
                    imageVector = Icons.Filled.Search,
                    contentDescription = "searchIcon",
                )
            },
            placeholder = {
                Text(
                    text = "Пошук аніме по назві",
                    style = TextStyle(
                        color = Color(0xFFBEBEBE),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    ),
                )
            },
            innerTextField = it,
            singleLine = true,
            enabled = true,
            interactionSource = remember { MutableInteractionSource() },
            visualTransformation = VisualTransformation.None,
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(
                horizontal = 16.dp
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = UiColors.greyBar,
                unfocusedContainerColor = UiColors.greyBar,
                disabledContainerColor = UiColors.greyBar,
                errorContainerColor = UiColors.greyBar,

                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            )
        )
    }
}