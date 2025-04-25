package com.example.devcollab.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.Activities.ChatActivity
import com.example.devcollab.Adapter.MessagesAdapter
import com.example.devcollab.Database.Firestore.UserModel
import com.example.devcollab.Model.Messages
import com.example.devcollab.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot


class MessagesFragment : Fragment() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val messagesList = mutableListOf<Messages>()
    private lateinit var adapter: MessagesAdapter
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_messages, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rv_chats)

        adapter = MessagesAdapter(messagesList) { otherUid ->
            startActivity(
                Intent(requireContext(), ChatActivity::class.java)
                    .putExtra("otherUid", otherUid)
            )
        }
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        fetchChatPartners()

        return view
    }

    private fun fetchChatPartners() {
        val currentUid = auth.currentUser?.uid ?: return

        listenerRegistration = firestore.collection("chats")
            .whereArrayContains("participants", currentUid)
            .orderBy("lastTimestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                messagesList.clear()

                for (doc in snapshot.documents) {
                    // doc.id is your "uidA-uidB"
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
                                adapter.notifyDataSetChanged()
                            }
                        }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
    }
}

