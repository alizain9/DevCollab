package com.example.devcollab.Model

data class ChatMessage(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Any? = null
)

