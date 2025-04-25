package com.example.devcollab.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.devcollab.Adapter.ApplicantsAdapter
import com.example.devcollab.Database.Firestore.ProjectRepository
import com.example.devcollab.Database.Firestore.UserModel
import com.example.devcollab.Model.Applicants
import com.example.devcollab.R
import com.example.devcollab.ViewModels.PostProjectViewModel
import com.example.devcollab.ViewModels.PostProjectViewModelFactory
import com.example.devcollab.databinding.ActivityApplicantsBinding
import com.example.devcollab.databinding.BottomSheetApplicantDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore

class ApplicantsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityApplicantsBinding
    private lateinit var viewModel: PostProjectViewModel
    private lateinit var adapter: ApplicantsAdapter
    private val userCache = mutableMapOf<String, UserModel>()
    private var currentBottomSheetDialog: BottomSheetDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityApplicantsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInsets()
        setupListeners()

        val projectId = intent.getStringExtra("projectId") ?: run {
            showErrorSnackbar("No project ID provided")
            finish()
            return
        }

        initializeViewModel()
        setupRecyclerView()
        fetchAndDisplayApplicants(projectId)
    }

    private fun initializeViewModel() {
        val repo = ProjectRepository()
        val factory = PostProjectViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory).get(PostProjectViewModel::class.java)
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        binding.rvApplicants.layoutManager = LinearLayoutManager(this)
        adapter = ApplicantsAdapter(
            emptyList(),
            onProfileClick = { uid -> showApplicantDetailBottomSheet(uid) },
            onContactClick = { uid -> openChatWith(uid) }
        )
        binding.rvApplicants.adapter = adapter

    }

    private fun openChatWith(applicantUid: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("otherUid", applicantUid)
        startActivity(intent)
    }

    private fun setupListeners() {
        binding.backToProjects.setOnClickListener {
            finish()
        }
    }

    private fun fetchAndDisplayApplicants(projectId: String) {
        showLoading(true)
        viewModel.fetchApplicants(projectId).observe(this) { applicants ->
            showLoading(false)
            if (applicants.isEmpty()) {
                showNoApplicantsView()
            } else {
                showApplicantsList(applicants)
            }
        }
    }

    private fun showNoApplicantsView() {
        binding.rvApplicants.visibility = View.GONE
        binding.noApplicantsTextView.visibility = View.VISIBLE
    }

    private fun showApplicantsList(applicants: List<Applicants>) {
        binding.rvApplicants.visibility = View.VISIBLE
        binding.noApplicantsTextView.visibility = View.GONE
        adapter.updateData(applicants)
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

    private fun showApplicantDetailBottomSheet(applicantUid: String) {
        // Dismiss any existing bottom sheet
        currentBottomSheetDialog?.dismiss()

        // Create new bottom sheet with animation
        val dialog = BottomSheetDialog(this).apply {
            behavior.isDraggable = true
            behavior.isHideable = true
            setOnDismissListener { currentBottomSheetDialog = null }
        }

        val sheetBinding = BottomSheetApplicantDetailBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)
        currentBottomSheetDialog = dialog


        // Show immediately
        dialog.show()

        // Check cache first
        userCache[applicantUid]?.let { cachedUser ->
            displayUserData(sheetBinding, cachedUser)
            return
        }

        // Show loading state
        showLoadingState(sheetBinding)

        // Fetch user data
        FirebaseFirestore.getInstance().collection("users")
            .document(applicantUid)
            .get()
            .addOnSuccessListener { doc ->
                val user = doc.toObject(UserModel::class.java)
                if (user != null) {
                    userCache[applicantUid] = user
                    displayUserData(sheetBinding, user)
                } else {
                    showErrorState(sheetBinding, "User data not found")
                }
            }
            .addOnFailureListener { e ->
                showErrorState(sheetBinding, "Failed to load user: ${e.localizedMessage}")
            }
    }

    private fun showLoadingState(binding: BottomSheetApplicantDetailBinding) {
        binding.apply {
            progress.visibility = View.VISIBLE
          //  contentGroup.visibility = View.GONE
          //  errorGroup.visibility = View.GONE
            tvName.text = "Loading..."
        }
    }

    private fun displayUserData(binding: BottomSheetApplicantDetailBinding, user: UserModel) {
        binding.apply {
            progress.visibility = View.GONE
           // contentGroup.visibility = View.VISIBLE
           // errorGroup.visibility = View.GONE

            tvName.text = user.username
            tvProfession.text = user.profession
            tvExperience.text = "${user.experience} years"
            tvSkills.text = user.skills.joinToString(", ")

            Glide.with(this@ApplicantsActivity)
                .load(user.profileImageUrl)
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(imgProfile)

            // Add refresh button functionality
           /* btnRefresh.setOnClickListener {
                userCache.remove(user.uid) // Remove from cache to force refresh
                showApplicantDetailBottomSheet(user.uid)
            }*/
        }
    }

    private fun showErrorState(binding: BottomSheetApplicantDetailBinding, message: String) {
        binding.apply {
            progress.visibility = View.GONE
           // contentGroup.visibility = View.GONE
           // errorGroup.visibility = View.VISIBLE

           // tvError.text = message
          /*  btnRetry.setOnClickListener {
                // Implement retry logic if needed
            }*/

        }
    }

    private fun showErrorSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        currentBottomSheetDialog?.dismiss()
    }
}