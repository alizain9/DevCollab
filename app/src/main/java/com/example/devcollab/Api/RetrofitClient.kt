package com.example.devcollab.Api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {

    const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val json = Json { ignoreUnknownKeys = true }

    // 👇 Retrofit instance with custom client
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val geminiApi: GeminiApiService = retrofit.create(GeminiApiService::class.java)
}
