package com.example.loginscreenv3

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role.Companion.Switch
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun AdmimScreen(navController: NavController) {
    var showAddMissionOptions by remember { mutableStateOf(false) }
    var showDeleteMissionOptions by remember { mutableStateOf(false) }
    var showEditMissionOptions by remember { mutableStateOf(false) }
    var tituloMissao by remember { mutableStateOf("") }
    var descricaoMissao by remember { mutableStateOf("") }
    var ordemMissao by remember { mutableStateOf("") }
    var missoes by remember { mutableStateOf(listOf<Pair<String, Map<String, Any>>>()) }
    var selectedMissionId by remember { mutableStateOf<String?>(null) }
    var ativo by remember { mutableStateOf(true) }
    var completa by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    // Atualiza lista de missões sempre que uma ação de edição ou exclusão é acionada
    LaunchedEffect(showDeleteMissionOptions || showEditMissionOptions) {
        if (showDeleteMissionOptions || showEditMissionOptions) {
            firestore.collection("missoes")
                .orderBy("ordem", Query.Direction.ASCENDING) // Ordena por ordem crescente
                .get()
                .addOnSuccessListener { documents ->
                    val fetchedMissions = documents.map { doc ->
                        doc.id to doc.data
                    }
                    missoes = fetchedMissions
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Erro ao buscar missões: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título
        Text("Administrador", fontSize = 30.sp, modifier = Modifier.padding(bottom = 24.dp))

        // Botão para Incluir Missão
        Button(
            onClick = { showAddMissionOptions = !showAddMissionOptions },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Incluir Missão")
        }

        // Seção para adicionar missão
        if (showAddMissionOptions) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = tituloMissao,
                onValueChange = { tituloMissao = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = descricaoMissao,
                onValueChange = { descricaoMissao = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ordemMissao,
                onValueChange = { ordemMissao = it },
                label = { Text("Ordem") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de seleção Ativo/Block
            Row(
                verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = ativo,
                    onCheckedChange = { ativo = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Green,
                        uncheckedThumbColor = Color.Red
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))

                Text(text = "Status: ", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                Text(
                    text = if (ativo) "Ativo" else "Block",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Switch para "Missão Completa"
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = completa,
                    onCheckedChange = { completa = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Green,
                        uncheckedThumbColor = Color.Red
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Completa: ${if (completa) "Sim" else "Não"}", fontSize = 16.sp)
            }

            // Botão Salvar Missão
            Button(
                onClick = {
                    if (tituloMissao.isNotBlank() && descricaoMissao.isNotBlank() && ordemMissao.isNotBlank()) {
                        val missao = hashMapOf(
                            "titulo" to tituloMissao,
                            "descricao" to descricaoMissao,
                            "ordem" to ordemMissao.toInt(),
                            "ativo" to ativo, // Adiciona o status no Firebase
                            "completa" to completa
                        )

                        firestore.collection("missoes")
                            .add(missao)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Missão salva com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                tituloMissao = ""
                                descricaoMissao = ""
                                ordemMissao = ""
                                ativo = true // Reseta o estado do switch
                                showAddMissionOptions = false
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Erro ao salvar: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Missão")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botão para Editar Missão
        Button(
            onClick = { showEditMissionOptions = !showEditMissionOptions },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar Missão")
        }

        // Seção de Edição de Missão
        if (showEditMissionOptions) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Selecione uma missão para editar:", fontSize = 18.sp)

            Box(modifier = Modifier.fillMaxHeight(0.2f)) {
                LazyColumn {
                    items(missoes) { (id, missionData) ->
                        val titulo = missionData["titulo"] as? String ?: "Sem título"
                        val ordem = missionData["ordem"] as? Int ?: 0

                        Text(
                            text = "$ordem - $titulo",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp)
                                .clickable {
                                    selectedMissionId = id
                                    tituloMissao = missionData["titulo"] as? String ?: ""
                                    descricaoMissao = missionData["descricao"] as? String ?: ""
                                    ordemMissao = (missionData["ordem"] as? Int)?.toString() ?: ""
                                    ativo = missionData["ativo"] as? Boolean ?: true
                                    completa = missionData["completa"] as? Boolean ?: false
                                }
                                .background(
                                    if (selectedMissionId == id) Color.LightGray
                                    else Color.Transparent
                                )
                                .padding(2.dp)
                        )
                    }
                }
            }

            // Campos para edição da missão selecionada
            if (selectedMissionId != null) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = tituloMissao,
                    onValueChange = { tituloMissao = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descricaoMissao,
                    onValueChange = { descricaoMissao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = ordemMissao,
                    onValueChange = { ordemMissao = it },
                    label = { Text("Ordem") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Campo de seleção Ativo/Block
                Row(
                    verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = ativo,
                        onCheckedChange = { ativo = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Green,
                            uncheckedThumbColor = Color.Red
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(text = "Status: ", fontSize = 16.sp, modifier = Modifier.padding(end = 8.dp))
                    Text(
                        text = if (ativo) "Ativo" else "Block",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Switch para "Missão Completa"
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = completa,
                        onCheckedChange = { completa = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Green,
                            uncheckedThumbColor = Color.Red
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Completa: ${if (completa) "Sim" else "Não"}",
                        fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botão para salvar alterações
                Button(
                    onClick = {
                        if (tituloMissao.isNotBlank() && descricaoMissao.isNotBlank() && ordemMissao.isNotBlank()) {
                            val updatedMission = hashMapOf(
                                "titulo" to tituloMissao,
                                "descricao" to descricaoMissao,
                                "ordem" to ordemMissao.toInt(),
                                "ativo" to ativo,
                                "completa" to completa
                            )
                            selectedMissionId?.let { id ->
                                firestore.collection("missoes").document(id)
                                    .update(updatedMission as Map<String, Any>)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "Missão atualizada com sucesso!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        // Atualiza localmente
                                        missoes = missoes.map {
                                            if (it.first == id) id to updatedMission
                                            else it
                                        }
                                        selectedMissionId = null
                                        tituloMissao = ""
                                        descricaoMissao = ""
                                        ordemMissao = ""
                                        showEditMissionOptions = false
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            context,
                                            "Erro ao atualizar missão: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        } else {
                            Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Salvar Alterações")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botão para Excluir Missão
        Button(
            onClick = { showDeleteMissionOptions = !showDeleteMissionOptions },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Excluir Missão")
        }

        // Seção para excluir missão
        if (showDeleteMissionOptions) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Selecione uma missão para excluir:", fontSize = 18.sp)

            // Lista de missões com barra de rolagem
            Box(modifier = Modifier.fillMaxHeight(0.6f)) { // 60% da altura da tela
                LazyColumn {
                    items(missoes) { (id, missionData) ->
                        val titulo = missionData["titulo"] as? String ?: "Sem título"
                        val ordem = missionData["ordem"] as? Int ?: 0
                        Text(
                            text = "$ordem - $titulo",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { selectedMissionId = id }
                                .background(
                                    if (selectedMissionId == id) Color.LightGray
                                    else Color.Transparent
                                )
                                .padding(8.dp)
                        )
                    }
                }
            }

            // Botão para confirmar exclusão
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    selectedMissionId?.let { id ->
                        firestore.collection("missoes").document(id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Missão excluída com sucesso!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Remove a missão excluída da lista local
                                missoes = missoes.filterNot { it.first == id }
                                selectedMissionId = null
                                showDeleteMissionOptions = false
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Erro ao excluir missão: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } ?: Toast.makeText(context, "Selecione uma missão para excluir!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Excluir Missão Selecionada")
            }
    }
}
}