package com.dieyteixeira.componentes.ui.elements.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@ExperimentalFoundationApi
@Composable
fun HeaderList(color: Color) {
    val categories = listOf("Apples", "Bananas", "Cherries", "Dates", "Eggfruit", "Farkleberry",
        "Grapes", "Hackberry", "Indonesian Lime", "Jackfruit", "Kaffir Lime", "Lemon", "Mango", "Navel Orange", "Oranges", "Papaya ",
        "Quince", "Rose Hips", "Star Fruit", "Tomato", "Ugli Fruit", "Vanilla Bean", "Watermelon", "Xigua",
        "Yellow Passion Fruit", "Zucchini")

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color)
                .height(55.dp),
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(Color.White)
                    .wrapContentHeight()
                    .fillMaxWidth(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = false
            ) {
                categories.forEach { category ->
                    stickyHeader {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            backgroundColor = color,
                            elevation = 8.dp,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = category,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    items(5) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            backgroundColor = Color.White,
                            elevation = 10.dp,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "$category ${item + 1}",
                                fontSize = 16.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}