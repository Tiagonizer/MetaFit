package com.example.loginscreenv3

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@Composable
fun DesafioScreen(navController: NavHostController) {
    // Estado para armazenar as missões carregadas do Firebase
    var missoes by remember { mutableStateOf<List<Missao>>(emptyList()) }

    // Carregar missões ao inicializar
    LaunchedEffect(Unit) {
        carregarMissoes { missaoList ->
            missoes = missaoList
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Missões",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(missoes) { missao ->
                    MissaoCard(missao)
                }
            }
        }
    }
}

@Composable
fun MissaoCard(missao: Missao) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagem circular gerada pelo Gemini (placeholder para exemplo)
//            Image(
//                painter = painterResource(id = R.drawable.placeholder_image), // Substituir pelo Gemini
//                contentDescription = "Imagem da Missão",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .size(50.dp)
//                    .clip(CircleShape)
//            )

            Spacer(modifier = Modifier.width(16.dp))

            // Título e descrição da missão
            Column {
                Text(
                    text = missao.titulo,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = missao.descricao,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// Modelo de dados da missão
data class Missao(
    val titulo: String = "",
    val descricao: String = "",
    val ordem: Int = 0
)

// Função para carregar missões do Firestore
fun carregarMissoes(onDataLoaded: (List<Missao>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("missoes")
        .orderBy("ordem", Query.Direction.ASCENDING) // Ordenar conforme o campo "ordem"
        .get()
        .addOnSuccessListener { result ->
            val missaoList = result.map { document ->
                Missao(
                    titulo = document.getString("titulo") ?: "",
                    descricao = document.getString("descricao") ?: "",
                    ordem = document.getLong("ordem")?.toInt() ?: 0
                )
            }
            onDataLoaded(missaoList)
        }
        .addOnFailureListener { exception ->
            // Adicione um tratamento de erro, como exibir uma mensagem ao usuário
        }
}

@Preview(showBackground = true)
@Composable
fun PreviewIconScreen() {
    val navController = rememberNavController()
    DesafioScreen(navController = navController)
}
