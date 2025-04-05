package com.example.bdmi.repositories

import android.R.attr.height
import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.transformation.expression.Expression.Companion.height
import com.cloudinary.transformation.resize.Resize.Companion.crop
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.io.File
import javax.inject.Inject

// Constants
private const val TAG = "FirestoreUtils"
const val USERS_COLLECTION = "users"
const val PUBLIC_PROFILES_COLLECTION = "publicProfiles"
const val FRIENDS_SUBCOLLECTION = "friends"

//Repository class for user database operations
class UserRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val cloudinary: Cloudinary
) {
    //Adds a user to the users collection. Information should already be validated and password hashed
    //Checks for unique email before adding
    fun createUser(
        userInformation: HashMap<String, Any>,
        onComplete: (Boolean) -> Unit
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
                    onComplete(false)
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
                                        "profilePicture" to "" //Will be default profile picture
                                    )
                                    createPublicProfile(documentId, profileInfo)
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

    //Creates a public profile for a user.
    //Called from the createUser function when a user is created
    private fun createPublicProfile(
        userId: String,
        profileInfo: HashMap<String, Any?>
    ) {
        val dbFunction = "addPublicProfile"
        db.collection(PUBLIC_PROFILES_COLLECTION).document(userId) //Use userID as document ID
            .set(profileInfo)
            .addOnSuccessListener {
                Log.d("$TAG$dbFunction", "Public profile added successfully for userID: $userId")
            }
            .addOnFailureListener { e ->
                Log.e("$TAG$dbFunction", "Error adding public profile", e)
            }
    }

    //Authenticates a user during the login process
    //Returns a HashMap of the user's information via onComplete, or null if the user doesn't exist
    fun authenticateUser(
        loginInformation: HashMap<String, String>,
        onComplete: (HashMap<String, Any?>?) -> Unit
    ) {
        val dbFunction = "loadUser"

        db.collection(USERS_COLLECTION)
            .whereEqualTo("email", loginInformation["email"]) //Change field if needed
            .whereEqualTo("password", loginInformation["password"])
            .get()
            .addOnSuccessListener { user: QuerySnapshot ->
                if (user.documents.isNotEmpty()) {
                    Log.d("$TAG$dbFunction", "User found")
                    val userInfo = user.documents[0].data as HashMap<String, Any?>
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

    fun loadUser(
        userId: String,
        onComplete: (HashMap<String, Any?>?) -> Unit
    ) {
        val dbFunction = "loadUser"
        db.collection(USERS_COLLECTION).document(userId)
            .get()
            .addOnSuccessListener { userDoc ->
                if (userDoc.exists()) {
                    val userInfo = userDoc.data as HashMap<String, Any?>
                    Log.d("$TAG$dbFunction", "User found")
                    onComplete(userInfo)
                }
            }
            .addOnFailureListener { e ->
                Log.e("$TAG$dbFunction", "Error loading user", e)
                onComplete(null)
            }
    }

    //Updates a user's information in the users collection
    //Returns true if the update was successful, false otherwise
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

    //Deletes a user from the users collection
    //Returns true if the deletion was successful, false otherwise
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
            val userFriendsRef =
                db.collection(USERS_COLLECTION).document(userId).collection(FRIENDS_SUBCOLLECTION)
                    .document(friendId)
            transaction.set(userFriendsRef, friendInfo)

            //Add user to friend's 'friends' subcollection
            val friendFriendsRef =
                db.collection(USERS_COLLECTION).document(friendId).collection(FRIENDS_SUBCOLLECTION)
                    .document(userId)
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
            onComplete(true)
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
    * Uploads an image to Cloudinary
    * Returns the URL of the uploaded image
    * Based on their documentation at: https://cloudinary.com/documentation/kotlin_integration
    * Update in future */
    fun uploadImage(file: File, onComplete: (String?) -> Unit) {
//        cloudinary.image {
//            publicId("image")
//            transformation {
//                width(100)
//                height(100)
//                crop("fill")
//            }
//        }
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