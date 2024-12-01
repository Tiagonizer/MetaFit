package com.example.loginscreenv3

import android.util.Log
import android.widget.Toast
import retrofit2.Call
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.loginscreenv3.network.GeminiRequest
import com.example.loginscreenv3.network.GeminiResponse
import com.example.loginscreenv3.network.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import retrofit2.Callback
import retrofit2.Response


@Composable
fun DesafioScreen(navController: NavController) {
    val context = LocalContext.current
    var missoes by remember { mutableStateOf(listOf<Pair<String, Map<String, Any>>>()) }
    var missaoAtual by remember { mutableStateOf(0) } // Define a missão desbloqueada
    val firestore = FirebaseFirestore.getInstance()

    // Busca missões ordenadas pela ordem
    LaunchedEffect(Unit) {
        firestore.collection("missoes")
            .orderBy("ordem", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val fetchedMissions = documents.map { doc -> doc.id to doc.data }
                missoes = fetchedMissions
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Erro ao carregar missões: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Missões", fontSize = 30.sp)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(missoes) { index, (id, missionData) ->
                val titulo = missionData["titulo"] as? String ?: "Missão"
                val ordem = missionData["ordem"] as? Int ?: 0

                Button(
                    onClick = {
                        if (index <= missaoAtual) {
                            navController.navigate("teste/${id}") // Passa o ID da missão
                        } else {
                            Toast.makeText(
                                context,
                                "Complete as missões anteriores para desbloquear esta.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = index <= missaoAtual, // Bloqueia missões futuras
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "$ordem - $titulo")
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
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = missao.titulo,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Imagem estática substituindo a funcionalidade de imagens dinâmicas
            Image(
                painter = painterResource(R.drawable.icon_maca), // Use um ícone estático
                contentDescription = "Imagem da Missão",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )
        }
    }
}

// Modelo de dados da missão
data class Missao(
    val titulo: String = "",
    val descricao: String = "",
    val ordem: Int = 0,
    var desbloqueada: Boolean = false, // Indica se a missão está desbloqueada
    var concluida: Boolean = false // Indica se a missão foi concluída
)

// Função para carregar missões do Firestore
fun carregarMissoes(onDataLoaded: (List<Missao>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("missoes")
        .orderBy("ordem", Query.Direction.ASCENDING)
        .get()
        .addOnSuccessListener { result ->
            val missaoList = result.mapIndexed { index, document ->
                val titulo = document.getString("titulo") ?: ""
                val descricao = document.getString("descricao") ?: ""
                val ordem = document.getLong("ordem")?.toInt() ?: 0
                Missao(
                    titulo = titulo,
                    descricao = descricao,
                    ordem = ordem,
                    desbloqueada = index == 0 // Apenas a primeira missão é desbloqueada inicialmente
                )
            }

            onDataLoaded(missaoList)
        }
        .addOnFailureListener { exception ->
            Log.e("Firestore", "Erro ao carregar missões: ${exception.localizedMessage}")
        }
}

@Preview(showBackground = true)
@Composable
fun PreviewIconScreen() {
    val navController = rememberNavController()
    DesafioScreen(navController = navController)
}
