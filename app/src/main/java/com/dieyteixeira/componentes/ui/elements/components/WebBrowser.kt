package com.dieyteixeira.componentes.ui.elements.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.google.accompanist.web.*

/*-------------------------------------------------------------------------------------------------|
|                                                                                                  |
|                                         WEB BROWSER                                              |
|                                          Dependência:                                            |
|        WebView: implementation("com.google.accompanist:accompanist-webview:0.29.1-alpha")        |
|                                                                                                  |
|------------------------------------------------------------------------------------------------ */

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebBrowser(
    color: Color
) {
    var url by remember { mutableStateOf("https://stevdza-san.com") }
    val state = rememberWebViewState(url = url)
    val navigator = rememberWebViewNavigator()
    var textFieldValue by remember(state.content.getCurrentUrl()) {
        mutableStateOf(state.content.getCurrentUrl() ?: "")
    }

    Column {
        TopAppBar(
            backgroundColor = color
        ) {
            IconButton(onClick = { navigator.navigateBack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            IconButton(onClick = { navigator.navigateForward() }) {
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = "Forward",
                    tint = Color.White
                )
            }
            Text(
                text = "Web Browser", style = TextStyle(
                    color = Color.White,
                    fontSize = MaterialTheme.typography.h6.fontSize,
                    fontWeight = MaterialTheme.typography.h6.fontWeight
                )
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { navigator.reload() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.White
                    )
                }
                IconButton(onClick = { url = textFieldValue }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowForward,
                        contentDescription = "Go",
                        tint = Color.White
                    )
                }
            }
        }

        Row(modifier = Modifier.padding(all = 12.dp)) {
            BasicTextField(
                modifier = Modifier.weight(9f),
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                maxLines = 1
            )
            if (state.errorsForCurrentRequest.isNotEmpty()) {
                Icon(
                    modifier = Modifier
                        .weight(1f),
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = Color.Red
                )
            }
        }

        val loadingState = state.loadingState
        if (loadingState is LoadingState.Loading) {
            LinearProgressIndicator(
                progress = loadingState.progress,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // A custom WebViewClient and WebChromeClient can be provided via subclassing
        val webClient = remember {
            object : AccompanistWebViewClient() {
                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: Bitmap?
                ) {
                    super.onPageStarted(view, url, favicon)
                    Log.d("Accompanist WebView", "Page started loading for $url")
                }
            }
        }

        WebView(
            state = state,
            modifier = Modifier.weight(1f),
            navigator = navigator,
            onCreated = { webView ->
                webView.settings.javaScriptEnabled = true
            },
            client = webClient
        )
    }
}