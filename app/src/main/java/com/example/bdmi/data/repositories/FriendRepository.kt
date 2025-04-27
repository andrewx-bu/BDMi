package com.example.bdmi.data.repositories

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

// Constants
private const val TAG = "FriendRepository"
private const val USERS_COLLECTION = "users"
private const val PUBLIC_PROFILES_COLLECTION = "publicProfiles"
private const val FRIENDS_SUBCOLLECTION = "friends"
private const val NOTIFICATIONS_SUBCOLLECTION = "notifications"
private const val OUTGOING_REQUESTS_SUBCOLLECTION = "outgoing_requests"

class FriendRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    // Returns a list of friendInfo objects for a given user
    fun getFriends(
        userId: String,
        onComplete: (List<ProfileBanner>) -> Unit
    ) {
        val dbFunction = "getFriends"
        val friendList = mutableListOf<ProfileBanner>()
        db.collection(USERS_COLLECTION).document(userId).collection(FRIENDS_SUBCOLLECTION)
            .get()
            .addOnSuccessListener { friends: QuerySnapshot ->
                for (friendDoc in friends) {
                    val friendInfo = friendDoc.toObject(ProfileBanner::class.java)
                    Log.d("$TAG$dbFunction", "Friend found")
                    friendList.add(friendInfo)
                }
                Log.d("$TAG$dbFunction", "Number of friends found: ${friendList.size}")
                onComplete(friendList)
            }
    }

    // Returns a list of friendInfo objects for users with a specified displayName
    fun searchUsers(
        displayName: String,
        onComplete: (List<ProfileBanner>) -> Unit
    ) {
        val dbFunction = "sendFriendInvite"
        val userList = mutableListOf<ProfileBanner>()
        db.collection(PUBLIC_PROFILES_COLLECTION)
            .whereEqualTo("displayName", displayName)
            .get()
            .addOnSuccessListener { users: QuerySnapshot ->
                for (userDoc in users) {
                    val userInfo = userDoc.toObject(UserInfo::class.java)
                    userList.add(userToProfileBanner(userInfo))
                }
                Log.d("$TAG$dbFunction", "Number of users found: ${userList.size}")
                onComplete(userList)
            }
    }

    /*
    * Sends a friend invite to a user.
    * Returns true if the invite was sent successfully, false otherwise
    * Also stores an outgoing request in the senders 'outgoing_requests' subcollection
    * */
    fun sendFriendInvite(
        sender: ProfileBanner,
        recipientId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val dbFunction = "sendFriendInvite"
        val newNotification = Notification(
            type = "friend_request",
            data = NotificationType.FriendRequest(
                userId = sender.userId,
                displayName = sender.displayName,
                profilePicture = sender.profilePicture,
                friendCount = sender.friendCount,
                listCount = sender.listCount,
                reviewCount = sender.reviewCount,
                isPublic = sender.isPublic
            )
        )
        val recipientRef = db.collection(USERS_COLLECTION).document(recipientId)
            .collection(NOTIFICATIONS_SUBCOLLECTION)
        recipientRef
            .add(newNotification)
            .addOnSuccessListener { documentReference ->
                Log.d("$TAG$dbFunction", "Friend invite sent successfully")
                documentReference.update(
                    "notificationId",
                    documentReference.id
                ) // Set the notificationId

                // Adds the recipients id to the senders outgoing request subcollection
                val senderRef = db.collection(USERS_COLLECTION).document(sender.userId)
                    .collection(OUTGOING_REQUESTS_SUBCOLLECTION)
                val outgoingRequest = mapOf(
                    "userId" to recipientId,
                    "timestamp" to Timestamp.now(),
                )
                senderRef.add(outgoingRequest)
                Log.d("$TAG$dbFunction", "New notification created: $newNotification")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("$TAG$dbFunction", "Error sending friend invite", e)
            }
    }

    /*
    * Adds a friend to a user's friend list.
    * Returns true if the friend was added successfully, false otherwise
    * Uses transactions to ensure friends are added atomically
    * */
    fun acceptFriendInvite(
        userId: String,
        friendId: String,
        onComplete: (ProfileBanner?) -> Unit
    ) {
        val dbFunction = "addFriend"
        val userRef = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
        val friendRef = db.collection(PUBLIC_PROFILES_COLLECTION).document(friendId)
        val userFriendsRef =
            db.collection(USERS_COLLECTION).document(userId).collection(FRIENDS_SUBCOLLECTION)
                .document(friendId)
        val friendFriendsRef =
            db.collection(USERS_COLLECTION).document(friendId).collection(FRIENDS_SUBCOLLECTION)
                .document(userId)
        var userInfo: ProfileBanner?
        var friendInfo: ProfileBanner? = null

        db.runTransaction { transaction ->
            // Get user and friend documents
            val userDoc = transaction.get(userRef)
            val friendDoc = transaction.get(friendRef)

            if (!userDoc.exists() || !friendDoc.exists()) {
                throw Exception("User document for $userId or Friend document for $friendId does not exist")
            }

            // Extract user and friend info
            userInfo = ProfileBanner(
                userId = userDoc.getString("userId").toString(),
                profilePicture = userDoc.getString("profilePicture").toString(),
                displayName = userDoc.getString("displayName").toString(),
                friendCount = userDoc.getLong("friendCount")!!,
                listCount = userDoc.getLong("listCount")!!,
                reviewCount = userDoc.getLong("reviewCount")!!,
                isPublic = userDoc.getBoolean("isPublic")!!
            )
            friendInfo = ProfileBanner(
                userId = friendDoc.getString("userId").toString(),
                profilePicture = friendDoc.getString("profilePicture").toString(),
                displayName = friendDoc.getString("displayName").toString(),
                friendCount = friendDoc.getLong("friendCount")!!,
                listCount = friendDoc.getLong("listCount")!!,
                reviewCount = friendDoc.getLong("reviewCount")!!,
                isPublic = friendDoc.getBoolean("isPublic")!!
            )

            // Add friend to user's 'friends' subcollection
            transaction.set(userFriendsRef, friendInfo)
            // Add user to friend's 'friends' subcollection
            transaction.set(friendFriendsRef, userInfo)

            // Increment friend counts for both profiles
            transaction.update(
                userRef, "friendCount", FieldValue.increment(1)
            ) // Copilot assisted with these 2 lines
            transaction.update(friendRef, "friendCount", FieldValue.increment(1))

            // Remove outgoing requests from friends outgoing request subcollection
            val outgoingRequestsRef =
                db.collection(USERS_COLLECTION).document(friendId)
                    .collection(OUTGOING_REQUESTS_SUBCOLLECTION).document(userId)
            transaction.delete(outgoingRequestsRef)

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
     * Declines a friend invite from a user.
     * Returns true if the invite was declined successfully, false otherwise
     */
    fun declineInvite(
        userId: String,
        friendId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val dbFunction = "declineInvite"
        db.collection(USERS_COLLECTION).document(friendId)
            .collection(OUTGOING_REQUESTS_SUBCOLLECTION)
            .document(userId)
            .delete()
            .addOnSuccessListener {
                Log.d("$TAG$dbFunction", "Friend invite declined successfully")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("$TAG$dbFunction", "Error declining friend invite", e)
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
     * Returns the friend status of a user with a given friendId
     * First checks if the two users are friends
     * Then checks if the currentUser has already sent a friend request to the userId of the visiting profile
     */
    fun getFriendStatus(
        currentUserId: String,
        friendId: String,
        onComplete: (FriendStatus) -> Unit
    ) {
        val dbFunction = "getFriendStatus"
        // First check if the currentUser and userId of the visiting profile are friends
        db.collection(USERS_COLLECTION).document(currentUserId).collection(FRIENDS_SUBCOLLECTION)
            .whereEqualTo("userId", friendId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    onComplete(FriendStatus.FRIEND)
                } else {
                    // Check if the currentUser has sent a friend request to the userId of the visiting profile
                    db.collection(USERS_COLLECTION).document(currentUserId)
                        .collection(OUTGOING_REQUESTS_SUBCOLLECTION)
                        .whereEqualTo("userId", friendId)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { qS ->
                            Log.d(
                                "$TAG$dbFunction",
                                "Friend status query snapshot: ${qS.documents}"
                            )
                            if (qS.documents.isNotEmpty()) {
                                onComplete(FriendStatus.PENDING)
                            } else {
                                onComplete(FriendStatus.NOT_FRIENDS)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("$TAG$dbFunction", "Error getting friend status", e)
                            onComplete(FriendStatus.NOT_FRIENDS)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("$TAG$dbFunction", "Error getting friend status", e)
                onComplete(FriendStatus.NOT_FRIENDS)
            }
    }

    /*
     * Cancels a friend request from a user.
     * First deletes the outgoing request, then deletes the users notification
     * Returns true if the request was cancelled successfully, false otherwise
     */
    fun cancelFriendRequest(
        currentUserId: String,
        friendId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val dbFunction = "cancelFriendRequest"

        db.collection(USERS_COLLECTION).document(currentUserId)
            .collection(OUTGOING_REQUESTS_SUBCOLLECTION)
            .whereEqualTo("userId", friendId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    document.reference.delete()
                }
                db.collection(USERS_COLLECTION).document(friendId)
                    .collection(NOTIFICATIONS_SUBCOLLECTION)
                    .whereEqualTo("type", "friend_request")
                    .whereEqualTo("data.userId", currentUserId)
                    .get()
                    .addOnSuccessListener { qS ->
                        for (document in qS.documents) {
                            document.reference.delete()
                        }
                        Log.d("$TAG$dbFunction", "Friend request cancelled successfully")
                        onComplete(true)
                    }
                    .addOnFailureListener { e ->
                        Log.e("$TAG$dbFunction", "Error cancelling friend request", e)
                        onComplete(false)
                    }
            }.addOnFailureListener { e ->
                Log.e("$TAG$dbFunction", "Error cancelling friend request", e)
                onComplete(false)
            }
    }

    private fun userToProfileBanner(userInfo: UserInfo): ProfileBanner {
        val friendInfo = ProfileBanner(
            userId = userInfo.userId,
            displayName = userInfo.displayName.toString(),
            profilePicture = userInfo.profilePicture.toString(),
            friendCount = userInfo.friendCount,
            listCount = userInfo.listCount,
            reviewCount = userInfo.reviewCount,
            isPublic = userInfo.isPublic
        )
        return friendInfo
    }
}