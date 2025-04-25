package com.example.devcollab.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.Model.ChatMessage
import com.example.devcollab.R

class ChatAdapter(private val currentUid: String) :
    ListAdapter<ChatMessage, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_SENT = 1
        private const val TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderId == currentUid) TYPE_SENT
        else TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_SENT) {
            val v = inflater.inflate(R.layout.item_message_sent, parent, false)
            SentVH(v)
        } else {
            val v = inflater.inflate(R.layout.item_message_received, parent, false)
            ReceivedVH(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = getItem(position)
        if (holder is SentVH) holder.bind(msg)
        else if (holder is ReceivedVH) holder.bind(msg)
    }

    class SentVH(view: View) : RecyclerView.ViewHolder(view) {
        private val tv = view.findViewById<TextView>(R.id.tvText)
        fun bind(m: ChatMessage) { tv.text = m.text }
    }

    class ReceivedVH(view: View) : RecyclerView.ViewHolder(view) {
        private val tv = view.findViewById<TextView>(R.id.tvText)
        fun bind(m: ChatMessage) { tv.text = m.text }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(a: ChatMessage, b: ChatMessage) =
            a.timestamp == b.timestamp && a.senderId == b.senderId
        override fun areContentsTheSame(a: ChatMessage, b: ChatMessage) = a == b
    }
}
