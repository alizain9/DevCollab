package com.example.devcollab.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide

import com.example.devcollab.Adapter.CategoriesAdapter
import com.example.devcollab.Adapter.ProjectAdapter
import com.example.devcollab.Activities.LoginActivity
import com.example.devcollab.Activities.MyProfileActivity
import com.example.devcollab.Database.Firestore.ProjectRepository

import com.example.devcollab.Database.Room.AppDatabase
import com.example.devcollab.Fragments.RecentProjectsFragment
import com.example.devcollab.Model.Category
import com.example.devcollab.Model.Contributer
import com.example.devcollab.Model.Project
import com.example.devcollab.R
import com.example.devcollab.ViewModels.PostProjectViewModel
import com.example.devcollab.ViewModels.PostProjectViewModelFactory
import com.example.devcollab.databinding.FragmentHomeBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

import kotlinx.coroutines.launch
import me.ibrahimsn.lib.SmoothBottomBar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var vmPost: PostProjectViewModel
    private lateinit var adapter: ProjectAdapter

    private lateinit var img1: ImageView
    private lateinit var name1: TextView
    private lateinit var prof1: TextView

    private lateinit var img2: ImageView
    private lateinit var name2: TextView
    private lateinit var prof2: TextView

    private lateinit var img3: ImageView
    private lateinit var name3: TextView
    private lateinit var prof3: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Init views
        img1 = binding.contributerOne
        name1 = binding.conOneName
        prof1 = binding.conOneProfession

        // Init views
        img2 = binding.contributerTwo
        name2 = binding.conTwoName
        prof2 = binding.conTwoProfession

        // Init views
        img3 = binding.contributerThree
        name3 = binding.conThreeName
        prof3 = binding.conThreeProfession

        setUphometop()
        setupSeeProjectsButton()
        setupBannerFlipper()
        setupCategoriesRecyclerView()

        val searchView = view?.findViewById<SearchView>(R.id.searchView)
        searchView?.queryHint = "Search here By Category"


        // Initialize ViewModel
        val repo = ProjectRepository()
        val factory = PostProjectViewModelFactory(repo)
        vmPost = ViewModelProvider(this, factory)[PostProjectViewModel::class.java]

        binding.cardProfiler.setOnClickListener {
            // jump on the Profile Activity
            startActivity(Intent(requireContext(), MyProfileActivity::class.java))
        }

        vmPost.loadTopContributors()
        setupPostRecylerview()
        fetchProjects()
        observeViewModels()



        return binding.root
    }

    private fun observeViewModels() {
        vmPost.topContributors.observe(viewLifecycleOwner) { users ->
            if (users.isNotEmpty()) bindContributor(0, users[0])
            if (users.size > 1) bindContributor(1, users[1])
            if (users.size > 2) bindContributor(2, users[2])
        }
    }
    private fun bindContributor(index: Int, user: Contributer) {
        when (index) {
            0 -> {
                name1.text = user.name
                prof1.text = user.profession
                Glide.with(this).load(user.profileImage).into(img1)
            }
            1 -> {
                name2.text = user.name
                prof2.text = user.profession
                Glide.with(this).load(user.profileImage).into(img2)
            }
            2 -> {
                name3.text = user.name
                prof3.text = user.profession
                Glide.with(this).load(user.profileImage).into(img3)
            }
        }
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

    private fun setContributer(){

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

    private fun setupPostRecylerview() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        adapter = ProjectAdapter(mutableListOf(), currentUserUid) { project ->
        }

        binding.rvProjects.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
        }
    }

    private fun fetchProjects() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null){
            vmPost.fetchRecentProjects(currentUser.uid)
        } else {
            vmPost.fetchProjects()
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        vmPost.projects.observe(viewLifecycleOwner) { fetchedProjects ->
            adapter.submitList(fetchedProjects)
        }

        vmPost.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.spinKit.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        fetchProjects()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
