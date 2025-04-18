package com.example.devcollab.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.devcollab.Database.Firestore.ProjectRepository
import com.example.devcollab.Model.Project
import com.google.firebase.Timestamp

class PostProjectViewModel(
    private val repo: ProjectRepository
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    fun postProject(
        ownerId: String,
        title: String,
        description: String,
        skills: List<String>,
        tags: List<String>,
        deadlineTs: Timestamp?,
    ) {
        _loading.value = true

        val project = Project(
            ownerId       = ownerId,
            title         = title,
            description   = description,
            requiredSkills= skills,
            tags           = tags,
            applicants    = emptyList(),
            selectedTeammate = null,
            deadline      = deadlineTs,
        )

        repo.addProject(project)
            .addOnSuccessListener {
                _loading.value = false
                _success.value = true
            }
            .addOnFailureListener {
                _loading.value = false
                _success.value = false
            }
    }
}