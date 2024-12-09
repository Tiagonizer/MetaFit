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
import android.util.Base64
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.firestore.Query
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.Matrix
import android.media.ExifInterface

fun saveImageToPostagem(
    firestore: FirebaseFirestore,
    missionId: String?,
    imageBase64: String,
    tituloMissao: String,
    apelidoUsuario: String,
    avatarUsuario: String,
    onComplete: (Boolean) -> Unit
) {
    val postagemData = hashMapOf(
        "missaoId" to missionId,
        "imageBase64" to imageBase64,
        "data" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
        "tituloMissao" to tituloMissao,
        "apelido" to apelidoUsuario,
        "avatar" to avatarUsuario
    )

    firestore.collection("postagem")
        .add(postagemData)
        .addOnSuccessListener { onComplete(true) }
        .addOnFailureListener { e ->
            Log.e("Firebase", "Erro ao salvar postagem: ${e.message}")
            onComplete(false)
        }
}

@Composable
fun Teste(navController: NavHostController, missaoId: String?) {
    var titulo by remember { mutableStateOf("Carregando...") }
    var descricao by remember { mutableStateOf("") }
    var ativa by remember { mutableStateOf(false) }
    var apelidoUsuario by remember { mutableStateOf("") }
    var avatarUsuario by remember { mutableStateOf("") }

    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    // Criando arquivo para a imagem
    val photoFile = createImageFile(context)
    val photoUri: Uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )

    // Lançador de câmera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val filePath = photoFile.absolutePath

            // Compacta e converte a imagem para Base64
            val imageBase64 = compressImage(filePath)
            if (imageBase64 != null) {
                // Salva a imagem na coleção "postagem"
                saveImageToPostagem(
                    firestore,
                    missaoId,
                    imageBase64,
                    titulo, // Garantir que o título seja passado corretamente
                    apelidoUsuario,
                    avatarUsuario
                ) { success ->
                    if (success) {
                        // Atualiza o status da missão
                        missaoId?.let {
                            updateMissionStatus(firestore, it) { missionUpdated ->
                                if (missionUpdated) {
                                    Toast.makeText(context, "Missão concluída!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                    navController.navigate("DesafioScreen")
                                } else {
                                    Toast.makeText(context, "Erro ao atualizar missão.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(context, "Erro ao salvar postagem.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Erro ao processar imagem.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Foto não capturada.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(missaoId) {
        missaoId?.let {
            try {
                val doc = firestore.collection("missoes").document(it).get().await()
                titulo = doc.getString("titulo") ?: "Missão"
                descricao = doc.getString("descricao") ?: "Sem descrição"
                ativa = doc.getBoolean("ativo") ?: false

                // Recupera apelido e avatar do usuário
                val userDoc = firestore.collection("usuarios").document("usuarioLogadoId").get().await()
                apelidoUsuario = userDoc.getString("apelido") ?: "Usuário"
                avatarUsuario = userDoc.getString("avatar") ?: ""
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
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                        putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    }
                    cameraLauncher.launch(cameraIntent)
                } else {
                    Toast.makeText(context, "Missão bloqueada. Complete as anteriores!", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = ativa,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Concluir Missão")
        }
    }
}

fun adjustImageOrientation(filePath: String, bitmap: Bitmap): Bitmap {
    val exif = ExifInterface(filePath)
    val orientation = exif.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_UNDEFINED
    )

    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
    }

    return Bitmap.createBitmap(
        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
    )
}

// Funções auxiliares
fun createImageFile(context: Context): File {
    val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile("JPEG_${System.currentTimeMillis()}_", ".jpg", storageDir)
}

fun compressImage(filePath: String): String? {
    val originalBitmap = BitmapFactory.decodeFile(filePath) ?: return null

    // Ajustar orientação
    val correctedBitmap = adjustImageOrientation(filePath, originalBitmap)

    // Reduzindo tamanho e qualidade
    val resizedBitmap = Bitmap.createScaledBitmap(correctedBitmap, 800, 800, true) // Reduz resolução
    val outputStream = ByteArrayOutputStream()
    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream) // Compactação JPEG

    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT) // Converte para Base64
}


fun updateMissionStatus(
    firestore: FirebaseFirestore,
    missionId: String,
    onComplete: (Boolean) -> Unit
) {
    firestore.collection("missoes").document(missionId)
        .update("completa", true)
        .addOnSuccessListener {
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
                            .addOnSuccessListener { onComplete(true) }
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
