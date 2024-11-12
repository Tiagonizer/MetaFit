package com.example.loginscreenv3

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


@Composable
fun Teste(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Não esqueça da sua meta de beber água!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(70.dp))

        Image(
            painter = painterResource(id = R.drawable.desafio_image),
            contentDescription = "Imagem do Desafio",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(70.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                // Ação para "Postar um Desafio"
            }
        ) {
            Text(text = "Postar um Desafio")
        }

        Spacer(modifier = Modifier.height(16.dp))  // Espaço entre o botão e o ícone

        // Adiciona o ícone "icon_gemini" abaixo do botão
        Image(
            painter = painterResource(id = R.drawable.icon_gemini),  // Use o ícone correto aqui
            contentDescription = "Icon Gemini",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(100.dp)
                .clickable {
                    // Navega para a tela GeminiScreen
                    navController.navigate("geminiScreen")
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TesteScreenPreview() {
    val navController = rememberNavController()  // Simula o NavController
    Teste(navController = navController)  // Chama a função principal com o controlador
}
