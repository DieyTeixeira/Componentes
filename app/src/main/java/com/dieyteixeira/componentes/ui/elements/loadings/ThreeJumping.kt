package com.dieyteixeira.componentes.ui.elements.loadings

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/*-------------------------------------------------------------------------------------------------|
|                                                                                                  |
|                             ANIMAÇÃO DE CARREGAMENTO 3 CIRCULOS                                  |
|                                                                                                  |
|------------------------------------------------------------------------------------------------ */

@Composable
fun ThreeJumping(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    color: Color,
    jump: Dp = 10.dp
) {

    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) }
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            delay(index * 100L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1200
                        0.0f at 0 with LinearOutSlowInEasing
                        1.0f at 300 with LinearOutSlowInEasing
                        0.0f at 600 with LinearOutSlowInEasing
                        0.0f at 1200 with LinearOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    val circleValues = circles.map { it.value }
    val jumpValue = with(LocalDensity.current) { jump.toPx() }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(size * 1 / 11)
    ) {
        circleValues.forEach { value ->
            Box(
                modifier = Modifier
                    .size(size * 3 / 11)
                    .graphicsLayer {
                        translationY = -value * jumpValue
                    }
                    .background(
                        color = color,
                        shape = CircleShape
                    )
            )
        }
    }
}