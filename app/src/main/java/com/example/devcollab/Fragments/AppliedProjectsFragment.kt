package com.example.devcollab.Fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.Activities.LoginActivity
import com.example.devcollab.Adapter.ProjectAdapter
import com.example.devcollab.Database.Firestore.AuthRepository
import com.example.devcollab.Database.Firestore.ProjectRepository
import com.example.devcollab.Fragments.RecentProjectsFragment
import com.example.devcollab.R
import com.example.devcollab.ViewModels.PostProjectViewModel
import com.example.devcollab.ViewModels.PostProjectViewModelFactory
import com.example.devcollab.databinding.FragmentAppliedProjectsBinding
import com.google.firebase.auth.FirebaseAuth

class AppliedProjectsFragment : Fragment() {
    private var _binding: FragmentAppliedProjectsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vmPost: PostProjectViewModel
    private lateinit var adapter: ProjectAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
      _binding = FragmentAppliedProjectsBinding.inflate(inflater, container, false)

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
        binding.rvAppliedProjects.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AppliedProjectsFragment.adapter
        }
    }

    private fun fetchProjects() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            binding.loginPromptContainer.visibility = View.GONE
            vmPost.fetchAppliedProjects(currentUser.uid)
            observeViewModel()
        } else{
           showLoginPrompt()
        }

    }

    private fun observeViewModel() {
        vmPost.appliedProjects.observe(viewLifecycleOwner) { fetchedProjects ->
            if (fetchedProjects.isEmpty()){
                binding.loginPromptContainer.visibility = View.GONE
                binding.appLiedProjecetStatus.visibility = View.VISIBLE
                binding.rvAppliedProjects.visibility = View.GONE
            } else{
                binding.loginPromptContainer.visibility = View.GONE
                binding.appLiedProjecetStatus.visibility = View.GONE
                binding.rvAppliedProjects.visibility = View.VISIBLE
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

    override fun onResume() {
        super.onResume()
        fetchProjects()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}