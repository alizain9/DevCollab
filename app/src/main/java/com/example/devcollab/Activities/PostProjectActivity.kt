package com.example.devcollab.Activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.Adapter.TagAdapter
import com.example.devcollab.ViewModels.TagsViewModel
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.example.devcollab.Activities.MyProfileActivity
import com.example.devcollab.Database.Firestore.ProjectRepository
import com.example.devcollab.ViewModels.PostProjectViewModel
import com.example.devcollab.ViewModels.PostProjectViewModelFactory
import com.example.devcollab.databinding.ActivityPostProjectBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostProjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostProjectBinding
    private lateinit var tagViewModel: TagsViewModel
    private lateinit var tagAdapter: TagAdapter
    private lateinit var postVM: PostProjectViewModel
    private var pickedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
        checkLoginStatus()
        setupBackButton()

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

    private fun checkLoginStatus() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            binding.loginPromptContainer.visibility = View.GONE
            binding.infoContainer.visibility = View.VISIBLE

            setupTags()
            setupSkillsAutoComplete()
            setupDeadlinePicker()
            setupViewModel()
            setupPostButton()
            observeViewModel()


        } else{
            binding.infoContainer.visibility = View.GONE
            showLoginPrompt()
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
    // AutoComplete to select skills
    // --------------------------

    private fun setupSkillsAutoComplete() {
        val skills = listOf(
            "Android Development", "iOS Development", "Web Development", "React", "Angular",
            "Vue.js", "Node.js", "Java", "Kotlin", "Python", "C++", "C#", "PHP", "SQL", "NoSQL",
            "UI/UX Design", "Graphic Design", "Machine Learning", "Deep Learning", "NLP",
            "Cybersecurity", "DevOps", "Cloud Computing", "AWS", "Azure", "GCP",
            "Blockchain", "Data Science", "Data Analysis", "QA Testing", "Software Engineering"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, skills)
        val autoCompleteTextView = binding.etRequiredSkills as AutoCompleteTextView
        autoCompleteTextView.setAdapter(adapter)
    }


    // --------------------------
    // Tags Section Setup
    // --------------------------
    private fun setupTags() {
        tagViewModel = ViewModelProvider(this).get(TagsViewModel::class.java)

        // 1) create one adapter instance
        tagAdapter = TagAdapter(emptyList()) { tagViewModel.removeTag(it) }
        binding.rvTags.apply {
            layoutManager = LinearLayoutManager(this@PostProjectActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = tagAdapter
        }

        // 2) observe once, update the adapter’s list
        tagViewModel.tags.observe(this) { tags ->
           //  Log.d("PostProject", "Tags updated: $tags")
            tagAdapter.submitList(tags)
        }

        // 3) on button click, add to the VM
        binding.btnAddTag.setOnClickListener {
            val newTag = binding.etTags.text.toString().trim()
            if (newTag.isNotEmpty()) {
                tagViewModel.addTag(newTag)
                binding.etTags.setText("")   // clear the input
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
        val cal = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                cal.set(year, month, day)
                pickedDate = cal.time
                binding.etDeadline.setText(
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(cal.time)
                )
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis()
            show()
        }
    }



    // --------------------------
    // ViewModel wiring
    // --------------------------
    private fun setupViewModel() {
        val repo    = ProjectRepository()
        val factory = PostProjectViewModelFactory(repo)
        postVM      = ViewModelProvider(this, factory)[PostProjectViewModel::class.java]
    }

    private fun setupPostButton() {
        binding.btnPostProject.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user == null) {
                Toast.makeText(this, "You must be signed in first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.etProjectTitle.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.etDescription.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.etRequiredSkills.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Please enter Required skills", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.etDeadline.text.toString().trim().isEmpty()){
                Toast.makeText(this, "Please select a deadline", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val title       = binding.etProjectTitle.text.toString().trim()
            val desc        = binding.etDescription.text.toString().trim()
            val skillsInput = (binding.etRequiredSkills as AutoCompleteTextView)
                .text.toString()
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            // convert pickedDate -> Firestore Timestamp
            val deadlineTs: Timestamp? = pickedDate?.let { Timestamp(it) }
            val tagsList: List<String> = tagViewModel.tags.value ?: emptyList()

          //  Toast.makeText(this, "Tags = $tagsList", Toast.LENGTH_SHORT).show()

            postVM.postProject(

                ownerId     = user.uid,
                title       = title,
                description = desc,
                skills      = skillsInput,
                tags        = tagsList,
                deadlineTs  = deadlineTs,
            )
        }
    }

    private fun observeViewModel() {
        postVM.loading.observe(this) { loading ->
            binding.spinKit.visibility = if (loading) View.VISIBLE else View.GONE
        }
        postVM.success.observe(this) { ok ->
            if (ok) {
                Toast.makeText(this, "Posted successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to post. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showLoginPrompt() {
        // Show the semi-transparent background and login prompt
        binding.loginPromptContainer.visibility = View.VISIBLE

        // Handle login button click
        binding.btnLogin.setOnClickListener {
            // Navigate to the login screen
            val intent = Intent(this@PostProjectActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }

}
