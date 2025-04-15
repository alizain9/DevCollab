package com.example.devcollab.Activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.Adapter.AddSkillsAdapter
import com.example.devcollab.Database.Firestore.FirestoreRepository
import com.example.devcollab.Database.Firestore.UserModel
import com.example.devcollab.Database.Room.AppDatabase
import com.example.devcollab.Database.Room.UserEntity
import com.example.devcollab.R
import com.example.devcollab.databinding.ActivityCreateProfileBinding
import com.example.devcollab.databinding.AddSkillsDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch

class CreateProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateProfileBinding
    private lateinit var addSkillsAdapter: AddSkillsAdapter
    private var selectedImageUri: Uri? = null
    private var firestoreRepository = FirestoreRepository()

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

    private fun uploadProfile() {

        val name = binding.etUsername.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show()
            return@uploadProfile
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Select a profile picture", Toast.LENGTH_SHORT).show()
            return@uploadProfile
        }

        val about = binding.etAbout.text.toString().trim()
        if (about.isEmpty()) {
            Toast.makeText(this, "Enter your about", Toast.LENGTH_SHORT).show()
            return@uploadProfile
        }

        val profession = binding.etProffession.text.toString().trim()
        if (profession.isEmpty()) {
            Toast.makeText(this, "Enter your profession", Toast.LENGTH_SHORT).show()
            return@uploadProfile
        }

        val experience = binding.etExperience.text.toString().trim()
        if (experience.isEmpty()) {
            Toast.makeText(this, "Enter your experience", Toast.LENGTH_SHORT).show()
            return@uploadProfile
        }
        val skills = skillsList.joinToString(", ")
        if (skills.isEmpty()) {
            Toast.makeText(this, "Enter your skills", Toast.LENGTH_SHORT).show()
            return@uploadProfile
        }

        uploadDatatoFirestore(name, about, profession, experience)


    }

    private fun uploadDatatoFirestore(name: String, about: String, profession: String, experience: String){
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val email = FirebaseAuth.getInstance().currentUser?.email

        if (uid == null || email == null || selectedImageUri == null) {
            Toast.makeText(this, "User not logged in or image missing", Toast.LENGTH_SHORT).show()
            return
        }

        val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/$uid.jpg")
        storageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener {
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

                    firestoreRepository.saveUserProfile(user) { isSuccess, message ->
                        if (isSuccess) {
                            Toast.makeText(this, "Profile created successfully", Toast.LENGTH_SHORT)
                                .show()
                            saveToRoom(user)
                        } else{
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
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
        val customFont: Typeface? = ResourcesCompat.getFont(this, R.font.poppins_medium)

        val adapter =
            object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, levels) {
                override fun isEnabled(position: Int): Boolean = true

                override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                    val view = super.getView(position, convertView, parent) as TextView
                    view.setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                    view.textSize = 14f
                    customFont?.let { view.typeface = it }
                    return view
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getDropDownView(position, convertView, parent) as TextView
                    view.setTextColor(Color.BLACK)
                    view.textSize = 14f
                    customFont?.let { view.typeface = it }
                    return view
                }
            }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(0) // Default selected: "Select Skill Level"

        // Arrow toggle logic
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
                Toast.makeText(this, "Skill Added", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter skill and select level", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    private fun saveToRoom(model: UserModel) {
        val userProfile = UserEntity(
            uid = model.uid,
            username = model.username,
            email = model.email,
            about = model.about,
            profession = model.profession,
            experience = model.experience,
            skills = model.skills,
            profileImageUrl = model.profileImageUrl
        )
        lifecycleScope.launch {
            AppDatabase.getDatabase(this@CreateProfileActivity).userDao().insertUser(userProfile)
        }
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}


