package com.dieyteixeira.componentes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
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