package com.dieyteixeira.componentes.ui.components.photo_picker

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dieyteixeira.componentes.R
import coil.compose.AsyncImage
import coil.request.ImageRequest

/*-------------------------------------------------------------------------------------------------|
|                                                                                                  |
|                            PEGAR FOTO DO GALERIA E MOSTRAR NA TELA                               |
|                                          Dependência:                                            |
|                     Coil: implementation("io.coil-kt:coil-compose:2.2.2")                        |
|                                                                                                  |
|------------------------------------------------------------------------------------------------ */

@Composable
fun PhotoPicker() {
    val context = LocalContext.current
    var imageUri: Any? by remember { mutableStateOf("") }
    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) {
        if (it != null) {
            Log.d("PhotoPicker", "Selected URI: $it")
            imageUri = it
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(1.dp, Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                modifier = Modifier
                    .size(250.dp)
                    .clickable {
                        photoPicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUri)
                    .crossfade(enable = true)
                    .build(),
                contentDescription = "Avatar Image",
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .size(60.dp) // Tamanho do círculo que envolve o ícone
                    .align(Alignment.Center) // Alinha o ícone ao canto inferior direito da imagem
                    .graphicsLayer {
                        alpha = 0.7f // Transparência do círculo
                    }
                    .background(color = Color.Gray, shape = CircleShape) // Fundo cinza com transparência
                    .clip(CircleShape)
                    .clickable {
                        // Ação para abrir o seletor de imagem ao clicar no ícone
                        photoPicker.launch(
                            PickVisualMediaRequest(
                                ActivityResultContracts.PickVisualMedia.ImageOnly
                            )
                        )
                    },
                contentAlignment = Alignment.Center // Centraliza o ícone dentro do círculo
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = "Camera Icon",
                    tint = Color.White, // Cor do ícone
                    modifier = Modifier.size(30.dp) // Tamanho do ícone
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            Toast.makeText(
                context,
                ActivityResultContracts.PickVisualMedia.isPhotoPickerAvailable().toString(),
                Toast.LENGTH_LONG
            ).show()
        }) {
            Text(text = "Validar Foto")
        }
    }
}

@Preview
@Composable
private fun PhotoPickerPreview() {
    PhotoPicker()
}