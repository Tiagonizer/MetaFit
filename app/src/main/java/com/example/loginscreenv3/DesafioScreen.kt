package com.example.loginscreenv3

import android.util.Log
import retrofit2.Call
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
fun DesafioScreen(navController: NavHostController) {
    var missoes by remember { mutableStateOf<List<Missao>>(emptyList()) }

    // Carregar missões ao inicializar
    LaunchedEffect(Unit) {
        carregarMissoes { missaoList ->
            missoes = missaoList

            // Atualiza a URL da imagem de cada missão
            missaoList.forEach { missao ->
                fetchGeminiImage(missao) { imageUrl ->
                    // Atualiza a lista de missões ao receber a URL
                    missoes = missoes.map {
                        if (it == missao) it.copy(imageUrl = imageUrl) else it
                    }
                }
            }
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

            if (missoes.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
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
            Image(
                painter = rememberAsyncImagePainter(
                    model = missao.imageUrl.ifEmpty { "https://example.com/placeholder.png" },
                    placeholder = painterResource(R.drawable.icon_maca),
                    error = painterResource(R.drawable.icon_agua)
                ),
                contentDescription = "Imagem da Missão",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

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
    val ordem: Int = 0,
    var imageUrl: String = "",
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

// Função para buscar a URL da imagem da API Gemini
private fun fetchGeminiImage(missao: Missao, onImageFetched: (String) -> Unit) {
    val request = GeminiRequest(prompt = missao.titulo)
    RetrofitInstance.api.generateImage(request).enqueue(object : Callback<GeminiResponse> {
        override fun onResponse(call: Call<GeminiResponse>, response: Response<GeminiResponse>) {
            if (response.isSuccessful) {
                response.body()?.imageUrl?.let { url ->
                    onImageFetched(url) // Atualiza o estado ao receber a URL
                }
            } else {
                Log.e("GeminiAPI", "Erro na resposta da API: ${response.errorBody()?.string()}")
            }
        }

        override fun onFailure(call: Call<GeminiResponse>, t: Throwable) {
            Log.e("GeminiAPI", "Erro ao buscar imagem: ${t.localizedMessage}")
        }
    })
}

@Preview(showBackground = true)
@Composable
fun PreviewIconScreen() {
    val navController = rememberNavController()
    DesafioScreen(navController = navController)
}
