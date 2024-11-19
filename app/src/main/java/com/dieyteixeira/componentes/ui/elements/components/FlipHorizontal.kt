package com.dieyteixeira.componentes.ui.elements.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieyteixeira.componentes.R

enum class FlipCard(val angle: Float) {
    Forward(0f) {
        override val next: FlipCard get() = Previous
    },
    Previous(180f) {
        override val next: FlipCard get() = Forward
    };

    abstract val next: FlipCard
}

@ExperimentalMaterialApi
@Composable
fun FlipHorizontal(color: Color) {
    var flipCard by remember { mutableStateOf(FlipCard.Forward) }

    FlipRotate(
        flipCard = flipCard,
        onClick = { flipCard = flipCard.next },
        modifier = Modifier
            .width(120.dp)
            .aspectRatio(1f),
        forward = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_double_arrow_left),
                    contentDescription = "Forward",
                    modifier = Modifier.size(80.dp)
                )
            }
        },
        previous = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_double_arrow_right),
                    contentDescription = "Previous",
                    modifier = Modifier.size(80.dp)
                )
            }
        }
    )
}

@ExperimentalMaterialApi
@Composable
fun FlipRotate(
    flipCard: FlipCard,
    onClick: (FlipCard) -> Unit,
    modifier: Modifier = Modifier,
    previous: @Composable () -> Unit = {},
    forward: @Composable () -> Unit = {}
) {
    val rotation = animateFloatAsState(
        targetValue = flipCard.angle,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            onClick = { onClick(flipCard) },
            modifier = modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation.value
                    cameraDistance = 12f * density
                },
            shape = RoundedCornerShape(15.dp)
        ) {
            if (rotation.value <= 90f) {
                Box(
                    Modifier
                        .fillMaxSize()
                ) {
                    forward()
                }
            } else {
                Box(
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationY = 180f
                        }
                ) {
                    previous()
                }
            }
        }
    }
}