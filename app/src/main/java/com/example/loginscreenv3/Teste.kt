package com.example.loginscreenv3

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.Query
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Teste(navController: NavHostController, missaoId: String?) {
    var titulo by remember { mutableStateOf("Carregando...") }
    var descricao by remember { mutableStateOf("") }
    var ativa by remember { mutableStateOf(false) } // Indica se a missão está ativa
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // Criando arquivo para a imagem
    val photoFile = createImageFile(context)
    val photoUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )

    // Lançadores para câmera e permissão
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val filePath = photoFile.absolutePath
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

            savePhotoMetadata(context, filePath, timeStamp)

            missaoId?.let {
                updateMissionStatus(firestore, it) { success ->
                    if (success) {
                        Toast.makeText(context, "Missão concluída!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                        navController.navigate("DesafioScreen")
                    } else {
                        Toast.makeText(context, "Erro ao atualizar missão.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(context, "Foto não capturada.", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            }
            cameraLauncher.launch(cameraIntent)
        } else {
            Toast.makeText(context, "Permissão de câmera necessária.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(missaoId) {
        missaoId?.let {
            try {
                val doc = firestore.collection("missoes").document(it).get().await()
                titulo = doc.getString("titulo") ?: "Missão"
                descricao = doc.getString("descricao") ?: "Sem descrição"
                ativa = doc.getBoolean("ativo") ?: false
            } catch (e: Exception) {
                Log.e("Firebase", "Erro ao carregar missão: ${e.message}")
                titulo = "Erro ao carregar missão"
                descricao = ""
                ativa = false
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
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = descricao,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (ativa) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        }
                        cameraLauncher.launch(cameraIntent)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                } else {
                    Toast.makeText(context, "Missão bloqueada. Complete as anteriores!", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = ativa, // Botão habilitado apenas se a missão estiver ativa
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Concluir Missão")
        }
    }
}

// Funções auxiliares (mantidas as mesmas)
fun savePhotoMetadata(context: Context, filePath: String, timestamp: String) {
    val sharedPreferences = context.getSharedPreferences("Postagens", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val existingData = sharedPreferences.getStringSet("photoData", mutableSetOf()) ?: mutableSetOf()
    existingData.add("$filePath|$timestamp")
    editor.putStringSet("photoData", existingData)
    editor.apply()
}

fun createImageFile(context: Context): File {
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${System.currentTimeMillis()}_", ".jpg", storageDir)
}

fun updateMissionStatus(
    firestore: FirebaseFirestore,
    missionId: String,
    onComplete: (Boolean) -> Unit
) {
    // Atualiza a missão atual para "completa"
    firestore.collection("missoes").document(missionId)
        .update("completa", true)
        .addOnSuccessListener {
            // Desbloqueia a próxima missão
            firestore.collection("missoes")
                .orderBy("ordem", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { result ->
                    val missoes = result.documents
                    val currentMissionIndex = missoes.indexOfFirst { it.id == missionId }

                    if (currentMissionIndex != -1 && currentMissionIndex < missoes.size - 1) {
                        val nextMission = missoes[currentMissionIndex + 1]
                        firestore.collection("missoes").document(nextMission.id)
                            .update("ativo", true)
                            .addOnSuccessListener {
                                onComplete(true)
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firebase", "Erro ao desbloquear próxima missão: ${e.message}")
                                onComplete(false)
                            }
                    } else {
                        onComplete(true)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Erro ao buscar missões: ${e.message}")
                    onComplete(false)
                }
        }
        .addOnFailureListener { e ->
            Log.e("Firebase", "Erro ao atualizar missão atual: ${e.message}")
            onComplete(false)
        }
}


