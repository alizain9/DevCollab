package com.example.devcollab.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.Adapter.ApplicantsAdapter
import com.example.devcollab.Database.Firestore.ProjectRepository
import com.example.devcollab.Model.Applicants
import com.example.devcollab.R
import com.example.devcollab.ViewModels.PostProjectViewModel
import com.example.devcollab.ViewModels.PostProjectViewModelFactory
import com.example.devcollab.databinding.ActivityApplicantsBinding

class ApplicantsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplicantsBinding
    private lateinit var viewModel: PostProjectViewModel
    private lateinit var adapter: ApplicantsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inflate the layout and set content view
        binding = ActivityApplicantsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize UI components and listeners
        setupInsets()
        setupListeners()

        // Retrieve project ID from intent
        val projectId = intent.getStringExtra("projectId") ?: ""

        // Initialize ViewModel and Repository
        val repo = ProjectRepository()
        val factory = PostProjectViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(PostProjectViewModel::class.java)

        // Set up RecyclerView
        setupRecyclerView()

        // Fetch applicants and update UI
        fetchAndDisplayApplicants(projectId)
    }

    // --------------------------
    // System Bars Insets
    // --------------------------
    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // RecyclerView Setup
    private fun setupRecyclerView() {
        val recyclerView = binding.rvApplicants
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ApplicantsAdapter(emptyList()){uid ->
            val intent = Intent(this, MyProfileActivity::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    // --------------------------
    // Click Listeners
    // --------------------------
    private fun setupListeners() {
        binding.backToProjects.setOnClickListener {
            finish()
        }
    }

    // --------------------------
    // Fetch and Display Applicants
    // --------------------------
    private fun fetchAndDisplayApplicants(projectId: String) {
        showLoading(true)

        viewModel.fetchApplicants(projectId).observe(this) { applicants ->
            showLoading(false)

            if (applicants.isEmpty()) {
                // Show "No Applicants" message
                binding.rvApplicants.visibility = View.GONE
                binding.noApplicantsTextView.visibility = View.VISIBLE
            } else {
                // Update RecyclerView with fetched applicants
                binding.rvApplicants.visibility = View.VISIBLE
                binding.noApplicantsTextView.visibility = View.GONE

                adapter = ApplicantsAdapter(applicants){uid ->
                    val intent = Intent(this, MyProfileActivity::class.java)
                    intent.putExtra("uid", uid)
                    startActivity(intent)
                }
                binding.rvApplicants.adapter = adapter
            }
        }
    }

    // --------------------------
    // Loading Spinner
    // --------------------------
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.spinKit.visibility = View.VISIBLE
            window.setFlags(
                android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            binding.spinKit.visibility = View.GONE
            window.clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }
}