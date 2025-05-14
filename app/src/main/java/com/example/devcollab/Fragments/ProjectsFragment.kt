package com.example.devcollab.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.devcollab.Adapter.ProjectsPagerAdapter
import com.example.devcollab.Activities.PostProjectActivity
import com.example.devcollab.databinding.FragmentProjectsBinding
import com.google.android.material.tabs.TabLayoutMediator

class ProjectsFragment : Fragment() {

    private var _binding: FragmentProjectsBinding? = null
    private val binding get() = _binding!!

    private lateinit var postProjectLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register activity result to get callback when post project activity finishes
        postProjectLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val currentItem = binding?.viewPager?.currentItem ?: 0
                val fragmentTag = "f$currentItem"
                val currentFragment = childFragmentManager.findFragmentByTag(fragmentTag)

                when (currentFragment) {
                    is MyProjectsFragment -> currentFragment.refreshMyProjectsAfterPost()
                    is RecentProjectsFragment -> currentFragment.refreshRecentProjectsAfterPost()
                    is AppliedProjectsFragment -> currentFragment.refreshAppliedProjectsAfterPost()
                }
            }
        }
    }

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

        val selectedPosition = arguments?.getInt("selected_position", 0) ?: 0
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
     * Sets up Floating Action Button to launch PostProjectActivity using launcher.
     */
    private fun setupFloatingActionButton() {
        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), PostProjectActivity::class.java)
            postProjectLauncher.launch(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
