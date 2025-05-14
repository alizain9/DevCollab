package com.example.devcollab.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.Activities.LoginActivity
import com.example.devcollab.Adapter.ProjectAdapter
import com.example.devcollab.Database.Firestore.AuthRepository
import com.example.devcollab.Database.Firestore.ProjectRepository
import com.example.devcollab.Model.Project
import com.example.devcollab.ViewModels.PostProjectViewModel
import com.example.devcollab.ViewModels.PostProjectViewModelFactory
import com.example.devcollab.databinding.FragmentMyProjectsBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
class MyProjectsFragment : Fragment() {

    private var _binding: FragmentMyProjectsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vmPost: PostProjectViewModel
    private lateinit var adapter: ProjectAdapter
    private  var authRepository: AuthRepository  = AuthRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProjectsBinding.inflate(inflater, container, false)

        // Initialize ViewModel
        val repo = ProjectRepository()
        val factory = PostProjectViewModelFactory(repo)
        vmPost = ViewModelProvider(this, factory)[PostProjectViewModel::class.java]

        setupRecyclerView()
        fetchMyProjects()

        return binding.root
    }

    private fun setupRecyclerView() {
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        adapter = ProjectAdapter(mutableListOf(), currentUserUid) { _ ->
            // No "Apply" action for "My Projects"
        }
        binding.rvMyProjects.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@MyProjectsFragment.adapter
        }
    }

    private fun fetchMyProjects(forceRefresh: Boolean = false) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            binding.loginPromptContainer.visibility = View.GONE
            vmPost.fetchUserProjects(currentUser.uid)
            observeViewModel()
        } else {
          showLoginPrompt()
        }
    }

    private fun observeViewModel() {
        vmPost.projects.observe(viewLifecycleOwner) { fetchedProjects ->
            if (fetchedProjects.isEmpty()){
                binding.loginPromptContainer.visibility = View.GONE
                binding.noProjectsTextview.visibility = View.VISIBLE
                binding.rvMyProjects.visibility = View.GONE
            } else {
                binding.loginPromptContainer.visibility = View.GONE
                binding.noProjectsTextview.visibility = View.GONE
                binding.rvMyProjects.visibility = View.VISIBLE
                adapter.submitList(fetchedProjects)
            }
        }

        vmPost.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.spinKit.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showLoginPrompt() {
        // Show the semi-transparent background and login prompt
        binding.loginPromptContainer.visibility = View.VISIBLE

        // Handle login button click
        binding.btnLogin.setOnClickListener {
            // Navigate to the login screen
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun refreshMyProjectsAfterPost() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        vmPost.fetchUserProjects(uid, forceRefresh = true)
    }



    override fun onResume() {
        super.onResume()
        vmPost.shouldRefreshMyProjects.observe(viewLifecycleOwner) { shouldRefresh ->
            if (shouldRefresh == true) {
                fetchMyProjects(forceRefresh = true)
                vmPost.onMyProjectsRefreshed()
            }else{
                fetchMyProjects()
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}