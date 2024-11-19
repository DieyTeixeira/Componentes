package com.dieyteixeira.componentes.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesomeMosaic
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.HdrStrong
import androidx.compose.material.icons.outlined.AutoAwesomeMosaic
import androidx.compose.material.icons.outlined.Games
import androidx.compose.material.icons.outlined.HdrWeak
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppScreen() {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { HomeTabs.entries.size })
    val selectedTabIndex = remember { derivedStateOf { pagerState.currentPage } }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex.value,
                modifier = Modifier.fillMaxWidth()
            ) {
                HomeTabs.entries.forEachIndexed { index, currentTab ->
                    Tab(
                        selected = selectedTabIndex.value == index,
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.outline,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(currentTab.ordinal)
                            }
                        },
                        text = { Text(text = currentTab.text) },
                        icon = {
                            Icon(
                                imageVector = if (selectedTabIndex.value == index)
                                    currentTab.selectedIcon else currentTab.unselectedIcon,
                                contentDescription = "Tab Icon"
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                HomeTabs.entries[selectedTabIndex.value].screen()
            }
        }
    }
}

enum class HomeTabs(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val text: String,
    val screen: @Composable () -> Unit
) {
    Loadings(
        unselectedIcon = Icons.Outlined.HdrWeak,
        selectedIcon = Icons.Filled.HdrStrong,
        text = "Loadings",
        screen = { LoadingsScreen() }
    ),
    Components(
        unselectedIcon = Icons.Outlined.AutoAwesomeMosaic,
        selectedIcon = Icons.Filled.AutoAwesomeMosaic,
        text = "Components",
        screen = { ComponentsScreen() }
    ),
    Games(
        unselectedIcon = Icons.Outlined.Games,
        selectedIcon = Icons.Filled.Games,
        text = "Games",
        screen = { GamesScreen() }
    )
}

