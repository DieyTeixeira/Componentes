package com.dieyteixeira.componentes.ui.elements.components

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieyteixeira.componentes.ui.theme.Green
import com.dieyteixeira.componentes.ui.theme.Red
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalAnimationApi::class, ExperimentalCoroutinesApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ObserveConnectivity(color: Color) {
    Surface(color = color) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "Top Bar",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                )
            },
            content = {
                Column {
                    ConnectivityStatus()
                    LazyColumn {
                        items(20) { index ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .padding(10.dp, 5.dp, 10.dp, 5.dp)
                                    .background(Color.White),
                                elevation = 10.dp,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Make it Easy ${index + 1}",
                                        color = Color.Black,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.padding(2.dp))

                                    Text(
                                        text = "Live Connectivity Status ${index + 1}",
                                        fontSize = 16.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun ConnectivityStatusBox(
    isConnected: Boolean
) {
    val backgroundColor by animateColorAsState(targetValue = if (isConnected) Green else Red)
    val message = if (isConnected) "Conectado!" else "Sem sinal!"
    val iconResource = if (isConnected) {
        Icons.Default.Wifi
    } else {
        Icons.Default.WifiOff
    }
    Box(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = iconResource,
                contentDescription = "Connection Image",
                modifier = Modifier.size(15.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = message,
                color = Color.White,
                fontSize = 12.sp
            )
        }
    }
}

@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@Composable
fun ConnectivityStatus() {
    val connection by connectivityState()
    val isConnected = connection == ConnectionState.Available
    var visibility by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = visibility,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        ConnectivityStatusBox(isConnected = isConnected)
    }

    LaunchedEffect(isConnected) {
        visibility = if (!isConnected) {
            true
        } else {
            delay(2000)
            false
        }
    }
}

sealed class ConnectionState {
    object Available: ConnectionState()
    object Unavailable: ConnectionState()
}

@ExperimentalCoroutinesApi
fun Context.observeConnectivityAsFlow() = callbackFlow {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val callback = NetworkCallback { connectionState -> trySend(connectionState)}
    val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .build()
    connectivityManager.registerNetworkCallback(networkRequest, callback)
    val currentState = getCurrentConnectivityState(connectivityManager)
    trySend(currentState)

    awaitClose {
        connectivityManager.unregisterNetworkCallback(callback)
    }
}

val Context.currentConnectivityState: ConnectionState
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return getCurrentConnectivityState(connectivityManager)
    }

private fun getCurrentConnectivityState(connectivityManager: ConnectivityManager) : ConnectionState {
    val connected = connectivityManager.allNetworks.any { network ->
        connectivityManager.getNetworkCapabilities(network)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            ?: false
    }

    return if (connected) ConnectionState.Available else ConnectionState.Unavailable
}

fun NetworkCallback(callback: (ConnectionState) -> Unit): ConnectivityManager.NetworkCallback {
    return object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            callback(ConnectionState.Available)
        }

        override fun onLost(network: Network) {
            callback(ConnectionState.Unavailable)
        }
    }
}

@ExperimentalCoroutinesApi
@Composable
fun connectivityState(): State<ConnectionState> {
    val context = LocalContext.current
    return produceState(
        initialValue = context.currentConnectivityState
    ) {
        context.observeConnectivityAsFlow().distinctUntilChanged().collect { value = it }
    }
}