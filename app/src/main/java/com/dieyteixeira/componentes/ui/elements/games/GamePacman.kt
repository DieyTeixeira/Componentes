package com.dieyteixeira.componentes.ui.elements.games

import android.content.Context
import androidx.compose.foundation.Canvas
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
import com.dieyteixeira.componentes.ui.theme.Food
import com.dieyteixeira.componentes.ui.theme.GhostVulnerable
import com.dieyteixeira.componentes.ui.theme.LightGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class PacManState(
    val pacman: Pair<Int, Int>,
    val food: List<Pair<Int, Int>>,
    val bestFood: List<Pair<Int, Int>>,
    val ghosts: List<Pair<Int, Int>>,
    val walls: List<Pair<Int, Int>>,
    val home: List<Pair<Int, Int>>,
    val entry: List<Pair<Int, Int>>,
    val score: Int = 0,
    val direction: String = "right",
    val isGameOver: Boolean = false,
    val isInvulnerable: Boolean = false,
    val isGhostVulnerable: Boolean = false
)

class PacManGame(private val scope: CoroutineScope, context: Context) {

    private val mutex = Mutex()
    private val mutableState = MutableStateFlow(
        PacManState(
            pacman = Pair(12, 18),
            food = generateFood(),
            bestFood = generateBestFood(),
            ghosts = listOf(Pair(0, 0), Pair(23, 0), Pair(0, 23), Pair(23, 23)),
            walls = generateWalls(),
            home = generateHome(),
            entry = generateEntry()
        )
    )
    val state: Flow<PacManState> = mutableState

    var move = Pair(1, 0)
        set(value) {
            scope.launch {
                mutex.withLock {
                    field = value // Evitar movimentos opostos
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
            mutableState.update { it.copy(isInvulnerable = true) } // Pac-Man começa invulnerável
            invulnerabilityJob = launch {
                delay(3000L) // Pac-Man é invulnerável por 3 segundos
                mutableState.update { it.copy(isInvulnerable = false) }
            }

            while (true) {
                delay(300L)
                mutableState.update {
                    if (it.isGameOver) return@update it

                    // Movimento do Pac-Man
                    val newPacmanPosition = movePacMan(it.pacman, it.walls, it.home, it.entry)
                    val ateFood = it.food.contains(newPacmanPosition)
                    val ateBestFood = it.bestFood.contains(newPacmanPosition)
                    val remainingFood = it.food.filter { food -> food != newPacmanPosition }
                    val remainingBestFood = it.bestFood.filter { bestFood -> bestFood != newPacmanPosition }

                    // Movimento dos fantasmas
                    val newGhostPositions = moveGhosts(
                        it.ghosts,
                        newPacmanPosition,
                        it.walls,
                        it.home,
                        it.isGhostVulnerable
                    )

                    // Verificar colisão com fantasmas
                    val isCollision = newGhostPositions.contains(newPacmanPosition) && !it.isInvulnerable

                    // Atualizar vulnerabilidade dos fantasmas ao comer "bestFood"
                    val newState = it.copy(
                        pacman = if (isCollision) it.pacman else newPacmanPosition,
                        food = remainingFood,
                        bestFood = remainingBestFood,
                        ghosts = newGhostPositions,
                        score = if (ateFood) it.score + 5 else if (ateBestFood) it.score + 10 else it.score,
                        isGameOver = isCollision || (remainingFood.isEmpty() && remainingBestFood.isEmpty()),
                        isGhostVulnerable = ateBestFood || it.isGhostVulnerable
                    )

                    // Se os fantasmas ficam vulneráveis ao comer "bestFood"
                    if (ateBestFood) {
                        scope.launch {
                            delay(5000L) // Fantasmas vulneráveis por 5 segundos
                            mutableState.update { state -> state.copy(isGhostVulnerable = false) }
                        }
                    }

                    newState
                }
            }
        }
    }

    private fun movePacMan(
        pacman: Pair<Int, Int>,
        walls: List<Pair<Int, Int>>,
        home: List<Pair<Int, Int>>,
        entry: List<Pair<Int, Int>>
    ): Pair<Int, Int> {

        // Calcula a nova posição do Pac-Man
        val newPacmanPosition = Pair(
            pacman.first + move.first,
            pacman.second + move.second
        )

        // Verifica se a nova posição está dentro dos limites do tabuleiro
        val withinBounds = newPacmanPosition.first in 0 until BOARD_SIZE &&
                newPacmanPosition.second in 0 until BOARD_SIZE

        // Se estiver dentro dos limites e não for uma parede, o Pac-Man pode se mover
        return if (withinBounds && !walls.contains(newPacmanPosition) && !home.contains(newPacmanPosition) && !entry.contains(newPacmanPosition)) {
            newPacmanPosition
        } else {
            pacman // Caso contrário, ele permanece na posição atual
        }
    }

    private fun moveGhosts(
        ghosts: List<Pair<Int, Int>>,
        pacmanPosition: Pair<Int, Int>,
        walls: List<Pair<Int, Int>>,
        home: List<Pair<Int, Int>>,
        isGhostVulnerable: Boolean
    ): List<Pair<Int, Int>> {
        return ghosts.mapIndexed { index, ghost ->
            when (index) {
                0 -> { // Blinky
                    if (isGhostVulnerable) {
                        moveToHome(ghost, home[0], walls)
                    } else {
                        moveToPacMan(ghost, pacmanPosition, walls) // Persegue o Pac-Man
                    }
                }
                1 -> { // Pinky
                    if (isGhostVulnerable) {
                        moveToHome(ghost, home[1], walls)
                    } else {
                        moveToAnticipatedPosition(ghost, pacmanPosition, walls) // Antecipação do movimento
                    }
                }
                2 -> { // Inky
                    if (isGhostVulnerable) {
                        moveToHome(ghost, home[2], walls)
                    } else {
                        moveToInkyPosition(ghost, pacmanPosition, walls)
                    }
                }
                3 -> { // Clyde
                    if (isGhostVulnerable) {
                        moveToHome(ghost, home[3], walls)
                    } else {
                        moveRandomlyOrChase(ghost, pacmanPosition, walls)
                    }
                }
                else -> ghost
            }
        }
    }

    // Função para mover o Blinky para a posição do Pac-Man
    private fun moveToPacMan(ghost: Pair<Int, Int>, pacmanPosition: Pair<Int, Int>, walls: List<Pair<Int, Int>>): Pair<Int, Int> {
        return moveTowardsTarget(ghost, pacmanPosition, walls)
    }

    // Função para mover o Pinky, que tenta antecipar o Pac-Man de forma inteligente
    private fun moveToAnticipatedPosition(ghost: Pair<Int, Int>, pacmanPosition: Pair<Int, Int>, walls: List<Pair<Int, Int>>): Pair<Int, Int> {
        val direction = Pair(pacmanPosition.first - ghost.first, pacmanPosition.second - ghost.second)
        val anticipatedPosition = Pair(
            pacmanPosition.first + direction.first * 2,
            pacmanPosition.second + direction.second * 2
        )
        return moveTowardsTarget(ghost, anticipatedPosition, walls)
    }

    // Função para mover o Inky, com uma estratégia melhorada
    private fun moveToInkyPosition(ghost: Pair<Int, Int>, pacmanPosition: Pair<Int, Int>, walls: List<Pair<Int, Int>>): Pair<Int, Int> {
        val blinkyPosition = Pair(ghost.first + 1, ghost.second)
        val targetPosition = Pair(
            (pacmanPosition.first + blinkyPosition.first) / 2,
            (pacmanPosition.second + blinkyPosition.second) / 2
        )
        return moveTowardsTarget(ghost, targetPosition, walls)
    }

    // Função para o Clyde, que alterna entre seguir o Pac-Man e mover aleatoriamente com um comportamento mais eficiente
    private fun moveRandomlyOrChase(ghost: Pair<Int, Int>, pacmanPosition: Pair<Int, Int>, walls: List<Pair<Int, Int>>): Pair<Int, Int> {
        return if (Math.random() > 0.4) {
            moveToPacMan(ghost, pacmanPosition, walls)
        } else {
            moveRandomly(ghost, walls)
        }
    }

    // Função para mover o fantasma vulnerável de volta para a "Home"
    private fun moveToHome(ghost: Pair<Int, Int>, home: Pair<Int, Int>, walls: List<Pair<Int, Int>>): Pair<Int, Int> {
        return moveTowardsTarget(ghost, home, walls)
    }

    // Função para mover aleatoriamente, com inteligência para evitar becos sem saída
    private fun moveRandomly(ghost: Pair<Int, Int>, walls: List<Pair<Int, Int>>): Pair<Int, Int> {
        val possibleMoves = listOf(
            Pair(ghost.first + 1, ghost.second),  // Direita
            Pair(ghost.first - 1, ghost.second),  // Esquerda
            Pair(ghost.first, ghost.second + 1),  // Baixo
            Pair(ghost.first, ghost.second - 1)   // Cima
        ).filter { move ->
            !walls.contains(move) && move.first in 0 until BOARD_SIZE &&
                    move.second in 0 until BOARD_SIZE

        }

        return possibleMoves.random()  // Escolhe um movimento aleatório
    }

    // Função para mover o fantasma em direção ao alvo (pacman ou home)
    private fun moveTowardsTarget(ghost: Pair<Int, Int>, targetPosition: Pair<Int, Int>, walls: List<Pair<Int, Int>>): Pair<Int, Int> {
        // Lista de movimentos possíveis (direções cardinais)
        val possibleMoves = listOf(
            Pair(ghost.first + 1, ghost.second),  // Direita
            Pair(ghost.first - 1, ghost.second),  // Esquerda
            Pair(ghost.first, ghost.second + 1),  // Baixo
            Pair(ghost.first, ghost.second - 1)   // Cima
        ).filter { move ->
            // Verificar se o movimento não é bloqueado por uma parede e se está dentro dos limites do tabuleiro
            !walls.contains(move) && move.first in 0 until BOARD_SIZE && move.second in 0 until BOARD_SIZE
        }

        // Se não houver movimentos válidos (beco sem saída), o fantasma ficará parado
        if (possibleMoves.isEmpty()) {
            return ghost
        }

        // Verificar se a posição do pacman está bloqueada nas direções mais próximas
        val bestMove = possibleMoves.minByOrNull { move ->
            val deltaX = targetPosition.first - move.first
            val deltaY = targetPosition.second - move.second
            Math.abs(deltaX) + Math.abs(deltaY)  // Distância Manhattan
        }

        // Se o fantasma está alternando entre dois pontos, force a escolha de uma nova direção
        if (ghost == bestMove) {
            // Tenta alternativas de movimento para fugir do ciclo
            val alternativeMoves = possibleMoves.filter { it != ghost }
            if (alternativeMoves.isNotEmpty()) {
                return alternativeMoves.random()  // Se estiver preso, tenta um movimento diferente aleatoriamente
            }
        }

        return bestMove ?: ghost
    }

    fun reset() {
        mutableState.update {
            PacManState(
                pacman = Pair(12, 18),
                food = generateFood(),
                bestFood = generateBestFood(),
                ghosts = listOf(Pair(0, 0), Pair(23, 0), Pair(0, 23), Pair(23, 23)),
                walls = generateWalls(),
                home = generateHome(),
                entry = generateEntry()
            )
        }
        move = Pair(1, 0)
        startGame()
    }

    companion object {
        const val BOARD_SIZE = 24

        fun generateFood(): List<Pair<Int, Int>> {
            return listOf(
                Pair(0, 0), Pair(1, 0), Pair(2, 0), Pair(3, 0), Pair(4, 0), Pair(5, 0), Pair(6, 0), Pair(7, 0), Pair(8, 0), Pair(9, 0), Pair(10, 0), Pair(11, 0), Pair(12, 0), Pair(13, 0), Pair(14, 0), Pair(15, 0), Pair(16, 0), Pair(17, 0), Pair(18, 0), Pair(19, 0), Pair(20, 0), Pair(21, 0), Pair(22, 0), Pair(23, 0),
                Pair(0, 1), Pair(4, 1), Pair(8, 1), Pair(15, 1), Pair(19, 1), Pair(23, 1),
                Pair(0, 2), Pair(1, 2), Pair(2, 2), Pair(4, 2), Pair(6, 2), Pair(7, 2), Pair(8, 2), Pair(9, 2), Pair(10, 2), Pair(13, 2), Pair(14, 2), Pair(15, 2), Pair(16, 2), Pair(17, 2), Pair(19, 2), Pair(21, 2), Pair(22, 2), Pair(23, 2),
                Pair(4, 3), Pair(6, 3), Pair(10, 3), Pair(13, 3), Pair(17, 3), Pair(19, 3),
                Pair(2, 4), Pair(4, 4), Pair(6, 4), Pair(17, 4), Pair(19, 4), Pair(21, 4),
                Pair(0, 5), Pair(1, 5), Pair(2, 5), Pair(3, 5), Pair(4, 5), Pair(5, 5), Pair(6, 5), Pair(7, 5), Pair(8, 5), Pair(9, 5), Pair(10, 5), Pair(11, 5), Pair(12, 5), Pair(13, 5), Pair(14, 5), Pair(15, 5), Pair(16, 5), Pair(17, 5), Pair(18, 5), Pair(19, 5), Pair(20, 5), Pair(21, 5), Pair(22, 5), Pair(23, 5),
                Pair(2, 6), Pair(6, 6), Pair(8, 6), Pair(15, 6), Pair(17, 6), Pair(21, 6),
                Pair(0, 7), Pair(1, 7), Pair(2, 7), Pair(3, 7), Pair(4, 7), Pair(5, 7), Pair(6, 7), Pair(8, 7), Pair(9, 7), Pair(10, 7), Pair(13, 7), Pair(14, 7), Pair(15, 7), Pair(17, 7), Pair(18, 7), Pair(19, 7), Pair(20, 7), Pair(21, 7), Pair(22, 7), Pair(23, 7),
                Pair(6, 8), Pair(10, 8), Pair(13, 8), Pair(17, 8),
                Pair(6, 9), Pair(8, 9), Pair(9, 9), Pair(10, 9), Pair(11, 9), Pair(12, 9), Pair(13, 9), Pair(14, 9), Pair(15, 9), Pair(17, 9),
                Pair(6, 10), Pair(8, 10), Pair(15, 10), Pair(17, 10),
                Pair(6, 11), Pair(8, 11), Pair(15, 11), Pair(17, 11),
                Pair(0, 12), Pair(1, 12), Pair(2, 12), Pair(3, 12), Pair(4, 12), Pair(5, 12), Pair(6, 12), Pair(7, 12), Pair(8, 12), Pair(15, 12), Pair(16, 12), Pair(17, 12), Pair(18, 12), Pair(19, 12), Pair(20, 12), Pair(21, 12), Pair(22, 12), Pair(23, 12),
                Pair(6, 13), Pair(8, 13), Pair(15, 13), Pair(17, 13),
                Pair(6, 14), Pair(8, 14), Pair(9, 14), Pair(10, 14), Pair(11, 14), Pair(12, 14), Pair(13, 14), Pair(14, 14), Pair(15, 14), Pair(17, 14),
                Pair(6, 15), Pair(8, 15), Pair(15, 15), Pair(17, 15),
                Pair(0, 16), Pair(1, 16), Pair(2, 16), Pair(3, 16), Pair(4, 16), Pair(5, 16), Pair(6, 16), Pair(7, 16), Pair(8, 16), Pair(9, 16), Pair(10, 16), Pair(13, 16), Pair(14, 16), Pair(15, 16), Pair(16, 16), Pair(17, 16), Pair(18, 16), Pair(19, 16), Pair(20, 16), Pair(21, 16), Pair(22, 16), Pair(23, 16),
                Pair(0, 17), Pair(6, 17), Pair(10, 17), Pair(13, 17), Pair(17, 17), Pair(23, 17),
                Pair(1, 18), Pair(2, 18), Pair(3, 18), Pair(4, 18), Pair(6, 18), Pair(7, 18), Pair(8, 18), Pair(9, 18), Pair(10, 18), Pair(11, 18), Pair(12, 18), Pair(13, 18), Pair(14, 18), Pair(15, 18), Pair(16, 18), Pair(17, 18), Pair(19, 18), Pair(20, 18), Pair(21, 18), Pair(22, 18),
                Pair(2, 19), Pair(4, 19), Pair(6, 19), Pair(8, 19), Pair(15, 19), Pair(17, 19), Pair(19, 19), Pair(21, 19),
                Pair(2, 20), Pair(4, 20), Pair(6, 20), Pair(8, 20), Pair(9, 20), Pair(10, 20), Pair(13, 20), Pair(14, 20), Pair(15, 20), Pair(17, 20), Pair(19, 20), Pair(21, 20),
                Pair(0, 21), Pair(1, 21), Pair(2, 21), Pair(4, 21), Pair(5, 21), Pair(10, 21), Pair(13, 21), Pair(18, 21), Pair(19, 21), Pair(21, 21), Pair(22, 21), Pair(23, 21),
                Pair(0, 22), Pair(4, 22), Pair(10, 22), Pair(13, 22), Pair(19, 22), Pair(23, 22),
                Pair(0, 23), Pair(1, 23), Pair(2, 23), Pair(3, 23), Pair(4, 23), Pair(5, 23), Pair(6, 23), Pair(7, 23), Pair(8, 23), Pair(9, 23), Pair(10, 23), Pair(11, 23), Pair(12, 23), Pair(13, 23), Pair(14, 23), Pair(15, 23), Pair(16, 23), Pair(17, 23), Pair(18, 23), Pair(19, 23), Pair(20, 23), Pair(21, 23), Pair(22, 23), Pair(23, 23)
            )
        }

        fun generateBestFood(): List<Pair<Int, Int>> {
            return listOf(
                Pair(2, 3), Pair(21, 3),
                Pair(10, 4), Pair(13, 4),
                Pair(0, 18), Pair(23, 18),
                Pair(6, 21), Pair(17, 21)
            )
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

        fun generateHome(): List<Pair<Int, Int>> {
            return listOf(
                Pair(10,11), Pair(11,11), Pair(12,11), Pair(13,11),
                Pair(10,12), Pair(11,12), Pair(12,12), Pair(13,12)
            )
        }

        fun generateEntry(): List<Pair<Int, Int>> {
            return listOf(
                Pair(11,10), Pair(12,10)
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
                GameOverPacman(score = it.score) {
                    game.reset()
                }
            } else {
                BoardPacman(it)
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
        ButtonsPacman {
            game.move = it
        }
    }
}

@Composable
fun GameOverPacman(score: Int, onRestart: () -> Unit) {
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
fun BoardPacman(state: PacManState) {
    BoxWithConstraints(
        Modifier
            .background(LightGreen)
            .padding(16.dp)
    ) {
        val tileSize = maxWidth / Game.BOARD_SIZE

        // Desenhar Pac-Man
        Canvas(
            modifier = Modifier
                .offset(x = tileSize * state.pacman.first, y = tileSize * state.pacman.second)
                .size(tileSize)
        ) {
            val (startAngle, sweepAngle) = when (state.direction) {
                "right" -> 30f to 300f  // Boca voltada para a direita
                "left" -> 210f to 300f  // Boca voltada para a esquerda
                "up" -> 120f to 300f    // Boca voltada para cima
                "down" -> -60f to 300f  // Boca voltada para baixo
                else -> 30f to 300f     // Padrão (direita)
            }

            drawArc(
                color = Color.Yellow,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                size = size
            )
        }


        // Desenhar comida
        state.food.forEach {
            Box(
                Modifier
                    .offset(x = tileSize * it.first, y = tileSize * it.second)
                    .size(tileSize)
                    .background(Food, RoundedCornerShape(2.dp))
                    .border(4.5.dp, LightGreen)
            )
        }

        // Desenhar comida especial
        state.bestFood.forEach {
            Box(
                Modifier
                    .offset(x = tileSize * it.first, y = tileSize * it.second)
                    .size(tileSize)
                    .background(Food, RoundedCornerShape(2.dp))
                    .border(3.dp, LightGreen)
            )
        }

        val ghostColors = listOf(
            Color(0xFFE43C2F), // Blinky - Fantasma vermelho
            Color(0xFFCC8BE4), // Pinky - Fantasma rosa
            Color(0xFF0080D8), // Inky - Fantasma azul
            Color(0xFFFFA500)  // Clyde - Fantasma laranja
        )

        // Desenhar fantasmas
        state.ghosts.forEachIndexed { index, ghost ->
            Box(
                Modifier
                    .offset(x = tileSize * ghost.first, y = tileSize * ghost.second)
                    .size(tileSize)
                    .background(
                        if (state.isGhostVulnerable) GhostVulnerable else ghostColors.getOrElse(index) { Color.Gray },
                        RoundedCornerShape(20.dp, 20.dp, 10.dp, 10.dp)
                    ),
                contentAlignment = Alignment.TopCenter
            ) {
                Spacer(modifier = Modifier.size(tileSize / 3))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(tileSize / 3)
                            .background(
                                if (state.isGhostVulnerable) Color.Gray else Color.White,
                                RoundedCornerShape(100)
                            ),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .size(tileSize / 6)
                                .background(
                                    if (state.isGhostVulnerable) Color.White else Color.Black,
                                    RoundedCornerShape(100)
                                )
                        )
                    }
                    Spacer(modifier = Modifier.size(tileSize / 6))
                    Box(
                        modifier = Modifier
                            .size(tileSize / 3)
                            .background(
                                if (state.isGhostVulnerable) Color.Gray else Color.White,
                                RoundedCornerShape(100)
                            ),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Box(
                            modifier = Modifier
                                .size(tileSize / 6)
                                .background(
                                    if (state.isGhostVulnerable) Color.White else Color.Black,
                                    RoundedCornerShape(100)
                                )
                        )
                    }
                }
            }
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

        Box(
            Modifier
                .size(maxWidth)
                .border(1.dp, DarkGreen)
        )
    }
}

@Composable
fun ButtonsPacman(onDirectionChange: (Pair<Int, Int>) -> Unit) {
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