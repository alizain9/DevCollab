package com.example.devcollab.Database.Firestore

import android.util.Log
import com.example.devcollab.Const.Constants
import com.example.devcollab.Model.Applicants
import com.example.devcollab.Model.Project
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

import com.google.firebase.firestore.QuerySnapshot


class ProjectRepository {

    // Firestore instance
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // “Projects” collection reference
    private val projects = firestore.collection("projects")

    /**
     * Adds a new Project document with an auto-generated ID.
     *
     * @param project the Project data to write
     * @return a Task<Void> you can attach listeners to for success / failure
     */
    fun addProject(project: Project): Task<Void> {
        // Generate a unique document ID
        val projectId = projects.document().id

        // Assign the generated ID to the project's `id` field
        val projectWithId = project.copy(id = projectId)

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

    fun fetchUserProjects(ownerId: String): Task<QuerySnapshot> {
        return projects
            .whereEqualTo("ownerId", ownerId) // Filter by ownerId
            .get()
    }

    fun addApplicantToProject(projectId: String, applicantUid: String): Task<Void> {
        return projects
            .document(projectId) // Reference the specific project document
            .update("applicants", FieldValue.arrayUnion(applicantUid)) // Add the applicant's UID to the applicants array
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
    }}
