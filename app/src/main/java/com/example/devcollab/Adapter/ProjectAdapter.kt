package com.example.devcollab.Adapter

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.Activities.ApplicantsActivity
import com.example.devcollab.Model.Project
import com.example.devcollab.Activities.ViewDetailsActivity
import com.example.devcollab.databinding.MyProjectItemBinding
import com.example.devcollab.databinding.ProjectItemBinding

class ProjectAdapter(private val projects: List<Project>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // View Types
    private val MY_PROJECTS = 0
    private val TYPE_NORMAL = 1

    override fun getItemViewType(position: Int): Int {
        return 0
        //if (projects[position].isMyProject) MY_PROJECTS else TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MY_PROJECTS) {
            val binding = MyProjectItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            FeaturedViewHolder(binding)
        } else {
            val binding = ProjectItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            NormalViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val project = projects[position]
        if (holder is FeaturedViewHolder) {
            holder.bind(project)
            holder.itemView.setOnClickListener {

            }
        } else if (holder is NormalViewHolder) {
            holder.bind(project)
        }
    }

    override fun getItemCount(): Int = projects.size

    // MY Project ViewHolder
    inner class FeaturedViewHolder(private val binding: MyProjectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.titleTextView.text = project.title
            binding.fieldTextView.text = project.description
            binding.dateTextView.text = project.deadline.toString()
            binding.descriptionTextView.text = project.description
            binding.btnViewApplicants.setOnClickListener {
                val intent = Intent(binding.root.context, ApplicantsActivity::class.java)
                binding.root.context.startActivity(intent)
            }
        }
    }

    // Normal Project ViewHolder
    inner class NormalViewHolder(private val binding: ProjectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.titleTextView.text = project.title
            binding.fieldTextView.text = project.description
            binding.dateTextView.text = project.deadline.toString()
            binding.descriptionTextView.text = project.description
            binding.viewButton.setOnClickListener {
                val intent = Intent(binding.root.context, ViewDetailsActivity::class.java)
                binding.root.context.startActivity(intent)
            }
            binding.btnApply.setOnClickListener {
                val dialog = AlertDialog.Builder(binding.root.context)
                    .setTitle("Apply")
                    .setMessage("Do you want to Apply for this project?")
                    .setPositiveButton("Yes") { dialogInterface, _ ->
                        // Do something
                        Toast.makeText(binding.root.context, "Applied Successfully", Toast.LENGTH_SHORT).show()
                        dialogInterface.dismiss() // dismisses the dialog
                    }
                    .setNegativeButton("No") { dialogInterface, _ ->
                        dialogInterface.dismiss() // also dismisses
                    }
                    .create()

                dialog.show()
            }
        }
    }
}
