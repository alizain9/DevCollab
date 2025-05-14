package com.example.devcollab.Activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.devcollab.Database.Firestore.ProjectRepository
import com.example.devcollab.Database.Firestore.UserRepository
import com.example.devcollab.R
import com.example.devcollab.ViewModels.PostProjectViewModel
import com.example.devcollab.databinding.ActivityViewDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class ViewDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewDetailsBinding
    private lateinit var repo: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        repo = UserRepository()

        // Retrieve data from the Intent
        val type = intent.getStringExtra("type")
        val ownerId = intent.getStringExtra("ownerId")
        val projectId = intent.getStringExtra("projectId")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val requiredSkills = intent.getStringExtra("requiredSkills")
        val deadline = intent.getStringExtra("deadline")

        binding.textTitle.text = title
        binding.textDescription.text = description
        binding.textDeadline.text = deadline
        binding.textRequiredSkills.text = requiredSkills

        // handle the visibility of the apply button
        if (type == "normal") {
            binding.btnApply.visibility = View.VISIBLE
        } else {
            binding.btnApply.visibility = View.GONE
        }

        repo.fetchUserProfile(ownerId!!) { user ->
            binding.textClientName.text = user?.username
        }

        // Handle the "Apply" button click
        binding.btnApply.setOnClickListener {
            if (projectId != null) {
                showApplyDialog(projectId, title!!)
            }
        }

        binding.backToProjects.setOnClickListener {
            finish()
        }
    }

    private fun showApplyDialog(projectId: String, title: String) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Apply for Project")
            .setMessage("Do you want to apply for \"${title}\"?")
            .setPositiveButton("Yes") { _, _ ->

                // Get the current user's UID
                val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                val vmProject = PostProjectViewModel(ProjectRepository())

                if (currentUserUid != null) {
                    // Check if the user has already applied
                    vmProject.checkIfUserApplied(projectId, currentUserUid)
                        .addOnSuccessListener { hasApplied ->
                            if (hasApplied) {
                                // User has already applied
                                Toast.makeText(
                                    this@ViewDetailsActivity,
                                    "You have already applied to this project",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                // User hasn't applied yet, proceed with applying
                                vmProject.applyToProject(projectId, currentUserUid)
                                Toast.makeText(
                                    this@ViewDetailsActivity,
                                    "Applied Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .addOnFailureListener {
                            // Handle failure to check application status
                            Toast.makeText(
                                this@ViewDetailsActivity,
                                "Failed to check application status",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    // Handle the case where the user is not logged in
                    Toast.makeText(
                        this@ViewDetailsActivity,
                        "You must be logged in to apply",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }
}