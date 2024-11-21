package com.dieyteixeira.componentes.ui.elements.components

import android.graphics.Color.argb
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieyteixeira.componentes.ui.theme.BlueSky
import com.dieyteixeira.componentes.ui.theme.Green500
import com.dieyteixeira.componentes.ui.theme.Orange
import com.dieyteixeira.componentes.ui.theme.Yellow
import java.util.Random

@Composable
fun StaggeredGrid(color: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .background(color),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Top Bar",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(5.dp)
        ) {
            StaggeredVerticalGrid(
                numColumns = 2,
                modifier = Modifier.padding(5.dp)
            ) {
                staggeredText.forEach { text ->
                    val rnd = Random()
                    val colorsRGB: Int = argb(255,
                        rnd.nextInt(256),
                        rnd.nextInt(256),
                        rnd.nextInt(256)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp),
                        backgroundColor = Color(colorsRGB),
                        elevation = 10.dp,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = text,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Suppress("NAME_SHADOWING")
@Composable
fun StaggeredVerticalGrid(
    modifier: Modifier = Modifier,
    numColumns: Int = 2,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurable, constraints ->
        val columnWidth = (constraints.maxWidth / numColumns)
        val itemConstraints = constraints.copy(maxWidth = columnWidth)
        val columnHeights = IntArray(numColumns) { 0 }
        val placeables = measurable.map { measurable ->
            val column = shortTestColumn(columnHeights)
            val placeable = measurable.measure(itemConstraints)
            columnHeights[column] += placeable.height
            placeable
        }

        val height = columnHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
            ?: constraints.minHeight

        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            val columnYPointers = IntArray(numColumns) { 0 }
            placeables.forEach { placeable ->
                val column = shortTestColumn(columnYPointers)

                placeable.place(
                    x = columnWidth * column,
                    y = columnYPointers[column]
                )

                columnYPointers[column] += placeable.height
            }
        }
    }
}

private fun shortTestColumn(columnHeights: IntArray): Int {
    var minHeight = Int.MAX_VALUE
    var columnIndex = 0

    columnHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            columnIndex = index
        }
    }

    return columnIndex
}

val staggeredText = listOf(
    "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries",
    "Lorem Ipsum is simply dummy text of the printing and typesetting industry.",
    "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s",
    "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s when an unknown printer took a galley of type",
    "Lorem Ipsum is simply dummy text",
    "It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters, as opposed to using 'Content here, content here'",
    "There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don't look even slightly believable.",
    "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form",
    "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s",
)