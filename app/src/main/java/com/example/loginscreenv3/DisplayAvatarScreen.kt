package com.example.loginscreenv3

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true)
@Composable
fun PreviewDisplayAvatarScreen() {
    DisplayAvatarScreen(navController = rememberNavController())
}

@Composable
fun DisplayAvatarScreen(navController: NavController) {
    // Definindo os avatares a partir dos recursos de drawable
    val avatar1 = R.drawable.avatar1
    val avatar2 = R.drawable.avatar2
    val avatar3 = R.drawable.avatar3
    val avatar4 = R.drawable.avatar4
    val avatar5 = R.drawable.avatar5
    val avatar6 = R.drawable.avatar6

    // Lista de avatares
    val avatarList = listOf(avatar1, avatar2, avatar3, avatar4, avatar5, avatar6)

    // Estado para controlar o avatar selecionado
    var selectedAvatar by remember { mutableStateOf<Int?>(null) }

    // Layout da tela
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Escolha seu Avatar", modifier = Modifier.padding(16.dp))

        // Exibição dos avatares em uma grid 3x2
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            avatarList.chunked(3).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { avatarId ->
                        Image(
                            painter = painterResource(id = avatarId),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (avatarId == selectedAvatar) 4.dp else 0.dp,
                                    color = if (avatarId == selectedAvatar) Color.Blue else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedAvatar = avatarId
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botão de selecionar avatar
        Button(
            onClick = {
                // Voltar à tela inicial, ignorando o avatar selecionado
                navController.navigate("Login")
            },
            enabled = selectedAvatar != null
        ) {
            Text(text = "Selecionar Avatar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão de continuar sem selecionar avatar
        Button(
            onClick = {
                // Voltar para a tela inicial, sem avatar
                navController.navigate("Login")
            }
        ) {
            Text(text = "Continuar sem Avatar")
        }
    }
}