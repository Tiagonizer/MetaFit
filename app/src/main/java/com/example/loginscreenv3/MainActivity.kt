package com.example.loginscreenv3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.loginscreenv3.ui.theme.LoginScreenV3Theme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.tasks.await


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}
@Composable
fun MyApp() {
    val navController = rememberNavController()

    // Obtenha a rota atual
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    // Scaffold controlando a BottomBar
    Scaffold(
        bottomBar = {
            // Exibe a BottomBar apenas nas telas "DesafioScreen" e "Postagens"
            if (currentRoute == "DesafioScreen" || currentRoute == "Postagens") {
                BottomBar(navController = navController)
            }
        }
    ) {
        NavHost(navController = navController, startDestination = "Login") {
            composable("Login") {
                LoginScreen(navController = navController)
            }

            composable("CadastroScreen") {
                CadastroScreen(navController = navController)
            }

            composable("DesafioScreen") {
                DesafioScreen(navController = navController)
            }

            composable("DisplayAvatarScreen") {
                DisplayAvatarScreen(navController = navController)
            }

            composable(
                "teste/{missaoId}", // Define corretamente o formato da rota com o parâmetro
                arguments = listOf(navArgument("missaoId") { type = NavType.StringType })
            ) { backStackEntry ->
                val missaoId = backStackEntry.arguments?.getString("missaoId")
                Teste(navController = navController, missaoId = missaoId) // Passa o parâmetro para a tela
            }

            composable("Postagens") {
                Postagens(navController = navController)
            }

            composable("RelatorioScreen") {
                RelatorioScreen(navController = navController)
            }

            composable("GeminiScreen") {
                GeminiScreen(navController = navController)
            }

            composable("AdmimScreen") {
                AdmimScreen(navController = navController)
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "missaoScreen") {
        composable("missaoScreen") { DesafioScreen(navController) }
        composable(
            "teste/{missaoId}",
            arguments = listOf(navArgument("missaoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val missaoId = backStackEntry.arguments?.getString("missaoId")
            Teste(navController, missaoId)
        }
    }
}
@Composable
fun BottomBar(navController: NavController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Desafios") },
            label = { Text("Desafios") },
            selected = false,
            onClick = {
                navController.navigate("DesafioScreen") // Direciona para a tela DesafioScreen
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, contentDescription = "Postagens") },
            label = { Text("Postagens") },
            selected = false,
            onClick = {
                navController.navigate("Postagens") // Direciona para a tela Postagens
            }
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun LoginScreen(navController: NavController) {
//
//    val context = LocalContext.current
//
//    val usuario = "exemplo@gmail.com"
//    val senha = "123456"
//
//    var textFieldUsuario by remember { mutableStateOf("") }
//    var textFieldSenha by remember { mutableStateOf("") }
//
//    Column(
//        modifier = Modifier
//            .padding(16.dp)
//            .fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                "\"App\"",
//                fontSize = 32.sp,
//                fontWeight = FontWeight.Bold,
//            )
//        }
//
//        Spacer(modifier = Modifier.height(66.dp))
//
//        Box(modifier = Modifier
//            .fillMaxWidth()
//            .padding()) {
//            OutlinedTextField(
//                value = textFieldUsuario,
//                onValueChange = { textFieldUsuario = it },
//                label = { Text("Usuário") },
//                modifier = Modifier.fillMaxWidth()
//            )
//        }
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        Box(modifier = Modifier
//            .fillMaxWidth()
//            .padding()) {
//            OutlinedTextField(
//                value = textFieldSenha,
//                onValueChange = { textFieldSenha = it },
//                label = { Text("Senha") },
//                modifier = Modifier.fillMaxWidth(),
//                visualTransformation = PasswordVisualTransformation()
//            )
//        }
//
//        Spacer(modifier = Modifier.height(38.dp))
//
//        Row(modifier = Modifier.padding(10.dp),verticalAlignment = Alignment.CenterVertically) {
//            //Box(modifier = Modifier
//            //    .fillMaxWidth()
//            //    .padding(10.dp),
//            //    contentAlignment = Alignment.Center) {
//                Button(modifier = Modifier.padding(10.dp), onClick = {
//
//                    if (textFieldUsuario != usuario) {
//                        Toast.makeText(context, "Usuario incorreto.", Toast.LENGTH_SHORT).show()
//                        return@Button
//                    }
//
//                    if (textFieldSenha != senha) {
//                        Toast.makeText(context, "Senha Incorreta", Toast.LENGTH_SHORT).show()
//                        return@Button
//                    }
//
//                    Toast.makeText(context, "Login bem sucedido.", Toast.LENGTH_SHORT).show()
//
//
//                if (textFieldUsuario==usuario){
//                    //Toast.makeText(context, "Usuario correto.", Toast.LENGTH_SHORT).show()
//                    if (textFieldSenha == senha) {
//                        Toast.makeText(context, "Login bem sucedido.", Toast.LENGTH_SHORT).show()
//                    } else {
//                        Toast.makeText(context, "Login falhou.", Toast.LENGTH_SHORT).show()
//                    }
//              }else{
//                    Toast.makeText(context, "Usuario incorreto.", Toast.LENGTH_SHORT).show()
//                }
//
//                }) {
//                    Text(text = "Entrar")
//                }
//           //}
//
//            //Box(modifier = Modifier
//            //    .fillMaxWidth()
//           //    .padding(10.dp),
//            //    contentAlignment = Alignment.Center){
//                Button(modifier = Modifier.padding(10.dp),onClick = {
//                    val isClicked = true
//                    if (isClicked) {
//                        navController.navigate("CadastroScreen")
//                    }
//
//                }) {
//                    Text(text = "Cadastrar-se")
//                }
//            //}
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    MyApp()
}

