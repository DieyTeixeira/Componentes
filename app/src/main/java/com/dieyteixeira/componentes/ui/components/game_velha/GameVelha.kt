package com.dieyteixeira.componentes.ui.components.game_velha

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieyteixeira.componentes.ui.theme.DarkGreen
import com.dieyteixeira.componentes.ui.theme.LightGreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

data class TicTacToeState(
    val board: List<String> = List(9) { "" },  // 9 casas vazias
    val currentPlayer: String = "X",  // "X" começa o jogo
    val isGameOver: Boolean = false,
    val winner: String? = null // Pode ser "X", "O" ou null (em caso de empate)
)

class TicTacToeGame(private val scope: CoroutineScope) {

    private val mutableState = MutableStateFlow(TicTacToeState())
    val state: Flow<TicTacToeState> = mutableState

    fun playMove(index: Int) {
        mutableState.update { state ->
            // Verifica se a casa está vazia e o jogo não acabou
            if (state.board[index].isEmpty() && !state.isGameOver) {
                // Atualiza a casa com o símbolo do jogador atual
                val newBoard = state.board.toMutableList()
                newBoard[index] = state.currentPlayer

                // Verifica se alguém ganhou
                val winner = checkWinner(newBoard)

                // Alterna o jogador
                val nextPlayer = if (state.currentPlayer == "X") "O" else "X"

                // Verifica se o jogo acabou
                val isGameOver = winner != null || newBoard.none { it.isEmpty() }

                TicTacToeState(
                    board = newBoard,
                    currentPlayer = nextPlayer,
                    isGameOver = isGameOver,
                    winner = winner
                )
            } else {
                state
            }
        }
    }

    private fun checkWinner(board: List<String>): String? {
        // Verifica as combinações possíveis de vitória
        val winningPatterns = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8), // linhas
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8), // colunas
            listOf(0, 4, 8), listOf(2, 4, 6) // diagonais
        )

        for (pattern in winningPatterns) {
            val (a, b, c) = pattern
            if (board[a] == board[b] && board[b] == board[c] && board[a].isNotEmpty()) {
                return board[a]  // Retorna o vencedor ("X" ou "O")
            }
        }
        return null
    }

    fun reset() {
        mutableState.update {
            TicTacToeState()
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun TicTacToeBoard(state: TicTacToeState, onCellClick: (Int) -> Unit) {
    BoxWithConstraints(
        Modifier
            .background(LightGreen)
            .padding(16.dp)
    ) {
        Box(
            Modifier
                .size(maxWidth)
                .border(2.dp, DarkGreen),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                for (row in 0..2) {
                    Row {
                        for (col in 0..2) {
                            val index = row * 3 + col
                            Box(
                                modifier = Modifier
                                    .size(107.dp)
                                    .background(LightGreen, RoundedCornerShape(8.dp))
                                    .border(1.dp, Color.Black)
                                    .clickable { onCellClick(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                DrawSymbol(state.board[index], 12.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GameOverScreen(winner: String?, onRestart: () -> Unit) {
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
                    text = winner?.let { "vencedor" } ?: "fim de jogo",
                    style = MaterialTheme.typography.displayMedium.copy(fontSize = 32.sp),
                    color = DarkGreen
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(65.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (winner != null) {
                        DrawSymbol(winner, 12.dp)
                    } else {
                        Text(
                            text = "empate",
                            style = MaterialTheme.typography.displayMedium.copy(fontSize = 24.sp),
                            color = DarkGreen
                        )
                    }
                }
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
                Spacer(modifier = Modifier.height(50.dp))
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
fun GameVelha() {
    val scope = rememberCoroutineScope()
    val game = remember { TicTacToeGame(scope) }
    val state = game.state.collectAsState(initial = TicTacToeState())
    var showGameOverScreen by remember { mutableStateOf(false) }

    LaunchedEffect(state.value.isGameOver) {
        if (state.value.isGameOver) {
            delay(500L) // Delay de 1 segundo (1000 milissegundos)
            showGameOverScreen = true // Define que a tela de game over deve ser exibida
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        if (!showGameOverScreen) {
            TicTacToeBoard(state = state.value) { index ->
                game.playMove(index)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(LightGreen),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "vez de jogar  ",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 20.sp
                        ),
                        color = DarkGreen
                    )
                    DrawSymbol(state.value.currentPlayer, 4.dp)
                }
            }
        }

        if (state.value.isGameOver && showGameOverScreen) {
            GameOverScreen(winner = state.value.winner) {
                game.reset()
                showGameOverScreen = false
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
        }
    }
}

@Composable
fun DrawSymbol(symbol: String, pixelSize: Dp) {

    val xShape = listOf(
        listOf(true, false, false, false, true),
        listOf(false, true, false, true, false),
        listOf(false, false, true, false, false),
        listOf(false, true, false, true, false),
        listOf(true, false, false, false, true)
    )

    val oShape = listOf(
        listOf(false, true, true, true, false),
        listOf(true, false, false, false, true),
        listOf(true, false, false, false, true),
        listOf(true, false, false, false, true),
        listOf(false, true, true, true, false)
    )

    val shape = if (symbol == "X") xShape else if (symbol == "O") oShape else emptyList()

    Column {
        for (row in shape) {
            Row {
                for (cell in row) {
                    Box(
                        modifier = Modifier
                            .size(pixelSize)
                            .background(if (cell) DarkGreen else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        if (pixelSize > 10.dp) {
                            Box(
                                modifier = Modifier
                                    .size(pixelSize * 0.75f) // Tamanho menor, 50% do tamanho do tile
                                    .align(Alignment.Center) // Centralizado dentro do tile
                                    .border(1.2.dp,
                                        if (cell) LightGreen else Color.Transparent,
                                        RoundedCornerShape(1.dp)
                                    )// Cor do Box menor
                            )
                        }
                    }
                }
            }
        }
    }
}