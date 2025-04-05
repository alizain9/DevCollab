package com.example.devcollab.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.devcollab.Adapter.MessagesAdapter
import com.example.devcollab.Model.Messages
import com.example.devcollab.R


class MessagesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_messages, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_chats)

        val users = listOf(
            Messages("Ali Khan", R.drawable.logo_image),
            Messages("Zara Fatima", R.drawable.logo_image),
            Messages("Umar Aziz", R.drawable.logo_image)
        )

        val adapter = MessagesAdapter(users)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())


        return view
    }
}