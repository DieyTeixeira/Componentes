package com.dieyteixeira.componentes.ui.elements.games.game_memory

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieyteixeira.componentes.ui.theme.Yellow

@Composable
fun GameSettings(
    color: Color,
    gridSize: GridSize,
    gameMode: GameMode,
    onGridSizeSelected: (GridSize) -> Unit,
    onGameModeSelected: (GameMode) -> Unit,
    player1Name: String,
    player2Name: String,
    onPlayer1NameChange: (String) -> Unit,
    onPlayer2NameChange: (String) -> Unit,
    onStartGame: (GameConfig) -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        Text(
            "CoNFIGuRAçõES Do JoGo",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 20.sp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        GameModeSelector(
            selectedMode = gameMode,
            onModeSelected = {
                onGameModeSelected(it)
                if (it == GameMode.OnePlayer) {
                    onGridSizeSelected(GridSize(4, 3))
                }
            },
            player1Name = player1Name,
            player2Name = player2Name,
            onPlayer1NameChange = onPlayer1NameChange,
            onPlayer2NameChange = onPlayer2NameChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        GridSizeSelector(
            selectedSize = gridSize,
            isSelectable = gameMode != GameMode.OnePlayer,
            onSizeSelected = onGridSizeSelected
        )

        Spacer(modifier = Modifier.height(25.dp))

        Box(
            modifier = Modifier
                .width(100.dp)
                .height(35.dp)
                .background(color, shape = RoundedCornerShape(100))
                .clickable { onStartGame(GameConfig(gridSize, gameMode)) },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Iniciar Jogo",
                style = MaterialTheme.typography.displaySmall.copy(fontSize = 14.sp),
                color = if (color == Yellow) Color.Black else Color.White
            )
        }
    }
}

@Composable
fun GridSizeSelector(
    selectedSize: GridSize,
    isSelectable: Boolean,
    onSizeSelected: (GridSize) -> Unit
) {
    val sizes = listOf(
        GridSize(4, 3), GridSize(4, 4), GridSize(5, 4), GridSize(6, 4),
        GridSize(6, 5), GridSize(6, 6), GridSize(7, 6), GridSize(8, 6)
    )

    Text(
        "Tamanho do jogo:",
        style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp)
    )

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            sizes.take(4).forEach { size ->
                val grid = if (size.columns < 4) {
                    "Grid ${size.columns}x${size.rows}"
                } else {
                    "Grid ${size.rows}x${size.columns}"
                }
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(30.dp)
                        .background(if (size == selectedSize) Color.DarkGray else Color.LightGray, shape = RoundedCornerShape(10.dp))
                        .let {
                            if (isSelectable) it.clickable { onSizeSelected(size) } else it
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = grid,
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 16.sp),
                        color = if (size == selectedSize) Color.White else (if (isSelectable) Color.Black else Color.Gray)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            sizes.drop(4).forEach { size ->
                val grid = if (size.columns < 6) {
                    "Grid ${size.columns}x${size.rows}"
                } else {
                    "Grid ${size.rows}x${size.columns}"
                }
                Box(
                    modifier = Modifier
                        .width(130.dp)
                        .height(30.dp)
                        .background(if (size == selectedSize) Color.DarkGray else Color.LightGray, shape = RoundedCornerShape(10.dp))
                        .let {
                            if (isSelectable) it.clickable { onSizeSelected(size) } else it
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = grid,
                        style = MaterialTheme.typography.displaySmall.copy(fontSize = 16.sp),
                        color = if (size == selectedSize) Color.White else (if (isSelectable) Color.Black else Color.Gray)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun GameModeSelector(
    selectedMode: GameMode,
    onModeSelected: (GameMode) -> Unit,
    player1Name: String,
    player2Name: String,
    onPlayer1NameChange: (String) -> Unit,
    onPlayer2NameChange: (String) -> Unit
) {
    Text(
        "Modo de jogo:",
        style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp)
    )

    Spacer(modifier = Modifier.height(8.dp))

    if (selectedMode == GameMode.OnePlayer) {
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
                        .background(if (mode == selectedMode) Color.DarkGray else Color.LightGray, shape = RoundedCornerShape(10.dp))
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
    } else {
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
                        .background(if (mode == selectedMode) Color.DarkGray else Color.LightGray, shape = RoundedCornerShape(10.dp))
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

    Spacer(modifier = Modifier.height(5.dp))

    if (selectedMode == GameMode.TwoPlayers) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Jogador 1  ",
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp),
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .fillMaxWidth()
                ) {
                    BasicTextField(
                        value = player1Name,
                        onValueChange = { newValue -> onPlayer1NameChange(newValue) },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Black,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Jogador 2  ",
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp),
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .fillMaxWidth()
                ) {
                    BasicTextField(
                        value = player2Name,
                        onValueChange = { newValue -> onPlayer2NameChange(newValue) },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Black,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    } else if (selectedMode == GameMode.OnePlayer) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier.width(150.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Jogador",
                    style = MaterialTheme.typography.displaySmall.copy(fontSize = 18.sp),
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Box(
                    modifier = Modifier
                        .height(34.dp)
                        .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                        .fillMaxWidth()
                ) {
                    BasicTextField(
                        value = player1Name,
                        onValueChange = { newValue -> onPlayer1NameChange(newValue) },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.Black,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}