package com.dieyteixeira.componentes.ui.elements.games

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieyteixeira.componentes.ui.theme.DarkGreen
import com.dieyteixeira.componentes.ui.theme.LightGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

data class State(
    val food: Pair<Int, Int>,
    val snake: List<Pair<Int, Int>>,
    val score: Int = 0,
    val speed: Long = 150L,
    val isGameOver: Boolean = false
)

class Game(private val scope: CoroutineScope, context: Context) {

    private val mutex = Mutex()
    private val mutableState =
        MutableStateFlow(State(food = Pair(5, 5), snake = listOf(Pair(7, 7))))
    val state: Flow<State> = mutableState

    var move = Pair(1, 0)
        set(value) {
            scope.launch {
                mutex.withLock {
                    if (value.first != -move.first || value.second != -move.second) {
                        field = value // Se não for oposta, atualiza a direção
                    }
                }
            }
        }

    private var gameJob: kotlinx.coroutines.Job? = null
    private var speed: Long = 150L
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("game_preferences", Context.MODE_PRIVATE)

    var highScoreSnake: Int = sharedPreferences.getInt("high_score_snake", 0)

    init {
        startGame()
    }

    private fun startGame() {

        gameJob?.cancel()

        gameJob = scope.launch {
            var snakeLength = 4

            while (true) {
                delay(speed)
                mutableState.update {
                    if (it.isGameOver) {
                        if (it.score > highScoreSnake) {
                            highScoreSnake = it.score
                            saveHighScore(highScoreSnake) // Salva o novo recorde
                        }
                        return@update it // Pausa o jogo se estiver em "Game Over"
                    }

                    val newPosition = it.snake.first().let { poz ->
                        mutex.withLock {
                            Pair(
                                (poz.first + move.first + BOARD_SIZE) % BOARD_SIZE,
                                (poz.second + move.second + BOARD_SIZE) % BOARD_SIZE
                            )
                        }
                    }

                    val ateFood = newPosition == it.food

                    if (ateFood) {
                        snakeLength++
                        speed = (speed * 0.99935).toLong().coerceAtLeast(50L)
                    }

                    val isCollision = it.snake.contains(newPosition)

                    it.copy(
                        food = if (ateFood) Pair(
                            Random().nextInt(BOARD_SIZE),
                            Random().nextInt(BOARD_SIZE)
                        ) else it.food,
                        snake = if (isCollision) it.snake else listOf(newPosition) + it.snake.take(snakeLength - 1),
                        score = if (ateFood) it.score + 5 else it.score,
                        speed = 150 - speed,
                        isGameOver = isCollision // Define "Game Over" em caso de colisão
                    )
                }
            }
        }
    }

    fun reset() {
        mutableState.update {
            State(food = Pair(5, 5), snake = listOf(Pair(7, 7)))
        }
        move = Pair(1, 0)
        speed = 150L
        startGame()
    }

    private fun saveHighScore(score: Int) {
        sharedPreferences.edit().putInt("high_score_snake", score).apply()
    }

    companion object {
        const val BOARD_SIZE = 24
    }
}

@Composable
fun GameSnake() {

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val game = remember { Game(scope, context) }
    val state = game.state.collectAsState(initial = null)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        state.value?.let {
            if (it.isGameOver) {

                GameOverSnake(score = it.score, highScore = game.highScoreSnake) {
                    game.reset()
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .background(LightGreen),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 20.sp
                        ),
                        color = DarkGreen
                    )
                }

            } else {

                BoardSnake(it)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .background(LightGreen),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.8f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "score: ${it.score}",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 20.sp
                            ),
                            color = DarkGreen
                        )
                        Text(
                            text = "vel: ${it.speed}",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 20.sp
                            ),
                            color = DarkGreen
                        )
                    }
                }
            }
        }
        ButtonsSnake {
            game.move = it
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun GameOverSnake(score: Int, highScore: Int, onRestart: () -> Unit) {
    BoxWithConstraints(
        Modifier
            .background(LightGreen)
            .padding(16.dp)
    ) {

        Box(
            Modifier
                .size(maxWidth)
                .border(2.dp, DarkGreen)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightGreen),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "game over",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp),
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "final score: $score",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "high score: $highScore",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .background(DarkGreen, RoundedCornerShape(10.dp))
                        .clickable {
                            onRestart()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Restart",
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
                        color = LightGreen,
                        modifier = Modifier.padding(15.dp, 5.dp, 15.dp, 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(70.dp))
                Text(
                    text = "criado por Diey Teixeira",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp),
                    color = DarkGreen
                )
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun BoardSnake(state: State) {
    BoxWithConstraints(
        Modifier
            .background(LightGreen)
            .padding(16.dp)
    ) {
        val tileSize = maxWidth / Game.BOARD_SIZE

        Box(
            Modifier
                .size(maxWidth)
                .border(2.dp, DarkGreen)
        )

        Box(
            Modifier
                .offset(x = tileSize * state.food.first, y = tileSize * state.food.second)
                .size(tileSize)
                .rotate(45f)
                .background(Color.Transparent)
        ) {
            Column {
                Row {
                    Box(
                        Modifier
                            .size(tileSize / 2)
                            .padding(0.8.dp)
                            .background(DarkGreen, RoundedCornerShape(1.dp))
                    )
                    Box(
                        Modifier
                            .size(tileSize / 2)
                            .padding(0.8.dp)
                            .background(DarkGreen, RoundedCornerShape(1.dp))
                    )
                }
                Row {
                    Box(
                        Modifier
                            .size(tileSize / 2)
                            .padding(0.8.dp)
                            .background(DarkGreen, RoundedCornerShape(1.dp))
                    )
                    Box(
                        Modifier
                            .size(tileSize / 2)
                            .padding(0.8.dp)
                            .background(DarkGreen, RoundedCornerShape(1.dp))
                    )
                }
            }
        }

        state.snake.forEach {
            Box(
                modifier = Modifier
                    .offset(x = tileSize * it.first, y = tileSize * it.second)
                    .size(tileSize)
                    .background(DarkGreen, RoundedCornerShape(2.dp))
                    .border(0.dp, LightGreen)
            ) {
                Box(
                    modifier = Modifier
                        .size(tileSize * 0.75f)
                        .align(Alignment.Center)
                        .border(1.5.dp, LightGreen, RoundedCornerShape(1.dp))
                )
            }
        }
    }
}

@Composable
fun ButtonsSnake(onDirectionChange: (Pair<Int, Int>) -> Unit) {
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