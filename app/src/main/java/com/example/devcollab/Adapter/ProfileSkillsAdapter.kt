package com.example.devcollab.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.R

class ProfileSkillsAdapter(private var skills: List<String>) :
    RecyclerView.Adapter<ProfileSkillsAdapter.SkillViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SkillViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_skils, parent, false)
        return SkillViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SkillViewHolder,
        position: Int
    ) {
        holder.bind(skills[position])
    }

    override fun getItemCount(): Int {
        return skills.size
    }

    class SkillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val skillTextView: TextView = itemView.findViewById(R.id.text_skills_profile)

        fun bind(skill: String) {
            skillTextView.text = skill
        }
    }
    fun updateSkills(newSkills: List<String>) {
        this.skills = newSkills
        notifyDataSetChanged()
    }
}