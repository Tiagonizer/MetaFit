package com.example.loginscreenv3

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class Usuario {

    lateinit var email: String
    lateinit var senha: String
    lateinit var primeiroNome: String
    lateinit var ultimoNome: String
    lateinit var apelido: String
    lateinit var idade: Number
    lateinit var altura: Number
    lateinit var peso: Number
    lateinit var genero:String

//
//    constructor(email: String, senha: String, primeiroNome: String, ultimoNome: String, apelido: String, altura: String, peso: String, idade: String) {
//        this.email = email
//        this.senha = senha
//        this.primeiroNome = primeiroNome
//        this.ultimoNome = ultimoNome
//        this.apelido = apelido
//        try {
//            this.altura = altura.toDouble()
//            this.peso = peso.toDouble()
//            this.idade = idade.toInt()
//        } catch (e: NumberFormatException) {
//            this.altura = 0.0
//            this.peso = 0.0
//            this.idade = 0
//        }
//    }

    val db = FirebaseFirestore.getInstance()

    fun cadastraUsuario(){

        var login= hashMapOf(
            "email" to email,
            "senha" to senha,
            "primeiroNome" to primeiroNome,
            "ultimoNome" to ultimoNome,
            "apelido" to apelido,
            "idade" to idade,
            "altura" to altura,
            "peso" to peso,
            "genero" to genero

        )

        db.collection("Logins").document(email).set(login).addOnCompleteListener{

        }.addOnFailureListener{

        }
    }

    fun validaUsuario(email: String, senha: String): Task<DocumentSnapshot> {

        return db.collection("Logins").document(email).get()


    }

}