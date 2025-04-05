package com.example.devcollab.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.Model.Applicants
import com.example.devcollab.R

class ApplicantsAdapter(private val applicantList: List<Applicants>): RecyclerView.Adapter<ApplicantsAdapter.viewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): viewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_applicants, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(
        holder: viewHolder,
        position: Int
    ) {
        val applicants = applicantList[position]
        holder.name.text = applicants.name
        holder.profileImage.setImageResource(applicants.profileImg)
    }

    override fun getItemCount(): Int {
       return applicantList.size
    }

    class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val name = itemView.findViewById<TextView>(R.id.name)
        val profileImage = itemView.findViewById<ImageView>(R.id.image_profile)
    }
}