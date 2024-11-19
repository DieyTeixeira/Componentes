package com.dieyteixeira.componentes.ui.elements.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieyteixeira.componentes.ui.theme.BlueSky
import com.dieyteixeira.componentes.ui.theme.Food
import com.dieyteixeira.componentes.ui.theme.GhostVulnerable
import com.dieyteixeira.componentes.ui.theme.Green500
import com.dieyteixeira.componentes.ui.theme.Orange
import com.dieyteixeira.componentes.ui.theme.Yellow
import kotlin.math.roundToInt

@Composable
fun DraggableObject() {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(15.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MultipleDraggableObject(letter = "A", bgColor = Yellow)
            MultipleDraggableObject(letter = "B", bgColor = BlueSky)
            MultipleDraggableObject(letter = "C", bgColor = Green500)
            MultipleDraggableObject(letter = "D", bgColor = Orange)
            MultipleDraggableObject(letter = "E", bgColor = GhostVulnerable)
        }
    }
}

@Composable
fun MultipleDraggableObject(letter: String, bgColor: Color) {
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = offsetX.value.roundToInt(),
                    y = offsetY.value.roundToInt()
                )
            }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()
                    offsetX.value += dragAmount.x
                    offsetY.value += dragAmount.y
                }
            }
            .size(80.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            fontSize = 30.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}