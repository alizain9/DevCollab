package com.example.devcollab

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.Adapter.ApplicantsAdapter
import com.example.devcollab.Adapter.MessagesAdapter
import com.example.devcollab.Model.Applicants
import com.example.devcollab.Model.Messages
import com.example.devcollab.databinding.ActivityApplicantsBinding

class ApplicantsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityApplicantsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityApplicantsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.rv_applicants)

        
        binding.backToProjects.setOnClickListener {
            finish()
        }

        val users = listOf(
            Applicants("Ali Khan", R.drawable.logo_image),
            Applicants("Zara Fatima", R.drawable.logo_image),
            Applicants("Umar Aziz", R.drawable.logo_image)
        )

        val adapter = ApplicantsAdapter(users)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}