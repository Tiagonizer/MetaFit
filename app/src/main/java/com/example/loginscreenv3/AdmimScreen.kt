package com.example.loginscreenv3

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun AdmimScreen(navController: NavController) {
    // Estado para controlar a visibilidade das opções de cada botão
    var showAddMissionOptions by remember { mutableStateOf(false) }
    var showDeleteMissionOptions by remember { mutableStateOf(false) }
    var showEditMissionOptions by remember { mutableStateOf(false) }

    // Título do administrador
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Administrador:", fontSize = 30.sp)

        // Adiciona espaçamento entre o título e os botões
        Spacer(modifier = Modifier.height(32.dp))

        // Botão para Incluir Missão
        Button(onClick = { showAddMissionOptions = !showAddMissionOptions }) {
            Text(text = "Incluir Missão")
        }

        // Se o botão "Incluir Missão" for clicado, exibe opções abaixo
        if (showAddMissionOptions) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Digite a nova missão:", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* Ação para criar missão */ }) {
                Text(text = "Criar Nova Missão")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para Excluir Missão
        Button(onClick = { showDeleteMissionOptions = !showDeleteMissionOptions }) {
            Text(text = "Excluir Missão")
        }

        // Se o botão "Excluir Missão" for clicado, exibe opções abaixo
        if (showDeleteMissionOptions) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Selecione uma missão para excluir:", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* Ação para excluir missão */ }) {
                Text(text = "Excluir Missão Selecionada")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para Editar Missão
        Button(onClick = { showEditMissionOptions = !showEditMissionOptions }) {
            Text(text = "Editar Missões")
        }

        // Se o botão "Editar Missão" for clicado, exibe opções abaixo
        if (showEditMissionOptions) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Escolha uma missão para editar:", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* Ação para editar missão */ }) {
                Text(text = "Editar Missão Selecionada")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAdmimScreen() {
    val navController = rememberNavController()
    AdmimScreen(navController = navController)
}
