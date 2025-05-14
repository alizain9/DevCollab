package com.example.devcollab.Fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.Activities.AboutActivity
import com.example.devcollab.Activities.LoginActivity
import com.example.devcollab.Activities.MainActivity
import com.example.devcollab.Adapter.ProfileAdapter
import com.example.devcollab.Model.Profile
import com.example.devcollab.Activities.MyProfileActivity
import com.example.devcollab.Database.Firestore.AuthRepository
import com.example.devcollab.Database.Room.AppDatabase
import com.example.devcollab.R
import com.example.devcollab.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import me.ibrahimsn.lib.SmoothBottomBar
import kotlin.jvm.java

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var authRepo: AuthRepository

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
            Profile("About", R.drawable.ic_about, "#D1C4E9"), // Fixed color code
            Profile("Logout", R.drawable.ic_logout, "#E3F2FD") // Fixed color code
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
                // Navigate to the Profile activity
                val intent = Intent(requireContext(), MyProfileActivity::class.java)
                startActivity(intent)
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
            "Logout" -> {
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        dialog.dismiss()
                        // logout the user
                        showLoading(true)
                        authRepo = AuthRepository()
                        if (authRepo.isUserLoggedIn()) {
                            authRepo.signOut()
                            val job = lifecycleScope.async {
                                AppDatabase.getDatabase(requireContext()).userDao().deleteUser()
                                AppDatabase.getDatabase(requireContext()).clearAllTables()
                            }
                            job.invokeOnCompletion {
                                showLoading(false)
                                Toast.makeText(requireContext(), "Logout successful", Toast.LENGTH_SHORT).show()
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        } else {
                            showLoading(false)
                            Toast.makeText(requireContext(), "User is not logged in", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }

        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.spinKit.visibility = View.VISIBLE

            requireActivity().window.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            binding.spinKit.visibility = View.GONE
            requireActivity().window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}


