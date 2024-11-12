package com.example.loginscreenv3



import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun RelatorioScreen(navController: NavHostController) {
    // Tela principal com coluna
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Ícone do Gemini (Substitua com o recurso adequado)
        Image(
            painter = painterResource(id = R.drawable.icon_relatorio), // Substitua pelo ícone real
            contentDescription = "Relatorio Icon",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(100.dp)
                .padding(bottom = 1.dp) // Espaço entre o ícone e o texto
        )

        // Texto com dicas geradas pelo Gemini
        Text(
            text = "Lorem ipsum dolor sit amet," +
                    " consectetur adipiscing elit." +
                    " Sed condimentum et nisl nec placerat." +
                    " Sed tempus sem non dolor consectetur," +
                    " quis laoreet eros porttitor. Sed aliquam," +
                    " erat ac ultricies tincidunt, nulla ex convallis neque," +
                    " fermentum hendrerit turpis urna sit amet velit.", // Substitua pelo texto gerado
            fontSize = 18.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRelatorioScreen() {
    val navController = rememberNavController()
    RelatorioScreen(navController = navController)
}
