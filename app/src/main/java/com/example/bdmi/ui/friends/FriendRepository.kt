package com.example.bdmi.ui.friends

import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.bdmi.ui.viewmodels.UserInfo
import com.google.firebase.firestore.QuerySnapshot
import kotlin.collections.get

// Constants
private const val TAG = "FriendRepository"
private const val USERS_COLLECTION = "users"
private const val PUBLIC_PROFILES_COLLECTION = "publicProfiles"
private const val FRIENDS_SUBCOLLECTION = "friends"

class FriendRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    /*
    * Adds a friend to a user's friend list.
    * Returns true if the friend was added successfully, false otherwise
    * Uses transactions to ensure friends are added atomically
    * */
    suspend fun addFriend(
        userId: String,
        friendId: String,
        onComplete: (FriendInfo?) -> Unit
    ) {
        val dbFunction = "addFriend"
        val userRef = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
        val friendRef = db.collection(PUBLIC_PROFILES_COLLECTION).document(friendId)
        val userFriendsRef =
            db.collection(USERS_COLLECTION).document(userId).collection(FRIENDS_SUBCOLLECTION).document(friendId)
        val friendFriendsRef =
            db.collection(USERS_COLLECTION).document(friendId).collection(FRIENDS_SUBCOLLECTION).document(userId)
        var userInfo: FriendInfo? = null
        var friendInfo: FriendInfo? = null

        db.runTransaction { transaction ->
            // Get user and friend documents
            val userDoc = transaction.get(userRef)
            val friendDoc = transaction.get(friendRef)

            if (!userDoc.exists() || !friendDoc.exists()) {
                throw Exception("User document for $userId or Friend document for $friendId does not exist")
            }

            // Extract user and friend info
            userInfo = FriendInfo(
                userId = userDoc.getString("userId").toString(),
                profilePicture = userDoc.getString("profilePicture"),
                displayName = userDoc.getString("displayName"),
                friendCount = userDoc.getLong("friendCount"),
                listCount = userDoc.getLong("listCount"),
                reviewCount = userDoc.getLong("reviewCount"),
                isPublic = userDoc.getBoolean("isPublic")
            )


            friendInfo = FriendInfo(
                userId = friendDoc.getString("userId").toString(),
                profilePicture = friendDoc.getString("profilePicture"),
                displayName = friendDoc.getString("displayName"),
                friendCount = friendDoc.getLong("friendCount"),
                listCount = friendDoc.getLong("listCount"),
                reviewCount = friendDoc.getLong("reviewCount"),
                isPublic = friendDoc.getBoolean("isPublic")
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
                onComplete(friendInfo)
            }
        }.addOnFailureListener { e ->
            Log.e("$TAG$dbFunction", "Error adding friend", e)
            onComplete(null)
        }
    }

    /*
    * Adds a friend to a user's friend list.
    * Returns true if the friend was added successfully, false otherwise
    * Uses batch writes to ensure friends are removed atomically
    * */
    suspend fun removeFriend(
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

    // Returns a list of friendInfo objects for a given user
    suspend fun getFriends(
        userId: String,
        onComplete: (List<FriendInfo>) -> Unit
    ) {
        val dbFunction = "getFriends"
        val friendList = mutableListOf<FriendInfo>()
        db.collection(USERS_COLLECTION).document(userId).collection(FRIENDS_SUBCOLLECTION)
            .get()
            .addOnSuccessListener { friends: QuerySnapshot ->
                for (friendDoc in friends) {
                    val friendInfo = friendDoc.toObject(FriendInfo::class.java)
                    Log.d("$TAG$dbFunction", "Friend found")
                    friendList.add(friendInfo)
                }
                Log.d("$TAG$dbFunction", "Number of friends found: ${friendList.size}")
                onComplete(friendList)
            }
    }

    suspend fun searchUsers (
        displayName: String,
        onComplete: (List<UserInfo>) -> Unit
    ) {
        val dbFunction = "sendFriendInvite"
        var userList = mutableListOf<UserInfo>()
        db.collection(PUBLIC_PROFILES_COLLECTION)
            .whereEqualTo("displayName", displayName)
            .get()
            .addOnSuccessListener { users: QuerySnapshot ->
                for (userDoc in users) {
                    val userInfo = userDoc.toObject(UserInfo::class.java)
                    userList.add(userInfo)
                }
                Log.d("$TAG$dbFunction", "Number of users found: ${userList.size}")
                onComplete(userList)
            }
    }
}