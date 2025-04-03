package com.example.devcollab

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AlphaAnimation
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.devcollab.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Fade in Animation
        val fadeIn = AlphaAnimation(0.0f, 1.0f).apply {
            duration = 1500
        }
        binding.logoImg.startAnimation(fadeIn)

        // Check if the welcome screen has been shown before
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)

        // Delay before moving to the next activity
        Handler(Looper.getMainLooper()).postDelayed({
            if (isFirstLaunch) {
                showWelcomeScreen(sharedPreferences)
            } else {
                navigateToMainActivity()
            }
        }, 2000) // 2-second delay
    }

    private fun showWelcomeScreen(sharedPreferences: android.content.SharedPreferences) {
        // Mark the welcome screen as shown
        sharedPreferences.edit().putBoolean("is_first_launch", false).apply()

        // Navigate to WelcomeActivity
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    private fun navigateToMainActivity() {
        // Navigate to MainActivity
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}