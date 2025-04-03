package com.example.devcollab.Adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.Model.Category
import com.example.devcollab.R

class CategoriesAdapter(private val categroryList: List<Category>):
    RecyclerView.Adapter<CategoriesAdapter.viewHolder>() {


    class viewHolder (itemview: View): RecyclerView.ViewHolder(itemview){
        val iconBg: View = itemView.findViewById(R.id.icon_bg)
        val icon: ImageView = itemView.findViewById(R.id.icon)
        val title: TextView = itemView.findViewById(R.id.title)
        val cardBackground: RelativeLayout = itemView.findViewById(R.id.card_bg)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.categoires_item, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return categroryList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val category = categroryList[position]

        holder.title.text = category.name
        holder.icon.setImageResource(category.iconRes)
        // Change colors dynamically
        val bgDrawable = holder.iconBg.background.mutate() as GradientDrawable
        bgDrawable.setColor(Color.parseColor(category.iconBgColor)) // Dynamically change color
        holder.cardBackground.setBackgroundColor(Color.parseColor(category.cardBgColor))

    }
}