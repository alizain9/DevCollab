package com.example.devcollab.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.devcollab.Fragments.HomeFragment
import com.example.devcollab.Fragments.MessagesFragment
import com.example.devcollab.Fragments.ProfileFragment
import com.example.devcollab.Fragments.ProjectsFragment
import com.example.devcollab.R
import com.example.devcollab.databinding.ActivityMainBinding
import me.ibrahimsn.lib.SmoothBottomBar


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var backPressedTime: Long = 0
    private var backToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        loadFragment(HomeFragment())

        val bottomBar: SmoothBottomBar = findViewById(R.id.bottomBar)


        bottomBar.onItemSelected = { position ->
            val fragment = when (position) {
                0 -> HomeFragment()
                1 -> ProjectsFragment()
                2 -> MessagesFragment()
                3 -> ProfileFragment()
                else -> null
            }

            fragment?.let { loadFragment(it) }

        }


    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backToast?.cancel()
            super.onBackPressed()
        } else {
            backToast = Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT)
            backToast?.show()
            backPressedTime = System.currentTimeMillis()
        }
    }
}