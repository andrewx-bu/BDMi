package com.example.bdmi.data.repositories

import android.util.Log
import com.cloudinary.android.MediaManager
import com.example.bdmi.UserInfo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

// Constants
private const val TAG = "UserRepository"
private const val USERS_COLLECTION = "users"
private const val PUBLIC_PROFILES_COLLECTION = "publicProfiles"

// Repository class for user database operations
class UserRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val mediaManager: MediaManager
) {
    // Adds a user to the users collection. Information should already be validated and password hashed
    // Checks for unique email before adding
    fun createUser(
        userInformation: HashMap<String, Any>,
        onComplete: (UserInfo?) -> Unit
    ) {
        val dbFunction = "addUser"

        // Check for email uniqueness
        db.collection(USERS_COLLECTION)
            .whereEqualTo("email", userInformation["email"])
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.documents.isNotEmpty()) {
                    // Email already exists, reject user addition
                    Log.d("$TAG$dbFunction", "Email already exists")
                    onComplete(null)
                } else {
                    // Add the new user
                    db.collection(USERS_COLLECTION)
                        .add(userInformation)
                        .addOnSuccessListener { documentReference ->
                            val documentId = documentReference.id
                            //Add the document ID to the user information
                            documentReference.update("userId", documentId)
                                .addOnSuccessListener {
                                    Log.d(
                                        "$TAG$dbFunction",
                                        "User added successfully with ID: $documentId"
                                    )
                                    //Create a public profile for the user
                                    val profileInfo: HashMap<String, Any?> = hashMapOf(
                                        "userId" to documentId,
                                        "displayName" to userInformation["displayName"],
                                        "isPublic" to true,
                                        "reviewCount" to 0,
                                        "friendCount" to 0,
                                        "listCount" to 0,
                                        "profilePicture" to "https://res.cloudinary.com/dle98umos/image/upload/v1744005666/default_hm4pfx.jpg" // Default user image
                                    )
                                    createPublicProfile(documentId, profileInfo) {
                                        Log.d("$TAG$dbFunction", "Public profile created")
                                        // Manually map the HashMap to UserInfo
                                        val userInfo = UserInfo(
                                            userId = profileInfo["userId"] as String,
                                            displayName = profileInfo["displayName"] as? String,
                                            profilePicture = profileInfo["profilePicture"] as? String,
                                            friendCount = (profileInfo["friendCount"] as? Number)?.toLong(),
                                            listCount = (profileInfo["listCount"] as? Number)?.toLong(),
                                            reviewCount = (profileInfo["reviewCount"] as? Number)?.toLong(),
                                            isPublic = profileInfo["isPublic"] as? Boolean
                                        )
                                        onComplete(userInfo)
                                    }

                                }
                                .addOnFailureListener { e ->
                                    Log.e("$TAG$dbFunction", "Error updating document ID", e)
                                    onComplete(null)
                                }
                        }
                        .addOnFailureListener { e: Exception ->
                            Log.e("$TAG$dbFunction", "Error adding user", e)
                            onComplete(null) //Error adding user
                        }
                }
            }
            .addOnFailureListener { e: Exception ->
                Log.e("$TAG$dbFunction", "Error checking email uniqueness", e)
                onComplete(null) //Error during email check
            }
    }

    // Creates a public profile for a user.
    // Called from the createUser function when a user is created
    private fun createPublicProfile(
        userId: String,
        profileInfo: HashMap<String, Any?>,
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

    // Authenticates a user during the login process
    // Returns a HashMap of the user's information via onComplete, or null if the user doesn't exist
    fun authenticateUser(
        loginInformation: HashMap<String, String>,
        onComplete: (String?) -> Unit
    ) {
        val dbFunction = "authenticateUser"

        db.collection(USERS_COLLECTION)
            .whereEqualTo("email", loginInformation["email"]) // Change field if needed
            .whereEqualTo("password", loginInformation["password"])
            .get()
            .addOnSuccessListener { user: QuerySnapshot ->
                if (user.documents.isNotEmpty()) {
                    Log.d("$TAG$dbFunction", "User found")
                    val userId: String = user.documents[0].data?.get("userId").toString()
                    // Return the userId via the callback if it exists
                    onComplete(userId)
                } else {
                    Log.d("$TAG$dbFunction", "No user found")
                    onComplete(null) // No matching user
                }
            }
            .addOnFailureListener { e: Exception ->
                Log.e("$TAG$dbFunction", "Error loading user", e)
                onComplete(null) // Return null in case of error
            }
    }

    // Updates a user's information in the users collection
    // Returns true if the update was successful, false otherwise
    fun updateUserInfo(
        userInfo: HashMap<String, Any>,
        onComplete: (Boolean) -> Unit
    ) {
        val dbFunction = "updateUserInfo"
        //Unsure to use this for identifying users or not
        val userId = userInfo["user_id"] ?: return onComplete(false) //Ensure 'id' exists
        val userRef = db.collection(USERS_COLLECTION).document(userId.toString())
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