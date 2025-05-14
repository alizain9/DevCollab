package com.example.devcollab.ViewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.devcollab.Database.Firestore.ProjectRepository
import com.example.devcollab.Model.Applicants
import com.example.devcollab.Model.Contributer
import com.example.devcollab.Model.Project
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp

class PostProjectViewModel(
    private val repo: ProjectRepository
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    private val _projects = MutableLiveData<List<Project>>()
    val projects: LiveData<List<Project>> = _projects

    private val _appliedProjects = MutableLiveData<List<Project>>()
    val appliedProjects: LiveData<List<Project>> = _appliedProjects

    private val _shouldRefreshMyProjects = MutableLiveData(false)
    val shouldRefreshMyProjects: LiveData<Boolean> = _shouldRefreshMyProjects

    private val _categoryProjects = MutableLiveData<List<Project>?>()
    val categoryProjects: LiveData<List<Project>> = _categoryProjects as LiveData<List<Project>>

    private val _topContributors = MutableLiveData<List<Contributer>>()
    val topContributors: LiveData<List<Contributer>> = _topContributors


    private var projectsLoaded = false
    private var appliedProjectsLoaded = false
    private var userProjectsLoaded = mutableMapOf<String, Boolean>()
    private var recentProjectsLoaded = mutableMapOf<String, Boolean>()
    private val applicantsLoaded = mutableMapOf<String, Boolean>()
    private var cachedProjects: List<Project>? = null


    fun loadTopContributors() {
        repo.getTopContributors { userList ->
            _topContributors.value = userList
        }
    }

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
                projectsLoaded = false // refresh after post
                _shouldRefreshMyProjects.value = true
            }
            .addOnFailureListener {
                _loading.value = false
                _success.value = false
            }
    }

    fun onMyProjectsRefreshed() {
        _shouldRefreshMyProjects.value = false
    }

    // ---- Fetch all projects ----
    fun fetchProjects(forceRefresh: Boolean = false) {
        if (projectsLoaded && !forceRefresh) return

        _loading.value = true
        repo.fetchProjects()
            .addOnSuccessListener { querySnapshot ->
                val projectList = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Project::class.java)?.copy(projectId = document.id)
                }
                _projects.value = projectList
                projectsLoaded = true
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
            }
    }

    // ---- Fetch recent projects (per user) ----
    fun fetchRecentProjects(currentUserId: String, forceRefresh: Boolean = false) {
        if (recentProjectsLoaded[currentUserId] == true && !forceRefresh) return

        _loading.value = true
        repo.fetchRecentProjects(currentUserId)
            .addOnSuccessListener { querySnapshot ->
                val projectList = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Project::class.java)?.copy(projectId = document.id)
                }
                _projects.value = projectList
                recentProjectsLoaded[currentUserId] = true
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
            }
    }

    // ---- Fetch projects by user ----
    fun fetchUserProjects(ownerId: String, forceRefresh: Boolean = false) {
        if (userProjectsLoaded[ownerId] == true && !forceRefresh) return

        _loading.value = true
        repo.fetchUserProjects(ownerId)
            .addOnSuccessListener { querySnapshot ->
                val projectList = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Project::class.java)?.copy(projectId = document.id)
                }
                _projects.value = projectList
                userProjectsLoaded[ownerId] = true
                _loading.value = false
            }
            .addOnFailureListener {
                _loading.value = false
            }
    }

    // ---- Apply to a project ----
    fun applyToProject(projectId: String, applicantUid: String) {
        _loading.value = true

        repo.applyToProject(projectId, applicantUid)
            .addOnSuccessListener {
                _loading.value = false
                _success.value = true
                appliedProjectsLoaded = false // mark for reload
            }
            .addOnFailureListener {
                _loading.value = false
                _success.value = false
            }
    }

    // ---- Check if applied ----
    fun checkIfUserApplied(projectId: String, applicantUid: String): Task<Boolean> {
        return repo.hasUserApplied(projectId, applicantUid)
    }

    // ---- Fetch Applicants ----
    fun fetchApplicants(projectId: String): LiveData<List<Applicants>> {
        val _applicants = MutableLiveData<List<Applicants>>()

        if (applicantsLoaded[projectId] == true) return _applicants

        _loading.value = true
        repo.fetchApplicants(projectId)
            .addOnSuccessListener { applicantUids ->
                if (applicantUids.isEmpty()) {
                    _loading.value = false
                    _applicants.value = emptyList()
                    applicantsLoaded[projectId] = true
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
                    applicantsLoaded[projectId] = true
                }
            }
            .addOnFailureListener {
                _loading.value = false
                _applicants.value = emptyList()
            }

        return _applicants
    }

    // ---- Fetch applied projects ----
    fun fetchAppliedProjects(userUid: String, forceRefresh: Boolean = false) {
        if (appliedProjectsLoaded && !forceRefresh) return

        _loading.value = true
        repo.fetchAppliedProjects(userUid)
            .addOnSuccessListener { projects ->
                val applied = projects.map { it.copy(isApplied = true) }
                _appliedProjects.value = applied
                appliedProjectsLoaded = true
                _loading.value = false
            }
            .addOnFailureListener {
                _appliedProjects.value = emptyList()
                _loading.value = false
            }
    }


    fun fetchProjectsBySkill(skill: String) {
        if (cachedProjects != null) {
            _categoryProjects.value = cachedProjects
            return
        }

        repo.getProjectsBySkill(skill)
            .addOnSuccessListener { querySnapshot ->
                Log.d("ViewModel", "Fetching projects with skill: $skill")
                val projects = querySnapshot.documents.mapNotNull { it.toObject(Project::class.java) }
                cachedProjects = projects
                _categoryProjects.value = projects
                Log.d("ViewModel", "Projects found: ${querySnapshot.size()}")

            }
            .addOnFailureListener {
                Log.e("ViewModel", "Error fetching projects with skill: $skill", it)
                _categoryProjects.value = emptyList()
            }
    }
}
