package com.example.loginscreenv3

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Cadastro(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var primeiroNomeTextField by remember { mutableStateOf("") }
    var ultimoNomeTextField by remember { mutableStateOf("") }
    var apelidoTextField by remember { mutableStateOf("") }
    var emailTextField by remember { mutableStateOf("") }
    var alturaTextField by remember { mutableStateOf("") }
    var pesoTextField by remember { mutableStateOf("") }
    var idadeTextField by remember { mutableStateOf("") }
    var senhaTextField by remember { mutableStateOf("") }
    var senhaConfirmTextField by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Cadastro:", fontSize = 30.sp, modifier = Modifier.padding(10.dp))

        OutlinedTextField(
            value = emailTextField,
            onValueChange = { emailTextField = it },
            label = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = senhaTextField,
            onValueChange = { senhaTextField = it },
            label = { Text("Senha") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = senhaConfirmTextField,
            onValueChange = { senhaConfirmTextField = it },
            label = { Text("Confirma senha") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            visualTransformation = PasswordVisualTransformation()
        )
        OutlinedTextField(
            value = primeiroNomeTextField,
            onValueChange = { primeiroNomeTextField = it },
            label = { Text("Primeiro nome") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = ultimoNomeTextField,
            onValueChange = { ultimoNomeTextField = it },
            label = { Text("Ultimo nome") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = apelidoTextField,
            onValueChange = { apelidoTextField = it },
            label = { Text("Apelido") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = idadeTextField,
            onValueChange = { idadeTextField = it },
            label = { Text("Idade") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = alturaTextField,
            onValueChange = { alturaTextField = it },
            label = { Text("Altura") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        OutlinedTextField(
            value = pesoTextField,
            onValueChange = { pesoTextField = it },
            label = { Text("Peso") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Button(
            onClick = {
                if (senhaTextField != senhaConfirmTextField) {
                    Toast.makeText(context, "As senhas não coincidem.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (emailTextField.isBlank() || senhaTextField.isBlank() || primeiroNomeTextField.isBlank()) {
                    Toast.makeText(context, "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val usuarioData = mapOf(
                    "primeiroNome" to primeiroNomeTextField,
                    "ultimoNome" to ultimoNomeTextField,
                    "apelido" to apelidoTextField,
                    "email" to emailTextField,
                    "altura" to alturaTextField.toDoubleOrNull(),
                    "peso" to pesoTextField.toDoubleOrNull(),
                    "idade" to idadeTextField.toIntOrNull()
                )

                scope.launch(Dispatchers.IO) {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("Logins")
                        .document(emailTextField)
                        .set(usuarioData)
                        .addOnSuccessListener {
                            scope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                navController.navigate("displayAvatarScreen/${emailTextField}")
                            }
                        }
                        .addOnFailureListener { e ->
                            scope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Erro: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Cadastrar")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CadastroPreview() {
    Cadastro(navController = rememberNavController())
}
