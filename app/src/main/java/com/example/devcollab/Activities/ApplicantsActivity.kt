package com.example.devcollab.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.Adapter.ApplicantsAdapter
import com.example.devcollab.Model.Applicants
import com.example.devcollab.R
import com.example.devcollab.databinding.ActivityApplicantsBinding

class ApplicantsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplicantsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityApplicantsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInsets()
        setupRecyclerView()
        setupListeners()
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

    // --------------------------
    // RecyclerView Setup
    // --------------------------
    private fun setupRecyclerView() {
        val applicants = listOf(
            Applicants("Ali Khan", R.drawable.logo_image),
            Applicants("Zara Fatima", R.drawable.logo_image),
            Applicants("Umar Aziz", R.drawable.logo_image)
        )

        binding.rvApplicants.apply {
            layoutManager = LinearLayoutManager(this@ApplicantsActivity)
            adapter = ApplicantsAdapter(applicants)
        }
    }

    // --------------------------
    // Click Listeners
    // --------------------------
    private fun setupListeners() {
        binding.backToProjects.setOnClickListener {
            finish()
        }
    }
}
