package com.dieyteixeira.componentes.ui.components.game_pacman

import android.content.Context
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
import com.dieyteixeira.componentes.ui.components.game_snake.Buttons
import com.dieyteixeira.componentes.ui.components.game_snake.Game
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

data class PacManState(
    val pacman: Pair<Int, Int>,
    val food: List<Pair<Int, Int>>,
    val ghosts: List<Pair<Int, Int>>,
    val walls: List<Pair<Int, Int>>,  // Novo campo de paredes
    val score: Int = 0,
    val isGameOver: Boolean = false,
    val isInvulnerable: Boolean = true
)

class PacManGame(private val scope: CoroutineScope, context: Context) {

    private val mutex = Mutex()
    private val mutableState =
        MutableStateFlow(
            PacManState(
                pacman = Pair(5, 5),
                food = generateFood(),
                ghosts = listOf(Pair(1, 1), Pair(7, 7)),
                walls = generateWalls()  // Gerar as paredes
            )
        )
    val state: Flow<PacManState> = mutableState

    var move = Pair(1, 0)
        set(value) {
            scope.launch {
                mutex.withLock {
                    if (value.first != -move.first || value.second != -move.second) {
                        field = value // Evitar movimentos opostos
                    }
                }
            }
        }

    private var gameJob: kotlinx.coroutines.Job? = null
    private var invulnerabilityJob: kotlinx.coroutines.Job? = null

    init {
        startGame()
    }

    private fun startGame() {
        gameJob?.cancel()
        invulnerabilityJob?.cancel()

        gameJob = scope.launch {
            mutableState.update {
                it.copy(isInvulnerable = true) // Inicia o jogo com invulnerabilidade
            }
            invulnerabilityJob = launch {
                delay(3000L) // Pac-Man fica invulnerável por 3 segundos
                mutableState.update { it.copy(isInvulnerable = false) }
            }

            while (true) {
                delay(300L)
                mutableState.update {
                    if (it.isGameOver) return@update it

                    // Movimento do Pac-Man
                    val newPacmanPosition = movePacMan(it.pacman, it.walls)
                    val ateFood = it.food.contains(newPacmanPosition)
                    val remainingFood = it.food.filter { food -> food != newPacmanPosition }

                    // Movimento dos fantasmas (logicamente, seguem o Pac-Man)
                    val newGhostPositions = moveGhosts(it.ghosts, newPacmanPosition, it.walls)

                    // Verificar colisão com fantasmas, exceto quando invulnerável
                    val isCollision = newGhostPositions.contains(newPacmanPosition) && !it.isInvulnerable

                    it.copy(
                        pacman = if (isCollision) it.pacman else newPacmanPosition,
                        food = remainingFood,
                        ghosts = newGhostPositions,
                        score = if (ateFood) it.score + 10 else it.score,
                        isGameOver = isCollision || remainingFood.isEmpty() // "Game Over" se colidir ou comer tudo
                    )
                }
            }
        }
    }

    private fun movePacMan(pacman: Pair<Int, Int>, walls: List<Pair<Int, Int>>): Pair<Int, Int> {
        // Calcula a nova posição do Pac-Man
        val newPacmanPosition = Pair(
            pacman.first + move.first,
            pacman.second + move.second
        )

        // Verifica se a nova posição está dentro dos limites do tabuleiro
        val withinBounds = newPacmanPosition.first in 0 until BOARD_SIZE &&
                newPacmanPosition.second in 0 until BOARD_SIZE

        // Se estiver dentro dos limites e não for uma parede, o Pac-Man pode se mover
        return if (withinBounds && !walls.contains(newPacmanPosition)) {
            newPacmanPosition
        } else {
            pacman // Caso contrário, ele permanece na posição atual
        }
    }

    private fun moveGhosts(ghosts: List<Pair<Int, Int>>, pacmanPosition: Pair<Int, Int>, walls: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        return ghosts.map { ghost ->
            // Calcular as diferenças horizontais e verticais entre o fantasma e o Pac-Man
            val deltaX = pacmanPosition.first - ghost.first
            val deltaY = pacmanPosition.second - ghost.second

            // Calcular o movimento desejado (horizontal e vertical)
            val moveX = Integer.signum(deltaX)
            val moveY = Integer.signum(deltaY)

            // Definir uma lista de movimentos possíveis (horizontal, vertical, diagonal)
            val possibleMoves = listOf(
                Pair(ghost.first + moveX, ghost.second),         // Movimento horizontal
                Pair(ghost.first, ghost.second + moveY),         // Movimento vertical
                Pair(ghost.first + moveX, ghost.second + moveY), // Movimento diagonal
                Pair(ghost.first - moveX, ghost.second),         // Movimento inverso horizontal
                Pair(ghost.first, ghost.second - moveY)          // Movimento inverso vertical
            )

            // Filtrar movimentos válidos que não colidam com paredes
            val validMoves = possibleMoves.filter { move -> !walls.contains(move) }

            // Se não há movimentos válidos, o fantasma não se move
            if (validMoves.isEmpty()) {
                return@map ghost
            }

            // Caso existam movimentos válidos, escolher o movimento mais eficaz (direção que mais aproxima do Pac-Man)
            val bestMove = validMoves.minByOrNull { move ->
                val newDeltaX = pacmanPosition.first - move.first
                val newDeltaY = pacmanPosition.second - move.second
                Math.abs(newDeltaX) + Math.abs(newDeltaY) // A soma das distâncias (Manhattan Distance)
            }

            // Retorna o melhor movimento encontrado
            bestMove ?: ghost
        }
    }

    fun reset() {
        mutableState.update {
            PacManState(pacman = Pair(10, 18), food = generateFood(), ghosts = listOf(Pair(1, 1), Pair(7, 7)), walls = generateWalls())
        }
        move = Pair(1, 0)
        startGame()
    }

    companion object {
        const val BOARD_SIZE = 24

        fun generateFood(): List<Pair<Int, Int>> {
            return List(10) { Pair(Random().nextInt(BOARD_SIZE), Random().nextInt(BOARD_SIZE)) }
        }

        fun generateWalls(): List<Pair<Int, Int>> {
            // Exemplo de layout de paredes para formar um labirinto simples
            return listOf(
                Pair(1, 1), Pair(2, 1), Pair(3, 1), Pair(5, 1), Pair(6, 1), Pair(7, 1), Pair(9, 1), Pair(10, 1), Pair(11, 1), Pair(12, 1), Pair(13, 1), Pair(14, 1), Pair(16, 1), Pair(17, 1), Pair(18, 1), Pair(20, 1), Pair(21, 1), Pair(22, 1),
                Pair(3, 2), Pair(5, 2), Pair(11, 2), Pair(12, 2), Pair(18, 2), Pair(20, 2),
                Pair(0, 3), Pair(1, 3), Pair(3, 3), Pair(5, 3), Pair(7, 3), Pair(8, 3), Pair(9, 3), Pair(11, 3), Pair(12, 3), Pair(14, 3), Pair(15, 3), Pair(16, 3), Pair(18, 3), Pair(20, 3), Pair(22, 3), Pair(23, 3),
                Pair(0, 4), Pair(1, 4), Pair(3, 4), Pair(5, 4), Pair(7, 4), Pair(8, 4), Pair(9, 4), Pair(11, 4), Pair(12, 4), Pair(14, 4), Pair(15, 4), Pair(16, 4), Pair(18, 4), Pair(20, 4), Pair(22, 4), Pair(23, 4),
                Pair(0, 6), Pair(1, 6), Pair(3, 6), Pair(4, 6), Pair(5, 6), Pair(7, 6), Pair(9, 6), Pair(10, 6), Pair(11, 6), Pair(12, 6), Pair(13, 6), Pair(14, 6), Pair(16, 6), Pair(18, 6), Pair(19, 6), Pair(20, 6), Pair(22, 6), Pair(23, 6),
                Pair(7, 7), Pair(11, 7), Pair(12, 7), Pair(16, 7),
                Pair(0, 8), Pair(1, 8), Pair(2, 8), Pair(3, 8), Pair(4, 8), Pair(5, 8), Pair(7, 8), Pair(8, 8), Pair(9, 8), Pair(11, 8), Pair(12, 8), Pair(14, 8), Pair(15, 8), Pair(16, 8), Pair(18, 8), Pair(19, 8), Pair(20, 8), Pair(21, 8), Pair(22, 8), Pair(23, 8),
                Pair(0, 9), Pair(1, 9), Pair(2, 9), Pair(3, 9), Pair(4, 9), Pair(5, 9), Pair(7, 9), Pair(16, 9), Pair(18, 9), Pair(19, 9), Pair(20, 9), Pair(21, 9), Pair(22, 9), Pair(23, 9),
                Pair(0, 10), Pair(1, 10), Pair(2, 10), Pair(3, 10), Pair(4, 10), Pair(5, 10), Pair(7, 10), Pair(9, 10), Pair(10, 10), Pair(13, 10), Pair(14, 10), Pair(16, 10), Pair(18, 10), Pair(19, 10), Pair(20, 10), Pair(21, 10), Pair(22, 10), Pair(23, 10),
                Pair(0, 11), Pair(1, 11), Pair(2, 11), Pair(3, 11), Pair(4, 11), Pair(5, 11), Pair(7, 11), Pair(9, 11), Pair(14, 11), Pair(16, 11), Pair(18, 11), Pair(19, 11), Pair(20, 11), Pair(21, 11), Pair(22, 11), Pair(23, 11),
                Pair(9, 12), Pair(14, 12),
                Pair(0, 13), Pair(1, 13), Pair(2, 13), Pair(3, 13), Pair(4, 13), Pair(5, 13), Pair(7, 13), Pair(9, 13), Pair(10, 13), Pair(11, 13), Pair(12, 13), Pair(13, 13), Pair(14, 13), Pair(16, 13), Pair(18, 13), Pair(19, 13), Pair(20, 13), Pair(21, 13), Pair(22, 13), Pair(23, 13),
                Pair(0, 14), Pair(1, 14), Pair(2, 14), Pair(3, 14), Pair(4, 14), Pair(5, 14), Pair(7, 14), Pair(16, 14), Pair(18, 14), Pair(19, 14), Pair(20, 14), Pair(21, 14), Pair(22, 14), Pair(23, 14),
                Pair(0, 15), Pair(1, 15), Pair(2, 15), Pair(3, 15), Pair(4, 15), Pair(5, 15), Pair(7, 15), Pair(9, 15), Pair(10, 15), Pair(11, 15), Pair(12, 15), Pair(13, 15), Pair(14, 15), Pair(16, 15), Pair(18, 15), Pair(19, 15), Pair(20, 15), Pair(21, 15), Pair(22, 15), Pair(23, 15),
                Pair(11, 16), Pair(12, 16),
                Pair(1, 17), Pair(2, 17), Pair(3, 17), Pair(4, 17), Pair(5, 17), Pair(7, 17), Pair(8, 17), Pair(9, 17), Pair(11, 17), Pair(12, 17), Pair(14, 17), Pair(15, 17), Pair(16, 17), Pair(18, 17), Pair(19, 17), Pair(20, 17), Pair(21, 17), Pair(22, 17),
                Pair(5, 18), Pair(18, 18),
                Pair(0, 19), Pair(1, 19), Pair(3, 19), Pair(5, 19), Pair(7, 19), Pair(9, 19), Pair(10, 19), Pair(11, 19), Pair(12, 19), Pair(13, 19), Pair(14, 19), Pair(16, 19), Pair(18, 19), Pair(20, 19), Pair(22, 19), Pair(23, 19),
                Pair(0, 20), Pair(1, 20), Pair(3, 20), Pair(5,20), Pair(7, 20), Pair(11, 20), Pair(12, 20), Pair(16, 20), Pair(18, 20), Pair(20, 20), Pair(22, 20), Pair(23, 20),
                Pair(3, 21), Pair(7, 21), Pair(9, 21), Pair(11, 21), Pair(12, 21), Pair(14, 21), Pair(16, 21), Pair(20, 21),
                Pair(1, 22), Pair(2, 22), Pair(3, 22), Pair(5, 22), Pair(6, 22), Pair(7, 22), Pair(8, 22), Pair(9, 22), Pair(11, 22), Pair(12, 22), Pair(14, 22), Pair(15, 22), Pair(16, 22), Pair(17, 22), Pair(18, 22), Pair(20, 22), Pair(21, 22), Pair(22, 22)
            )
        }
    }
}

@Composable
fun GamePacMan() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val game = remember { PacManGame(scope, context) }
    val state = game.state.collectAsState(initial = null)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        state.value?.let {
            if (it.isGameOver) {
                GameOverScreen(score = it.score) {
                    game.reset()
                }
            } else {
                PacManBoard(it)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .background(LightGreen),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "score: ${it.score}",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 20.sp
                        ),
                        color = DarkGreen
                    )
                }
            }
        }
        Buttons {
            game.move = it
        }
    }
}

@Composable
fun GameOverScreen(score: Int, onRestart: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(LightGreen),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Game Over", fontSize = 32.sp, color = DarkGreen)
            Text("Final Score: $score", fontSize = 24.sp, color = DarkGreen)
            Box(
                modifier = Modifier
                    .background(DarkGreen, RoundedCornerShape(10.dp))
                    .clickable { onRestart() },
                contentAlignment = Alignment.Center
            ) {
                Text("Restart", fontSize = 24.sp, color = LightGreen, modifier = Modifier.padding(10.dp))
            }
        }
    }
}

@Composable
fun PacManBoard(state: PacManState) {
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

        // Desenhar Pac-Man
        Box(
            Modifier
                .offset(x = tileSize * state.pacman.first, y = tileSize * state.pacman.second)
                .size(tileSize)
                .background(if (state.isInvulnerable) Color.Gray else Color.Yellow, RoundedCornerShape(50))
        )

        // Desenhar comida
        state.food.forEach { food ->
            Box(
                Modifier
                    .offset(x = tileSize * food.first, y = tileSize * food.second)
                    .size(tileSize / 2)
                    .background(Color.Red, RoundedCornerShape(50))
            )
        }

        // Desenhar fantasmas
        state.ghosts.forEach { ghost ->
            Box(
                Modifier
                    .offset(x = tileSize * ghost.first, y = tileSize * ghost.second)
                    .size(tileSize)
                    .background(Color.Blue, RoundedCornerShape(50))
            )
        }

        // Desenhar paredes
        state.walls.forEach {
            Box(
                Modifier
                    .offset(x = tileSize * it.first, y = tileSize * it.second)
                    .size(tileSize)
                    .background(DarkGreen, RoundedCornerShape(2.dp))
                    .border(0.dp, LightGreen)
            )
        }
    }
}

@Composable
fun Buttons(onDirectionChange: (Pair<Int, Int>) -> Unit) {
    val buttonSize = 64.dp
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
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