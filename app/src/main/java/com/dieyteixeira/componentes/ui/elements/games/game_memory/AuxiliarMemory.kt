package com.dieyteixeira.componentes.ui.elements.games.game_memory

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp

fun countPairsByPlayer(matchedPairs: MutableMap<Int, Int>, playerId: Int): Int {
    return matchedPairs.values.count { it == playerId } / 2
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
    Log.d("GameState", "Gerando grid com tamanho ${size.rows} x ${size.columns}")
}

fun Modifier.clickable(onClick: () -> Unit): Modifier = this.pointerInput(Unit) {
    detectTapGestures(onTap = { onClick() })
}