package com.dieyteixeira.componentes.ui.elements.components

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieyteixeira.componentes.ui.theme.BlueSky

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalAnimationApi
@Composable
fun FloatingButtonExpanded() {
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            MultiFloatingActionButton(
                items = listOf(
                    MultiFabItem(
                        id = 1,
                        icon = Icons.Default.Person,
                        label = "Add User"
                    ),
                    MultiFabItem(
                        id = 2,
                        icon = Icons.Default.Groups,
                        label = "Create Group"
                    ),
                    MultiFabItem(
                        id = 3,
                        icon = Icons.Default.CameraAlt,
                        label = "Video Call"
                    )
                ),
                fabIcon = FabIcon(icon = Icons.Default.Add, iconRotate = 45f),
                onFabItemClicked = {
                    Toast.makeText(context, it.label, Toast.LENGTH_SHORT).show()
                },
                fabOption = FabOption(
                    iconTint = Color.White,
                    showLabel = true
                )
            )
        },
        content = {}
    )
}

@ExperimentalAnimationApi
@Composable
fun MultiFloatingActionButton(
    modifier: Modifier = Modifier,
    items: List<MultiFabItem>,
    fabState: MutableState<MultiFabState> = rememberMultiFabState(),
    fabIcon: FabIcon,
    fabOption: FabOption = FabOption(),
    onFabItemClicked: (fabItem: MultiFabItem) -> Unit,
    stateChanged: (fabState: MultiFabState) -> Unit = {}
) {
    val rotation by animateFloatAsState(
        if (fabState.value == MultiFabState.Expand) {
            fabIcon.iconRotate ?: 0f
        } else {
            0f
        }
    )

    Column(
        modifier = modifier.wrapContentSize(),
        horizontalAlignment = Alignment.End
    ) {
        AnimatedVisibility(
            visible = fabState.value.isExpanded(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut()
        ) {
            LazyColumn(
                modifier = Modifier.wrapContentSize(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items.size) { index ->
                    MiniFabItem(
                        item = items[index],
                        fabOption = fabOption,
                        onFabItemClicked = onFabItemClicked
                    )
                }

                item {}
            }
        }

        FloatingActionButton(
            onClick = {
                fabState.value = fabState.value.toggleValue()
                stateChanged(fabState.value)
            },
            backgroundColor = fabOption.backgroundTint,
            contentColor = fabOption.iconTint
        ) {
            Icon(
                imageVector = fabIcon.icon,
                contentDescription = "FAB",
                modifier = Modifier.rotate(rotation),
                tint = fabOption.iconTint
            )
        }
    }
}

@Composable
fun MiniFabItem(
    item: MultiFabItem,
    fabOption: FabOption,
    onFabItemClicked: (item: MultiFabItem) -> Unit
) {
    val iconForId: (Int) -> ImageVector = { id ->
        when (id) {
            1 -> Icons.Default.Person
            2 -> Icons.Default.Groups
            3 -> Icons.Default.CameraAlt
            else -> Icons.Default.Add // Ícone padrão
        }
    }

    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(end = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (fabOption.showLabel) {
            Text(
                text = item.label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(MaterialTheme.colors.surface)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
        }

        FloatingActionButton(
            onClick = {
                onFabItemClicked(item)
            },
            modifier = Modifier.size(40.dp),
            backgroundColor = fabOption.backgroundTint,
            contentColor = fabOption.iconTint
        ) {
            Icon(
                imageVector = iconForId(item.id),
                contentDescription = "Float Icon",
                tint = fabOption.iconTint
            )
        }
    }
}

sealed class MultiFabState {
    object Collapsed: MultiFabState()
    object Expand: MultiFabState()

    fun isExpanded() = this == Expand

    fun toggleValue() = if (isExpanded()) {
        Collapsed
    } else {
        Expand
    }
}

@Composable
fun rememberMultiFabState() = remember { mutableStateOf<MultiFabState>(MultiFabState.Collapsed) }

data class MultiFabItem(
    val id: Int,
    val icon: ImageVector,
    val label: String = ""
)

@Immutable
interface FabOption {
    @Stable val iconTint: Color
    @Stable val backgroundTint: Color
    @Stable val showLabel: Boolean
}

private class FabOptionImpl(
    override val iconTint: Color,
    override val backgroundTint: Color,
    override val showLabel: Boolean
) : FabOption

@SuppressLint("ComposableNaming")
@Composable
fun FabOption(
    backgroundTint: Color = BlueSky,
    iconTint: Color = contentColorFor(backgroundColor = backgroundTint),
    showLabel: Boolean = false
) : FabOption = FabOptionImpl(iconTint, backgroundTint, showLabel)


@Immutable
interface FabIcon {
    @Stable
    val icon: ImageVector
    @Stable
    val iconRotate: Float?
}

private class FabIconImpl(
    override val icon: ImageVector,
    override val iconRotate: Float?
) : FabIcon

fun FabIcon(
    icon: ImageVector,
    iconRotate: Float? = null
) : FabIcon = FabIconImpl(icon, iconRotate)
