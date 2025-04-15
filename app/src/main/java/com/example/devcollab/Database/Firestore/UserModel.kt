package com.example.devcollab.Database.Firestore

data class UserModel(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val about: String = "",
    val profession: String = "",
    val experience: String = "",
    val skills: List<String> = listOf(),
    val profileImageUrl: String = ""
)