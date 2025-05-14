package com.example.devcollab.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.Activities.ChatActivity
import com.example.devcollab.Activities.LoginActivity
import com.example.devcollab.Adapter.MessagesAdapter
import com.example.devcollab.Database.Firestore.UserModel
import com.example.devcollab.Model.Messages
import com.example.devcollab.databinding.FragmentMessagesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query


class MessagesFragment : Fragment() {
    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val messagesList = mutableListOf<Messages>()
    private lateinit var adapter: MessagesAdapter
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val rv = binding.rvChats

        adapter = MessagesAdapter(messagesList) { otherUid ->
            startActivity(
                Intent(requireContext(), ChatActivity::class.java)
                    .putExtra("otherUid", otherUid)
            )
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        fetchChatPartners()

        return binding.root
    }

    private fun fetchChatPartners() {
        val currentUser = auth.currentUser

        if (currentUser == null) {
            showLoginPrompt()
            binding.noChatsTV.visibility = View.GONE
            binding.rvChats.visibility = View.GONE
            return
        }

        // Hide login prompt if logged in
        binding.loginPromptContainer.visibility = View.GONE

        val currentUid = currentUser.uid

        listenerRegistration = firestore.collection("chats")
            .whereArrayContains("participants", currentUid)
            .orderBy("lastTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    binding.rvChats.visibility = View.GONE
                    binding.noChatsTV.visibility = View.VISIBLE
                    return@addSnapshotListener
                }

                messagesList.clear()

                if (snapshot.isEmpty) {
                    binding.rvChats.visibility = View.GONE
                    binding.noChatsTV.visibility = View.VISIBLE
                    return@addSnapshotListener
                }

                var pendingFetches = snapshot.documents.size
                for (doc in snapshot.documents) {
                    val parts = doc.id.split("-")
                    val otherId = parts.firstOrNull { it != currentUid } ?: continue

                    firestore.collection("users")
                        .document(otherId)
                        .get()
                        .addOnSuccessListener { userSnap ->
                            userSnap.toObject(UserModel::class.java)?.let { u ->
                                messagesList.add(
                                    Messages(
                                        uid = otherId,
                                        name = u.username ?: "Unknown",
                                        profileImageUrl = u.profileImageUrl ?: ""
                                    )
                                )
                            }
                            pendingFetches--
                            if (pendingFetches == 0) updateMessagesUI()
                        }
                        .addOnFailureListener {
                            pendingFetches--
                            if (pendingFetches == 0) updateMessagesUI()
                        }
                }
            }
    }

    private fun updateMessagesUI() {
        if (messagesList.isEmpty()) {
            binding.rvChats.visibility = View.GONE
            binding.noChatsTV.visibility = View.VISIBLE
        } else {
            binding.rvChats.visibility = View.VISIBLE
            binding.noChatsTV.visibility = View.GONE
            adapter.notifyDataSetChanged()
        }
    }



    private fun showLoginPrompt() {
        // Show the semi-transparent background and login prompt
        binding.loginPromptContainer.visibility = View.VISIBLE

        // Handle login button click
        binding.btnLogin.setOnClickListener {
            // Navigate to the login screen
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
    }
}

