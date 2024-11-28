package com.dieyteixeira.componentes.ui.elements.games.game_memory

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieyteixeira.componentes.ui.elements.components.FlipCard
import com.dieyteixeira.componentes.ui.theme.BlueSky
import com.dieyteixeira.componentes.ui.theme.Green500
import com.dieyteixeira.componentes.ui.theme.Red
import com.dieyteixeira.componentes.ui.theme.Yellow
import kotlinx.coroutines.delay

@Composable
fun GameMemory(color: Color) {
    var gridSize by remember { mutableStateOf(GridSize(4, 3)) }
    var gameMode by remember { mutableStateOf(GameMode.OnePlayer) }
    var player1Name by remember { mutableStateOf("") }
    var player2Name by remember { mutableStateOf("") }
    var showSettings by remember { mutableStateOf(true) }

    val onStartGame: (GameConfig) -> Unit = { config ->
        showSettings = false
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "JoGo DA MEMóRIA",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 30.sp),
            color = if (color == Yellow) Color.Black else Color.White
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showSettings) {
            GameSettings(
                color = color,
                gridSize = gridSize,
                gameMode = gameMode,
                onGridSizeSelected = { selectedSize -> gridSize = selectedSize },
                onGameModeSelected = { selectedMode -> gameMode = selectedMode },
                player1Name = player1Name,
                player2Name = player2Name,
                onPlayer1NameChange = { player1Name = it },
                onPlayer2NameChange = { player2Name = it },
                onStartGame = onStartGame
            )
        } else {
            MemoryGame(
                config = GameConfig(gridSize, gameMode),
                isTwoPlayers = gameMode == GameMode.TwoPlayers,
                player1Name = player1Name,
                player2Name = player2Name,
                onNewGame = { showSettings = true }
            )
        }
    }
}

@Composable
fun MemoryGame(
    config: GameConfig,
    isTwoPlayers: Boolean,
    player1Name: String,
    player2Name: String,
    onNewGame: () -> Unit
) {
    val context = LocalContext.current
    var isWaitingForFlip by remember { mutableStateOf(false) }
    var showEndOneGameDialog by remember { mutableStateOf(false) }
    var showEndTwoGameDialog by remember { mutableStateOf(false) }

    var winnerName by remember { mutableStateOf("") }
    var winnerScore by remember { mutableStateOf(0) }
    var winnerColor by remember { mutableStateOf(Color.White) }
    var loserName by remember { mutableStateOf("") }
    var loserScore by remember { mutableStateOf(0) }
    var loserColor by remember { mutableStateOf(Color.White) }
    var isDraw by remember { mutableStateOf(false) }
    var gridSize by remember { mutableStateOf(config.gridSize) }
    var gridSizeAtual by remember { mutableStateOf(GridSize(4, 3)) }
    var levelAtual by remember { mutableStateOf(1) }

    var gameState by remember {
        mutableStateOf(
            MemoryGameState(
                grid = generateGrid(config.gridSize),
                revealed = MutableList(config.gridSize.rows * config.gridSize.columns) { false },
                matched = MutableList(config.gridSize.rows * config.gridSize.columns) { false },
                firstChoice = null,
                secondChoice = null,
                movesPlayer = 0,
                currentPlayer = 1,
                player1Name = player1Name,
                player2Name = player2Name,
                rows = config.gridSize.rows,
                columns = config.gridSize.columns,
                matchedPairs = mutableMapOf()
            )
        )
    }

    if (showEndOneGameDialog) {
        EndOneGameDialog(
            winnerName = winnerName,
            gridSize = gridSizeAtual,
            onDismiss = {
                showEndOneGameDialog = false
                onNewGame()
            },
            onNextLevel = {
                Log.d("GameState", "GridSize inicial: ${config.gridSize}")
                Log.d("GameState", "Nível atual: $gridSizeAtual")

                val nextGridSize = when (gridSizeAtual) {
                    GridSize(4, 3) -> GridSize(4, 4)
                    GridSize(4, 4) -> GridSize(5, 4)
                    GridSize(5, 4) -> GridSize(6, 4)
                    GridSize(6, 4) -> GridSize(6, 5)
                    GridSize(6, 5) -> GridSize(6, 6)
                    GridSize(6, 6) -> GridSize(7, 6)
                    GridSize(7, 6) -> GridSize(8, 6)
                    else -> gridSizeAtual
                }

                val level = when (nextGridSize) {
                    GridSize(4, 3) -> 1
                    GridSize(4, 4) -> 2
                    GridSize(5, 4) -> 3
                    GridSize(6, 4) -> 4
                    GridSize(6, 5) -> 5
                    GridSize(6, 6) -> 6
                    GridSize(7, 6) -> 7
                    else -> 8
                }

                Log.d("GameState", "Atualizando para o próximo gridSize: $nextGridSize")

                gameState = gameState.copy(
                    grid = generateGrid(nextGridSize),
                    revealed = MutableList(nextGridSize.rows * nextGridSize.columns) { false },
                    matched = MutableList(nextGridSize.rows * nextGridSize.columns) { false },
                    firstChoice = null,
                    secondChoice = null,
                    movesPlayer = 0,
                    currentPlayer = 3,
                    player1Name = player1Name,
                    player2Name = player2Name,
                    rows = nextGridSize.rows,
                    columns = nextGridSize.columns,
                    matchedPairs = mutableMapOf()
                )

                gridSizeAtual = nextGridSize
                levelAtual = level

                showEndOneGameDialog = false
                Log.d("GameState", "GridSize atualizada para: $gridSizeAtual")
            }
        )

        Log.d("GameState", "ShowEndOneGameDialog: $showEndOneGameDialog")
    }

    if (showEndTwoGameDialog) {
        EndTwoGameDialog(
            winnerName = winnerName,
            winnerScore = winnerScore,
            winnerColor = winnerColor,
            loserName = loserName,
            loserScore = loserScore,
            loserColor = loserColor,
            isDraw = isDraw,
            onNewGame = {
                showEndTwoGameDialog = false
                onNewGame()
            },
            onRestartGame = {
                showEndTwoGameDialog = false
                gameState = gameState.copy(
                    grid = generateGrid(config.gridSize),
                    revealed = MutableList(config.gridSize.rows * config.gridSize.columns) { false },
                    matched = MutableList(config.gridSize.rows * config.gridSize.columns) { false },
                    firstChoice = null,
                    secondChoice = null,
                    movesPlayer = 0,
                    currentPlayer = 1,
                    player1Name = player1Name,
                    player2Name = player2Name,
                    rows = config.gridSize.rows,
                    columns = config.gridSize.columns,
                    matchedPairs = mutableMapOf()
                )
            }
        )
    }

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

                if (isTwoPlayers) {
                    gameState = gameState.copy(currentPlayer = if (gameState.currentPlayer == 1) 2 else 1)
                } else {
                    gameState = gameState.copy(currentPlayer = 3)
                }

                isWaitingForFlip = false
            }

            gameState = gameState.copy(firstChoice = null, secondChoice = null)

            if (!isTwoPlayers && gameState.currentPlayer == 3) {
                gameState = gameState.copy(movesPlayer = gameState.movesPlayer + 1)
            }
        }
    }

    LaunchedEffect(gameState.matched) {
        if (gameState.matched.all { it }) {
            val player1Score = countPairsByPlayer(gameState.matchedPairs, 1)
            val player2Score = countPairsByPlayer(gameState.matchedPairs, 2)

            if (isTwoPlayers) {
                if (player1Score > player2Score) {
                    winnerName = gameState.player1Name
                    winnerScore = player1Score
                    winnerColor = BlueSky
                    loserName = gameState.player2Name
                    loserScore = player2Score
                    loserColor = Red

                    saveTwoPlayersVictory(
                        context,
                        gameState.player1Name,
                        gameState.player2Name,
                        "Memoria"
                    )
                } else if (player2Score > player1Score) {
                    winnerName = gameState.player2Name
                    winnerScore = player2Score
                    winnerColor = Red
                    loserName = gameState.player1Name
                    loserScore = player1Score
                    loserColor = BlueSky

                    saveTwoPlayersVictory(
                        context,
                        gameState.player2Name,
                        gameState.player1Name,
                        "Memoria"
                    )
                } else {
                    isDraw = true
                    winnerScore = player1Score
                }

                showEndTwoGameDialog = true
            } else {
                winnerName = gameState.player1Name
                gridSize = gridSizeAtual

                showEndOneGameDialog = true
            }
        }
    }

    val player1Victories = getTwoPlayersVictories(context, player1Name, player2Name, "Memoria")
    val player2Victories = getTwoPlayersVictories(context, player2Name, player1Name, "Memoria")

    // Desenho do tabuleiro
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isTwoPlayers) {
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Placar total",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 10.sp),
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier
                        .width(150.dp)
                        .height(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .width(75.dp)
                            .height(30.dp)
                            .background(
                                color = BlueSky,
                                shape = CutCornerShape(0, 50, 50, 0)
                            )
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$player1Victories",
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = 22.sp),
                            color = Color.White
                        )
                    }
                    Row(
                        modifier = Modifier
                            .width(75.dp)
                            .height(30.dp)
                            .background(
                                color = Red,
                                shape = CutCornerShape(50, 0, 0, 50)
                            )
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$player2Victories",
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = 22.sp),
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .width(150.dp)
                            .height(40.dp)
                            .background(
                                color = if (gameState.currentPlayer == 1) BlueSky else Color.LightGray.copy(
                                    alpha = 0.5f
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = player1Name,
                                style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp),
                                color = if (gameState.currentPlayer == 1) Color.White else BlueSky
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(40.dp)
                                .padding(2.dp)
                                .background(
                                    Color.White,
                                    shape = RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${countPairsByPlayer(gameState.matchedPairs, 1)}",
                                style = MaterialTheme.typography.displaySmall.copy(fontSize = 20.sp),
                                color = BlueSky
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Row(
                        modifier = Modifier
                            .width(150.dp)
                            .height(40.dp)
                            .background(
                                color = if (gameState.currentPlayer == 2) Red else Color.LightGray.copy(
                                    alpha = 0.5f
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(40.dp)
                                .padding(2.dp)
                                .background(
                                    Color.White,
                                    shape = RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${countPairsByPlayer(gameState.matchedPairs, 2)}",
                                style = MaterialTheme.typography.displaySmall.copy(fontSize = 20.sp),
                                color = Red
                            )
                        }
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = player2Name,
                                style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp),
                                color = if (gameState.currentPlayer == 2) Color.White else Red
                            )
                        }
                    }
                }
            }
        } else {
            Column (
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier
                            .width(150.dp)
                            .height(30.dp)
                            .background(
                                color = Green500,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Nível: $levelAtual",
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = 12.sp),
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(15.dp))
                    Row(
                        modifier = Modifier
                            .width(150.dp)
                            .height(30.dp)
                            .background(
                                color = Green500,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Grid: ${gridSizeAtual.rows} x ${gridSizeAtual.columns}",
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = 12.sp),
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Movimentos: ${gameState.movesPlayer}",
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp),
                    color = Color.Black
                )
            }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GridBoard(
    gameState: MemoryGameState,
    cardSize: Dp,
    onItemClick: (Int) -> Unit
) {

    val fontSize = with(LocalDensity.current) {
        (cardSize.toPx() * 0.75f).toSp()
    }

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
                                                1 -> BlueSky
                                                2 -> Red
                                                3 -> Green500
                                                else -> Color.LightGray
                                            }
                                        } else Color.LightGray
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = gameState.grid[index].toString(),
                                    style = MaterialTheme.typography.titleLarge.copy(fontSize = fontSize),
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