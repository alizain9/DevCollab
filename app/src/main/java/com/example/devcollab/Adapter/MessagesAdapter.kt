package com.example.devcollab.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.Model.Messages
import com.example.devcollab.R

class MessagesAdapter (private val messagesList : List<Messages>):
    RecyclerView.Adapter<MessagesAdapter.viewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): viewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_messages, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(
        holder: viewHolder,
        position: Int
    ) {
        val messages = messagesList[position]
        holder.userName.text = messages.name
        holder.profileImage.setImageResource(messages.profileImageResId)
    }

    override fun getItemCount(): Int {
     return messagesList.size
    }

    inner class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.image_profile)
        val userName: TextView = itemView.findViewById(R.id.name)
    }
}