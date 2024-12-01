package com.example.loginscreenv3

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.tasks.await


@Composable
fun Teste(navController: NavHostController, missaoId: String?) {
    var titulo by remember { mutableStateOf("Carregando...") }
    var descricao by remember { mutableStateOf("") }
    var proximaMissaoId by remember { mutableStateOf<String?>(null) } // ID da próxima missão
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // Busca detalhes da missão de forma assíncrona
    LaunchedEffect(missaoId) {
        missaoId?.let {
            try {
                val doc = firestore.collection("missoes").document(it).get().await()
                titulo = doc.getString("titulo") ?: "Missão"
                descricao = doc.getString("descricao") ?: "Sem descrição"
                proximaMissaoId = doc.getString("proximaMissao") // Obtem o ID da próxima missão
            } catch (e: Exception) {
                titulo = "Erro ao carregar missão"
                descricao = ""
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titulo,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = descricao,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                missaoId?.let { id ->
                    // Atualiza o status da missão no Firestore
                    firestore.collection("missoes").document(id)
                        .update("completada", true)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Missão concluída!", Toast.LENGTH_SHORT).show()

                            // Verifica se há uma próxima missão
                            proximaMissaoId?.let { proximaId ->
                                navController.navigate("teste/$proximaId") // Navega para a próxima missão
                            } ?: run {
                                Toast.makeText(context, "Parabéns! Todas as missões concluídas.", Toast.LENGTH_SHORT).show()
                                navController.navigateUp() // Retorna à    umlista de missões
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Erro ao atualizar missão.", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Concluir Missão")
        }
    }
}
