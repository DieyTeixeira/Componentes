package com.dieyteixeira.componentes.ui.elements.games

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieyteixeira.componentes.ui.theme.DarkGreen
import com.dieyteixeira.componentes.ui.theme.DarkGreen1
import com.dieyteixeira.componentes.ui.theme.DarkGreen10
import com.dieyteixeira.componentes.ui.theme.DarkGreen11
import com.dieyteixeira.componentes.ui.theme.DarkGreen12
import com.dieyteixeira.componentes.ui.theme.DarkGreen13
import com.dieyteixeira.componentes.ui.theme.DarkGreen14
import com.dieyteixeira.componentes.ui.theme.DarkGreen15
import com.dieyteixeira.componentes.ui.theme.DarkGreen16
import com.dieyteixeira.componentes.ui.theme.DarkGreen17
import com.dieyteixeira.componentes.ui.theme.DarkGreen18
import com.dieyteixeira.componentes.ui.theme.DarkGreen19
import com.dieyteixeira.componentes.ui.theme.DarkGreen2
import com.dieyteixeira.componentes.ui.theme.DarkGreen3
import com.dieyteixeira.componentes.ui.theme.DarkGreen4
import com.dieyteixeira.componentes.ui.theme.DarkGreen5
import com.dieyteixeira.componentes.ui.theme.DarkGreen6
import com.dieyteixeira.componentes.ui.theme.DarkGreen7
import com.dieyteixeira.componentes.ui.theme.DarkGreen8
import com.dieyteixeira.componentes.ui.theme.DarkGreen9
import com.dieyteixeira.componentes.ui.theme.LightGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.*

data class TetrisState(
    val board: Array<Array<Color>>,
    val currentTetromino: Tetromino,
    val position: Pair<Int, Int>,
    val score: Int = 0,
    val speed: Long = 500L,
    val isGameOver: Boolean = false
)

data class Tetromino(val shape: List<Pair<Int, Int>>, val color: Color)

class TetrisGame(private val scope: CoroutineScope, context: Context) {

    private val mutex = Mutex()
    private val mutableState = MutableStateFlow(
        TetrisState(board = Array(20) { Array(10) { DarkGreen.copy(alpha = 0.05f) } },
        currentTetromino = Tetromino(listOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(3, 0)), DarkGreen1),
        position = Pair(0, 5))
    )

    val state: Flow<TetrisState> = mutableState

    private var gameJob: kotlinx.coroutines.Job? = null
    private var speed: Long = 500L
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("game_preferences", Context.MODE_PRIVATE)

    var highScore: Int = sharedPreferences.getInt("high_score_tetris", 0)

    init {
        startGame()
    }

    private fun startGame() {
        gameJob?.cancel()

        gameJob = scope.launch {
            while (true) {
                delay(speed)
                mutableState.update {
                    if (it.isGameOver) {
                        if (it.score > highScore) {
                            highScore = it.score
                            saveHighScore(highScore)
                        }
                        return@update it
                    }

                    val newPosition = Pair(it.position.first + 1, it.position.second)

                    // Verifique a colisão com o fundo ou peças fixadas
                    val collision = checkCollision(it.currentTetromino, newPosition, it.board)
                    if (!collision) {
                        // Atualize a posição
                        it.copy(position = newPosition)
                    } else {
                        // Fixe a peça atual no tabuleiro
                        val updatedBoard = placeTetromino(it.board, it.currentTetromino, it.position)
                        if (checkCollision(it.currentTetromino, Pair(0, 5), updatedBoard)) {
                            return@update it.copy(isGameOver = true) // Fim do jogo
                        }
                        val newTetromino = generateNewTetromino()
                        val (clearedBoard, linesCleared) = clearFullLines(updatedBoard)

                        // Atualize o estado e gere a nova peça
                        it.copy(
                            board = clearedBoard,
                            currentTetromino = newTetromino,
                            position = Pair(0, 5), // A nova peça começa no topo
                            score = it.score + (linesCleared * 5)
                        )
                    }
                }
            }
        }
    }

    fun reset() {
        mutableState.update {
            TetrisState(board = Array(20) { Array(10) { DarkGreen.copy(alpha = 0.05f) } },
                currentTetromino = generateNewTetromino(), position = Pair(0, 5))
        }
        speed = 500L
        startGame()
    }

    private fun saveHighScore(score: Int) {
        sharedPreferences.edit().putInt("high_score_tetris", score).apply()
    }

    private fun checkCollision(tetromino: Tetromino, position: Pair<Int, Int>, board: Array<Array<Color>>): Boolean {
        return tetromino.shape.any { (x, y) ->
            val newX = position.first + x
            val newY = position.second + y
            newX >= 20 || newY < 0 || newY >= 10 || board[newX][newY] != DarkGreen.copy(alpha = 0.05f)
        }
    }

    private fun placeTetromino(board: Array<Array<Color>>, tetromino: Tetromino, position: Pair<Int, Int>): Array<Array<Color>> {
        val newBoard = board.map { it.clone() }.toTypedArray()
        tetromino.shape.forEach { (x, y) ->
            val newX = position.first + x
            val newY = position.second + y
            newBoard[newX][newY] = tetromino.color
        }
        return newBoard
    }

    private fun generateNewTetromino(): Tetromino {
        val tetrominoes = listOf(
            Tetromino(listOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(3, 0)), DarkGreen1), // I-h
            Tetromino(listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(0, 3)), DarkGreen2), // I-v

            Tetromino(listOf(Pair(0, 0), Pair(1, 0), Pair(0, 1), Pair(1, 1)), DarkGreen3), // O

            Tetromino(listOf(Pair(0, 1), Pair(1, 1), Pair(1, 0), Pair(2, 1)), DarkGreen4), // T-hc
            Tetromino(listOf(Pair(0, 0), Pair(1, 0), Pair(1, 1), Pair(2, 0)), DarkGreen5), // T-hb
            Tetromino(listOf(Pair(0, 0), Pair(0, 1), Pair(1, 1), Pair(0, 2)), DarkGreen6), // T-vd
            Tetromino(listOf(Pair(1, 0), Pair(1, 1), Pair(0, 1), Pair(1, 2)), DarkGreen7), // T-ve

            Tetromino(listOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(2, 1)), DarkGreen8), // L-h
            Tetromino(listOf(Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(0, 1)), DarkGreen9), // J-h
            Tetromino(listOf(Pair(0, 1), Pair(1, 1), Pair(2, 1), Pair(2, 0)), DarkGreen10), // L-h
            Tetromino(listOf(Pair(0, 0), Pair(0, 1), Pair(1, 1), Pair(2, 1)), DarkGreen11), // J-h

            Tetromino(listOf(Pair(0, 0), Pair(0, 1), Pair(0, 2), Pair(1, 2)), DarkGreen12), // L-v
            Tetromino(listOf(Pair(1, 0), Pair(1, 1), Pair(1, 2), Pair(0, 2)), DarkGreen13), // J-v
            Tetromino(listOf(Pair(1, 0), Pair(0, 0), Pair(0, 1), Pair(0, 2)), DarkGreen14), // L-v
            Tetromino(listOf(Pair(0, 0), Pair(1, 0), Pair(1, 1), Pair(1, 2)), DarkGreen15), // J-v

            Tetromino(listOf(Pair(0, 1), Pair(1, 1), Pair(1, 0), Pair(2, 0)), DarkGreen16), // S-h
            Tetromino(listOf(Pair(0, 0), Pair(1, 0), Pair(1, 1), Pair(2, 1)), DarkGreen17), // Z-h
            Tetromino(listOf(Pair(0, 0), Pair(0, 1), Pair(1, 1), Pair(1, 2)), DarkGreen18), // S-v
            Tetromino(listOf(Pair(1, 0), Pair(1, 1), Pair(0, 1), Pair(0, 2)), DarkGreen19)  // Z-v
        )
        return tetrominoes[Random().nextInt(tetrominoes.size)]
    }

    private fun clearFullLines(board: Array<Array<Color>>): Pair<Array<Array<Color>>, Int> {
        val newBoard = board.filterIndexed { index, row -> row.any { it == DarkGreen.copy(alpha = 0.05f) } }.toMutableList()
        val linesCleared  = 20 - newBoard.size
        repeat(linesCleared) {
            newBoard.add(0, Array(10) { DarkGreen.copy(alpha = 0.05f) })
        }
        return Pair(newBoard.toTypedArray(), linesCleared)
    }

    private fun rotateTetromino(tetromino: Tetromino): Tetromino {

        val centerX = tetromino.shape.sumOf { it.first } / tetromino.shape.size
        val centerY = tetromino.shape.sumOf { it.second } / tetromino.shape.size

        // Rotacione os blocos ao redor do centro
        val rotatedShape = tetromino.shape.map { (x, y) ->
            val relativeX = x - centerX
            val relativeY = y - centerY

            // Rotacione 90 graus no sentido horário
            val newX = -relativeY
            val newY = relativeX

            Pair(newX + centerX, newY + centerY)
        }

        return Tetromino(rotatedShape, tetromino.color)
    }

    fun move(move: Move) {
        mutableState.update { currentState ->
            if (currentState.isGameOver) return@update currentState

            val newPosition: Pair<Int, Int>
            val newTetromino: Tetromino

            when (move) {
                Move.Left -> {
                    newPosition = Pair(currentState.position.first, currentState.position.second - 1)
                    newTetromino = currentState.currentTetromino
                }
                Move.Right -> {
                    newPosition = Pair(currentState.position.first, currentState.position.second + 1)
                    newTetromino = currentState.currentTetromino
                }
                Move.Drop -> {
                    newPosition = Pair(currentState.position.first + 1, currentState.position.second)
                    newTetromino = currentState.currentTetromino
                }
            }

            val collision = checkCollision(newTetromino, newPosition, currentState.board)

            if (!collision) {
                currentState.copy(position = newPosition, currentTetromino = newTetromino)
            } else {
                // Caso de colisão ou no movimento de queda, fixar a peça e gerar uma nova
                if (move == Move.Drop) {
                    val updatedBoard = placeTetromino(currentState.board, currentState.currentTetromino, currentState.position)
                    val newTetrominoGenerated = generateNewTetromino()
                    val (clearedBoard, linesCleared) = clearFullLines(updatedBoard)
                    val newScore = currentState.score + (linesCleared * 5)
                    currentState.copy(
                        board = clearedBoard,
                        currentTetromino = newTetrominoGenerated,
                        position = Pair(0, 5),
                        score = newScore
                    )
                } else {
                    // Retornar sem alterar o estado para outros movimentos
                    currentState
                }
            }
        }
    }
}

@Composable
fun GameTetris() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val game = remember { TetrisGame(scope, context) }
    val state = game.state.collectAsState(initial = null)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        state.value?.let {
            if (it.isGameOver) {
                GameOverTetris(score = it.score, highScore = game.highScore) {
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
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp),
                        color = DarkGreen
                    )
                }
            } else {
                BoardTetris(it)

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
                            style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp),
                            color = DarkGreen
                        )
                    }
                }
            }
        }

        Controls { move ->
            game.move(
                when (move) {
                    Move.Left -> Move.Left
                    Move.Right -> Move.Right
                    Move.Drop -> Move.Drop
                }
            )
        }
    }
}

@Composable
fun GameOverTetris(score: Int, highScore: Int, onRestart: () -> Unit) {
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
                    text = "Game Over",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp),
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Final score: $score",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "High score: $highScore",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .background(DarkGreen, RoundedCornerShape(10.dp))
                        .clickable { onRestart() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Restart",
                        color = LightGreen,
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 20.sp),
                        modifier = Modifier.padding(8.dp)
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

@Composable
fun Controls(onMove: (Move) -> Unit) {
    val buttonSize = 64.dp
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        Row {
            Box(
                modifier = Modifier
                    .height(buttonSize)
                    .width(buttonSize)
                    .background(
                        DarkGreen,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onMove(Move.Left) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.size(15.dp))
            Box(
                modifier = Modifier
                    .height(buttonSize)
                    .width(buttonSize)
                    .background(
                        DarkGreen,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onMove(Move.Right) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.size(15.dp))
            Box(
                modifier = Modifier
                    .height(buttonSize)
                    .width(buttonSize)
                    .background(
                        DarkGreen,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onMove(Move.Drop) },
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
}

enum class Move {
    Left, Right, Drop
}

@Composable
fun BoardTetris(state: TetrisState) {
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
                Box {
                    // Renderiza o tabuleiro
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (rowIndex in 0 until state.board.size) {
                            Row(
                                horizontalArrangement = Arrangement.Center
                            ) {
                                for (colIndex in 0 until state.board[rowIndex].size) {
                                    val color = state.board[rowIndex][colIndex]

                                    // Renderiza a célula atual
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(color, RoundedCornerShape(2.dp)) // Cor do bloco
                                            .border(0.dp, LightGreen)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp * 0.75f)
                                                .align(Alignment.Center)
                                                .border(1.5.dp, LightGreen, RoundedCornerShape(1.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Adiciona a peça em movimento (não fixada) ao tabuleiro
                    state.currentTetromino.shape.forEach { (x, y) ->
                        val (pieceX, pieceY) = state.position
                        val boardX = pieceX + x
                        val boardY = pieceY + y

                        // Verifica se a posição está dentro dos limites do tabuleiro
                        if (boardX in 0 until state.board.size && boardY in 0 until state.board[0].size) {
                            Box(
                                modifier = Modifier
                                    .offset(x = (boardY * 16).dp, y = (boardX * 16).dp)
                                    .size(16.dp)
                                    .background(state.currentTetromino.color, RoundedCornerShape(2.dp))
                                    .border(0.dp, LightGreen)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp * 0.75f)
                                        .align(Alignment.Center)
                                        .border(1.5.dp, LightGreen, RoundedCornerShape(1.dp))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}