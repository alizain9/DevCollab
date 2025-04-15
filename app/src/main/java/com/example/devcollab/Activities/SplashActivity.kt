package com.example.devcollab.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.devcollab.R
import com.example.devcollab.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    // Lazy initialization for SharedPreferences
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("app_preferences", MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInsets()
        applyFadeInAnimation()
        checkFirstLaunch()


        }

    /**
     * Adjusts window insets for proper UI alignment.
     */
    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Applies a fade-in animation to the logo.
     */
    private fun applyFadeInAnimation() {
        val fadeIn = AlphaAnimation(0.0f, 1.0f).apply { duration = 1500 }
        binding.logoImg.startAnimation(fadeIn)
    }

    /**
     * Checks if it's the first launch and navigates accordingly.
     */
    private fun checkFirstLaunch() {
        val isFirstLaunch = sharedPreferences.getBoolean("is_first_launch", true)

        Handler(Looper.getMainLooper()).postDelayed({
            if (isFirstLaunch) {
                showWelcomeScreen()
            } else {
                navigateToMainActivity()
            }
        }, 2000) // 2-second delay
    }

    /**
     * Navigates to the welcome screen and updates the preference.
     */
    private fun showWelcomeScreen() {
        sharedPreferences.edit().putBoolean("is_first_launch", false).apply()
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    /**
     * Navigates to the main activity.
     */
    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
