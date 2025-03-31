package com.example.bdmi

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/* Consider using Repository Pattern (i.e. injecting a class of functions into a view model) */

// Constants
private const val TAG = "FirestoreUtils"

// Initialize Firestore
fun initializeFirestore(): FirebaseFirestore {
    return Firebase.firestore
}

//Adds a user to the users collection. Information should already be validated and password hashed
//Checks for unique email before adding
fun addUser(
    db : FirebaseFirestore,
    userInformation : HashMap<String, Any>,
    onComplete: (Boolean) -> Unit
) {
    val dbFunction = "addUser"

    // Check for email uniqueness
    db.collection("users")
        .whereEqualTo("email", userInformation["email"])
        .get()
        .addOnSuccessListener { querySnapshot ->
            if (querySnapshot.documents.isNotEmpty()) {
                // Email already exists, reject user addition
                Log.d("$TAG$dbFunction", "Email already exists")
                onComplete(false)
            } else {
                // Add the new user
                db.collection("users")
                    .add(userInformation)
                    .addOnSuccessListener { documentReference ->
                        val documentId = documentReference.id
                        //Add the document ID to the user information
                        userInformation["user_id"] = documentId
                        documentReference.update("user_id", documentId)
                            .addOnSuccessListener {
                                Log.d("$TAG$dbFunction", "User added successfully with ID: $documentId")
                                onComplete(true)
                            }
                            .addOnFailureListener { e ->
                                Log.e("$TAG$dbFunction", "Error updating document ID", e)
                                onComplete(false)
                            }
                    }
                    .addOnFailureListener { e: Exception ->
                        Log.e("$TAG$dbFunction", "Error adding user", e)
                        onComplete(false) //Error adding user
                    }
            }
        }
        .addOnFailureListener { e: Exception ->
            Log.e("$TAG$dbFunction", "Error checking email uniqueness", e)
            onComplete(false) //Error during email check
        }
}

//Authenticates a user during the login process
//Returns a HashMap of the user's information via onComplete, or null if the user doesn't exist
fun authenticateUser(
    db: FirebaseFirestore,
    loginInformation: HashMap<String, String>,
    onComplete: (HashMap<*, *>?) -> Unit
) {
    val dbFunction = "loadUser"

    db.collection("users")
        .whereEqualTo("email", loginInformation["email"]) //Change field if needed
        .whereEqualTo("password", loginInformation["password"])
        .get()
        .addOnSuccessListener { user: QuerySnapshot ->
            if (user.documents.isNotEmpty()) {
                Log.d("$TAG$dbFunction", "User found")
                val userInfo = user.documents[0].data as HashMap<*, *>
                onComplete(userInfo) //Return the user information via the callback
            } else {
                Log.d("$TAG$dbFunction", "No user found")
                onComplete(null) //No matching user
            }
        }
        .addOnFailureListener { e: Exception ->
            Log.e("$TAG$dbFunction", "Error loading user", e)
            onComplete(null) //Return null in case of error
        }
}

//Updates a user's information in the users collection
//Returns true if the update was successful, false otherwise
fun updateUserInfo(
    db: FirebaseFirestore,
    userInfo: HashMap<String, Any>,
    onComplete: (Boolean) -> Unit
) {
    val dbFunction = "updateUserInfo"
    //Unsure to use this for identifying users or not
    val userId = userInfo["user_id"] ?: return onComplete(false) //Ensure 'id' exists
    val userRef = db.collection("users").document(userId.toString())
    userRef.update(userInfo) //Update fields in the document
        .addOnSuccessListener {
            Log.d("$TAG$dbFunction", "User information updated successfully")
            onComplete(true)
        }
        .addOnFailureListener { e: Exception ->
            Log.e("$TAG$dbFunction", "Error updating user information", e)
            onComplete(false)
        }
}

//Deletes a user from the users collection
//Returns true if the deletion was successful, false otherwise
fun deleteUser(
    db: FirebaseFirestore,
    userId: String,
    onComplete: (Boolean) -> Unit
) {
    val dbFunction = "deleteUser"
    val userRef = db.collection("users").document(userId)
    userRef.delete()
        .addOnSuccessListener {
            Log.d("$TAG$dbFunction", "User deleted successfully")
            onComplete(true)
        }
        .addOnFailureListener { e: Exception ->
            Log.e("$TAG$dbFunction", "Error deleting user", e)
            onComplete(false)
        }
}

/*
* Database Operations:
* add(): Adds a new document to the collection and creates a collection if one doesn't exist
* - Returns DocumentReference type, takes data usually in the form of a HashMap
* set(): Sets the document data.
* Overwrites a document if it already exists or creates a new document if it doesn't
* update(): Updates the document data.
* - Can specify which fields to update usually by using a HashMap
* get(): Retrieves a document(s) from the collection
* - There are many where clauses to filter the data (all are functions)
* - orderBy() can sort the data and limit() can limit the amount of data retrieved
* Aggregate functions supported:
* - count(), sum(), average()
* - Aggregate functions cannot be used offline or real-time listeners
* startAt(), startAfter(), endAt(), endBefore() functions allow you to specify a range of values
* - Helpful for pagination
* */


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
    // Retrieves all documents in the "users" collection
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

    // Query that uses a where clause
    db.collection("cities")
        .whereEqualTo("capital", true)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                Log.d(TAG, "${document.id} => ${document.data}")
            }
        }
        .addOnFailureListener { exception ->
            Log.w(TAG, "Error getting documents: ", exception)
        }

    // Retrieves all documents in a subcollection
    // Must alternate between collection then document to get to the correct document/collection
    db.collection("cities")
        .document("SF")
        .collection("landmarks")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                Log.d(TAG, "${document.id} => ${document.data}")
            }
        }
        .addOnFailureListener { exception ->
            Log.d(TAG, "Error getting documents: ", exception)
        }
}