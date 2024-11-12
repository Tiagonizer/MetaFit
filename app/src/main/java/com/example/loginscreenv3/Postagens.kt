package com.example.loginscreenv3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.loginscreenv3.ui.theme.LoginScreenV3Theme

@Composable
fun Postagens(navController: NavHostController) {
    // Removendo o estado de selectedItem, já que não estamos mais utilizando a BottomBar
    LoginScreenV3Theme {
        Scaffold(
            // Remover a BottomBar
            // bottomBar = { ... }
        ) { paddingValues ->
            PostagensScreenContent(modifier = Modifier.padding(paddingValues))
        }
    }
}

@Composable
fun PostagensScreenContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(10) {
            PostagemItem()
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PostagemItem() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.avatar1),
                contentDescription = "Foto do Usuário",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .background(Color.Gray, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Nome do Usuário",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Image(
            painter = painterResource(id = R.drawable.post_image_1),
            contentDescription = "Imagem da Postagem",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Postado há 1 hora",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PostagensScreenPreview() {
    val navController = rememberNavController()
    Postagens(navController = navController)
}
