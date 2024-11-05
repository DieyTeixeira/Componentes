package com.dieyteixeira.componentes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dieyteixeira.componentes.ui.components.loading_animation.FadingCircle
import com.dieyteixeira.componentes.ui.screen.AppScreen
import com.dieyteixeira.componentes.ui.theme.ComponentesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComponentesTheme {
                AppScreen()
            }
        }
    }
}

@Preview
@Composable
private fun ComponentPreview() {
    ComponentesTheme {
        AppScreen()
    }
}