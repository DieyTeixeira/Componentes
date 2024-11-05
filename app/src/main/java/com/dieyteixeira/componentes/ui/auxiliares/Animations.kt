package com.dieyteixeira.componentes.ui.auxiliares

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

val animationDuration = 2000

fun enterTransition(): EnterTransition =
    fadeIn(animationSpec = tween(animationDuration))

fun exitTransition(): ExitTransition =
    fadeOut(animationSpec = tween(animationDuration))

fun popEnterTransition(): EnterTransition =
    fadeIn(animationSpec = tween(animationDuration))

fun popExitTransition(): ExitTransition =
    fadeOut(animationSpec = tween(animationDuration))
