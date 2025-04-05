package com.example.devcollab.Adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.Model.Profile
import com.example.devcollab.R
import androidx.core.content.ContextCompat


class ProfileAdapter(private val itemList: List<Profile>, private val onItemClick: (Profile) -> Unit) :
    RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    class ProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconBackground: LinearLayout = view.findViewById(R.id.icon_bg)
        val icon: ImageView = view.findViewById(R.id.icon)
        val title: TextView = view.findViewById(R.id.title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_fragment_item, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val item = itemList[position]

        holder.title.text = item.title
        holder.icon.setImageResource(item.icon)

        // Convert color string to Color
        try {
            val colorInt = Color.parseColor(item.backgroundColor)
            val backgroundDrawable = holder.iconBackground.background as GradientDrawable
            backgroundDrawable.setColor(colorInt)
        } catch (e: IllegalArgumentException) {
            Log.e("ProfileAdapter", "Invalid color: ${item.backgroundColor}")
        }

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick(item) // Trigger the click action
        }
    }

    override fun getItemCount(): Int = itemList.size
}
