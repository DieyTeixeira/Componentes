package com.dieyteixeira.componentes.ui.elements.components

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.FloatingActionButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@RequiresApi(Build.VERSION_CODES.S)
private fun getRenderEffect(): RenderEffect {

    val blurEffect = RenderEffect
        .createBlurEffect(80f, 80f, Shader.TileMode.MIRROR)

    val alphaMatrix = RenderEffect.createColorFilterEffect(
        ColorMatrixColorFilter(
            ColorMatrix(
                floatArrayOf(
                    1f, 0f, 0f, 0f, 0f,
                    0f, 1f, 0f, 0f, 0f,
                    0f, 0f, 1f, 0f, 0f,
                    0f, 0f, 0f, 50f, -5000f
                )
            )
        )
    )

    return RenderEffect
        .createChainEffect(alphaMatrix, blurEffect)
}

fun Easing.transform(from: Float, to: Float, value: Float): Float {
    return transform(((value - from) * (1f / (to - from))).coerceIn(0f, 1f))
}

operator fun PaddingValues.times(value: Float): PaddingValues = PaddingValues(
    top = calculateTopPadding() * value,
    bottom = calculateBottomPadding() * value,
    start = calculateStartPadding(LayoutDirection.Ltr) * value,
    end = calculateEndPadding(LayoutDirection.Ltr) * value
)

@Composable
fun FluidButtom(
    color: Color
) {
    val isMenuExtended = remember { mutableStateOf(false) }

    val fabAnimationProgress by animateFloatAsState(
        targetValue = if (isMenuExtended.value) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        )
    )

    val clickAnimationProgress by animateFloatAsState(
        targetValue = if (isMenuExtended.value) 1f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = LinearEasing
        )
    )

    val renderEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getRenderEffect().asComposeRenderEffect()
    } else {
        null
    }

    MainScreen(
        renderEffect = renderEffect,
        fabAnimationProgress = fabAnimationProgress,
        clickAnimationProgress = clickAnimationProgress,
        color = color
    ) {
        isMenuExtended.value = isMenuExtended.value.not()
    }
}

@Composable
fun MainScreen(
    renderEffect: androidx.compose.ui.graphics.RenderEffect?,
    fabAnimationProgress: Float = 0f,
    clickAnimationProgress: Float = 0f,
    color: Color,
    toggleAnimation: () -> Unit = { }
) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Diamond(
            color = color.copy(alpha = 0.5f),
            animationProgress = 0.5f
        )
        FabGroup(
            renderEffect = renderEffect,
            animationProgress = fabAnimationProgress,
            backgroundColor = color
        )
        FabGroup(
            renderEffect = null,
            animationProgress = fabAnimationProgress,
            toggleAnimation = toggleAnimation,
            backgroundColor = color
        )
        Diamond(
            color = Color.White,
            animationProgress = clickAnimationProgress
        )
    }
}

@Composable
fun Diamond(color: Color, animationProgress: Float) {
    val animationValue = sin(PI * animationProgress).toFloat()

    Box(
        modifier = Modifier
            .padding(44.dp)
            .size(56.dp)
            .scale(2 - animationValue)
            .rotate(45f)
            .border(
                width = 2.dp,
                color = color.copy(alpha = color.alpha * animationValue),
                shape = RoundedCornerShape(0.dp, 10.dp, 0.dp, 10.dp)
            )
    )
}

@Composable
fun FabGroup(
    animationProgress: Float = 0f,
    renderEffect: androidx.compose.ui.graphics.RenderEffect? = null,
    toggleAnimation: () -> Unit = { },
    backgroundColor: Color
) {
    var isOriginalClicked by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .graphicsLayer { this.renderEffect = renderEffect }
            .padding(bottom = 44.dp),
        contentAlignment = Alignment.BottomCenter
    ) {

        // Desenha o primeiro FAB animado
        AnimatedFab(
            icon = Icons.Default.PhotoCamera,
            modifier = Modifier
                .padding(
                    PaddingValues(
                        bottom = 55.dp,
                        end = 120.dp
                    ) * FastOutSlowInEasing.transform(0f, 0.8f, animationProgress)
                ),
            opacity = LinearEasing.transform(0.2f, 0.7f, animationProgress),
            iconRotation = -45f,
            iconColor = Color.White,
            backgroundColor = backgroundColor
        )

        // Desenha o segundo FAB animado
        AnimatedFab(
            icon = Icons.Default.Settings,
            modifier = Modifier
                .padding(
                    PaddingValues(
                        bottom = 115.dp,
                    ) * FastOutSlowInEasing.transform(0.1f, 0.9f, animationProgress)
                ),
            opacity = LinearEasing.transform(0.3f, 0.8f, animationProgress),
            iconRotation = -45f,
            iconColor = Color.White,
            backgroundColor = backgroundColor
        )

        // Desenha o terceiro FAB animado
        AnimatedFab(
            icon = Icons.Default.ShoppingCart,
            modifier = Modifier.padding(
                PaddingValues(
                    bottom = 55.dp,
                    start = 120.dp
                ) * FastOutSlowInEasing.transform(0.2f, 1.0f, animationProgress)
            ),
            opacity = LinearEasing.transform(0.4f, 0.9f, animationProgress),
            iconRotation = -45f,
            iconColor = Color.White,
            backgroundColor = backgroundColor
        )

        // Desenha o FAB central animado (sem ícone)
        AnimatedFab(
            modifier = Modifier
                .scale(1f - LinearEasing.transform(0.5f, 0.85f, animationProgress)),
            iconRotation = -45f,
            backgroundColor = backgroundColor
        )

        // Desenha o FAB principal com o ícone de adicionar (+), que alterna a animação
        AnimatedFab(
            icon = Icons.Default.Add,
            modifier = Modifier
                .rotate(
                    225 * FastOutSlowInEasing
                        .transform(0.35f, 0.65f, animationProgress)
                ),
            onClick = {
                isOriginalClicked = !isOriginalClicked
                toggleAnimation()
            },
            iconColor = if (isOriginalClicked) backgroundColor else Color.White,
            backgroundColor = Color.Transparent,
            iconRotation = -45f
        )
    }
}

@Composable
fun AnimatedFab(
    modifier: Modifier,
    icon: ImageVector? = null,
    opacity: Float = 1f,
    backgroundColor: Color,
    iconColor: Color? = null,
    iconRotation: Float = 0f,
    onClick: () -> Unit = {}
) {
    FloatingActionButton(
        onClick = onClick,
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp),
        backgroundColor = backgroundColor,
        shape = RoundedCornerShape(0.dp, 10.dp, 0.dp, 10.dp),
        modifier = modifier
            .scale(1.10f)
            .rotate(45f)
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = null,
                tint = iconColor?.copy(alpha = opacity) ?: Color.White,
                modifier = Modifier.rotate(iconRotation)
            )
        }
    }
}