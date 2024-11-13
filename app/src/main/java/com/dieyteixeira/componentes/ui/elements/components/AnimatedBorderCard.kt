package com.dieyteixeira.componentes.ui.elements.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp

/*-------------------------------------------------------------------------------------------------|
|                                                                                                  |
|                                   ANIMAÇÃO DE BORDA DE CARD                                      |
|                                                                                                  |
|------------------------------------------------------------------------------------------------ */

@Composable
fun AnimatedBorderCard(
    textCard: String = "",
) {
    val shapeExt = 15
    val shapeInt = shapeExt - (shapeExt * getReductionFactor(shapeExt.toFloat()))
    val infiniteTransition = rememberInfiniteTransition(label = "Infinite Color Animation")
    val degrees by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Infinite Colors"
    )
    val gradientBrush = Brush.sweepGradient(
        listOf(
            Color.Red,
            Color.Blue
        )
    )

    Surface(
        modifier = Modifier
            .padding(16.dp)
            .height(100.dp),
        shape = RoundedCornerShape(shapeExt.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
                .drawWithContent {
                    rotate(degrees = degrees) {
                        drawCircle(
                            brush = gradientBrush,
                            radius = size.width,
                            blendMode = BlendMode.SrcIn,
                        )
                    }
                    drawContent()
                },
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(shapeInt.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = textCard
                )
            }
        }
    }
}

fun getReductionFactor(shapeExtValue: Float): Float {
    return when {
        shapeExtValue <= 10 -> 0.10f
        shapeExtValue <= 20 -> 0.09f
        shapeExtValue <= 30 -> 0.08f
        shapeExtValue <= 40 -> 0.07f
        shapeExtValue <= 50 -> 0.06f
        shapeExtValue <= 60 -> 0.05f
        shapeExtValue <= 70 -> 0.04f
        shapeExtValue <= 80 -> 0.03f
        shapeExtValue <= 90 -> 0.02f
        else -> 0.01f // Para valores maiores que 90.dp
    }
}