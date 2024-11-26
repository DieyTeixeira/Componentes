package com.dieyteixeira.componentes.ui.elements.games

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dieyteixeira.componentes.ui.theme.DarkGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Random

data class EscapeState(
    val lightPosition: Pair<Int, Int>,
    val shadows: List<Pair<Int, Int>>,
    val sparks: List<Pair<Int, Int>>,
    val score: Int = 0,
    val isGameOver: Boolean = false
)

class EscapeGame(private val scope: CoroutineScope) {
    private val mutableState = MutableStateFlow(
        EscapeState(
            lightPosition = Pair(10, 10),
            shadows = emptyList(),
            sparks = listOf(Pair(5, 5), Pair(15, 15))
        )
    )
    val state: Flow<EscapeState> = mutableState

    var moveDirection = Pair(1, 0)
    private var gameJob: kotlinx.coroutines.Job? = null

    init {
        startGame()
    }

    private fun startGame() {
        gameJob?.cancel()
        gameJob = scope.launch {
            while (true) {
                delay(200)
                mutableState.update { state ->
                    if (state.isGameOver) return@update state

                    // Atualiza posição da luz
                    val newLightPosition = Pair(
                        (state.lightPosition.first + moveDirection.first + BOARD_SIZE) % BOARD_SIZE,
                        (state.lightPosition.second + moveDirection.second + BOARD_SIZE) % BOARD_SIZE
                    )

                    // Adiciona novas sombras
                    val newShadows = state.shadows.map { shadow ->
                        val direction = Pair(
                            (newLightPosition.first - shadow.first).coerceIn(-1, 1),
                            (newLightPosition.second - shadow.second).coerceIn(-1, 1)
                        )
                        Pair(
                            (shadow.first + direction.first + BOARD_SIZE) % BOARD_SIZE,
                            (shadow.second + direction.second + BOARD_SIZE) % BOARD_SIZE
                        )
                    } + if (Random().nextInt(5) == 0) {
                        listOf(Pair(Random().nextInt(BOARD_SIZE), Random().nextInt(BOARD_SIZE)))
                    } else emptyList()

                    // Detecta colisão
                    val isCollision = newShadows.any { it == newLightPosition }

                    // Coleta faíscas
                    val newSparks = state.sparks.filter { it != newLightPosition }
                    val collectedSpark = newSparks.size < state.sparks.size

                    state.copy(
                        lightPosition = newLightPosition,
                        shadows = newShadows,
                        sparks = newSparks,
                        score = state.score + if (collectedSpark) 10 else 0,
                        isGameOver = isCollision
                    )
                }
            }
        }
    }

    fun reset() {
        mutableState.update {
            EscapeState(
                lightPosition = Pair(10, 10),
                shadows = emptyList(),
                sparks = listOf(Pair(5, 5), Pair(15, 15))
            )
        }
        startGame()
    }

    companion object {
        const val BOARD_SIZE = 20
    }
}

@Composable
fun EscapeScreen() {
    val scope = rememberCoroutineScope()
    val game = remember { EscapeGame(scope) }
    val state = game.state.collectAsState(initial = null)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        state.value?.let {
            if (it.isGameOver) {
                Text(text = "Game Over! Score: ${it.score}")
                Button(onClick = { game.reset() }) {
                    Text("Restart")
                }
            } else {
                EscapeBoard(state = it)
                Text("Score: ${it.score}")
            }
        }
        ButtonsEscape(onDirectionChange = {
            game.moveDirection = it
        })
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun EscapeBoard(state: EscapeState) {
    BoxWithConstraints(
        Modifier
            .background(Color.Black)
            .padding(16.dp)
    ) {
        val tileSize = maxWidth / EscapeGame.BOARD_SIZE

        // Desenha borda do tabuleiro
        Box(
            Modifier
                .size(maxWidth)
                .border(2.dp, Color.Gray)
        )

        // Desenha faíscas
        state.sparks.forEach { spark ->
            Box(
                modifier = Modifier
                    .offset(x = tileSize * spark.first, y = tileSize * spark.second)
                    .size(tileSize)
                    .background(Color.Yellow, RoundedCornerShape(50))
            )
        }

        // Desenha sombras
        state.shadows.forEach { shadow ->
            Box(
                modifier = Modifier
                    .offset(x = tileSize * shadow.first, y = tileSize * shadow.second)
                    .size(tileSize)
                    .background(Color.DarkGray, RoundedCornerShape(10))
            )
        }

        // Desenha luz
        Box(
            modifier = Modifier
                .offset(
                    x = tileSize * state.lightPosition.first,
                    y = tileSize * state.lightPosition.second
                )
                .size(tileSize)
                .background(Color.Cyan, RoundedCornerShape(50))
        )
    }
}

@Composable
fun ButtonsEscape(onDirectionChange: (Pair<Int, Int>) -> Unit) {
    val buttonSize = 65.dp
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(10.dp)) {
        Box(
            modifier = Modifier
                .height(buttonSize)
                .width(buttonSize)
                .background(
                    DarkGreen,
                    RoundedCornerShape(10.dp)
                )
                .clickable { onDirectionChange(Pair(0, -1)) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        Row {
            Box(
                modifier = Modifier
                    .height(buttonSize)
                    .width(buttonSize)
                    .background(
                        DarkGreen,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onDirectionChange(Pair(-1, 0)) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.size(buttonSize))
            Box(
                modifier = Modifier
                    .height(buttonSize)
                    .width(buttonSize)
                    .background(
                        DarkGreen,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onDirectionChange(Pair(1, 0)) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Box(
            modifier = Modifier
                .height(buttonSize)
                .width(buttonSize)
                .background(
                    DarkGreen,
                    RoundedCornerShape(10.dp)
                )
                .clickable { onDirectionChange(Pair(0, 1)) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
