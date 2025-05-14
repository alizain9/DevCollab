package com.example.devcollab.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.devcollab.Model.Messages
import com.example.devcollab.R

class MessagesAdapter(
    private val messagesList: List<Messages>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_messages, parent, false)
        )

    override fun getItemCount() = messagesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msg = messagesList[position]
        holder.userName.text = msg.name

        // load profile image URL
        Glide.with(holder.itemView.context)
            .load(msg.profileImageUrl)
            .placeholder(R.drawable.user)
            .error(R.drawable.user)
            .into(holder.profileImage)

        // open chat on click and also send the image to the Next Activity
        holder.itemView.setOnClickListener { onItemClick(msg.uid) }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.image_profile)
        val userName: TextView    = itemView.findViewById(R.id.name)
    }
}
