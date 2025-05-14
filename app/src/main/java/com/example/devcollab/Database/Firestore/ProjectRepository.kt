package com.example.devcollab.Database.Firestore

import android.util.Log
import com.example.devcollab.Const.Constants
import com.example.devcollab.Model.Applicants
import com.example.devcollab.Model.Contributer
import com.example.devcollab.Model.Project
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import com.google.firebase.firestore.QuerySnapshot


class ProjectRepository {

    // Firestore instance
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val projects = firestore.collection(Constants.PROJECTS_NODE)
    private val users = firestore.collection(Constants.USERS_NODE)

    private var topContributorsCache: List<Contributer>? = null

    fun getTopContributors(onResult: (List<Contributer>) -> Unit) {
        // Return cached data if exists
        topContributorsCache?.let {
            onResult(it)
            return
        }
        firestore.collection(Constants.PROJECTS_NODE)
            .get()
            .addOnSuccessListener { projectDocs ->
                val ownerCountMap = mutableMapOf<String, Int>()

                for (doc in projectDocs) {
                    val ownerId = doc.getString("ownerId") ?: continue
                    ownerCountMap[ownerId] = ownerCountMap.getOrDefault(ownerId, 0) + 1
                }

                val top3OwnerIds = ownerCountMap.entries
                    .sortedByDescending { it.value }
                    .take(3)
                    .map { it.key }

                fetchUserDetails(top3OwnerIds, onResult)
            }
    }

    private fun fetchUserDetails(userIds: List<String>, onResult: (List<Contributer>) -> Unit) {
        val usersRef = firestore.collection(Constants.USERS_NODE)
        val contributors = mutableListOf<Contributer>()

        for (userId in userIds) {
            usersRef.document(userId)
                .get()
                .addOnSuccessListener { doc ->
                    val user = Contributer(
                        id = userId,
                        name = doc.getString("username") ?: "",
                        profileImage = doc.getString("profileImageUrl") ?: "",
                        profession = doc.getString("profession") ?: ""
                    )
                    contributors.add(user)

                    if (contributors.size == userIds.size) {
                        // Sort based on original order
                        val sorted = userIds.map { id -> contributors.first { it.id == id } }
                        topContributorsCache = sorted
                        onResult(sorted)
                    }
                }
        }
    }

    fun addProject(project: Project): Task<Void> {
        // Generate a unique document ID
        val projectId = projects.document().id

        // Assign the generated ID to the project's id field
        val projectWithId = project.copy(projectId = projectId)

        // Write the project data to Firestore using the generated ID
        return projects
            .document(projectId) // Use the generated ID as the document ID
            .set(projectWithId)  // Include the ID in the project data
    }

    fun fetchProjects(): Task<QuerySnapshot> {
        return projects
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
    }

    fun fetchRecentProjects(currentUserId: String): Task<QuerySnapshot>{
        return projects
            .whereNotEqualTo("ownerId", currentUserId)
            .get()
    }

    fun fetchUserProjects(ownerId: String): Task<QuerySnapshot> {
        return projects
            .whereEqualTo("ownerId", ownerId) // Filter by ownerId
            .get()
    }


    fun hasUserApplied(projectId: String, applicantUid: String): Task<Boolean> {
        return projects
            .document(projectId) // Reference the specific project document
            .get()
            .continueWith { task ->
                if (task.isSuccessful) {
                    val project = task.result?.toObject(Project::class.java)
                    project?.applicants?.contains(applicantUid) ?: false
                } else {
                    false
                }
            }
    }


    // Fetch the  All applicants for a project
    fun fetchApplicants(projectId: String): Task<List<String>> {
        return projects
            .document(projectId)
            .get()
            .continueWith { task ->
                if (task.isSuccessful) {
                    task.result?.toObject(Project::class.java)?.applicants ?: emptyList()
                } else {
                    emptyList()
                }
            }
    }

    fun fetchUser(uid: String): Task<Applicants> {
        return firestore.collection("users")
            .document(uid)
            .get()
            .continueWith { task ->
                if (task.isSuccessful) {
                    val user = task.result?.toObject(Applicants::class.java)?.copy(uid = uid)
                    Log.d("ProjectRepository", "Fetched user data for UID $uid: $user")
                    user ?: Applicants() // Return an empty Applicant object if data is missing
                } else {
                    Log.e("ProjectRepository", "Error fetching user data for UID $uid", task.exception)
                    Applicants() // Return an empty Applicant object on failure
                }
            }
    }

    // Apply to a project
    fun applyToProject(projectId: String, applicantUid: String): Task<Void> {

        // Add the applicant's UID to the project's applicants array
        val updateApplicantsTask = projects
            .document(projectId)
            .update("applicants", FieldValue.arrayUnion(applicantUid))

        // Add the project ID to the user's appliedProjects array
        val updateUserProfileTask = users
            .document(applicantUid)
            .update("appliedProjects", FieldValue.arrayUnion(projectId))

        // Return both tasks as a single task
        return Tasks.whenAll(updateApplicantsTask, updateUserProfileTask)

    }

    fun getProjectsBySkill(skill: String): Task<QuerySnapshot> {
        return projects
            .whereArrayContains("requiredSkills", skill)
            .get()
    }

    fun fetchAppliedProjects(userUid: String): Task<List<Project>> {
        return users
            .document(userUid)
            .get()
            .continueWithTask { task ->
                if (task.isSuccessful) {
                    val user = task.result?.toObject(Applicants::class.java)
                    val appliedProjectIds = user?.appliedProjects ?: emptyList()

                    // Fetch project details for each applied project ID
                    val tasks = appliedProjectIds.map { projectId ->
                        projects.document(projectId).get().continueWith { projectTask ->
                            projectTask.result?.toObject(Project::class.java)
                        }
                    }

                    // Wait for all tasks to complete
                    Tasks.whenAllSuccess<Project>(tasks)
                } else {
                    Tasks.forResult(emptyList()) // Return an empty list if the task fails
                }
            }
    }
}