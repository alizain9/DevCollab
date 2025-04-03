package com.example.devcollab.Fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.devcollab.Adapter.ProjectsPagerAdapter
import com.example.devcollab.PostProjectActivity
import com.example.devcollab.R
import com.example.devcollab.databinding.FragmentProjectsBinding
import com.google.android.material.tabs.TabLayoutMediator


class ProjectsFragment : Fragment() {
    private var _binding: FragmentProjectsBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProjectsBinding.inflate(inflater, container, false)

        // Set up ViewPager with Adapter
        val adapter = ProjectsPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Connect TabLayout with ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Recent Projects"
                1 -> "My Projects"
                else -> "Tab $position"
            }
        }.attach()


        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(requireContext(), PostProjectActivity::class.java))
        }

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}