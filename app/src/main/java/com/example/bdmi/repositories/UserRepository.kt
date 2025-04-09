package com.example.bdmi.repositories

import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.bdmi.viewmodels.UserInfo
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject
import kotlin.collections.get

// Constants
private const val TAG = "FirestoreUtils"
const val USERS_COLLECTION = "users"
const val PUBLIC_PROFILES_COLLECTION = "publicProfiles"
const val FRIENDS_SUBCOLLECTION = "friends"

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
                            userInformation["userId"] = documentId
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
        onComplete: (UserInfo?) -> Unit
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
                    loadUser(userId.toString()) { userInfo ->
                        onComplete(userInfo)
                    } // Return the user information via the callback
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

    /*
    * Adds a friend to a user's friend list.
    * Returns true if the friend was added successfully, false otherwise
    * Uses transactions to ensure friends are added atomically
    * */
    fun addFriend(
        userId: String,
        friendId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val dbFunction = "addFriend"
        val userRef = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
        val friendRef = db.collection(PUBLIC_PROFILES_COLLECTION).document(friendId)
        val userFriendsRef =
            db.collection(USERS_COLLECTION).document(userId).collection(FRIENDS_SUBCOLLECTION).document(friendId)
        val friendFriendsRef =
            db.collection(USERS_COLLECTION).document(friendId).collection(FRIENDS_SUBCOLLECTION).document(userId)

        db.runTransaction { transaction ->
            // Get user and friend documents
            val userDoc = transaction.get(userRef)
            val friendDoc = transaction.get(friendRef)

            if (!userDoc.exists() || !friendDoc.exists()) {
                throw Exception("User document for $userId or Friend document for $friendId does not exist")
            }

            // Extract user and friend info
            val userInfo = mapOf(
                "userId" to userDoc.getString("userId"),
                "profilePicture" to userDoc.getString("profilePicture"),
                "displayName" to userDoc.getString("displayName"),
                "friendCount" to userDoc.getLong("friendCount"),
                "listCount" to userDoc.getLong("listCount"),
                "reviewCount" to userDoc.getLong("reviewCount")
            )

            val friendInfo = mapOf(
                "userId" to friendDoc.getString("userId"),
                "profilePicture" to friendDoc.getString("profilePicture"),
                "displayName" to friendDoc.getString("displayName"),
                "friendCount" to friendDoc.getLong("friendCount"),
                "listCount" to friendDoc.getLong("listCount"),
                "reviewCount" to friendDoc.getLong("reviewCount")
            )

            //Add friend to user's 'friends' subcollection
            transaction.set(userFriendsRef, friendInfo)
            //Add user to friend's 'friends' subcollection
            transaction.set(friendFriendsRef, userInfo)

            //Increment friend counts for both profiles
            transaction.update(
                userRef,
                "friendCount",
                FieldValue.increment(1)
            ) //Copilot assisted with these 2 lines
            transaction.update(friendRef, "friendCount", FieldValue.increment(1))

        }.addOnSuccessListener {
            Log.d("$TAG$dbFunction", "Friend relationship added successfully")
            userFriendsRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("$TAG$dbFunction", "Listen failed.", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    Log.d("$TAG$dbFunction", "Snapshot Listener added")
                } else {
                    Log.d("$TAG$dbFunction", "Issue not adding Snapshot Listener")
                }
                onComplete(true)
            }
        }.addOnFailureListener { e ->
            Log.e("$TAG$dbFunction", "Error adding friend", e)
            onComplete(false)
        }
    }

    /*
    * Adds a friend to a user's friend list.
    * Returns true if the friend was added successfully, false otherwise
    * Uses batch writes to ensure friends are removed atomically
    * */
    fun removeFriend(
        userId: String,
        friendId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val dbFunction = "removeFriend"
        val userRef =
            db.collection(USERS_COLLECTION).document(userId).collection(FRIENDS_SUBCOLLECTION)
                .document(friendId)
        val friendRef =
            db.collection(USERS_COLLECTION).document(friendId).collection(FRIENDS_SUBCOLLECTION)
                .document(userId)
        db.runBatch { batch ->
            batch.delete(userRef)
            batch.delete(friendRef)
            batch.update(
                db.collection(PUBLIC_PROFILES_COLLECTION).document(userId),
                "friendCount",
                FieldValue.increment(-1)
            )
            batch.update(
                db.collection(PUBLIC_PROFILES_COLLECTION).document(friendId),
                "friendCount",
                FieldValue.increment(-1)
            )
        }.addOnSuccessListener {
            Log.d(
                "$TAG$dbFunction",
                "Friend relationship between $userId and $friendId removed successfully"
            )
            onComplete(true)
        }.addOnFailureListener { e ->
            Log.e(
                "$TAG$dbFunction",
                "Error removing friend relationship between $userId and $friendId",
                e
            )
            onComplete(false)
        }

    }

    /*
    * Changes a user's profile picture
    * Call function to upload image to Cloudinary then updates
    * database on the callback function
    * */
    fun changeProfilePicture(
        userId: String,
        profilePicture: Uri,
        onComplete: (String?) -> Unit
    ) {
        val dbFunction = "changeProfilePicture"
        val userRef = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
        uploadImage(profilePicture) { profilePictureUrl ->
            if (profilePictureUrl != null) {
                // Update database only after successful upload
                userRef.update("profilePicture", profilePictureUrl)
                    .addOnSuccessListener {
                        Log.d("$TAG$dbFunction", "Profile picture updated successfully")
                        onComplete(profilePictureUrl)
                    }
                    .addOnFailureListener { e ->
                        Log.e("$TAG$dbFunction", "Error updating profile picture", e)
                        onComplete(null)
                    }
            } else {
                Log.e("$TAG$dbFunction", "Error uploading profile picture")
                onComplete(null)
            }
        }

    }

    /*
    * Uploads an image to Cloudinary
    * Returns the URL of the uploaded image
    * Based on their documentation at: https://cloudinary.com/documentation/kotlin_integration
    * Documentation is really bad so following this repository from 5 years ago:
    * https://github.com/riyhs/Android-Kotlin-Cloudinary-Example */
    private fun uploadImage(imageUri: Uri, onComplete: (String?) -> Unit) {
        var imageUrl: String? = null
        mediaManager.upload(imageUri).callback(object : UploadCallback {
            override fun onStart(requestId: String) {
                Log.d("Cloudinary", "Upload started")
            }
            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
            }
            override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                Log.d("Cloudinary", "Upload successful")
                imageUrl = resultData?.get("secure_url") as String?
                Log.d("Cloudinary", "URL: $imageUrl")
                onComplete(imageUrl)
            }
            override fun onError(requestId: String, error: ErrorInfo) {
                Log.e("Cloudinary", "Upload error: ${error.description}")
            }
            override fun onReschedule(requestId: String, error: ErrorInfo) {
            }
        }).dispatch()
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