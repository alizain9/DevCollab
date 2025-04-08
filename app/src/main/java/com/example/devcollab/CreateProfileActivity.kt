package com.example.devcollab

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.Adapter.SkillsAdapter
import com.example.devcollab.databinding.ActivityCreateProfileBinding
import com.example.devcollab.databinding.AddSkillsDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CreateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProfileBinding
    private lateinit var skillsAdapter: SkillsAdapter

    private val skillsList = mutableListOf<String>()
    private val IMAGE_PICK_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCreateProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupRecyclerView()
        setupClickListeners()
    }

    // --------------------------
    // Image Picker
    // --------------------------
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            binding.profileImg.setImageURI(data?.data)
        }
    }

    // --------------------------
    // Skills List
    // --------------------------
    private fun setupRecyclerView() {
        skillsAdapter = SkillsAdapter(skillsList) { skill ->
            skillsList.remove(skill)
            skillsAdapter.notifyDataSetChanged()
        }

        binding.rvSkills.apply {
            layoutManager = LinearLayoutManager(this@CreateProfileActivity)
            adapter = skillsAdapter
        }
    }

    // --------------------------
    // Dialogs
    // --------------------------
    private fun setupClickListeners() {
        binding.cardUploadPic.setOnClickListener {
            pickImageFromGallery()
        }

        binding.btnAddSkills.setOnClickListener {
            showAddSkillsDialog()
        }
    }

    private fun showAddSkillsDialog() {
        val dialogBinding = AddSkillsDialogBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()
            .apply {
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                show()
            }

        dialogBinding.addButton.setOnClickListener {
            val skill = dialogBinding.skillEditText.text.toString().trim()
            val level = dialogBinding.levelEditText.text.toString().trim()

            if (skill.isNotEmpty() && level.isNotEmpty()) {
                skillsAdapter.addSkill("$skill ~ $level")
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.levelEditText.setOnClickListener {
            showLevelSuggestions(dialogBinding.levelEditText, listOf("Beginner", "Intermediate", "Expert"))
        }
    }

    private fun showLevelSuggestions(editText: EditText, levels: List<String>) {
        AlertDialog.Builder(this)
            .setTitle("Select Level")
            .setItems(levels.toTypedArray()) { _, which ->
                editText.setText(levels[which])
            }
            .show()
    }
}
