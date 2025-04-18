package com.example.devcollab.Activities

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.devcollab.Adapter.AddSkillsAdapter
import com.example.devcollab.Database.Firestore.FirestoreRepository
import com.example.devcollab.Database.Firestore.UserModel
import com.example.devcollab.Database.Room.AppDatabase
import com.example.devcollab.Database.Room.UserEntity
import com.example.devcollab.R
import com.example.devcollab.databinding.ActivityCreateProfileBinding
import com.example.devcollab.databinding.AddSkillsDialogBinding
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class CreateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProfileBinding
    private lateinit var addSkillsAdapter: AddSkillsAdapter
    private var selectedImageUri: Uri? = null
    private var firestoreRepository = FirestoreRepository()
    private var mode: String? = null
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

        setupExperienceSuggestions()
        setupRecyclerView()
        setupClickListeners()

        // Retrieve data from Intent
        mode = intent.getStringExtra("mode")
        val username = intent.getStringExtra("username")
        val profession = intent.getStringExtra("profession")
        val about = intent.getStringExtra("about")
        val experience = intent.getStringExtra("experience")
        val skills = intent.getStringArrayListExtra("skills")
        val profileImageUrl = intent.getStringExtra("profileImageUrl")

        // Update UI based on mode
        if (mode == "edit") {
            binding.titleLayout.setText("Edit Profile")
            binding.btnCreateProfile.setText("Update Profile")
            binding.etUsername.setText(username)
            binding.etProffession.setText(profession)
            binding.etAbout.setText(about)
            binding.etExperience.setText(experience)
            skills?.let { skillsList.addAll(it) }
            addSkillsAdapter.notifyDataSetChanged()
            Glide.with(this).load(profileImageUrl).placeholder(R.drawable.user).into(binding.profileImg)
        } else {
            binding.titleLayout.setText("Complete your Profile")
            binding.btnCreateProfile.setText("Create Profile")
        }
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
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            selectedImageUri = data?.data
            binding.profileImg.setImageURI(data?.data)
        }
    }

    // --------------------------
    // Skills List
    // --------------------------
    private fun setupRecyclerView() {
        addSkillsAdapter = AddSkillsAdapter(skillsList) { skill ->
            skillsList.remove(skill)
            addSkillsAdapter.notifyDataSetChanged()
        }
        binding.rvSkills.apply {
            layoutManager = LinearLayoutManager(this@CreateProfileActivity)
            adapter = addSkillsAdapter
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
        binding.btnCreateProfile.setOnClickListener {
            uploadProfile()
        }
    }

    private fun setupExperienceSuggestions() {
        binding.etExperience.setOnClickListener {
            // Create a PopupMenu
            val popupMenu = PopupMenu(this, binding.etExperience)
            popupMenu.menu.add("Less than 1 year")
            popupMenu.menu.add("1-3 years")
            popupMenu.menu.add("3-5 years")
            popupMenu.menu.add("More than 5 years")

            // Handle item selection
            popupMenu.setOnMenuItemClickListener { menuItem ->
                binding.etExperience.setText(menuItem.title) // Set selected suggestion
                true
            }

            // Show the PopupMenu
            popupMenu.show()
        }
    }
    private fun uploadProfile() {
        val name = binding.etUsername.text.toString().trim()
        val about = binding.etAbout.text.toString().trim()
        val profession = binding.etProffession.text.toString().trim()
        val experience = binding.etExperience.text.toString().trim()

        // Validate input fields
        if (name.isEmpty()) {
            showToast("Enter your name")
            return
        }
        if (selectedImageUri == null && mode != "edit") {
            showToast("Select a profile picture")
            return
        }
        if (about.isEmpty()) {
            showToast("Enter your about")
            return
        }
        if (profession.isEmpty()) {
            showToast("Enter your profession")
            return
        }
        if (experience.isEmpty()) {
            showToast("Enter your experience")
            return
        }
        if (skillsList.isEmpty()) {
            showToast("Add at least one skill")
            return
        }
        // Show progress dialog
        showLoading(true)
        uploadDataToFirestore(name, about, profession, experience)
    }

    private fun uploadDataToFirestore(name: String, about: String, profession: String, experience: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val email = FirebaseAuth.getInstance().currentUser?.email
        if (uid == null || email == null) {
            showToast("User not logged in")
            showLoading(false)
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$uid.jpg")
        val imageUploadTask = if (selectedImageUri != null) {
            storageRef.putFile(selectedImageUri!!)
        } else {
            null // No new image to upload in edit mode
        }

        imageUploadTask?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val user = UserModel(
                    uid = uid,
                    username = name,
                    email = email,
                    about = about,
                    profession = profession,
                    experience = experience,
                    skills = skillsList,
                    profileImageUrl = uri.toString()
                )
                saveUserData(user)
            }.addOnFailureListener {
                showToast("Failed to retrieve download URL")
                showLoading(false)
            }
        }?.addOnFailureListener {
            showToast("Failed to upload image")
            showLoading(false)
        } ?: run {
            // If no new image is uploaded, use the existing profileImageUrl
            val existingImageUrl = intent.getStringExtra("profileImageUrl")
            val user = UserModel(
                uid = uid,
                username = name,
                email = email,
                about = about,
                profession = profession,
                experience = experience,
                skills = skillsList,
                profileImageUrl = existingImageUrl ?: ""
            )
            saveUserData(user)
        }
    }

    private fun saveUserData(user: UserModel) {
        firestoreRepository.saveUserProfile(user) { isSuccess, message ->
            if (isSuccess) {
                lifecycleScope.launch {
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
                    AppDatabase.getDatabase(this@CreateProfileActivity).userDao().insertUser(userProfile)
                }
                showToast("Profile ${if (mode == "edit") "updated" else "created"} successfully")
                showLoading(false)

                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                showToast(message.toString())
                showLoading(false)
            }
        }
    }

    private fun showAddSkillsDialog() {
        val dialogBinding = AddSkillsDialogBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .setCancelable(true)
            .create()

        val skillEditText = dialogBinding.skillEditText
        val spinner = dialogBinding.spinnerLevel
        val arrow = dialogBinding.spinnerArrow
        val levels = arrayOf("Select Skill Level", "Beginner", "Intermediate", "Expert")
        val customFont = ResourcesCompat.getFont(this, R.font.poppins_medium)

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, levels) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                view.setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                view.textSize = 14f
                customFont?.let { view.typeface = it }
                return view
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(Color.BLACK)
                view.textSize = 14f
                customFont?.let { view.typeface = it }
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0)

        spinner.setOnTouchListener { _, _ ->
            arrow.animate().rotation(180f).setDuration(200).start()
            false
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                arrow.animate().rotation(0f).setDuration(200).start()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        dialogBinding.addButton.setOnClickListener {
            val skill = skillEditText.text.toString().trim()
            val selectedLevel = spinner.selectedItem.toString()
            if (skill.isNotEmpty() && spinner.selectedItemPosition != 0) {
                skillsList.add("$skill: $selectedLevel")
                addSkillsAdapter.notifyDataSetChanged()
                dialog.dismiss()
                showToast("Skill Added")
            } else {
                showToast("Please enter skill and select level")
            }
        }

        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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