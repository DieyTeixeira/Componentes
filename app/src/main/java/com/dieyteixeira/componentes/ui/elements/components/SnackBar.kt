package com.dieyteixeira.componentes.ui.elements.components

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SnackBar(color: Color) {
    val scaffoldState: ScaffoldState = rememberScaffoldState(
        snackbarHostState = SnackbarHostState()
    )
    val visibleState: MutableState<Boolean> = remember { mutableStateOf(false) }
    val snackBarMessage: MutableState<String> = remember { mutableStateOf("") }

    Scaffold(
        scaffoldState = scaffoldState,
        content = {
            ShowSnackBar(visibleState, snackBarMessage)
        },
        backgroundColor = Color.White,
        snackbarHost = {
            if (visibleState.value) {
                Snackbar(
                    modifier = Modifier
                        .height(50.dp),
                    action = {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier
                                    .clickable { visibleState.value = false }
                                    .size(24.dp)
                            )
                        }
                    },
                    content = {
                        Text(
                            text = snackBarMessage.value
                        )
                    },
                    backgroundColor = color
                )
            }
        }
    )
}

@Composable
fun ShowSnackBar(
    visibleState: MutableState<Boolean>,
    snackBarMessage: MutableState<String>
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                scope.launch {
                    visibleState.value = true
                    snackBarMessage.value = "File Upload Successful"
                }
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.LightGray
            )
        ) {
            Text(
                text = "Show SnackBar"
            )
        }
    }
}