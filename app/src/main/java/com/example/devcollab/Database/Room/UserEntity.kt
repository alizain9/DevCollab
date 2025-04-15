package com.example.devcollab.Database.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

// UserEntity.kt
@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val uid: String,
    val username: String,
    val email: String,
    val about: String,
    val profession: String,
    val experience: String,
    val skills: List<String>,
    val profileImageUrl: String
)
