package com.example.devcollab.Fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.devcollab.Adapter.ProjectsPagerAdapter
import com.example.devcollab.Activities.PostProjectActivity
import com.example.devcollab.databinding.FragmentProjectsBinding
import com.google.android.material.tabs.TabLayoutMediator

class ProjectsFragment : Fragment() {

    private var _binding: FragmentProjectsBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectsBinding.inflate(inflater, container, false)

        setupViewPager()
        setupFloatingActionButton()

        return binding.root
    }

    /**
     * Initializes ViewPager with the adapter and connects it with TabLayout.
     */
    private fun setupViewPager() {
        val adapter = ProjectsPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Get the position passed from ProfileFragment
        val selectedPosition = arguments?.getInt("selected_position", 0) ?: 0

        // Set the ViewPager to the selected position
        binding.viewPager.setCurrentItem(selectedPosition, false)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Recent"
                1 -> "My Projects"
                2 -> "Applied"
                else -> "Tab $position"
            }
        }.attach()
    }

    /**
     * Sets up Floating Action Button to navigate to PostProjectActivity.
     */
    private fun setupFloatingActionButton() {
        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(requireContext(), PostProjectActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
