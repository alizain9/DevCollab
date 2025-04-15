package com.example.devcollab.Activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.devcollab.Adapter.ProfileSkillsAdapter
import com.example.devcollab.Database.Room.AppDatabase
import com.example.devcollab.R
import com.example.devcollab.databinding.ActivityMyProfileBinding
import kotlinx.coroutines.launch

class MyProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.icBack.setOnClickListener {
            finish()
        }

        fetchProfileData()

        binding.icEdit.setOnClickListener {
            val intent = Intent(this, CreateProfileActivity::class.java)
            intent.putExtra("name", binding.name.text.toString())
            intent.putExtra("proffession", binding.proffession.text.toString())
            intent.putExtra("about", binding.about.text.toString())
            startActivity(intent)
        }
    }

    private fun fetchProfileData() {

        lifecycleScope.launch {
            try {
                val user = AppDatabase.getDatabase(this@MyProfileActivity).userDao().getUser()
                if (user != null) {
                    binding.name.text = user.username
                    binding.proffession.text = user.profession
                    binding.about.text = user.about

                    val skillsAdapter = ProfileSkillsAdapter(user.skills)
                    binding.rvSkillsProfile.adapter = skillsAdapter
                    binding.rvSkillsProfile.layoutManager =
                        LinearLayoutManager(this@MyProfileActivity)


                    val imgProfile = user.profileImageUrl
                    Glide.with(this@MyProfileActivity)
                        .load(imgProfile)
                        .placeholder(R.drawable.user)
                        .into(binding.imageProfile)
                }

            } catch (e: Exception) {

            }
        }
    }
}