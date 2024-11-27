package com.example.loginscreenv3

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current

    var textFieldUsuario by remember { mutableStateOf("") }
    var textFieldSenha by remember { mutableStateOf("") }
    var check by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Conteúdo principal
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título do aplicativo
            Text(
                "MetaFit",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(66.dp))

            // Campo de texto para o usuário
            OutlinedTextField(
                value = textFieldUsuario,
                onValueChange = { textFieldUsuario = it },
                label = { Text("Usuário") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo de texto para a senha
            OutlinedTextField(
                value = textFieldSenha,
                onValueChange = { textFieldSenha = it },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            // Opção "Lembrar usuário"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = check, onCheckedChange = { check = it })
                Text(text = "Lembrar usuário", fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(38.dp))

            // Botões "Entrar" e "Cadastrar-se"
            Row(modifier = Modifier.padding(10.dp)) {
                Button(onClick = { navController.navigate("DesafioScreen") }) {
                    Text(text = "Entrar")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(onClick = { navController.navigate("CadastroScreen") }) {
                    Text(text = "Cadastrar-se")
                }
            }
        }

        // Ícone "icon_admim" no canto inferior direito
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            var mostrarDialogo by remember { mutableStateOf(false) }
            Image(
                painter = painterResource(id = R.drawable.icon_admim),  // Certifique-se de que o ícone esteja no diretório res/drawable
                contentDescription = "Icon Admim",
                modifier = Modifier
                    .size(50.dp)
                    .clickable { mostrarDialogo = true },
                contentScale = ContentScale.Crop
            )
            if (mostrarDialogo) {
                exibirDialogoSenha(
                    context = context,
                    navController = navController,
                    onDismiss = { mostrarDialogo = false }
                )
            }
        }
    }
}

@Composable
fun exibirDialogoSenha(
    context: Context,
    navController: NavController,
    onDismiss: () -> Unit
) {
    var senhaDigitada by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Acesso Restrito") },
        text = {
            Column {
                Text(text = "Digite a senha de administrador:")
                OutlinedTextField(
                    value = senhaDigitada,
                    onValueChange = { senhaDigitada = it },
                    label = { Text("Senha") },
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                validarSenha(context, senhaDigitada, navController)
                onDismiss() // Fechar o diálogo após validar
            }) {
                Text(text = "Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancelar")
            }
        }
    )
}


fun validarSenha(context: Context, senhaDigitada: String, navController: NavController) {
    val db = FirebaseFirestore.getInstance()

    db.collection("Admim")
        .document("senhaDoc")
        .get()
        .addOnSuccessListener { document ->
            val senhaCorreta = document?.getString("senha")
            if (!senhaCorreta.isNullOrEmpty()) {
                if (senhaDigitada == senhaCorreta) {
                    Toast.makeText(context, "Acesso autorizado!", Toast.LENGTH_SHORT).show()
                    navController.navigate("AdmimScreen")
                } else {
                    Toast.makeText(context, "Senha incorreta.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Senha não configurada.", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, "Erro ao verificar a senha: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
        }

}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(rememberNavController())
}
