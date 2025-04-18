package com.example.devcollab.Database.Firestore

import com.example.devcollab.Model.Project
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore


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
        return projects
            .document()   // generate new doc ID
            .set(project) // write the entire Project object
    }
}
