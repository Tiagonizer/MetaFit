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
    var ofensivas by remember { mutableStateOf(0) } // Contador de missões concluídas
    val firestore = FirebaseFirestore.getInstance()

    // Busca missões ordenadas pela ordem
    LaunchedEffect(Unit) {
        firestore.collection("missoes")
            .orderBy("ordem", Query.Direction.ASCENDING) // Ordenação crescente
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(context, "Erro ao carregar missões: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val fetchedMissions = snapshot.documents.map { doc ->
                        doc.id to doc.data!!
                    }
                    missoes = fetchedMissions
                    // Atualiza o contador de missões concluídas
                    ofensivas = fetchedMissions.count { it.second["completa"] as? Boolean == true }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text("Missões", fontSize = 30.sp)

        // Contador de missões concluídas
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Ofensivas:", fontSize = 18.sp)
            Text("$ofensivas", fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        }

        // Lista de missões
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(missoes) { index, (id, missionData) ->
                val titulo = missionData["titulo"] as? String ?: "Missão"
                val ordem = missionData["ordem"] as? Int ?: 0
                val completa = missionData["completa"] as? Boolean ?: false
                val desbloqueada = index == 0 || (missoes.getOrNull(index - 1)?.second?.get("completa") as? Boolean == true)

                Button(
                    onClick = {
                        if (desbloqueada) {
                            navController.navigate("teste/$id") // Passa o ID da missão
                        } else {
                            Toast.makeText(
                                context,
                                "Complete a missão anterior para desbloquear esta.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = desbloqueada, // Bloqueia o botão se a missão não estiver desbloqueada
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("$ordem - $titulo")
                        if (completa) {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_concluido), // Substitua pelo seu ícone
                                contentDescription = "Missão concluída",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewIconScreen() {
    val navController = rememberNavController()
    DesafioScreen(navController = navController)
}
