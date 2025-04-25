package com.example.devcollab.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.devcollab.Adapter.ChatAdapter
import com.example.devcollab.Model.ChatMessage
import com.example.devcollab.R
import com.example.devcollab.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val auth      = FirebaseAuth.getInstance()

    // 1) Class-level properties for our UIDs and chat ID
    private lateinit var recipientId: String
    private val userId: String by lazy { auth.currentUser!!.uid }
    private lateinit var conversationId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2) Pull the other-user’s UID from the Intent
        recipientId = intent.getStringExtra("otherUid")
            ?: throw IllegalArgumentException("otherUid missing from Intent")

        // 3) Build a consistent conversationId (no slashes!)
        conversationId = if (userId < recipientId) {
            "$userId-$recipientId"
        } else {
            "$recipientId-$userId"
        }

        setupRecyclerView()
        listenForMessages()

        binding.btnSend.setOnClickListener {
            val text = binding.etMessage.text.toString().trim()
            if (text.isNotEmpty()) sendMessage(text)
        }
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(userId)
        binding.rvChat.layoutManager = LinearLayoutManager(this)
        binding.rvChat.adapter = chatAdapter
    }

    private fun listenForMessages() {
        firestore.collection("chats")
            .document(conversationId)            // use the class property
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, _ ->
                val msgs = snap?.documents
                    ?.mapNotNull { it.toObject(ChatMessage::class.java) }
                    ?: emptyList()
                chatAdapter.submitList(msgs)
                binding.rvChat.scrollToPosition(msgs.size - 1)
            }
    }

    private fun sendMessage(text: String) {
        val chatRef = firestore
            .collection("chats")
            .document(conversationId)

        // 1) Write the message to the subcollection
        val messageData = mapOf(
            "senderId"    to userId,
            "text"        to text,
            "timestamp"   to FieldValue.serverTimestamp()
        )
        chatRef.collection("messages").add(messageData)

        // 2) Update the parent doc with metadata
        val meta = mapOf(
            "participants"  to listOf(userId, recipientId),
            "lastTimestamp" to FieldValue.serverTimestamp()
        )
        chatRef.set(meta, SetOptions.merge())

        // 3) Clear the input box
        binding.etMessage.text?.clear()
    }
}

