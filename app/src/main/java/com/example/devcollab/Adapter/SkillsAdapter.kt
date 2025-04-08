package com.example.devcollab.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.R

class SkillsAdapter(private var skillsList: MutableList<String>,
                    private val onDeleteClick: (String) -> Unit) :
    RecyclerView.Adapter<SkillsAdapter.SkillsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SkillsViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.add_skills_item, parent, false)
        return SkillsViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: SkillsViewHolder,
        position: Int
    ) {
        holder.bind(skillsList[position])
        holder.deleteButton.setOnClickListener {
            onDeleteClick(skillsList[position])
        }
    }

    override fun getItemCount(): Int = skillsList.size

    // ViewHolder class to hold references to the views in each item
    inner class SkillsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val skillTextView: TextView = itemView.findViewById(R.id.text_skills)
        internal val deleteButton: ImageView = itemView.findViewById(R.id.button_delete)

        fun bind(skill: String) {
            skillTextView.text = skill
        }
    }

    // Add a new skill to the list and notify the adapter
    fun addSkill(skill: String) {
        skillsList.add(skill)
        notifyItemInserted(skillsList.size - 1)
    }
}