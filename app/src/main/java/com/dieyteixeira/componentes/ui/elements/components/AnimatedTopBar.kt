package com.dieyteixeira.componentes.ui.elements.components

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/*-------------------------------------------------------------------------------------------------|
|                                                                                                  |
|                                 ANIMAÇÃO DA BARRA DE TÍTULO                                      |                                  |
|                                                                                                  |
|------------------------------------------------------------------------------------------------ */

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AnimatedTopBar() {
    val lazyListState = rememberLazyListState()
    Scaffold(
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                MainContent(lazyListState = lazyListState)
                TopBar(lazyListState = lazyListState)
            }
        }
    )
}

@Composable
fun TopBar(lazyListState: LazyListState) {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Yellow)
            .animateContentSize(animationSpec = tween(durationMillis = 300))
            .height(height = if (lazyListState.isScrolledTopBar) 0.dp else TOP_BAR_HEIGHT)
        ,
        contentPadding = PaddingValues(start = 16.dp)
    ) {
        Text(
            text = "Title",
            style = TextStyle(
                fontSize = 16.sp,
                color = Color.Gray
            )
        )
    }
}

@Composable
fun MainContent(lazyListState: LazyListState) {
    val numbers = remember { List(size = 25) { it } }
    val padding by animateDpAsState(
        targetValue = if (lazyListState.isScrolledTopBar) 0.dp else TOP_BAR_HEIGHT,
        animationSpec = tween(durationMillis = 300)
    )
    LazyColumn(
        modifier = Modifier.padding(top = padding),
        state = lazyListState
    ) {
        items(
            items = numbers,
            key = { it }
        ) {
            NumberHolder(number = it)
        }
    }
}

@Composable
fun NumberHolder(number: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = number.toString(),
            style = TextStyle(
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

val TOP_BAR_HEIGHT = 56.dp
val LazyListState.isScrolledTopBar: Boolean
    get() = firstVisibleItemIndex > 0 || firstVisibleItemScrollOffset > 0

@Preview
@Composable
private fun AnimatedTopBarPreview() {
    AnimatedTopBar()
}