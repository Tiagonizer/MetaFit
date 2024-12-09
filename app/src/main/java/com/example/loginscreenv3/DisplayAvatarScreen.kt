package com.example.loginscreenv3

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



@Composable
fun DisplayAvatarScreen(navController: NavController, email: String?) {
    val avatar1 = R.drawable.avatar1
    val avatar2 = R.drawable.avatar2
    val avatar3 = R.drawable.avatar3
    val avatar4 = R.drawable.avatar4
    val avatar5 = R.drawable.avatar5
    val avatar6 = R.drawable.avatar6

    val avatarList = listOf(
        avatar1 to "avatar1.png", avatar2 to "avatar2.png",
        avatar3 to "avatar3.png", avatar4 to "avatar4.png",
        avatar5 to "avatar5.png", avatar6 to "avatar6.png"
    )

    var selectedAvatar by remember { mutableStateOf<Pair<Int, String>?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Escolha seu Avatar", modifier = Modifier.padding(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            avatarList.chunked(3).forEach { row ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEach { (avatarId, avatarName) ->
                        Image(
                            painter = painterResource(id = avatarId),
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (avatarId == selectedAvatar?.first) 4.dp else 0.dp,
                                    color = if (avatarId == selectedAvatar?.first) Color.Blue else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable {
                                    selectedAvatar = avatarId to avatarName
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                selectedAvatar?.let { (_, avatarName) ->
                    scope.launch(Dispatchers.IO) {
                        val db = FirebaseFirestore.getInstance()

                        if (!email.isNullOrBlank()) {
                            db.collection("Logins")
                                .document(email)
                                .update("avatar", avatarName)
                                .addOnSuccessListener {
                                    scope.launch(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Avatar salvo com sucesso!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate("Login")
                                    }
                                }
                                .addOnFailureListener { e ->
                                    scope.launch(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "Erro ao salvar avatar: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            scope.launch(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Erro: E-mail inválido!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            },
            enabled = selectedAvatar != null
        ) {
            Text(text = "Selecionar Avatar")
        }
    }
}