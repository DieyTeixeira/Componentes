package com.dieyteixeira.componentes.ui.screen

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.ExperimentalMaterialApi
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
import com.dieyteixeira.componentes.ui.elements.components.AnimationExpandList
import com.dieyteixeira.componentes.ui.elements.components.BottomBar
import com.dieyteixeira.componentes.ui.elements.components.CircularProgressSlider
import com.dieyteixeira.componentes.ui.elements.components.CurvedScrollView
import com.dieyteixeira.componentes.ui.elements.components.DarkModeSwitch
import com.dieyteixeira.componentes.ui.elements.components.DraggableObject
import com.dieyteixeira.componentes.ui.elements.components.FlipHorizontal
import com.dieyteixeira.componentes.ui.elements.components.FloatingButtonExpanded
import com.dieyteixeira.componentes.ui.elements.components.FloatingButtonShowHide
import com.dieyteixeira.componentes.ui.elements.components.FluidButtom
import com.dieyteixeira.componentes.ui.elements.components.HeaderList
import com.dieyteixeira.componentes.ui.elements.components.HorizontalPager
import com.dieyteixeira.componentes.ui.elements.components.LazyListScrollState
import com.dieyteixeira.componentes.ui.elements.components.ListDragDrop
import com.dieyteixeira.componentes.ui.elements.components.PhotoPicker
import com.dieyteixeira.componentes.ui.elements.components.SnackBar
import com.dieyteixeira.componentes.ui.elements.components.SpeedIndicator
import com.dieyteixeira.componentes.ui.elements.components.StaggeredGrid
import com.dieyteixeira.componentes.ui.elements.components.SwipeToDelete
import com.dieyteixeira.componentes.ui.elements.components.WebBrowser
import com.dieyteixeira.componentes.ui.theme.BlueSky
import com.dieyteixeira.componentes.ui.theme.Green500
import com.dieyteixeira.componentes.ui.theme.Orange
import com.dieyteixeira.componentes.ui.theme.Yellow

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
        BlueSky,
        Orange,
        Green500,
        Yellow
    )
    return colors.random()
}

    @OptIn(
        ExperimentalAnimationApi::class,
        ExperimentalMaterialApi::class,
        ExperimentalFoundationApi::class
    )
    val screensComponents = listOf<@Composable () -> Unit>(
        { AnimatedBorderCard() },
        { AnimatedSelectItem(color = getRandomColorComponents()) },
        { AnimatedStopWatch() },
        { AnimatedTopBar() },
        { HorizontalPager() },
        { LazyListScrollState() },
        { PhotoPicker() },
        { WebBrowser(color = getRandomColorComponents()) },
        { FluidButtom(color = getRandomColorComponents()) },
        { DarkModeSwitch() },
        { SpeedIndicator() },
        { CircularProgressSlider(colors = getRandomColorComponents()) },
        { CurvedScrollView() },
        { DraggableObject() },
        { FloatingButtonShowHide(color = getRandomColorComponents()) },
        { BottomBar(color = getRandomColorComponents()) },
        { SnackBar(color = getRandomColorComponents()) },
        { FlipHorizontal(color = getRandomColorComponents()) },
        { HeaderList(color = getRandomColorComponents()) },
        { FloatingButtonExpanded() },
        { ListDragDrop(color = getRandomColorComponents()) },
        { StaggeredGrid(color = getRandomColorComponents()) },
        { SwipeToDelete(color = getRandomColorComponents()) },
        { AnimationExpandList() }
    )

    val textsComponents = listOf(
        "Animated Border Card",
        "Animated Select Item",
        "Animated Stop Watch",
        "Animated Top Bar",
        "Horizontal Pager",
        "Lazy List Scroll State",
        "Photo Picker",
        "Web Browser",
        "Fluid Buttom",
        "Dark Mode Switch",
        "Speed Indicator",
        "Circular Progress Slider",
        "Curved ScrollView",
        "Draggable Object",
        "Floating Button Show/Hide",
        "Bottom Bar",
        "Snack Bar",
        "Flip Horizontal",
        "Header List",
        "Floating Button Expanded",
        "List Drag Drop",
        "Staggered Grid",
        "Swipe To Delete",
        "Animation Expand List"
    )