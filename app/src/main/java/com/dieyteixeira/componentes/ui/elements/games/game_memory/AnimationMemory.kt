package com.dieyteixeira.componentes.ui.elements.games.game_memory

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.dieyteixeira.componentes.ui.elements.components.FlipCard
import com.dieyteixeira.componentes.ui.theme.BlueSky
import com.dieyteixeira.componentes.ui.theme.Green500
import com.dieyteixeira.componentes.ui.theme.Red
import kotlinx.coroutines.delay

@ExperimentalMaterialApi
@Composable
fun FlipRotate(
    flipCard: FlipCard,
    onClick: () -> Unit,
    isMatched: Boolean,
    matchPlayer: Int,
    modifier: Modifier = Modifier,
    forward: @Composable () -> Unit,
    previous: @Composable () -> Unit
) {
    var showExplosion  by remember { mutableStateOf(false) }

    LaunchedEffect(isMatched) {
        if (isMatched) {
            showExplosion  = true
            delay(1000)
            showExplosion  = false
        }
    }

    val rotation = animateFloatAsState(
        targetValue = flipCard.angle,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )

    val scale = animateFloatAsState(
        targetValue = if (isMatched) 1f else 1f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
    )

    Card(
        onClick = { onClick() },
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 12f * density
                scaleX = scale.value
                scaleY = scale.value
            },
        shape = RoundedCornerShape(15.dp),
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (rotation.value <= 90f) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    forward()
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = 180f
                        }
                ) {
                    previous()

                    if (showExplosion) {
                        SparksEffect(modifier, matchPlayer)
                    }
                }
            }
        }
    }
}

@Composable
fun SparksEffect(modifier: Modifier, matchPlayer: Int) {
    val explosionColor = when (matchPlayer) {
        1 -> BlueSky
        2 -> Red
        3 -> Green500
        else -> Color.LightGray
    }

    val infiniteTransition = rememberInfiniteTransition()

    val radius = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    )

    val alpha = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing
            )
        )
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2

        // Gerando faíscas com dispersão e alpha
        drawCircle(
            color = explosionColor,
            radius = radius.value * size.minDimension, // expanding circle
            center = Offset(centerX, centerY),
            alpha = alpha.value // fade out the explosion
        )
    }
}