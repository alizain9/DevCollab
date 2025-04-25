package com.example.devcollab.Model

data class Applicants(
    val uid: String = "",
    val username: String = "",
    val profileImageUrl: String = "",
    val appliedProjects: List<String> = emptyList(),
)
