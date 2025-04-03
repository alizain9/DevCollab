package com.example.devcollab.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.Model.Project
import com.example.devcollab.databinding.MyProjectItemBinding
import com.example.devcollab.databinding.ProjectItemBinding

class ProjectAdapter(private val projects: List<Project>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // View Types
    private val TYPE_FEATURED = 0
    private val TYPE_NORMAL = 1

    override fun getItemViewType(position: Int): Int {
        return if (projects[position].isMyProject) TYPE_FEATURED else TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_FEATURED) {
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
        } else if (holder is NormalViewHolder) {
            holder.bind(project)
        }
    }

    override fun getItemCount(): Int = projects.size

    // Featured Project ViewHolder
    inner class FeaturedViewHolder(private val binding: MyProjectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.titleTextView.text = project.title
            binding.fieldTextView.text = project.field
            binding.dateTextView.text = project.date
            binding.descriptionTextView.text = project.description
        }
    }

    // Normal Project ViewHolder
    inner class NormalViewHolder(private val binding: ProjectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(project: Project) {
            binding.titleTextView.text = project.title
            binding.fieldTextView.text = project.field
            binding.dateTextView.text = project.date
            binding.descriptionTextView.text = project.description
        }
    }
}
