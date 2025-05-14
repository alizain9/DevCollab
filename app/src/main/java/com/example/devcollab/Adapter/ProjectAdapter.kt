package com.example.devcollab.Adapter

import com.example.devcollab.R
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.Activities.ApplicantsActivity
import com.example.devcollab.Activities.ViewDetailsActivity
import com.example.devcollab.Database.Firestore.ProjectRepository
import com.example.devcollab.Model.Project
import com.example.devcollab.ViewModels.PostProjectViewModel
import com.example.devcollab.databinding.MyProjectItemBinding
import com.example.devcollab.databinding.ProjectItemBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

class ProjectAdapter(
    private val projects: MutableList<Project>,
    private val currentUserUid: String,
    private val onApplyClick: (Project) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // View Types
    private val MY_PROJECTS = 0
    private val TYPE_NORMAL = 1
    private val TYPE_APPLIED = 2

    override fun getItemViewType(position: Int): Int {
        val project = projects[position]
        Log.d("ProjectAdapter", "Project at position $position: ${project.title}, isApplied: ${project.isApplied}")
        return when{
            project.ownerId == currentUserUid -> MY_PROJECTS
            project.isApplied == true -> TYPE_APPLIED
            else -> TYPE_NORMAL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            MY_PROJECTS -> {
                val binding = MyProjectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MyProjectViewHolder(binding)
            }
            TYPE_APPLIED -> {
                val binding = ProjectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AppliedViewHolder(binding)
            }
            else -> {
                val binding = ProjectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                NormalViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val project = projects[position]
        when (holder) {
            is MyProjectViewHolder -> holder.bind(project)
            is NormalViewHolder -> holder.bind(project, onApplyClick)
            is AppliedViewHolder -> holder.bind(project)
        }
    }

    override fun getItemCount(): Int = projects.size

    // ViewHolder for "My Projects"
    inner class MyProjectViewHolder(private val binding: MyProjectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.titleTextView.text = project.title
            binding.fieldTextView.text = project.requiredSkills.joinToString(", ")
            val dateFormat =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Format as "day/month/year"
            binding.dateTextView.text = project.deadline?.toDate()?.let { date ->
                dateFormat.format(date) // Format the date
            } ?: "No Deadline"
            binding.descriptionTextView.text = project.description
            binding.btnViewApplicants.setOnClickListener {
                // Navigate to ApplicantsActivity
                val intent = Intent(itemView.context, ApplicantsActivity::class.java)
                intent.putExtra("projectId", project.projectId)
                itemView.context.startActivity(intent)
            }

            binding.applicantsItemLayout.setOnClickListener {
                sendDataToViewDetailsActivity(project)
            }
        }
        private fun sendDataToViewDetailsActivity(project: Project) {
            val intent = Intent(itemView.context, ViewDetailsActivity::class.java)
            intent.putExtra("type", "my_project")
            intent.putExtra("title", project.title)
            intent.putExtra("projectId", project.projectId)
            intent.putExtra("description", project.description)
            intent.putExtra("deadline", project.deadline?.toDate()?.toString() ?: "No Deadline")
            intent.putExtra("ownerId", project.ownerId)
            intent.putExtra("requiredSkills", project.requiredSkills.joinToString(", "))
            itemView.context.startActivity(intent)
        }

    }

    // ViewHolder for "Recent Projects"
    inner class NormalViewHolder(private val binding: ProjectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project, onApplyClick: (Project) -> Unit) {
            binding.titleTextView.text = project.title
            binding.fieldTextView.text = project.requiredSkills.joinToString(", ")
            val dateFormat =
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Format as "day/month/year"
            binding.dateTextView.text = project.deadline?.toDate()?.let { date ->
                dateFormat.format(date) // Format the date
            } ?: "No Deadline"
            binding.descriptionTextView.text = project.description

            // Disable "Apply" button if the project belongs to the current user
            if (project.ownerId == currentUserUid) {
                binding.btnApply.isEnabled = false
                binding.btnApply.text = "Your Project"
            } else {
                binding.btnApply.isEnabled = true
                binding.btnApply.setOnClickListener {
                    showApplyDialog(project)
                }
            }

            binding.btnView.setOnClickListener {
                sendDataToViewDetailsActivity(project)
            }
        }

        private fun sendDataToViewDetailsActivity(project: Project) {
            val intent = Intent(itemView.context, ViewDetailsActivity::class.java)
            intent.putExtra("type", "normal")
            intent.putExtra("title", project.title)
            intent.putExtra("projectId", project.projectId)
            intent.putExtra("description", project.description)
            intent.putExtra("deadline", project.deadline?.toDate()?.toString() ?: "No Deadline")
            intent.putExtra("ownerId", project.ownerId)
            intent.putExtra("requiredSkills", project.requiredSkills.joinToString(", "))
            itemView.context.startActivity(intent)
        }

        private fun showApplyDialog(project: Project) {
            val dialog = MaterialAlertDialogBuilder(itemView.context)
                .setTitle("Apply for Project")
                .setMessage("Do you want to apply for \"${project.title}\"?")
                .setPositiveButton("Yes") { _, _ ->

                    // Get the current user's UID
                    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
                    val vmProject = PostProjectViewModel(ProjectRepository())

                    if (currentUserUid != null) {
                        // Check if the user has already applied
                        vmProject.checkIfUserApplied(project.projectId, currentUserUid)
                            .addOnSuccessListener { hasApplied ->
                                if (hasApplied) {
                                    // User has already applied
                                    Toast.makeText(
                                        itemView.context,
                                        "You have already applied to this project",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    // User hasn't applied yet, proceed with applying
                                    vmProject.applyToProject(project.projectId, currentUserUid)
                                    Toast.makeText(
                                        itemView.context,
                                        "Applied Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            .addOnFailureListener {
                                // Handle failure to check application status
                                Toast.makeText(
                                    itemView.context,
                                    "Failed to check application status",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        // Handle the case where the user is not logged in
                        Toast.makeText(itemView.context, "You must be logged in to apply", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()

            dialog.show()
        }
    }

    //ViewHolder for the "Applied Projects"
    inner class AppliedViewHolder(private val binding: ProjectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(project: Project) {
            binding.titleTextView.text = project.title
            binding.fieldTextView.text = project.requiredSkills.joinToString(", ")
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.dateTextView.text = project.deadline?.toDate()?.let { date ->
                dateFormat.format(date)
            } ?: "No Deadline"
            binding.descriptionTextView.text = project.description

            // Customize UI for applied projects
            binding.btnApply.isEnabled = false
            binding.btnApply.text = "Applied"

            // change the drawable button shape
            binding.btnApply.background = itemView.context.getDrawable(R.drawable.btn_applied)




            binding.btnView.setOnClickListener {
                sendDataToViewDetailsActivity(project)
            }
        }

        private fun sendDataToViewDetailsActivity(project: Project) {
            val intent = Intent(itemView.context, ViewDetailsActivity::class.java)
            intent.putExtra("title", project.title)
            intent.putExtra("description", project.description)
            intent.putExtra("deadline", project.deadline?.toDate()?.toString() ?: "No Deadline")
            intent.putExtra("ownerId", project.ownerId)
            intent.putExtra("requiredSkills", project.requiredSkills.joinToString(", "))
            itemView.context.startActivity(intent)
        }
    }


    // Update the adapter's data
    fun submitList(newProjects: List<Project>) {
        projects.clear()
        projects.addAll(newProjects)
        notifyDataSetChanged()
    }
}