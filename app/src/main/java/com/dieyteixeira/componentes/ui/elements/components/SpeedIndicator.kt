package com.dieyteixeira.componentes.ui.elements.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieyteixeira.componentes.ui.theme.Green200
import com.dieyteixeira.componentes.ui.theme.Green500
import com.dieyteixeira.componentes.ui.theme.GreenGradient
import com.dieyteixeira.componentes.ui.theme.LightColor
import kotlin.math.floor

@Composable
fun SpeedIndicator() {
    val angle = 240f
    val arcValue = 0.83f
    val value = "83%"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(ratio = 1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp)
            ) {
                drawLines(arcValue, angle)
                drawArcs(arcValue, angle)
            }
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = value,
                    fontSize = 40.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun DrawScope.drawArcs(progress: Float, maxValue: Float) {

    val startAngle = 270 - maxValue / 2
    val sweepAngle = maxValue * progress

    val topLeft = Offset(x = 50f, y = 50f)
    val size = Size(width = size.width - 82f, height = size.height - 82f)

    fun drawBlur() {
        for (i in 0..20) {
            drawArc(
                color = Green200.copy(alpha = i / 900f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = size,
                style = Stroke(width = 80f + (20 - i) * 20, cap = StrokeCap.Round)
            )
        }
    }

    fun drawStroke() {
        drawArc(
            color = Green500,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = size,
            style = Stroke(width = 86f, cap = StrokeCap.Round)
        )
    }

    fun drawGradient() {
        drawArc(
            brush = GreenGradient,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = size,
            style = Stroke(width = 80f, cap = StrokeCap.Round)
        )
    }

    drawBlur()
    drawStroke()
    drawGradient()
}

fun DrawScope.drawLines(progress: Float, maxValue: Float, numberOfLines: Int = 40) {

    val oneRotation = maxValue / numberOfLines
    val startValue = if (progress == 0f) 0 else floor(x = progress * numberOfLines).toInt() + 1

    for (i in startValue..numberOfLines) {
        rotate(degrees = i * oneRotation + (180 - maxValue) / 2) {
            drawLine(
                color = LightColor,
                start = Offset(if (i % 5 == 0) 60f else 30f, y = size.height / 2),
                end = Offset(x = 0f, y = size.height / 2),
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
        }
    }
}