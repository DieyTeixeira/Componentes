package com.dieyteixeira.componentes.ui.elements.games.game_memory

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min

fun countPairsByPlayer(matchedPairs: MutableMap<Int, Int>, playerId: Int): Int {
    return matchedPairs.values.count { it == playerId } / 2
}

@Composable
fun calculateCardSize(rows: Int, columns: Int): Dp {
    val screenWidth = 320.dp
    val screenHeight = 405.dp

    val availableWidth = screenWidth / columns
    val availableHeight = screenHeight / rows

    return min(availableWidth, availableHeight) * 0.85f
}

fun generateGrid(size: GridSize): List<Int> {
    val numbers = mutableListOf<Int>()
    for (i in 1..(size.rows * size.columns / 2)) {
        numbers.add(i)
        numbers.add(i)
    }
    numbers.shuffle()
    return numbers
    Log.d("GameState", "Gerando grid com tamanho ${size.rows} x ${size.columns}")
}

fun Modifier.clickable(onClick: () -> Unit): Modifier = this.pointerInput(Unit) {
    detectTapGestures(onTap = { onClick() })
}