package com.example.bdmi.data.repositories

import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import javax.inject.Inject

// Constants
private const val TAG = "ProfileRepository"
private const val PUBLIC_PROFILES_COLLECTION = "publicProfiles"
private const val REVIEWS_COLLECTION = "reviews"

class ProfileRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val mediaManager: MediaManager
) {
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

    fun getReviews(
        userId: String,
        rating: Float? = null,
        timeFilter: TimeFilter = TimeFilter.DESCENDING,
        lastVisible: DocumentSnapshot? = null,
        pageSize: Int = 10,
        onComplete: (List<UserReview>, DocumentSnapshot?) -> Unit
    ) {
        val dbFunction = "GetReviews"
        Log.d("$TAG$dbFunction", "Getting reviews for user $userId")

        val queryDirection = when (timeFilter) {
            TimeFilter.ASCENDING -> Query.Direction.ASCENDING
            TimeFilter.DESCENDING -> Query.Direction.DESCENDING
        }

        val reviewQuery = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
            .collection(REVIEWS_COLLECTION)
            .orderBy("timestamp", queryDirection)
            .limit(pageSize.toLong())

        val ratingQuery = if (rating != null) {
            reviewQuery.whereEqualTo("rating", rating)
        } else
            reviewQuery

        val paginatedQuery = if (lastVisible != null) {
            ratingQuery.startAfter(lastVisible)
        } else {
            ratingQuery
        }

        paginatedQuery.get().addOnSuccessListener { querySnapshot ->
            val reviews = querySnapshot.toObjects(UserReview::class.java)
            val newLastVisible = querySnapshot.documents.lastOrNull()
            onComplete(reviews, newLastVisible)
        }
    }
}