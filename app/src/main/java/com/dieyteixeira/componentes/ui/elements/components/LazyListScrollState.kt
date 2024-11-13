package com.dieyteixeira.componentes.ui.elements.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/*-------------------------------------------------------------------------------------------------|
|                                                                                                  |
|                                 STATUS DE ROLAGEM DA LISTA                                       |
|                  Indica quando está rolando e quando está no topo da lista.                      |
|                                                                                                  |
|------------------------------------------------------------------------------------------------ */

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun LazyListScrollState() {
    val lazyListState = rememberLazyListState()

    Scaffold(
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                List(lazyListState = lazyListState)
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = if (lazyListState.isScrolledState) "Scrolling..." else "Idle",
                    color = if (lazyListState.isScrolledState) MaterialTheme.colors.primary
                    else MaterialTheme.colors.onBackground,
                    style = TextStyle(fontSize = MaterialTheme.typography.h5.fontSize)
                )
            }
        }
    )
}

@Composable
fun List(lazyListState: LazyListState) {
    val items = remember {
        List(size = 25) { it }
    }

    LazyColumn(
        contentPadding = PaddingValues(12.dp),
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(
            items = items,
            key = { it }
        ) {
            ItemHolder()
        }
    }
}

@Composable
fun ItemHolder() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.LightGray.copy(alpha = ContentAlpha.disabled))
    )
}

val LazyListState.isScrolledState: Boolean
    get() = firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0

@Preview
@Composable
private fun LazyListScrollPreview() {
    LazyListScrollState()
}