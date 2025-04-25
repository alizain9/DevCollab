package com.example.devcollab.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.devcollab.Database.Firestore.UserRepository
import com.example.devcollab.R
import com.example.devcollab.databinding.ActivityViewDetailsBinding

class ViewDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewDetailsBinding
    private lateinit var repo: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityViewDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        repo = UserRepository()

        // Retrieve data from the Intent
        val ownerId = intent.getStringExtra("ownerId")
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val requiredSkills = intent.getStringExtra("requiredSkills")
        val deadline = intent.getStringExtra("deadline")

        binding.textTitle.text = title
        binding.textDescription.text = description
        binding.textDeadline.text = deadline
        binding.textRequiredSkills.text = requiredSkills

        repo.fetchUserProfile(ownerId!!){ user ->
            binding.textClientName.text = user?.username
        }



        binding.backToProjects.setOnClickListener {
            finish()
        }
    }
}