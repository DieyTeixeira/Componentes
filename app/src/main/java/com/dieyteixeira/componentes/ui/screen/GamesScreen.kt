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
import com.dieyteixeira.componentes.ui.elements.games.EscapeScreen
import com.dieyteixeira.componentes.ui.elements.games.GameMemory
import com.dieyteixeira.componentes.ui.elements.games.GamePacMan
import com.dieyteixeira.componentes.ui.elements.games.GameSnake
import com.dieyteixeira.componentes.ui.elements.games.GameTetris
import com.dieyteixeira.componentes.ui.elements.games.GameVelha
import com.dieyteixeira.componentes.ui.elements.games.MemoryGame

@Composable
fun GamesScreen() {

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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            screensGames[currentIndex]()
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
                        currentIndex = if (currentIndex > 0) currentIndex - 1 else screensGames.size - 1
                    },
                Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_double_arrow_left),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(text = textsGames[currentIndex])

            // Botão para incrementar o índice
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        currentIndex = (currentIndex + 1) % screensGames.size
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

fun getRandomColorGames(): Color {
    val colors = listOf(
        Color.Blue,
        Color.Red,
        Color.Green,
        Color.Magenta
    )
    return colors.random()
}

    @OptIn(ExperimentalAnimationApi::class)
    val screensGames = listOf<@Composable () -> Unit>(
        { GameSnake() },
        { GameVelha() },
        { GameTetris() },
        { GamePacMan() },
        { EscapeScreen() },
        { GameMemory() }
    )

    val textsGames = listOf(
        "Game Snake",
        "Game Velha",
        "Game Tetris",
        "Game PacMan",
        "Escape Game",
        "Game Memory"
    )