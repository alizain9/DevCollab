package com.example.devcollab.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.AboutActivity
import com.example.devcollab.Adapter.ProfileAdapter
import com.example.devcollab.Model.Profile
import com.example.devcollab.R
import com.example.devcollab.databinding.FragmentProfileBinding
import me.ibrahimsn.lib.SmoothBottomBar
import kotlin.jvm.java
import kotlin.text.replace

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        setupRecyclerView()
        return binding.root
    }

    /**
     * Initializes RecyclerView with profile items.
     */
    private fun setupRecyclerView() {
        val profileItems = listOf(
            Profile("Profile", R.drawable.ic_profile_tab, "#D0E6A5"),
            Profile("Dashboard", R.drawable.ic_dashboard, "#87CEEB"),
            Profile("My Projects", R.drawable.ic_my_project, "#FF9A3D"),
            Profile("About", R.drawable.ic_about, "#D1C4E9") // Fixed color code
        )

        binding.rvProfile.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProfile.adapter = ProfileAdapter(profileItems){ profile ->
            onProfileItemClick(profile)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Profile Fragment Item Click Listener
    private fun onProfileItemClick(profile: Profile) {
        when (profile.title) {
            "Profile" -> {
                // Handle Profile item click
            }

            "Dashboard" -> {
                // Navigate to Home Fragment
                val fragmentHome = HomeFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragmentHome)
                    .commit()

                // Update Bottom Navigation Indicator
                val bottomNav = requireActivity().findViewById<SmoothBottomBar>(R.id.bottomBar)
                bottomNav.itemActiveIndex = 0
            }

            "My Projects" -> {
                // Navigate to ProjectsFragment with position 1 (My Projects page)
                val bundle = Bundle()
                bundle.putInt("selected_position", 1)  // Passing the position to ProjectsFragment
                val projectsFragment = ProjectsFragment()
                projectsFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, projectsFragment)
                    .commit()

                // Update Bottom Navigation Indicator
                val bottomNav = requireActivity().findViewById<SmoothBottomBar>(R.id.bottomBar)
                bottomNav.itemActiveIndex = 1
            }

            "About" -> {
                // go to the about activity
                val intent = Intent(requireContext(), AboutActivity::class.java)
                startActivity(intent)
            }
        }
    }
}


