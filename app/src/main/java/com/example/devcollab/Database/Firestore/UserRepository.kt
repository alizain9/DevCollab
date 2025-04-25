package com.example.devcollab.Database.Firestore


import com.example.devcollab.Const.Constants
import com.google.firebase.firestore.FirebaseFirestore

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun saveUserProfile(user: UserModel, onComplete: (Boolean, String?) -> Unit) {
        firestore.collection(Constants.USERS_NODE)
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                onComplete(true, null)
            }
            .addOnFailureListener {
                onComplete(false, it.message)
            }
    }

    fun fetchUserProfile(uid: String, onResult: (UserModel?) -> Unit) {
        firestore.collection(Constants.USERS_NODE)
            .document(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.toObject(UserModel::class.java)
                onResult(user)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }
}
