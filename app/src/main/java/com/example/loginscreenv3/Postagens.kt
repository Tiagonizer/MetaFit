package com.example.loginscreenv3

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.File
import android.util.Base64
import android.util.Log
import androidx.compose.ui.graphics.graphicsLayer
import java.io.FileOutputStream

@Composable
fun Postagens(navController: NavHostController) {
    Scaffold { paddingValues ->
        PostagensScreenContent(modifier = Modifier.padding(paddingValues))
    }
}

suspend fun fetchPostagens(context: Context): List<Pair<Uri, String>> {
    val firestore = FirebaseFirestore.getInstance()
    val postagens = mutableListOf<Pair<Uri, String>>()

    try {
        val result = firestore.collection("postagem").get().await()
        for (document in result) {
            val imageBase64 = document.getString("imageBase64") ?: continue
            val timestamp = document.getString("data") ?: "Data desconhecida"

            // Decodifica a imagem Base64 e salva como arquivo local
            val imageData = Base64.decode(imageBase64, Base64.DEFAULT)
            val file = File(context.cacheDir, "${document.id}.jpg")
            FileOutputStream(file).use { it.write(imageData) }

            val uri = Uri.fromFile(file)
            postagens.add(uri to timestamp)
        }
    } catch (e: Exception) {
        Log.e("Firebase", "Erro ao buscar postagens: ${e.message}")
    }

    return postagens
}

@Composable
fun PostagensScreenContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val postagens = remember { mutableStateOf(emptyList<Pair<Uri, String>>()) }

    LaunchedEffect(Unit) {
        postagens.value = fetchPostagens(context)
    }

    if (postagens.value.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nenhuma postagem encontrada",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(items = postagens.value) { (uri, timestamp) ->
                PostagemItem(photoUri = uri, timestamp = timestamp)
            }
        }
    }
}

@Composable
fun PostagemItem(photoUri: Uri, timestamp: String) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Exibe a imagem com ajuste de rotação e proporção
            Image(
                painter = rememberAsyncImagePainter(model = photoUri),
                contentDescription = null,
                contentScale = ContentScale.Fit, // Ajusta para manter a proporção original
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Mantém a imagem quadrada
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Exibe o timestamp
            Text(
                text = "Feito na data: $timestamp",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Botão de compartilhar
            Button(
                onClick = {
                    compartilharPost(context, photoUri, timestamp)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Compartilhar")
            }
        }
    }
}

fun compartilharPost(context: Context, photoUri: Uri, timestamp: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, photoUri)
        putExtra(Intent.EXTRA_TEXT, "Confira esta postagem que fiz em $timestamp!")
    }
    context.startActivity(Intent.createChooser(intent, "Compartilhar postagem via:"))
}
