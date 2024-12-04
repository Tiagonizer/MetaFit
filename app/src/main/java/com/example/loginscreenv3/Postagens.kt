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
import java.io.File

@Composable
fun Postagens(navController: NavHostController) {
    Scaffold { paddingValues ->
        PostagensScreenContent(modifier = Modifier.padding(paddingValues))
    }
}

fun loadPhotoMetadata(context: Context): List<Pair<String, String>> {
    val sharedPreferences = context.getSharedPreferences("Postagens", Context.MODE_PRIVATE)
    val savedData = sharedPreferences.getStringSet("photoData", emptySet()) ?: emptySet()
    return savedData.mapNotNull { data ->
        val parts = data.split("|")
        if (parts.size == 2) {
            val filePath = parts[0]
            if (File(filePath).exists()) Pair(filePath, parts[1]) else null
        } else null
    }
}

@Composable
fun PostagensScreenContent(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val photoMetadata = remember { mutableStateOf(emptyList<Pair<String, String>>()) }

    LaunchedEffect(Unit) {
        photoMetadata.value = loadPhotoMetadata(context)
    }

    if (photoMetadata.value.isEmpty()) {
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
            items(items = photoMetadata.value) { (filePath, timestamp) ->
                PostagemItem(photoPath = filePath, timestamp = timestamp)
            }
        }
    }
}

@Composable
fun PostagemItem(photoPath: String, timestamp: String) {
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
            // Exibe a imagem
            if (File(photoPath).exists()) {
                Image(
                    painter = rememberAsyncImagePainter(model = Uri.parse(photoPath)),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

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
                    compartilharPost(context, photoPath, timestamp)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(text = "Compartilhar")
            }
        }
    }
}

fun compartilharPost(context: Context, photoPath: String, timestamp: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/*"
        putExtra(Intent.EXTRA_STREAM, Uri.parse(photoPath))
        putExtra(Intent.EXTRA_TEXT, "Confira esta postagem que fiz em $timestamp!")
    }
    context.startActivity(Intent.createChooser(intent, "Compartilhar postagem via:"))
}
