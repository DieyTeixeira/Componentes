package com.dieyteixeira.componentes.ui.elements.components

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dieyteixeira.componentes.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Random

@SuppressLint("RememberReturnType")
@Composable
fun AudioPlayer(color: Color) {
    val context = LocalContext.current
    val songs = listOf(
        SongData("Pirata e Tesouro", "Ferrugem", R.drawable.img_ferrugem, R.raw.pirata_e_tesouro),
        SongData("Cheia de Manias", "Raça Negra", R.drawable.img_raca_negra, R.raw.cheia_de_manias),
        SongData("Matadinha de Saudade", "Grupo Menos é Mais", R.drawable.img_grupo_menos_mais, R.raw.matadinha_de_saudade),
        SongData("Deixa Alagar", "Revelação", R.drawable.img_revelacao, R.raw.deixa_alagar)
    )
    // Estado para a música atual
    var currentSongIndex by remember { mutableStateOf(0) }
    val currentSong = songs[currentSongIndex]

    // MediaPlayer atualizado com a música atual
    val mediaPlayer = remember(currentSongIndex) {
        MediaPlayer.create(context, currentSong.audioResId)
    }

    val viewModel: MediaPlayerViewModel = viewModel()

    fun resetPlayer() {
        mediaPlayer.reset()
        viewModel.resetState()
    }

    Surface(color = color) {
        AudioPlayerLayout(
            mediaPlayer = mediaPlayer,
            song = currentSong,
            onNext = {
                resetPlayer()
                currentSongIndex = (currentSongIndex + 1) % songs.size
            },
            onPrevious = {
                resetPlayer()
                currentSongIndex = if (currentSongIndex - 1 < 0) songs.size - 1 else currentSongIndex - 1
            }
        )
    }
}

@Composable
fun AudioPlayerLayout(
    mediaPlayer: MediaPlayer,
    song: SongData,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val viewModel: MediaPlayerViewModel = viewModel()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        viewModel.getFirstColor(),
                        viewModel.getSecondColor()
                    )
                )
            )
            .padding(horizontal = 10.dp)
    ) {
        TopAppBar()
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(30.dp))
            Image(
                painter = painterResource(id = song.imageResId),
                contentDescription = "Image Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .weight(10f)
            )
            Spacer(modifier = Modifier.height(30.dp))
            SongDescription(song.title, song.artist)
            Spacer(modifier = Modifier.height(35.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(10f)
            ) {
                PlayerSlider(mediaPlayer)
                Spacer(modifier = Modifier.height(40.dp))
                PlayerButtons(
                    modifier = Modifier.padding(vertical = 8.dp),
                    mediaPlayer = mediaPlayer,
                    onNext = onNext,
                    onPrevious = onPrevious
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun TopAppBar() {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back Icon",
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Default.PlaylistAdd,
                contentDescription = "Add list",
                tint = Color.White
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More Icon",
                tint = Color.White
            )
        }
    }
}

@Composable
fun SongDescription(
    title: String,
    name: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.h5,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        color = Color.White,
        fontWeight = FontWeight.Bold
    )

    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = name,
            style = MaterialTheme.typography.body2,
            maxLines = 1,
            color = Color.White
        )
    }
}

@Composable
fun PlayerSlider(mediaPlayer: MediaPlayer) {
    val viewModel: MediaPlayerViewModel = viewModel()
    val currentMinutes = viewModel.currentMinutes.observeAsState()

    val currentPosition = remember { mutableStateOf(0f) }
    val duration = mediaPlayer.duration.toFloat()

    LaunchedEffect(mediaPlayer.isPlaying) {
        while (mediaPlayer.isPlaying) {
            delay(1000)
            currentPosition.value = mediaPlayer.currentPosition.toFloat()
            viewModel.updateCurrentTime(mediaPlayer.currentPosition / 1000)
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Slider(
            value = currentPosition.value,
            onValueChange = { newValue ->
                mediaPlayer.seekTo(newValue.toInt())
                currentPosition.value = newValue
            },
            valueRange = 0f..duration,
            onValueChangeFinished = {
                mediaPlayer.seekTo(currentPosition.value.toInt())
            },
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color.White
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = formatTime(currentMinutes.value?.times(1000) ?: 0),
                color = Color.White
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = formatTime(mediaPlayer.duration),
                color = Color.White
            )
        }
    }
}

@SuppressLint("DefaultLocale")
fun formatTime(milliseconds: Int): String {
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

@Composable
fun PlayerButtons(
    modifier: Modifier = Modifier,
    mediaPlayer: MediaPlayer,
    playerButtonSize: Dp = 72.dp,
    sideButtonSize: Dp = 42.dp,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    val viewModel : MediaPlayerViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val audioFinish = viewModel.audioFinish.observeAsState()
    val audioFlag = remember { mutableStateOf(true) }
    val isPlaying by viewModel.isPlaying.observeAsState(false)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val buttonModifier = Modifier
            .size(sideButtonSize)
            .semantics { role = Role.Button }

        Image(
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = "Skip Icon",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = buttonModifier.clickable { onPrevious() }
        )
        Image(
            imageVector = Icons.Filled.Replay10,
            contentDescription = "Reply 10 Sec Icon",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = buttonModifier
        )
        Image(
            imageVector = if (isPlaying) Icons.Filled.PauseCircleFilled else Icons.Filled.PlayCircleFilled,
            contentDescription = "Play / Pause Icon",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier
                .size(playerButtonSize)
                .semantics { role = Role.Button }
                .clickable {
                    if (isPlaying) {
                        mediaPlayer.pause()
                        viewModel.setPlayingState(false)
                    } else {
                        mediaPlayer.start()
                        scope.launch {
                            delay(500)
                            viewModel.getMediaDuration(mediaPlayer)
                        }
                        viewModel.setPlayingState(true)
                    }
                }
        )

        Image(
            imageVector = Icons.Filled.Forward10,
            contentDescription = "Forward Icon",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = buttonModifier
        )

        Image(
            imageVector = Icons.Filled.SkipNext,
            contentDescription = "Next Icon",
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = buttonModifier.clickable { onNext() }
        )
    }
}

class MediaPlayerViewModel: ViewModel() {
    private var currentDuration: CountDownTimer? = null

    private val _isPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _currentMinutes = MutableLiveData(0)
    val currentMinutes: LiveData<Int> get() = _currentMinutes

    private val _audioFinish = MutableLiveData(false)
    val audioFinish: LiveData<Boolean> get() = _audioFinish

    fun getMediaDuration(mediaPlayer: MediaPlayer) {
        currentDuration?.cancel()
        currentDuration = object : CountDownTimer(mediaPlayer.duration.toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _currentMinutes.postValue(mediaPlayer.currentPosition / 1000)
            }

            override fun onFinish() {
                _audioFinish.postValue(true)
            }
        }.start()
    }

    fun updateCurrentTime(currentTimeInSeconds: Int) {
        _currentMinutes.value = currentTimeInSeconds
    }

    fun resetState() {
        _currentMinutes.value = 0
        _audioFinish.value = false
        _isPlaying.value = false
        currentDuration?.cancel()
    }

    fun setPlayingState(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    fun getFirstColor(): Color {
        val rnd = Random()
        val color: Int = android.graphics.Color.argb(255,
            rnd.nextInt(256),
            rnd.nextInt(256),
            rnd.nextInt(256)
        )
        return Color(color)
    }

    fun getSecondColor(): Color {
        val rnd = Random()
        val color: Int = android.graphics.Color.argb(255,
            rnd.nextInt(256),
            rnd.nextInt(256),
            rnd.nextInt(256)
        )
        return Color(color)
    }
}

data class SongData(
    val title: String,
    val artist: String,
    val imageResId: Int,
    val audioResId: Int
)