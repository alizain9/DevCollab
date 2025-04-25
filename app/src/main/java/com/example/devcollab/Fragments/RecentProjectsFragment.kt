package com.example.devcollab.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.Adapter.ProjectAdapter
import com.example.devcollab.Database.Firestore.ProjectRepository
import com.example.devcollab.Model.Project
import com.example.devcollab.ViewModels.PostProjectViewModel
import com.example.devcollab.ViewModels.PostProjectViewModelFactory
import com.example.devcollab.databinding.FragmentRecentProjectsBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth

class RecentProjectsFragment : Fragment() {

    private var _binding: FragmentRecentProjectsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vmPost: PostProjectViewModel
    private lateinit var adapter: ProjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecentProjectsBinding.inflate(inflater, container, false)

        // Initialize ViewModel
        val repo = ProjectRepository()
        val factory = PostProjectViewModelFactory(repo)
        vmPost = ViewModelProvider(this, factory)[PostProjectViewModel::class.java]

        setupRecyclerView()
        fetchProjects()

        return binding.root
    }

    private fun setupRecyclerView() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        adapter = ProjectAdapter(mutableListOf(), currentUserUid) { project ->
        }
        binding.rvRecentProjects.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecentProjectsFragment.adapter
        }
    }

    private fun fetchProjects() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
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