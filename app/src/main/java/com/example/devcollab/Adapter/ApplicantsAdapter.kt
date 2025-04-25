package com.example.devcollab.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.devcollab.Activities.MyProfileActivity
import com.example.devcollab.Model.Applicants
import com.example.devcollab.R
import com.example.devcollab.databinding.ItemApplicantsBinding

class ApplicantsAdapter(
    private var applicants: List<Applicants>,
    private val onProfileClick: (String) -> Unit,
    private val onContactClick: (String) -> Unit
) : RecyclerView.Adapter<ApplicantsAdapter.viewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): viewHolder {
        val binding =
            ItemApplicantsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return viewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: viewHolder, position: Int
    ) {
        holder.bind(applicants[position])
    }

    override fun getItemCount(): Int {
        return applicants.size
    }

    fun updateData(newApplicants: List<Applicants>) {
        this.applicants = newApplicants
        notifyDataSetChanged()
    }

    inner class viewHolder(private val binding: ItemApplicantsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(applicant: Applicants) {
            binding.name.text = applicant.username


            // Load profile image using Glide or Picasso
            Glide.with(itemView.context).load(applicant.profileImageUrl)
                .placeholder(R.drawable.user).into(binding.imageProfile)

            binding.cardProfileHolder.setOnClickListener {
                onProfileClick(applicant.uid)
            }

            binding.btnContact.setOnClickListener {
                onContactClick(applicant.uid)
            }
        }
    }
}
