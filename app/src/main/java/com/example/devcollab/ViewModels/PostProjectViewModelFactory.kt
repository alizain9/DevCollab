package com.example.devcollab.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.devcollab.Database.Firestore.ProjectRepository


class PostProjectViewModelFactory(
    private val repo: ProjectRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostProjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PostProjectViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}