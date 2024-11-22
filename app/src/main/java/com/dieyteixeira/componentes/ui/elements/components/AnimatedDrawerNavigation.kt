package com.dieyteixeira.componentes.ui.elements.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AnimatedDrawerNavigation(color: Color) {
    Surface(color = color) {
        NavigationDrawer(color)
        BodyContent()
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun BodyContent() {
    var navigateClick by remember { mutableStateOf(false) }
    val offSetAnim by animateDpAsState(targetValue = if (navigateClick) 253.dp else 0.dp)
    val scaleAnim by animateFloatAsState(targetValue = if (navigateClick) 0.6f else 1.0f)
    val clipAnim by animateDpAsState(targetValue = if (navigateClick) 25.dp else 15.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .scale(scaleAnim)
            .offset(x = offSetAnim)
            .clip(RoundedCornerShape(clipAnim))
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Image(
                    imageVector = Icons.Filled.List,
                    contentDescription = "Menu",
                    modifier = Modifier
                        .clickable { navigateClick = !navigateClick }
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Top Bar",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationDrawer(color: Color) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
    ) {
        NavigationItem(
            icon = Icons.Filled.Person,
            text = "Profile",
            topPadding = 100.dp
        ) {}
        NavigationItem(
            icon = Icons.Filled.Sell,
            text = "Sale"
        ) {}
        NavigationItem(
            icon = Icons.Filled.CompareArrows,
            text = "Transaction"
        ) {}
        NavigationItem(
            icon = Icons.Filled.History,
            text = "History"
        ) {}
        NavigationItem(
            icon = Icons.Filled.Settings,
            text = "Setting"
        ) {}

        Row(
            modifier = Modifier
                .padding(start = 30.dp, bottom = 80.dp)
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sign Out",
                color = Color.White,
                fontSize = 20.sp
            )

            Image(
                imageVector = Icons.Filled.ArrowForward,
                contentDescription = "Logout",
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
    }
}

@Composable
fun NavigationItem(
    icon: ImageVector,
    text: String,
    topPadding: Dp = 8.dp,
    bottomPadding: Dp = 8.dp,
    destination: String = "",
    itemClicked: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 25.dp, top = topPadding, bottom = bottomPadding)
            .clickable { itemClicked(destination) }
    ) {
        Row(
            modifier = Modifier
                .size(175.dp, 35.dp)
                .background(Color.White.copy(alpha = 0.2f), CutCornerShape(35.dp, 0.dp, 35.dp, 0.dp))
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    imageVector = icon,
                    contentDescription = "Item Image",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(30.dp)
                )
                Text(
                    text = text,
                    color = Color.White,
                    fontSize = 20.sp
                )
            }
        }
    }
}