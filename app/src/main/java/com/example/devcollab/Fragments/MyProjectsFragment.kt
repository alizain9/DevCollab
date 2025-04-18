package com.example.devcollab.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.Adapter.ProjectAdapter
import com.example.devcollab.Model.Project
import com.example.devcollab.databinding.FragmentMyProjectsBinding
import com.google.firebase.Timestamp

class MyProjectsFragment : Fragment() {

    private var _binding: FragmentMyProjectsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProjectsBinding.inflate(inflater, container, false)

        setupRecyclerView()
        return binding.root
    }

    /**
     * Initializes RecyclerView with project data.
     */
    private fun setupRecyclerView() {
        val projects = getProjectList()

        binding.rvMyProjects.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ProjectAdapter(projects)
        }
    }

    /**
     * Returns a list of sample projects.
     */
    private fun getProjectList(): List<Project> = listOf(
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
            isMyProject = false,
            tags = listOf("One", "Two")
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
