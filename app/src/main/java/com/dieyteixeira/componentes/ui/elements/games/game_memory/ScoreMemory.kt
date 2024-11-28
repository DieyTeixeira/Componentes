package com.dieyteixeira.componentes.ui.elements.games.game_memory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieyteixeira.componentes.ui.theme.Green500
import kotlinx.coroutines.delay

@Composable
fun EndOneGameDialog(
    winnerName: String,
    gridSize: GridSize,
    onDismiss: () -> Unit,
    onNextLevel: () -> Unit
) {
    var message by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (gridSize.rows == 8) {
            onDismiss()
        } else {
            message = "Parabéns, $winnerName!\nNível concluído!"
            delay(3000)
            message = "Iniciando\npróximo nível..."
            delay(3000)
            onNextLevel()
        }
    }

    AlertDialog(
        onDismissRequest = {},
        buttons = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (gridSize.rows == 8) {
                    Text(
                        text = "Parabéns, você venceu!",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Green500,
                            fontSize = 20.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            color = Color.DarkGray,
                            fontSize = 20.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}

@Composable
fun EndTwoGameDialog(
    winnerName: String,
    winnerScore: Int,
    winnerColor: Color,
    loserName: String,
    loserScore: Int,
    loserColor: Color,
    isDraw: Boolean,
    onNewGame: () -> Unit,
    onRestartGame: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        buttons = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Título do Diálogo
                Text(
                    text = "Fim do Jogo",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        color = Color.DarkGray,
                        fontSize = 20.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(40.dp))

                if (isDraw) {
                    // Empate
                    Row(
                        modifier = Modifier
                            .width(280.dp)
                            .height(80.dp)
                            .background(
                                color = Color.Gray,
                                shape = RoundedCornerShape(15.dp)
                            ),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(4f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Jogo empatado...",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontSize = 24.sp
                                ),
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(2f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$winnerScore",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontSize = 50.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "pares cada",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontSize = 8.sp
                                )
                            )
                        }
                    }
                } else {
                    // Vencedor
                    Row(
                        modifier = Modifier
                            .width(280.dp)
                            .height(80.dp)
                            .background(
                                color = winnerColor,
                                shape = RoundedCornerShape(15.dp)
                            ),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(4f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "vencedor",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontSize = 16.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = winnerName,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontSize = 30.sp
                                ),
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(2f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$winnerScore",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontSize = 50.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (winnerScore == 1) "par" else "pares",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontSize = 8.sp
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // Perdedor
                    Row(
                        modifier = Modifier
                            .width(180.dp)
                            .height(65.dp)
                            .background(
                                color = loserColor,
                                shape = RoundedCornerShape(15.dp)
                            ),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(4f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "perdedor",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontSize = 12.sp
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = loserName,
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontSize = 20.sp
                                ),
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(2f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "$loserScore",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontSize = 35.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (loserScore == 1) "par" else "pares",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = Color.White,
                                    fontSize = 8.sp
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))

                // Botões
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(35.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(100))
                            .clickable { onNewGame() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Novo jogo",
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = 14.sp),
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(35.dp)
                            .background(Color.LightGray, shape = RoundedCornerShape(100))
                            .clickable { onRestartGame() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Reiniciar",
                            style = MaterialTheme.typography.displaySmall.copy(fontSize = 14.sp),
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}