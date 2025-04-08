package com.example.devcollab.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.R

class TagAdapter( private val tags: MutableList<String>,
                  private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TagViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tag, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TagViewHolder,
        position: Int
    ) {
        val tag = tags[position]
        holder.tagText.text = tag
        holder.deleteIcon.setOnClickListener { onDeleteClick(tag) }
    }

    override fun getItemCount(): Int {
       return tags.size
    }

    inner class TagViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tagText: TextView = view.findViewById(R.id.text_tag)
        val deleteIcon: ImageView = view.findViewById(R.id.button_delete)
    }

    fun updateTags(newTags: List<String>) {
        tags.clear()
        tags.addAll(newTags)
        notifyDataSetChanged()
    }
}