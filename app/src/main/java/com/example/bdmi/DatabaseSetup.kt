package com.example.bdmi

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// Constants
private const val TAG = "FirestoreUtils"

// Initialize Firestore
fun initializeFirestore(): FirebaseFirestore {
    return Firebase.firestore
}

// Add data to Firestore
fun addData(db: FirebaseFirestore) {
    val user = hashMapOf(
        "first_name" to "John",
        "last_name" to "Doe",
        "email" to "john.doe@gmail.com"
    )

    db.collection("users")
        .add(user)
        .addOnSuccessListener { documentReference: DocumentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e: Exception ->
            Log.w(TAG, "Error adding document", e)
        }

    val user2 = hashMapOf(
        "first" to "Alan",
        "middle" to "Mathison",
        "last" to "Turing",
        "born" to 1912,
    )

    db.collection("users")
        .add(user2)
        .addOnSuccessListener { documentReference: DocumentReference ->
            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
        }
        .addOnFailureListener { e: Exception ->
            Log.w(TAG, "Error adding document", e)
        }
}

// Read data from Firestore
fun readData(db: FirebaseFirestore) {
    db.collection("users")
        .get()
        .addOnSuccessListener { result: QuerySnapshot ->
            for (document in result) {
                Log.d(TAG, "${document.id} => ${document.data}")
            }
        }
        .addOnFailureListener { e: Exception ->
            Log.w(TAG, "Error getting documents.", e)
        }
}