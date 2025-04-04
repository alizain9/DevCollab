package com.example.devcollab.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.Adapter.ProfileAdapter
import com.example.devcollab.Model.Profile
import com.example.devcollab.R
import com.example.devcollab.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        val recyclerView = binding.rvProfile
        val profileItems = listOf(
            Profile("Profile", R.drawable.ic_profile_tab, "#4CAF5030"),
            Profile("Dashboard", R.drawable.ic_messages_tab, "#2196F330"),
            Profile("My Projects", R.drawable.ic_messages_tab, "#FF980030"),
            Profile("About", R.drawable.ic_app_development, "#F4433630x")
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ProfileAdapter(profileItems)

        return binding.root
    }
}