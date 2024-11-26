package com.dieyteixeira.componentes.ui.elements.games

import android.graphics.fonts.Font
import android.graphics.fonts.FontFamily
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.dieyteixeira.componentes.ui.elements.components.FlipCard
import com.dieyteixeira.componentes.ui.theme.BlueSky
import com.dieyteixeira.componentes.ui.theme.Green
import com.dieyteixeira.componentes.ui.theme.Green500
import com.dieyteixeira.componentes.ui.theme.Red
import kotlinx.coroutines.delay

@Composable
fun GameMemory() {
    var gridSize by remember { mutableStateOf(GridSize(5, 4)) }
    var gameMode by remember { mutableStateOf(GameMode.OnePlayer) }
    var showDialog by remember { mutableStateOf(true) }

    val onStartGame: (GameConfig) -> Unit = { config ->
        showDialog = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "JOGO DA MEMÓRIA",
            style = MaterialTheme.typography.displaySmall.copy(fontSize = 30.sp),
            color = Color.Black
        )

        if (showDialog) {
            GameSettingsDialog(
                gridSize = gridSize,
                gameMode = gameMode,
                onGridSizeSelected = { selectedSize -> gridSize = selectedSize },
                onGameModeSelected = { selectedMode -> gameMode = selectedMode },
                onStartGame = onStartGame
            )
        } else {
            MemoryGame(config = GameConfig(gridSize, gameMode), isTwoPlayers = gameMode == GameMode.TwoPlayers)
        }
    }
}

@Composable
fun GameSettingsDialog(
    gridSize: GridSize,
    gameMode: GameMode,
    onGridSizeSelected: (GridSize) -> Unit,
    onGameModeSelected: (GameMode) -> Unit,
    onStartGame: (GameConfig) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))

        Text(
            "Configurações do Jogo",
            style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Tamanho do jogo:",
            style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        GridSizeSelector(selectedSize = gridSize, onSizeSelected = onGridSizeSelected)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Modo de jogo:",
            style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        GameModeSelector(selectedMode = gameMode, onModeSelected = onGameModeSelected)

        Spacer(modifier = Modifier.height(25.dp))

        Box(
            modifier = Modifier
                .width(100.dp)
                .height(35.dp)
                .background(Green500, shape = RoundedCornerShape(100))
                .clickable { onStartGame(GameConfig(gridSize, gameMode)) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Iniciar Jogo",
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 14.sp),
                color = Color.White
            )
        }
    }
}

@Composable
fun GridSizeSelector(selectedSize: GridSize, onSizeSelected: (GridSize) -> Unit) {
    val sizes = listOf(
        GridSize(5, 4), GridSize(6, 4), GridSize(6, 5),
        GridSize(6, 6), GridSize(7, 6)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            sizes.take(3).forEach { size ->
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(30.dp)
                        .background(if (size == selectedSize) BlueSky else Color.LightGray, shape = RoundedCornerShape(10.dp))
                        .clickable{ onSizeSelected(size) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Grid ${size.columns}x${size.rows}",
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 16.sp),
                        color = if (size == selectedSize) Color.White else Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            sizes.drop(3).forEach { size ->
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(30.dp)
                        .background(if (size == selectedSize) BlueSky else Color.LightGray, shape = RoundedCornerShape(10.dp))
                        .clickable{ onSizeSelected(size) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Grid ${size.columns}x${size.rows}",
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 16.sp),
                        color = if (size == selectedSize) Color.White else Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun GameModeSelector(selectedMode: GameMode, onModeSelected: (GameMode) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GameMode.entries.forEach { mode ->
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .height(30.dp)
                    .background(if (mode == selectedMode) BlueSky else Color.LightGray, shape = RoundedCornerShape(10.dp))
                    .clickable{onModeSelected(mode)},
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = mode.displayName,
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 16.sp),
                    color = if (mode == selectedMode) Color.White else Color.Black
                )
            }
        }
    }
}

enum class GameMode(val displayName: String) {
    OnePlayer("1 Player"),
    TwoPlayers("2 Players")
}

data class GridSize(val rows: Int, val columns: Int)

data class GameConfig(val gridSize: GridSize, val gameMode: GameMode)

data class MemoryGameState(
    val grid: List<Int>,
    val revealed: MutableList<Boolean>,
    val matched: MutableList<Boolean>,
    var firstChoice: Int?,
    var secondChoice: Int?,
    var movesPlayer1: Int,
    var movesPlayer2: Int,
    var currentPlayer: Int,
    val rows: Int,
    val columns: Int,
    val matchedPairs: MutableMap<Int, Int>
)

@Composable
fun MemoryGame(config: GameConfig, isTwoPlayers: Boolean) {
    var gameState by remember {
        mutableStateOf(
            MemoryGameState(
                grid = generateGrid(config.gridSize),
                revealed = MutableList(config.gridSize.rows * config.gridSize.columns) { false },
                matched = MutableList(config.gridSize.rows * config.gridSize.columns) { false },
                firstChoice = null,
                secondChoice = null,
                movesPlayer1 = 0,
                movesPlayer2 = 0,
                currentPlayer = 1,
                rows = config.gridSize.rows,
                columns = config.gridSize.columns,
                matchedPairs = mutableMapOf()
            )
        )
    }

    var isWaitingForFlip by remember { mutableStateOf(false) }

    LaunchedEffect(gameState.firstChoice, gameState.secondChoice) {

        if (gameState.firstChoice != null && gameState.secondChoice != null) {
            val first = gameState.firstChoice!!
            val second = gameState.secondChoice!!

            if (gameState.grid[first] == gameState.grid[second]) {
                isWaitingForFlip = true
                delay(400)

                val newMatched = gameState.matched.toMutableList()
                newMatched[first] = true
                newMatched[second] = true

                val newMatchedPairs = gameState.matchedPairs.toMutableMap()
                newMatchedPairs[first] = gameState.currentPlayer
                newMatchedPairs[second] = gameState.currentPlayer

                gameState = gameState.copy(
                    matched = newMatched,
                    matchedPairs = newMatchedPairs
                )

                isWaitingForFlip = false
            } else {
                isWaitingForFlip = true
                delay(800)
                val newRevealed = gameState.revealed.toMutableList()
                newRevealed[first] = false
                newRevealed[second] = false

                gameState = gameState.copy(revealed = newRevealed)
                isWaitingForFlip = false
            }

            gameState = gameState.copy(firstChoice = null, secondChoice = null)

            if (gameState.currentPlayer == 1) {
                gameState = gameState.copy(movesPlayer1 = gameState.movesPlayer1 + 1)
            } else {
                gameState = gameState.copy(movesPlayer2 = gameState.movesPlayer2 + 1)
            }

            if (isTwoPlayers) {
                gameState = gameState.copy(currentPlayer = if (gameState.currentPlayer == 1) 2 else 1)
            }

//            gameState = gameState.copy(currentPlayer = if (gameState.currentPlayer == 1) 2 else 1)
        }
    }

    // Desenho do tabuleiro
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isTwoPlayers) {
            Text(
                text = "Jogador da vez: ${gameState.currentPlayer}",
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp),
                color = Color.Black
            )
        } else {
            Text(
                text = "Movimentos: ${gameState.movesPlayer1}",
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp),
                color = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val cardSize = calculateCardSize(gameState.rows, gameState.columns)

        GridBoard(gameState, cardSize) { index ->
            if (!gameState.revealed[index] && !gameState.matched[index] && !isWaitingForFlip) {
                val newRevealed = gameState.revealed.toMutableList()
                newRevealed[index] = true

                if (gameState.firstChoice == null) {
                    gameState = gameState.copy(firstChoice = index, revealed = newRevealed)
                } else {
                    gameState = gameState.copy(secondChoice = index, revealed = newRevealed)
                }
            }
        }
    }
}

@Composable
fun calculateCardSize(rows: Int, columns: Int): Dp {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val availableWidth = screenWidth / columns
    val availableHeight = screenHeight / rows

    return min(availableWidth, availableHeight) * 0.7f
}

fun generateGrid(size: GridSize): List<Int> {
    val numbers = mutableListOf<Int>()
    for (i in 1..(size.rows * size.columns / 2)) {
        numbers.add(i)
        numbers.add(i)
    }
    numbers.shuffle()
    return numbers
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GridBoard(
    gameState: MemoryGameState,
    cardSize: Dp,
    onItemClick: (Int) -> Unit
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in 0 until gameState.rows) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (col in 0 until gameState.columns) {
                    val index = row * gameState.columns + col
                    var showExplosion by remember { mutableStateOf(false) }
                    LaunchedEffect(gameState.matched[index]) {
                        if (gameState.matched[index]) {
                            delay(350)
                            showExplosion = true
                        }
                    }

                    FlipRotate(
                        flipCard = if (gameState.revealed[index] || gameState.matched[index]) FlipCard.Previous else FlipCard.Forward,
                        onClick = { onItemClick(index) },
                        isMatched = gameState.matched[index],
                        matchPlayer = gameState.matchedPairs[index] ?: 0,
                        modifier = Modifier
                            .padding(4.dp)
                            .size(cardSize),
                        forward = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {}
                        },
                        previous = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        if (showExplosion) {
                                            when (gameState.matchedPairs[index]) {
                                                1 -> Red
                                                2 -> BlueSky
                                                else -> Green
                                            }
                                        } else Color.LightGray
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = gameState.grid[index].toString(),
                                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 30.sp),
                                    color = if (gameState.matched[index]) Color.White else Color.Black
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun FlipRotate(
    flipCard: FlipCard,
    onClick: () -> Unit,
    isMatched: Boolean,
    matchPlayer: Int,
    modifier: Modifier = Modifier,
    forward: @Composable () -> Unit,
    previous: @Composable () -> Unit
) {
    var showExplosion  by remember { mutableStateOf(false) }

    LaunchedEffect(isMatched) {
        if (isMatched) {
            showExplosion  = true
            delay(1000)
            showExplosion  = false
        }
    }

    val rotation = animateFloatAsState(
        targetValue = flipCard.angle,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )

    val scale = animateFloatAsState(
        targetValue = if (isMatched) 1f else 1f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )

    Card(
        onClick = { onClick() },
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 12f * density
                scaleX = scale.value
                scaleY = scale.value
            },
        shape = RoundedCornerShape(15.dp),
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (rotation.value <= 90f) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    forward()
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = 180f
                        }
                ) {
                    previous()

                    if (showExplosion) {
                        SparksEffect(modifier, matchPlayer)
                    }
                }
            }
        }
    }
}

@Composable
fun SparksEffect(modifier: Modifier, matchPlayer: Int) {
    val explosionColor = when (matchPlayer) {
        1 -> Red
        2 -> BlueSky
        else -> Green
    }

    val infiniteTransition = rememberInfiniteTransition()

    val radius = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    )

    val alpha = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Gerando faíscas com dispersão e alpha
        drawCircle(
            color = explosionColor,
            radius = radius.value * size.minDimension, // expanding circle
            center = Offset(centerX, centerY),
            alpha = alpha.value // fade out the explosion
        )
    }
}

fun Modifier.clickable(onClick: () -> Unit): Modifier = this.pointerInput(Unit) {
    detectTapGestures(onTap = { onClick() })
}