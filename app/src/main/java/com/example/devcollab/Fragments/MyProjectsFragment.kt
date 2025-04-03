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
    private var _binding: FragmentMyProjectsBinding ?= null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMyProjectsBinding.inflate(inflater, container, false)

        // RecyclerView Setup
        binding.rvMyProjects.layoutManager = LinearLayoutManager(requireContext())
        val projects = listOf(
            Project(
                "Need a Graphic Designer with expertise in Adobe Illustrator and Photoshop",
                "Graphic Designer",
                "We are looking for a skilled graphic designer to join our team. The ideal candidate should have experience with Photoshop, Illustrator, and other design tools.",
                "1/4/2025", true
            ),
            Project(
                "Need an Android Native Developer with strong skills in Kotlin and Java",
                "App Development",
                "Join our growing tech team as an Android Developer. Expertise in Kotlin, Java, and Android SDK is required. Experience with Firebase and RESTful APIs is a plus.",
                "1/4/2025", true
            ),
            Project(
                "Need a Web Developer proficient in HTML, CSS, and JavaScript",
                "Web Development",
                "We are looking for a creative web designer to design functional and user-friendly websites. Proficiency in HTML, CSS, JavaScript, and design tools like Figma or Sketch is required.",
                "1/4/2025",true
            ),
        )
        binding.rvMyProjects.adapter = ProjectAdapter(projects)

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}