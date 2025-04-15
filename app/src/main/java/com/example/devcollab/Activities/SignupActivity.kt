package com.example.devcollab.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.devcollab.Database.Firestore.AuthRepository
import com.example.devcollab.R
import com.example.devcollab.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = AuthRepository()


        binding.btnGotoLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        binding.btnCreateAccount.setOnClickListener {
            createNewAccount()
        }
    }
    private fun createNewAccount(){
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (!isValidEmail(email)){
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
            return
        }

        if (!isValidPassword(password)){
            Toast.makeText(this, "Password Must be atleast 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signUpWithEmail(email, password){ isSuccess, message ->
            if (isSuccess){
                startActivity(Intent(this, CreateProfileActivity::class.java))
                finish()
                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun isValidUsername(username: String): Boolean {
        val regex = "^[a-zA-Z0-9]+$".toRegex()
        return username.matches(regex)
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }
}