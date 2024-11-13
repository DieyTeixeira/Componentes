package com.dieyteixeira.componentes.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dieyteixeira.componentes.R
import com.dieyteixeira.componentes.ui.elements.components.AnimatedBorderCard
import com.dieyteixeira.componentes.ui.elements.components.AnimatedSelectItem
import com.dieyteixeira.componentes.ui.elements.components.AnimatedStopWatch
import com.dieyteixeira.componentes.ui.elements.components.AnimatedTopBar
import com.dieyteixeira.componentes.ui.elements.components.HorizontalPager
import com.dieyteixeira.componentes.ui.elements.components.LazyListScrollState
import com.dieyteixeira.componentes.ui.elements.components.PhotoPicker
import com.dieyteixeira.componentes.ui.elements.components.WebBrowser

@Composable
fun ComponentsScreen() {

    var currentIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.LightGray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.8f)
                .background(Color.White, shape = RoundedCornerShape(15.dp))
                .clip(RoundedCornerShape(15.dp)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            screensComponents[currentIndex]()
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botão para decrementar o índice
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        currentIndex = if (currentIndex > 0) currentIndex - 1 else screensComponents.size - 1
                    },
                Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_double_arrow_left),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(text = textsComponents[currentIndex])

            // Botão para incrementar o índice
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        currentIndex = (currentIndex + 1) % screensComponents.size
                    },
                Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_double_arrow_right),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

fun getRandomColorComponents(): Color {
    val colors = listOf(
        Color.Blue,
        Color.Red,
        Color.Green,
        Color.Magenta
    )
    return colors.random()
}

    @OptIn(ExperimentalAnimationApi::class)
    val screensComponents = listOf<@Composable () -> Unit>(
        { AnimatedBorderCard(textCard = "Sample Text") },
        { AnimatedSelectItem(
            title = "Sample Item",
            subtitle = "This is a sample subtitle to test the AnimatedSelectItem composable."
        ) },
        { AnimatedStopWatch() },
        { AnimatedTopBar() },
        { HorizontalPager() },
        { LazyListScrollState() },
        { PhotoPicker() },
        { WebBrowser(color = getRandomColorComponents()) }
    )

    val textsComponents = listOf(
        "Animated Border Card",
        "Animated Select Item",
        "Animated Stop Watch",
        "Animated Top Bar",
        "Horizontal Pager",
        "Lazy List Scroll State",
        "Photo Picker",
        "Web Browser"
    )