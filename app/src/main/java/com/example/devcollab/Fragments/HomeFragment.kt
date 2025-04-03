package com.example.devcollab.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.Adapter.CategoriesAdapter
import com.example.devcollab.Adapter.ProjectAdapter
import com.example.devcollab.LoginActivity
import com.example.devcollab.Model.Category
import com.example.devcollab.Model.Project
import com.example.devcollab.R
import com.example.devcollab.databinding.FragmentHomeBinding
import me.ibrahimsn.lib.SmoothBottomBar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupJoinButton()
        setupSeeProjectsButton()
        setupBannerFlipper()
        setupCategoriesRecyclerView()
        setupProjectsRecyclerView()

        return binding.root
    }

    private fun setupJoinButton() {
        binding.btnJoin.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
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
                "Need a Graphic Designer with expertise in Adobe Illustrator and Photoshop",
                "Graphic Designer",
                "We are looking for a skilled graphic designer to join our team. The ideal candidate should have experience with Photoshop, Illustrator, and other design tools.",
                "1/4/2025", false
            ),
            Project(
                "Need an Android Native Developer with strong skills in Kotlin and Java",
                "App Development",
                "Join our growing tech team as an Android Developer. Expertise in Kotlin, Java, and Android SDK is required. Experience with Firebase and RESTful APIs is a plus.",
                "1/4/2025", false
            ),
            Project(
                "Need a Web Developer proficient in HTML, CSS, and JavaScript",
                "Web Development",
                "We are looking for a creative web designer to design functional and user-friendly websites. Proficiency in HTML, CSS, JavaScript, and design tools like Figma or Sketch is required.",
                "1/4/2025", false
            ),
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
