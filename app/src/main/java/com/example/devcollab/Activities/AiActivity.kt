package com.example.devcollab.Activities

import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.devcollab.Api.GeminiViewModel
import com.example.devcollab.R
import com.example.devcollab.databinding.ActivityAiBinding

class AiActivity : AppCompatActivity() {

    // View binding and ViewModel
    private lateinit var binding: ActivityAiBinding
    private val viewModel: GeminiViewModel by viewModels()

    // Request throttling
    private var lastRequestTime = 0L
    private val minRequestInterval = 10000 // 10 seconds in ms

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupEdgeToEdge()
        initializeViews()
        setupTitleBasedOnType()
        setupTitleGradient()
        setupClickListeners()
    }

    private fun setupEdgeToEdge() {
        enableEdgeToEdge()
        binding = ActivityAiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initializeViews() {
        binding = ActivityAiBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupTitleBasedOnType() {
        val type = intent.getStringExtra("type") ?: "title"
        if (type == "title") {
            binding.title.text = "Generate Project Title\nwith AI"
            binding.etPrompt.hint = "Describe your project for a title"
            binding.etResult.hint = "AI-generated title"
        } else {
            binding.title.text = "Generate Project Description\nwith AI"
            binding.etPrompt.hint = "Explain your project briefly"
            binding.etResult.hint = "AI-generated description"
        }
    }

    private fun setupTitleGradient() {
        val shader = LinearGradient(
            0f, 0f, 0f, binding.title.textSize,
            intArrayOf(
                Color.parseColor("#00FF99"),
                Color.parseColor("#00C6FF")
            ),
            null,
            Shader.TileMode.CLAMP
        )
        binding.title.paint.shader = shader
    }

    private fun setupClickListeners() {
        binding.btnGenerate.setOnClickListener { handleGenerateClick() }
        binding.btnCopy.setOnClickListener { handleCopyClick() }
        binding.btnRefine.setOnClickListener { handleRefineClick() }
        binding.icBack.setOnClickListener { finish() }
    }

    private fun handleGenerateClick() {
        if (!canSendRequest()) {
            Toast.makeText(this, "Please wait few seconds for another request", Toast.LENGTH_SHORT).show()
            return
        }

        val prompt = binding.etPrompt.text.toString()
        if (prompt.isEmpty()) {
            Toast.makeText(this, "Enter a description first", Toast.LENGTH_SHORT).show()
            return
        }

        val type = intent.getStringExtra("type") ?: "title"
        val finalPrompt = if (type == "title") {
            getString(R.string.ai_prompt_title, prompt).trimIndent()
        } else {
            getString(R.string.ai_prompt_description, prompt).trimIndent()
        }

        generateAiResponse(finalPrompt)
    }

    private fun handleCopyClick() {
        val result = binding.etResult.text.toString()
        if (result.isEmpty()) {
            Toast.makeText(this, "No text to copy", Toast.LENGTH_SHORT).show()
            return
        }

        val resultIntent = Intent().apply {
            putExtra("ai_result", result)
            putExtra("type", intent.getStringExtra("type"))
        }
        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun handleRefineClick() {
        val text = binding.etPrompt.text.toString()
        if (text.isEmpty()) {
            Toast.makeText(this, "Enter the text first", Toast.LENGTH_SHORT).show()
            return
        }

        val refinePrompt = getString(R.string.ai_prompt_refinement, text).trimIndent()
        generateAiResponse(refinePrompt)
    }

    private fun canSendRequest(): Boolean {
        val currentTime = System.currentTimeMillis()
        return if (currentTime - lastRequestTime > minRequestInterval) {
            lastRequestTime = currentTime
            true
        } else {
            false
        }
    }

    private fun generateAiResponse(prompt: String) {
        viewModel.generateText(
            prompt,
            onSuccess = { result ->
                binding.etResult.setText(result)
                binding.etPrompt.setText(null)
            },
            onError = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )
    }
}