package com.example.bdmi.data.repositories

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

// Constants
private const val TAG = "ListRepository"
private const val PUBLIC_PROFILES_COLLECTION = "publicProfiles"
private const val LISTS_COLLECTION = "lists"
private const val ITEMS_COLLECTION = "items"

class WatchlistRepository @Inject constructor(
        private val db: FirebaseFirestore
) {
    // Creates a new custom list in the database
    fun createList(userId: String, list: CustomList, onComplete: (Boolean) -> Unit) {
        val dbFunction = "CreateList"

        db.collection(PUBLIC_PROFILES_COLLECTION)
            .document(userId)
            .collection(LISTS_COLLECTION)
            .add(list)
            .addOnSuccessListener { documentReference ->
                Log.d("$TAG$dbFunction", "List created with ID: ${documentReference.id}")
                val listId = documentReference.id
                documentReference.update("listId", listId)
                    .addOnSuccessListener {
                        Log.d("$TAG$dbFunction", "List ID updated successfully")
                        onComplete(true)
                    }
                    .addOnFailureListener { e ->
                        Log.w("$TAG$dbFunction", "Error updating list ID", e)
                        onComplete(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error adding list", e)
                onComplete(false)
            }

    }

    // Retrieves a custom list from the database given a list ID
    fun getList(userId: String, listId: String, onComplete: (List<MediaItem>) -> Unit) {
        val dbFunction = "GetList"

        db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
            .collection(LISTS_COLLECTION).document(listId)
            .collection(ITEMS_COLLECTION)
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val items = querySnapshot.toObjects(MediaItem::class.java)
                onComplete(items)
            }
            .addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error getting list", e)
                onComplete(emptyList())
            }

    }

    // Retrieves all custom lists from the database given a user ID
    fun getLists(userId: String, publicOnly: Boolean = false, onComplete: (List<CustomList>) -> Unit) {
        val dbFunction = "GetLists"

        // If publicOnly is true, only retrieve public lists, otherwise retrieve all lists
        val collectionRef = if (publicOnly) {
            db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
                .collection(LISTS_COLLECTION).whereEqualTo("isPublic", true)
        } else {
            db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
                .collection(LISTS_COLLECTION)
        }
        collectionRef.orderBy("timestamp")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val lists = querySnapshot.toObjects(CustomList::class.java)
                Log.d("$TAG$dbFunction", "Lists retrieved successfully: $lists")
                onComplete(lists)
            }
            .addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error getting lists", e)
                onComplete(emptyList())
            }

    }

    // Checks if the movie/show is already in the list
    // Returns true if it is, false otherwise
    fun checkIfItemInList(listId: String, userId: String, id: Int, onComplete: (Boolean) -> Unit) {
        val dbFunction = "CheckIfItemInList"

        db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
            .collection(LISTS_COLLECTION).document(listId)
            .collection(ITEMS_COLLECTION)
            .whereEqualTo("id", id)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val itemExists = !querySnapshot.isEmpty
                onComplete(itemExists)
            }
            .addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error checking if item in list", e)
                onComplete(false)
            }
    }

    fun addToList(listId: String, userId: String, listItem: MediaItem) {
        val dbFunction = "AddToList"

        db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
            .collection(LISTS_COLLECTION).document(listId)
            .collection(ITEMS_COLLECTION)
            .add(listItem)
            .addOnSuccessListener { documentReference ->
                Log.d("$TAG$dbFunction", "Item added to list with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error adding item to list", e)
            }
    }

    fun removeFromList(userId: String, listId: String, itemId: Int, onComplete: (Boolean) -> Unit) {
        val dbFunction = "RemoveFromList"

        // Get the document ID of the item to remove
        db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
            .collection(LISTS_COLLECTION).document(listId)
            .collection(ITEMS_COLLECTION)
            .whereEqualTo("id", itemId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("$TAG$dbFunction", "Item removed from list")
                            onComplete(true)
                        }
                        .addOnFailureListener { e ->
                            Log.w("$TAG$dbFunction", "Error removing item from list", e)
                            onComplete(false)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error getting item to remove", e)
                onComplete(false)
            }
    }

    fun deleteList(userId: String, listId: String) {
        val dbFunction = "DeleteList"

        db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
            .collection(LISTS_COLLECTION).document(listId)
            .delete()
            .addOnSuccessListener {
                Log.d("$TAG$dbFunction", "List deleted successfully")
                }
            .addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error deleting list", e)
            }
    }

    fun updateListInfo(userId: String, listId: String, list: CustomList, onComplete: (Boolean) -> Unit) {
        val dbFunction = "UpdateListInfo"

        db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
            .collection(LISTS_COLLECTION).document(listId)
            .set(list)
            .addOnSuccessListener {
                Log.d("$TAG$dbFunction", "List updated successfully")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error updating list", e)
                onComplete(false)
            }
    }
}

data class CustomList(
    val listId: String = "",
    val name: String = "",
    val description: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val isPublic: Boolean = true
)

data class MediaItem(
    val id: Int = 0,
    val title: String = "",
    val posterPath: String = "",
    val releaseDate: String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val isWatched: Boolean = false
)