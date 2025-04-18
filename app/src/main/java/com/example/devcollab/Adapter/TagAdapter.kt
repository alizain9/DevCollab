package com.example.devcollab.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.R

import com.example.devcollab.databinding.ItemTagBinding

class TagAdapter(
    private var tags: List<String>,
    private val onRemove: (String) -> Unit
) : RecyclerView.Adapter<TagAdapter.TagVH>() {

    fun submitList(newTags: List<String>) {
        tags = newTags
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TagVH(
        ItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = tags.size
    override fun onBindViewHolder(holder: TagVH, pos: Int) {
        val tag = tags[pos]
        holder.binding.textTag.text = tag
        holder.binding.buttonDelete.setOnClickListener { onRemove(tag) }
    }

    class TagVH(val binding: ItemTagBinding) : RecyclerView.ViewHolder(binding.root)
}
