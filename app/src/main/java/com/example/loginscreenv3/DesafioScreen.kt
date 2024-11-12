package com.example.loginscreenv3

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconScreen(navController: NavHostController) {
    var streakCount by remember { mutableIntStateOf(3) }

    // Removendo o selectedItem, pois não há mais BottomBar

    val iconList = listOf(
        R.drawable.icon_agua,
        R.drawable.icon_dieta,
        R.drawable.icon_fastfood,
        R.drawable.icon_higiene,
        R.drawable.icon_maca,
        R.drawable.icon_agua,
        R.drawable.icon_dieta,
        R.drawable.icon_fastfood,
        R.drawable.icon_higiene,
        R.drawable.icon_maca
    )

    Scaffold(
        // Remova a BottomBar aqui
        // bottomBar = { ... }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Ofensivas: $streakCount",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        // Navega para a tela RelatorioScreen
                        navController.navigate("RelatorioScreen")
                    }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(iconList) { index, iconId ->
                    IconRow(iconId = iconId, isLeft = index % 2 == 0, navController = navController)
                }
            }
        }
    }
}




@Composable
fun IconRow(iconId: Int, isLeft: Boolean, navController: NavHostController) {
    var selected by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = if (isLeft) Arrangement.Start else Arrangement.End
    ) {
        IconWithArrow(iconId = iconId, selected = selected, isLeft = isLeft, onSelect = {
            selected = it
            navController.navigate("Teste")
        })
    }
}

@Composable
fun IconWithArrow(iconId: Int, selected: Boolean, isLeft: Boolean, onSelect: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (isLeft) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLeft) {
            IconWithLine(iconId, selected, onSelect)
            Image(
                painter = painterResource(id = R.drawable.direitabaixo),
                contentDescription = null,
                modifier = Modifier.size(100.dp).padding(start = 8.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.esquerdabaixo),
                contentDescription = null,
                modifier = Modifier.size(100.dp).padding(end = 8.dp)
            )
            IconWithLine(iconId, selected, onSelect)
        }
    }
}

@Composable
fun IconWithLine(iconId: Int, selected: Boolean, onSelect: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clickable { onSelect(!selected) }
    ) {
        Image(
            painter = painterResource(id = iconId),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(100.dp).align(Alignment.Center).padding(8.dp)
        )

        if (selected) {
            Surface(
                shape = CircleShape,
                color = Color.Green.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxSize().align(Alignment.Center)
            ) {}
        }
    }

    Canvas(
        modifier = Modifier.height(100.dp).width(2.dp)
    ) {
        drawLine(
            color = Color.Black,
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewIconScreen() {
    val navController = rememberNavController()
    IconScreen(navController = navController)
}
