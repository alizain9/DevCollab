package com.example.devcollab.Activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.Adapter.TagAdapter
import com.example.devcollab.ViewModels.TagsViewModel
import com.example.devcollab.databinding.ActivityPostProjectBinding
import java.util.Calendar

class PostProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostProjectBinding
    private lateinit var tagViewModel: TagsViewModel
    private lateinit var tagAdapter: TagAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        setupBackButton()
        setupTags()
        setupDeadlinePicker()
    }

    // --------------------------
    // View and Insets
    // --------------------------
    private fun setupView() {
        enableEdgeToEdge()
        binding = ActivityPostProjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // --------------------------
    // Back Navigation
    // --------------------------
    private fun setupBackButton() {
        binding.backToProjects.setOnClickListener {
            finish()
        }
    }

    // --------------------------
    // Tags Section Setup
    // --------------------------
    private fun setupTags() {
        tagViewModel = ViewModelProvider(this)[TagsViewModel::class.java]

        tagAdapter = TagAdapter(mutableListOf()) { tag ->
            tagViewModel.removeTag(tag)
        }

        binding.rvTags.apply {
            layoutManager = LinearLayoutManager(this@PostProjectActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = tagAdapter
        }

        tagViewModel.tags.observe(this) { tags ->
            tagAdapter = TagAdapter(tags.toMutableList()) { tagViewModel.removeTag(it) }
            binding.rvTags.adapter = tagAdapter
        }

        binding.btnAddTag.setOnClickListener {
            val newTag = binding.etTags.text.toString().trim()
            if (newTag.isNotEmpty()) {
                tagViewModel.addTag(newTag)
                binding.etTags.text.clear()
            }
        }
    }

    // --------------------------
    // Deadline Picker Setup
    // --------------------------
    private fun setupDeadlinePicker() {
        binding.etDeadline.setOnClickListener {
            showDatePicker()
        }

        binding.etDeadline.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                binding.etDeadline.isFocusableInTouchMode = true
                binding.etDeadline.requestFocus()
            }
            false
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear)
                binding.etDeadline.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }
}
