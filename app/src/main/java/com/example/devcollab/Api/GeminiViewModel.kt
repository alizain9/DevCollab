package com.example.devcollab.Api

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devcollab.Api.RetrofitClient.geminiApi
import kotlinx.coroutines.launch
import android.util.Log

class GeminiViewModel : ViewModel() {
    private val apiKey = "AIzaSyC3O1WCviXsVpy_YUlPmPHaBuZfGErDY2Y"

    fun generateText(
        prompt: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val request = GeminiRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                )
                Log.d("GeminiViewModel", "Sending prompt: $prompt")
                val response = geminiApi.generateContent(apiKey, request = request)
                val generatedText = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "No response generated"
                onSuccess(generatedText)
            } catch (e: retrofit2.HttpException) {
                    Log.e("GeminiViewModel", "HTTP error: ${e.code()}")
                    onError("HTTP error: ${e.code()}")
            } catch (e: Exception) {
                Log.e("GeminiViewModel", "Unexpected error: ${e.message}", e)
                onError("Unexpected error: ${e.message}")
            }
        }
    }
}
