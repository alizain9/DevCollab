package com.example.devcollab.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.devcollab.Const.Constants
import com.example.devcollab.Database.Firestore.AuthRepository
import com.example.devcollab.Database.Firestore.FirestoreRepository
import com.example.devcollab.Database.Firestore.UserModel
import com.example.devcollab.Database.Room.AppDatabase
import com.example.devcollab.Database.Room.UserEntity
import com.example.devcollab.R
import com.example.devcollab.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var authRepo: AuthRepository
    private val firestoreRepository = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        authRepo = AuthRepository()


        // Navigate to SignupActivity
        binding.btnGotoSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }

        // Handle login button click
        binding.btnLogin.setOnClickListener {
            loginToAccount()
        }
    }

    private fun loginToAccount() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Validate email
        if (!authRepo.isValidEmail(email)) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate password
        if (!authRepo.isValidPassword(password)) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ Login to account
        showLoading(true)
        authRepo.loginWithEmail(email, password){ isSuccess, message ->
            if (isSuccess) {
                checkProfileCompletion()
                showLoading(false)
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
            } else {
                showLoading(false)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkProfileCompletion() {

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val uid = user.uid
           firestoreRepository.fetchUserProfile(uid) { user ->
               if (user != null) {
                   val userProfile = UserEntity(
                       uid = user.uid,
                       username = user.username,
                       email = user.email,
                       about = user.about,
                       profession = user.profession,
                       experience = user.experience,
                       skills = user.skills,
                       profileImageUrl = user.profileImageUrl
                   )
                   lifecycleScope.launch {
                       try {
                           // Insert the user profile into the Room database
                           AppDatabase.getDatabase(this@LoginActivity).userDao()
                               .insertUser(userProfile)
                           // Navigate to MainActivity after insertion is complete
                           startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                           finish()
                       } catch (e: Exception) {
                           e.printStackTrace()
                           Toast.makeText(
                               this@LoginActivity,
                               "Failed to save user data",
                               Toast.LENGTH_SHORT
                           ).show()
                           showLoading(false)
                       }
                   }
               } else {
                   // User profile not found, navigate to CreateProfileActivity
                   showLoading(false)
                   startActivity(Intent(this, CreateProfileActivity::class.java))
                   finish()
               }
           }
        } else {
            // User not logged in, navigate to LoginActivity
            showLoading(false)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

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