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
            title = "Need a Graphic Designer with expertise in Adobe Illustrator and Photoshop",
            field = "Graphic Designer",
            description = "We are looking for a skilled graphic designer to join our team. " +
                    "The ideal candidate should have experience with Photoshop, Illustrator, and other design tools.",
            date = "1/4/2025",
            isMyProject = true
        ),
        Project(
            title = "Need an Android Native Developer with strong skills in Kotlin and Java",
            field = "App Development",
            description = "Join our growing tech team as an Android Developer. Expertise in Kotlin, Java, " +
                    "and Android SDK is required. Experience with Firebase and RESTful APIs is a plus.",
            date = "1/4/2025",
            isMyProject = true
        ),
        Project(
            title = "Need a Web Developer proficient in HTML, CSS, and JavaScript",
            field = "Web Development",
            description = "We are looking for a creative web designer to design functional and user-friendly websites. " +
                    "Proficiency in HTML, CSS, JavaScript, and design tools like Figma or Sketch is required.",
            date = "1/4/2025",
            isMyProject = true
        )
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
