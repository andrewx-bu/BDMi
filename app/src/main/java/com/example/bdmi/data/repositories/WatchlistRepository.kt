package com.example.bdmi.data.repositories

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
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
    fun createList(userId: String, list: CustomList, onComplete: (CustomList?) -> Unit) {
        val dbFunction = "CreateList"

        val userProfileRef = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)

        db.collection(PUBLIC_PROFILES_COLLECTION)
            .document(userId)
            .collection(LISTS_COLLECTION)
            .add(list)
            .addOnSuccessListener { documentReference ->
                Log.d("$TAG$dbFunction", "List created with ID: ${documentReference.id}")
                val listId = documentReference.id
                db.runTransaction { transaction ->
                    transaction.update(documentReference, "listId", listId)
                    transaction.update(userProfileRef, "listCount", FieldValue.increment(1))
                }.addOnSuccessListener {
                    Log.d("$TAG$dbFunction", "List ID updated successfully")
                    val updatedList = list.copy(listId = listId)
                    onComplete(updatedList)
                }.addOnFailureListener { e ->
                    Log.w("$TAG$dbFunction", "Error updating list ID", e)
                    onComplete(null)
                }
            }
            .addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error adding list", e)
                onComplete(null)
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

    fun getListInfo(userId: String, listId: String, onComplete: (CustomList?) -> Unit) {
        val dbFunction = "GetListInfo"

        db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
            .collection(LISTS_COLLECTION).document(listId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val list = documentSnapshot.toObject(CustomList::class.java)
                onComplete(list)
            }
            .addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error getting list info", e)
                onComplete(null)
            }
    }

    // Retrieves all custom lists from the database given a user ID
    fun getLists(
        userId: String,
        publicOnly: Boolean = false,
        onComplete: (List<CustomList>) -> Unit
    ) {
        val dbFunction = "GetLists"

        // If publicOnly is true, only retrieve public lists, otherwise retrieve all lists
        val collectionRef = if (publicOnly) {
            db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
                .collection(LISTS_COLLECTION).whereEqualTo("isPublic", true)
        } else {
            db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
                .collection(LISTS_COLLECTION)
        }
        collectionRef.orderBy("timestamp", Query.Direction.DESCENDING)
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
            .get(Source.CACHE)
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

        val listRef = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
            .collection(LISTS_COLLECTION).document(listId)

        Log.d("$TAG$dbFunction", "User ID: $userId")
        Log.d("$TAG$dbFunction", "Adding item to list with ID: $listId")
        db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
            .collection(LISTS_COLLECTION).document(listId)
            .collection(ITEMS_COLLECTION)
            .add(listItem)
            .addOnSuccessListener { documentReference ->
                listRef.update("numOfItems", FieldValue.increment(1))
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
                db.runTransaction { transaction ->
                    for (document in querySnapshot.documents) {
                        transaction.delete(document.reference)
                    }
                    val listRef = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
                        .collection(LISTS_COLLECTION).document(listId)
                    transaction.update(listRef, "numOfItems", FieldValue.increment(-1))
                }
                    .addOnSuccessListener {
                        Log.d("$TAG$dbFunction", "Item removed from list")
                        onComplete(true)
                    }
                    .addOnFailureListener { e ->
                        Log.w("$TAG$dbFunction", "Error removing item from list", e)
                        onComplete(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error getting item to remove", e)
                onComplete(false)
            }
    }

    fun deleteList(userId: String, listId: String, onComplete: (Boolean) -> Unit) {
        val dbFunction = "DeleteList"
        val userRef = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
        val listRef = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
            .collection(LISTS_COLLECTION).document(listId)
        val itemsRef = listRef.collection(ITEMS_COLLECTION)

        itemsRef.get()
            .addOnSuccessListener { querySnapshot ->
                val batch = db.batch()
                querySnapshot.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }

                // Delete the list document after deleting all items
                batch.delete(listRef)
                batch.update(userRef, "listCount", FieldValue.increment(-1))
                batch.commit()
                    .addOnSuccessListener {
                        Log.d("$TAG$dbFunction", "List and items deleted successfully")
                        onComplete(true)
                    }
                    .addOnFailureListener { e ->
                        Log.w("$TAG$dbFunction", "Batch deletion failed", e)
                        onComplete(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Failed to fetch list items", e)
                onComplete(false)
            }
    }

    fun updateListInfo(
        userId: String,
        listId: String,
        list: CustomList,
        onComplete: (Boolean) -> Unit
    ) {
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