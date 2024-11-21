package com.dieyteixeira.componentes.ui.elements.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dieyteixeira.componentes.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AnimationExpandList() {
    LazyColumn(
        state = rememberLazyListState(),
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item { AnimationForStates() }
    }
}

@Composable
fun AnimationForStates() {
    var expand by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ImageItem(
            imageRes = R.drawable.apple,
            expand = expand,
            translationX = -330f,
            onClick = { expand = !expand }
        )
        ImageItem(
            imageRes = R.drawable.banana,
            expand = expand,
            translationX = -110f,
            onClick = { expand = !expand }
        )
        ImageItem(
            imageRes = R.drawable.fig,
            expand = expand,
            translationX = 110f,
            onClick = { expand = !expand }
        )
        ImageItem(
            imageRes = R.drawable.cherries,
            expand = expand,
            translationX = 330f,
            onClick = { expand = !expand }
        )
    }
}

@Composable
fun ImageItem(
    imageRes: Int,
    expand: Boolean,
    translationX: Float,
    onClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val rotation = remember { androidx.compose.animation.core.Animatable(0f) }
    val translation = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(expand) {
        scope.launch {
            if (expand) {
                rotation.animateTo(
                    targetValue = 45f,
                    animationSpec = tween(durationMillis = 300)
                )
                delay(100)
                translation.animateTo(
                    targetValue = translationX,
                    animationSpec = tween(durationMillis = 300)
                )
            } else {
                translation.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 300)
                )
                delay(100)
                rotation.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .size(100.dp)
            .graphicsLayer(
                rotationY = rotation.value,
                translationX = translation.value
            )
            .shadow(
                elevation = if (expand) 10.dp else 5.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )
    }
}