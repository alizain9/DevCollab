package com.example.devcollab.Model

import com.google.firebase.Timestamp


data class Project(
    val ownerId: String = "",
    val title: String = "",
    val description: String = "",
    val requiredSkills: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val applicants: List<String> = emptyList(),
    val selectedTeammate: String? = null,
    val deadline: Timestamp? = null,
    val isMyProject: Boolean = true,
)
