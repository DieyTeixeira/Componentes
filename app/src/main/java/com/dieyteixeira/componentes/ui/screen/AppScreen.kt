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
import com.dieyteixeira.componentes.ui.components.animated_border_card.AnimatedBorderCard
import com.dieyteixeira.componentes.ui.components.animated_select_item.AnimatedSelectItem
import com.dieyteixeira.componentes.ui.components.animated_stop_watch.AnimatedStopWatch
import com.dieyteixeira.componentes.ui.components.animated_top_bar.AnimatedTopBar
import com.dieyteixeira.componentes.ui.components.game_pacman.GamePacMan
import com.dieyteixeira.componentes.ui.components.game_snake.GameSnake
import com.dieyteixeira.componentes.ui.components.game_tetris.GameTetris
import com.dieyteixeira.componentes.ui.components.game_velha.GameVelha
import com.dieyteixeira.componentes.ui.components.horizontal_pager.HorizontalPager
import com.dieyteixeira.componentes.ui.components.lazy_list_scroll_state.LazyListScrollState
import com.dieyteixeira.componentes.ui.components.loading_animation.ChasingDots
import com.dieyteixeira.componentes.ui.components.loading_animation.ChasingTwoDots
import com.dieyteixeira.componentes.ui.components.loading_animation.Circle
import com.dieyteixeira.componentes.ui.components.loading_animation.CubeGrid
import com.dieyteixeira.componentes.ui.components.loading_animation.DoubleBounce
import com.dieyteixeira.componentes.ui.components.loading_animation.FadingCircle
import com.dieyteixeira.componentes.ui.components.loading_animation.FoldingCube
import com.dieyteixeira.componentes.ui.components.loading_animation.InstaSpinner
import com.dieyteixeira.componentes.ui.components.loading_animation.Pulse
import com.dieyteixeira.componentes.ui.components.loading_animation.RotatingPlane
import com.dieyteixeira.componentes.ui.components.loading_animation.ThreeBounce
import com.dieyteixeira.componentes.ui.components.loading_animation.ThreeJumping
import com.dieyteixeira.componentes.ui.components.loading_animation.WanderingCubes
import com.dieyteixeira.componentes.ui.components.loading_animation.Wave
import com.dieyteixeira.componentes.ui.components.photo_picker.PhotoPicker
import com.dieyteixeira.componentes.ui.components.web_browser.WebBrowser

@Composable
fun AppScreen() {

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
            screens[currentIndex]()
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
                        currentIndex = if (currentIndex > 0) currentIndex - 1 else screens.size - 1
                    },
                Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_double_arrow_left),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(text = texts[currentIndex])

            // Botão para incrementar o índice
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        currentIndex = (currentIndex + 1) % screens.size
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

fun getRandomColor(): Color {
    val colors = listOf(
        Color.Blue,
        Color.Red,
        Color.Green,
        Color.Magenta
    )
    return colors.random()
}

    @OptIn(ExperimentalAnimationApi::class)
    val screens = listOf<@Composable () -> Unit>(
        { ChasingDots(color = getRandomColor()) },
        { ChasingTwoDots(color = getRandomColor()) },
        { Circle(color = getRandomColor()) },
        { CubeGrid(color = getRandomColor()) },
        { DoubleBounce(color = getRandomColor()) },
        { FadingCircle(color = getRandomColor()) },
        { FoldingCube(color = getRandomColor()) },
        { InstaSpinner(color = getRandomColor()) },
        { Pulse(color = getRandomColor()) },
        { RotatingPlane(color = getRandomColor()) },
        { ThreeBounce(color = getRandomColor()) },
        { ThreeJumping(color = getRandomColor()) },
        { WanderingCubes(color = getRandomColor()) },
        { Wave(color = getRandomColor()) },
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
        { WebBrowser(color = getRandomColor()) },
        { GameSnake() },
        { GameVelha() },
        { GameTetris() },
        { GamePacMan() }
    )

    val texts = listOf(
        "Chasing Dots",
        "Chasing Two Dots",
        "Circle",
        "Cube Grid",
        "Double Bounce",
        "Fading Circle",
        "Folding Cube",
        "Insta Spinner",
        "Pulse",
        "Rotating Plane",
        "Three Bounce",
        "Three Jumping",
        "Wandering Cubes",
        "Wave",
        "Animated Border Card",
        "Animated Select Item",
        "Animated Stop Watch",
        "Animated Top Bar",
        "Horizontal Pager",
        "Lazy List Scroll State",
        "Photo Picker",
        "Web Browser",
        "Game Snake",
        "Game Velha",
        "Game Tetris",
        "Game PacMan"
    )