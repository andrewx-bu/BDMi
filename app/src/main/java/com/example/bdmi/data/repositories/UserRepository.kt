package com.example.bdmi.data.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import javax.inject.Inject

// Constants
private const val TAG = "UserRepository"
private const val USERS_COLLECTION = "users"
private const val PUBLIC_PROFILES_COLLECTION = "publicProfiles"

// Repository class for user database operations
class UserRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private var userListener: ListenerRegistration? = null

    // Adds a listener to the users collection for real-time updates
    // Written by ChatGPT
    fun listenToUser(userId: String, onUserChanged: (UserInfo?) -> Unit) {
        val userDoc = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
        userListener?.remove() // Clear old listener if any
        userListener = userDoc.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("UserRepository", "Listen failed.", error)
                onUserChanged(null)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val userInfo = snapshot.toObject(UserInfo::class.java)
                onUserChanged(userInfo)
            } else {
                onUserChanged(null)
            }
        }
    }

    fun removeUserListener() {
        userListener?.remove()
        userListener = null
    }


    // Creates a user in FirebaseAuth first, then adds user info to Firestore
    fun createUser(
        userInformation: HashMap<String, Any>,
        onComplete: (UserInfo?) -> Unit
    ) {
        val dbFunction = "createUser"

        auth.createUserWithEmailAndPassword(userInformation["email"].toString(), userInformation["password"].toString())
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid ?: return@addOnSuccessListener onComplete(null)

                // Add userId into userInformation
                userInformation["userId"] = userId
                userInformation.remove("password") // Remove password from userInformation

                db.collection(USERS_COLLECTION)
                    .document(userId)
                    .set(userInformation)
                    .addOnSuccessListener {
                        Log.d("$TAG$dbFunction", "User added successfully with ID: $userId")

                        val profileInfo = UserInfo(
                            userId = userId,
                            displayName =  userInformation["displayName"] as String
                        )

                        createPublicProfile(userId, profileInfo) {
                            Log.d("$TAG$dbFunction", "Public profile created")

                            onComplete(profileInfo)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("$TAG$dbFunction", "Error saving user info", e)
                        onComplete(null)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("$TAG$dbFunction", "Error creating FirebaseAuth user", e)
                onComplete(null)
            }
    }

    // Creates a public profile for a user.
    // Called from the createUser function when a user is created
    private fun createPublicProfile(
        userId: String,
        profileInfo: UserInfo,
        onComplete: (Boolean) -> Unit = {}
    ) {
        val dbFunction = "addPublicProfile"
        db.collection(PUBLIC_PROFILES_COLLECTION).document(userId) //Use userID as document ID
            .set(profileInfo)
            .addOnSuccessListener {
                Log.d("$TAG$dbFunction", "Public profile added successfully for userID: $userId")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("$TAG$dbFunction", "Error adding public profile", e)
                onComplete(false)
            }
    }

    // Loads a user's profile from the publicProfiles collection
    fun loadUser(
        userId: String,
        onComplete: (UserInfo?) -> Unit
    ) {
        val dbFunction = "loadProfile"
        db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
            .get()
            .addOnSuccessListener { profileDoc ->
                if (profileDoc.exists()) {
                    val profileInfo = profileDoc.toObject(UserInfo::class.java)
                    Log.d("$TAG$dbFunction", "Profile found")
                    onComplete(profileInfo)
                } else {
                    Log.d("$TAG$dbFunction", "No profile found")
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("$TAG$dbFunction", "Error loading profile", e)
                onComplete(null)
            }
    }

    // Authenticates a user with Firebase Authentication
    fun authenticateUser(
        email: String,
        password: String,
        onComplete: (String?) -> Unit
    ) {
        val dbFunction = "authenticateUser"

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val userId = authResult.user?.uid
                if (userId != null) {
                    Log.d("$TAG$dbFunction", "Authentication successful")
                    onComplete(userId)
                } else {
                    Log.d("$TAG$dbFunction", "Authentication failed: no UID")
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("$TAG$dbFunction", "Authentication failed", e)
                onComplete(null)
            }
    }

    // Updates a user's information in the users collection
    // Returns true if the update was successful, false otherwise
    fun updateUserInfo(
        userId: String,
        userInfo: Map<String, Any>,
        onComplete: (Boolean) -> Unit
    ) {
        val dbFunction = "updateUserInfo"
        val userRef = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
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

    // Deletes a user from the users collection
    // Returns true if the deletion was successful, false otherwise
    fun deleteUser(
        userId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val dbFunction = "deleteUser"
        val userRef = db.collection(USERS_COLLECTION).document(userId)
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
}