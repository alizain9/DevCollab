package com.example.devcollab.Activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.Adapter.ProjectAdapter
import com.example.devcollab.Database.Firestore.ProjectRepository
import com.example.devcollab.R
import com.example.devcollab.ViewModels.PostProjectViewModel
import com.example.devcollab.ViewModels.PostProjectViewModelFactory
import com.example.devcollab.databinding.ActivityCategoryBinding
import com.google.firebase.auth.FirebaseAuth

class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var viewModel: PostProjectViewModel
    private lateinit var adapter: ProjectAdapter
    private lateinit var skillType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle window insets for full screen support
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get skill type from intent
        skillType = intent.getStringExtra("skill") ?: return
        if (skillType == "Android Development"){
            binding.headingCategory.text = "Android Development"
        }else if (skillType == "Web Development"){
            binding.headingCategory.text = "Web Development"
        } else{
            binding.headingCategory.text = "Ui Designing"
        }

        Log.d("CategoryActivity", "Skill type received: $skillType")

        // Initialize adapter
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        adapter = ProjectAdapter(mutableListOf(), currentUserUid) { project ->
            // Handle apply click if needed
        }

        // Setup RecyclerView
        binding.rvCatagories.layoutManager = LinearLayoutManager(this)
        binding.rvCatagories.adapter = adapter

        // Show loading spinner
        binding.spinKit.visibility = View.VISIBLE
        binding.noProjectsTextview.visibility = View.GONE

        // Setup ViewModel
        val repo = ProjectRepository()
        val factory = PostProjectViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[PostProjectViewModel::class.java]

        // Observe project list
        viewModel.categoryProjects.observe(this) { projects ->
            binding.spinKit.visibility = View.GONE

            if (projects.isNullOrEmpty()) {
                binding.noProjectsTextview.visibility = View.VISIBLE
                binding.rvCatagories.visibility = View.GONE
            } else {
                binding.noProjectsTextview.visibility = View.GONE
                binding.rvCatagories.visibility = View.VISIBLE
                adapter.submitList(projects)
            }
        }

        // Fetch projects by skill
        viewModel.fetchProjectsBySkill(skillType)
    }
}
