package com.example.devcollab.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.devcollab.Database.Firestore.ProjectRepository
import com.example.devcollab.Model.Applicants
import com.example.devcollab.Model.Project
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp

class PostProjectViewModel(
    private val repo: ProjectRepository
) : ViewModel() {

    // --------------------------
    // LiveData Properties
    // --------------------------
    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _projects = MutableLiveData<List<Project>>()
    val projects: LiveData<List<Project>> = _projects

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    // --------------------------
    // Post a New Project
    // --------------------------
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
            ownerId = ownerId,
            title = title,
            description = description,
            requiredSkills = skills,
            tags = tags,
            applicants = emptyList(),
            selectedTeammate = null,
            deadline = deadlineTs,
            timestamp = Timestamp.now()
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

    // --------------------------
    // Fetch All Projects
    // --------------------------
    fun fetchProjects() {
        _loading.value = true

        repo.fetchProjects()
            .addOnSuccessListener { querySnapshot ->
                val projectList = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Project::class.java)?.copy(id = document.id)
                }
                _projects.value = projectList
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
            }
    }

    // --------------------------
    // Fetch User-Specific Projects
    // --------------------------
    fun fetchUserProjects(ownerId: String) {
        _loading.value = true

        repo.fetchUserProjects(ownerId)
            .addOnSuccessListener { querySnapshot ->
                val projectList = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Project::class.java)?.copy(id = document.id)
                }
                _projects.value = projectList
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
            }
    }

    // --------------------------
    // Apply to a Project
    // --------------------------
    fun applyToProject(projectId: String, applicantUid: String) {
        _loading.value = true

        repo.addApplicantToProject(projectId, applicantUid)
            .addOnSuccessListener {
                _loading.value = false
                _success.value = true
            }
            .addOnFailureListener {
                _loading.value = false
                _success.value = false
            }
    }

    // --------------------------
    // Check if User Has Applied
    // --------------------------
    fun checkIfUserApplied(projectId: String, applicantUid: String): Task<Boolean> {
        return repo.hasUserApplied(projectId, applicantUid)
    }

    // --------------------------
    // Fetch Applicants for a Project
    // --------------------------
    fun fetchApplicants(projectId: String): LiveData<List<Applicants>> {
        _loading.value = true
        val _applicants = MutableLiveData<List<Applicants>>()

        repo.fetchApplicants(projectId)
            .addOnSuccessListener { applicantUids ->
                if (applicantUids.isEmpty()) {
                    _loading.value = false
                    _applicants.value = emptyList()
                    return@addOnSuccessListener
                }

                val applicants = mutableListOf<Applicants>()
                val tasks = applicantUids.map { uid ->
                    repo.fetchUser(uid).continueWith { task ->
                        if (task.isSuccessful && task.result != null) {
                            applicants.add(task.result!!)
                        }
                    }
                }

                Tasks.whenAllComplete(tasks).addOnCompleteListener {
                    _loading.value = false
                    _applicants.value = applicants
                }
            }
            .addOnFailureListener {
                _loading.value = false
                _applicants.value = emptyList()
            }

        return _applicants
    }
}