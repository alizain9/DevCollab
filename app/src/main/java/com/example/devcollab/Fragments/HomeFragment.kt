package com.example.devcollab.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.example.devcollab.Adapter.CategoriesAdapter
import com.example.devcollab.Adapter.ProjectAdapter
import com.example.devcollab.Activities.LoginActivity

import com.example.devcollab.Database.Room.AppDatabase
import com.example.devcollab.Model.Category
import com.example.devcollab.Model.Project
import com.example.devcollab.R
import com.example.devcollab.databinding.FragmentHomeBinding
import com.google.firebase.Timestamp

import kotlinx.coroutines.launch
import me.ibrahimsn.lib.SmoothBottomBar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setUphometop()
        setupSeeProjectsButton()
        setupBannerFlipper()
        setupCategoriesRecyclerView()
        setupProjectsRecyclerView()


        return binding.root
    }

    private fun setUphometop() {

        lifecycleScope.launch {
            val user = AppDatabase.getDatabase(requireContext()).userDao().getUser()
            if (user != null) {
                // User is logged in
                binding.tvCollabrate.visibility = View.GONE
                binding.btnJoin.visibility = View.GONE

                binding.cardProfiler.visibility = View.VISIBLE
                binding.nameProfiler.visibility = View.VISIBLE
                binding.proffesssionProfiler.visibility = View.VISIBLE
                binding.btnProfile.visibility = View.VISIBLE

                binding.nameProfiler.text = user.username
                binding.proffesssionProfiler.text = user.profession
                val profileImageUrl = user.profileImageUrl
                Glide.with(requireContext())
                    .load(profileImageUrl)
                    .placeholder(R.drawable.user)
                    .into(binding.imageProfiler)
            } else {
                // No user in local DB
                binding.tvCollabrate.visibility = View.VISIBLE
                binding.btnJoin.visibility = View.VISIBLE
                binding.btnJoin.setOnClickListener {
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                }

                binding.cardProfiler.visibility = View.GONE
                binding.nameProfiler.visibility = View.GONE
                binding.proffesssionProfiler.visibility = View.GONE
                binding.btnProfile.visibility = View.GONE
            }
        }
    }

    private fun setupSeeProjectsButton() {
        binding.seeProjects.setOnClickListener {
            val fragment = ProjectsFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()

            // Update Bottom Navigation Indicator
            val bottomNav = requireActivity().findViewById<SmoothBottomBar>(R.id.bottomBar)
            bottomNav.itemActiveIndex = 1
        }
    }

    private fun setupBannerFlipper() {
        binding.viewFlipper.startFlipping()
    }

    private fun setupCategoriesRecyclerView() {
        val categories = listOf(
            Category("App Development", R.drawable.ic_app_development, "#660EC746", "#D0E6A5"),
            Category("Web Development", R.drawable.ic_web_development, "#FF9A3D", "#FFB84D"),
            Category("UI Designing", R.drawable.ic_ui_designing, "#87CEEB", "#ADD8E3")
        )

        binding.rvCatagories.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = CategoriesAdapter(categories)
        }
    }

    private fun setupProjectsRecyclerView() {

        val projects = listOf(
            Project(
                ownerId = "user123",
                title = "Need a Graphic Designer with expertise in Adobe Illustrator and Photoshop",
                description = "We are looking for a skilled graphic designer to join our team. The ideal candidate should have experience with Photoshop, Illustrator, and other design tools.",
                requiredSkills = listOf("Adobe Illustrator", "Photoshop", "Graphic Design", "UI/UX"),
                applicants = listOf("user456", "user789"),
                selectedTeammate = null,
                deadline = Timestamp(1735948800, 0), // 1/4/2025
                isMyProject = false
            ),
            Project(
                ownerId = "user234",
                title = "Need an Android Native Developer with strong skills in Kotlin and Java",
                description = "Join our growing tech team as an Android Developer. Expertise in Kotlin, Java, and Android SDK is required. Experience with Firebase and RESTful APIs is a plus.",
                requiredSkills = listOf("Kotlin", "Java", "Android SDK", "Firebase", "REST APIs"),
                applicants = listOf("user567", "user890", "user123"),
                selectedTeammate = "user567",
                deadline = Timestamp(1735948800, 0), // 1/4/2025
                isMyProject = true
            ),
            Project(
                ownerId = "user345",
                title = "Need a Web Developer proficient in HTML, CSS, and JavaScript",
                description = "We are looking for a creative web designer to design functional and user-friendly websites. Proficiency in HTML, CSS, JavaScript, and design tools like Figma or Sketch is required.",
                requiredSkills = listOf("HTML", "CSS", "JavaScript", "Figma", "Responsive Design"),
                applicants = listOf("user678", "user901"),
                selectedTeammate = null,
                deadline = Timestamp(1735948800, 0), // 1/4/2025
                isMyProject = false
            ),
            Project(
                ownerId = "user456",
                title = "Need a Backend Developer with Node.js experience",
                description = "Looking for a backend developer to build scalable APIs using Node.js and Express. Knowledge of databases like MongoDB is required.",
                requiredSkills = listOf("Node.js", "Express", "MongoDB", "REST APIs", "Authentication"),
                applicants = listOf("user789", "user234", "user567"),
                selectedTeammate = null,
                deadline = Timestamp(1738540800, 0), // 2/2/2025
                isMyProject = true
            ),
            Project(
                ownerId = "user567",
                title = "UI/UX Designer for Mobile App Project",
                description = "Seeking a talented UI/UX designer to create beautiful and intuitive interfaces for our mobile application. Experience with prototyping tools is a must.",
                requiredSkills = listOf("UI Design", "UX Design", "Figma", "Prototyping", "Mobile Design"),
                applicants = listOf("user123", "user345"),
                selectedTeammate = "user123",
                deadline = Timestamp(1736640000, 0), // 1/12/2025
                isMyProject = false
            )
        )

        binding.rvProjects.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = ProjectAdapter(projects)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
