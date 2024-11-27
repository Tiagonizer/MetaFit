package com.example.loginscreenv3.network


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

// Modelo da requisição
data class GeminiRequest(val prompt: String)

// Modelo da resposta
data class GeminiResponse(val imageUrl: String)

interface GeminiApi {
    @Headers("Authorization: Bearer AIzaSyD5dLGUjWWuhWHDJ058Hsx4QvueOtHBS6w", "Content-Type: application/json")
    @POST("v1beta/models/gemini-1.5-flash-latest:generateContent")
    fun generateImage(@Body request: GeminiRequest): Call<GeminiResponse>

}
