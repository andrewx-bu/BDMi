package com.example.bdmi.data.repositories

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import javax.inject.Inject

// Constants
private const val TAG = "ReviewRepository"
private const val REVIEWS_COLLECTION = "reviews"
private const val PUBLIC_PROFILES_COLLECTION = "publicProfiles"
private const val RATINGS_COLLECTION = "ratings"
private const val MOVIES_COLLECTION = "movies"

class ReviewRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    fun getMovieData(movieId: Int, onComplete: (MovieMetrics) -> Unit) {
        val dbFunction = "GetMovieData"
        Log.d("$TAG$dbFunction", "Getting movie data for movie $movieId")

        checkIfMovieExists(movieId) {
            db.collection(MOVIES_COLLECTION).document(movieId.toString()).get()
                .addOnSuccessListener { documentSnapshot ->
                    val movieData = documentSnapshot.toObject(MovieMetrics::class.java)
                    Log.d("$TAG$dbFunction", "Movie data retrieved: $movieData")
                    onComplete(movieData!!)
                }
                .addOnFailureListener { e ->
                    Log.w("$TAG$dbFunction", "Error getting movie data", e)
                }
        }
    }

    /*
     * Creates a new review for a movie using transactions
     * Updates the movies total review count
     * Calls setRating to update the rating meta data
     */
    fun createReview(
        userId: String, movieId: Int,
        movieReview: MovieReview, userReview: UserReview,
        onComplete: (Boolean) -> Unit
    ) {
        val dbFunction = "CreateReview"
        Log.d("$TAG$dbFunction", "Creating review for user $userId and movie $movieId")

        checkIfMovieExists(movieId) {
            val movieDoc = db.collection(MOVIES_COLLECTION).document(movieId.toString())
            val movieReviewDoc = movieDoc.collection(REVIEWS_COLLECTION).document(userId)
            val profileDoc = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
            val profileReviewDoc = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
                .collection(REVIEWS_COLLECTION).document(movieId.toString())

            db.runTransaction { transaction ->
                val snapshot = transaction.get(movieDoc)
                val reviewSnapshot = transaction.get(movieReviewDoc)

                // Updates movie meta data and sets review
                val movieReviewCount = snapshot.getLong("reviewCount")?: 0
                val newMovieReviewCount = if (reviewSnapshot.exists()) movieReviewCount else movieReviewCount + 1

                transaction.update(movieDoc, "reviewCount", newMovieReviewCount)
                transaction.set(movieReviewDoc, movieReview)

                // Updates users review meta data and sets review
                val profileSnapshot = transaction.get(profileDoc)
                val profileReviewSnapshot = transaction.get(profileReviewDoc)

                val profileReviewCount = profileSnapshot.getLong("reviewCount")?: 0
                val newProfileReviewCount = if (profileReviewSnapshot.exists()) profileReviewCount else profileReviewCount + 1

                transaction.update(profileDoc, "reviewCount", newProfileReviewCount)
                transaction.set(profileReviewDoc, userReview)
            }.addOnSuccessListener {
                Log.d("$TAG$dbFunction", "Review created successfully")
                setRating(userId, movieId, movieReview.rating)

                onComplete(true)
            }.addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error creating review", e)

                onComplete(false)
            }
        }
    }

    // Deletes review and updates the movies total review count
    fun deleteReview(userId: String, movieId: Int) {
        val dbFunction = "DeleteReview"
        Log.d("$TAG$dbFunction", "Deleting review")

        checkIfMovieExists(movieId) {
            val movieDoc = db.collection(MOVIES_COLLECTION).document(movieId.toString())
            val reviewDoc = movieDoc.collection(REVIEWS_COLLECTION).document(userId)
            val profileDoc = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
            val profileReviewDoc = db.collection(PUBLIC_PROFILES_COLLECTION).document(userId)
                .collection(REVIEWS_COLLECTION).document(movieId.toString())

            db.runTransaction { transaction ->
                val snapshot = transaction.get(movieDoc)

                // Gets new values for review count and average meta data
                val reviewCount = snapshot.getDouble("reviewCount")?: 0.0
                val newReviewCount = reviewCount - 1
                transaction.update(movieDoc, "reviewCount", newReviewCount)
                transaction.delete(reviewDoc)

                val profileSnapshot = transaction.get(profileDoc)

                val profileReviewCount = profileSnapshot.getLong("reviewCount")?: 0
                val newProfileReviewCount = if (profileReviewCount > 0) profileReviewCount - 1 else 0
                transaction.update(profileDoc, "reviewCount", newProfileReviewCount)
                transaction.delete(profileReviewDoc)
            }.addOnSuccessListener {
                Log.d("$TAG$dbFunction", "Review deleted successfully")
            }.addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error deleting review", e)
            }
        }

    }

    /*
     * Retrieves all reviews for a movie with pagination
     * If lastVisible is null, it retrieves the first page of reviews
     * Otherwise, it retrieves the next page of reviews
     * Offers support for filtering by rating
     * Pagination code written by ChatGPT
     */
    fun getReviews(
        movieId: Int,
        rating: Float? = null,
        lastVisible: DocumentSnapshot? = null,
        pageSize: Int = 20,
        onComplete: (List<MovieReview>, DocumentSnapshot?) -> Unit
    ) {
        val dbFunction = "GetReviews"
        Log.d("$TAG$dbFunction", "Getting reviews for movie $movieId")

        checkIfMovieExists(movieId) {
            val reviewQuery = db.collection(MOVIES_COLLECTION)
                .document(movieId.toString())
                .collection(REVIEWS_COLLECTION)
                .orderBy("timestamp", Query.Direction.DESCENDING)
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
                val reviews = querySnapshot.toObjects(MovieReview::class.java)
                val newLastVisible = querySnapshot.documents.lastOrNull()
                onComplete(reviews, newLastVisible)
            }
        }
    }

    // Gets the review of the logged in user to pin on the carousel
    fun getReview(userId: String, movieId: Int, onComplete: (MovieReview?) -> Unit) {
        val dbFunction = "GetReview"
        Log.d("$TAG$dbFunction", "Getting review for user $userId and movie $movieId")

        checkIfMovieExists(movieId) {
            val reviewDoc = db.collection(MOVIES_COLLECTION).document(movieId.toString())
                .collection(REVIEWS_COLLECTION).document(userId)

            reviewDoc.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val review = documentSnapshot.toObject(MovieReview::class.java)
                    onComplete(review)
                } else {
                    onComplete(null)
                }
            }
        }
    }

    /*
     * Whenever a user rates a movie, this function is called
     * Updates the rating meta data with new values that depend if a user has rated before
     */
    fun setRating(userId: String, movieId: Int, rating: Float) {
        val dbFunction = "SetRating"
        Log.d("$TAG$dbFunction", "Setting rating for user $userId and movie $movieId")

        checkIfMovieExists(movieId) {
            val movieDoc = db.collection(MOVIES_COLLECTION).document(movieId.toString())
            val ratingDoc = movieDoc.collection(RATINGS_COLLECTION).document(userId)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(movieDoc)
                val ratingSnapshot = transaction.get(ratingDoc)
                val updates = mutableMapOf<String, Any>()

                // Gets new values for rating count and average meta data
                val existingUserRating = ratingSnapshot.getDouble("rating")
                val ratingCount = snapshot.getLong("ratingCount")?: 0
                val ratingSum = snapshot.getDouble("ratingSum") ?: 0.0

                // If the user has already rated the movie, update the rating count and sum
                val newRatingSum = if (existingUserRating != null)
                    ratingSum - existingUserRating + rating
                else ratingSum + rating

                val newRatingCount = if (existingUserRating != null)
                    ratingCount
                else ratingCount + 1
                val newAverageRating = newRatingSum / newRatingCount

                updates["ratingCount"] = newRatingCount
                updates["ratingSum"] = newRatingSum
                updates["averageRating"] = newAverageRating

                // Gets new values for rating breakdown
                val ratingBreakdown = snapshot.get("ratingBreakdown") as Map<*, *>
                val newRatingBreakdown = ratingBreakdown.toMutableMap()
                val ratingString = rating.toString()
                val currentCount = newRatingBreakdown[ratingString]?.toString()?.toInt() ?: 0
                newRatingBreakdown[ratingString] = currentCount + 1
                if (existingUserRating != null) {
                    val oldBucket = existingUserRating.toString()
                    newRatingBreakdown[oldBucket] = (newRatingBreakdown[oldBucket]?.toString()?.toInt() ?: 1) - 1
                }

                updates["ratingBreakdown"] = newRatingBreakdown

                // Updates the movie meta data
                transaction.update(movieDoc, updates)

                // Sets the rating for the user in the movie's ratings collection
                transaction.set(ratingDoc, mapOf("rating" to rating))
            }.addOnSuccessListener {
                Log.d("$TAG$dbFunction", "Rating set successfully")
            }.addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error setting rating", e)
            }
        }
    }

    /*
     * Whenever a user removes a rating, this function is called
     * Updates the rating meta data with new values that depend if a user has rated before
     */
    fun removeRating(userId: String, movieId: Int) {
        val dbFunction = "RemoveRating"
        Log.d("$TAG$dbFunction", "Removing rating for user $userId and movie $movieId")

        checkIfMovieExists(movieId) {
            val movieDoc = db.collection(MOVIES_COLLECTION).document(movieId.toString())
            val ratingDoc = movieDoc.collection(RATINGS_COLLECTION).document(userId)

            db.runTransaction { transaction ->
                val snapshot = transaction.get(movieDoc)
                val ratingSnapshot = transaction.get(ratingDoc)
                val updates = mutableMapOf<String, Any>()

                // Gets new values for rating count and average meta data
                val existingUserRating = ratingSnapshot.getDouble("rating")
                val ratingCount = snapshot.getLong("ratingCount")?: 0
                val ratingSum = snapshot.getDouble("ratingSum") ?: 0.0

                val newRatingSum = if (existingUserRating != null)
                    ratingSum - existingUserRating
                else ratingSum

                val newRatingCount = if (existingUserRating != null)
                    ratingCount - 1
                else ratingCount
                val newAverageRating = newRatingSum / newRatingCount

                updates["ratingCount"] = newRatingCount
                updates["ratingSum"] = newRatingSum
                updates["averageRating"] = newAverageRating

                // Gets new values for rating breakdown
                val ratingBreakdown = snapshot.get("ratingBreakdown") as Map<*, *>
                val newRatingBreakdown = ratingBreakdown.toMutableMap()
                val ratingString = existingUserRating.toString()
                val currentCount = newRatingBreakdown[ratingString]?.toString()?.toInt() ?: 0
                newRatingBreakdown[ratingString] = currentCount - 1

                updates["ratingBreakdown"] = newRatingBreakdown

                // Updates the movie meta data
                transaction.update(movieDoc, updates)

                // Deletes the rating for the user in the movie's ratings collection
                transaction.delete(ratingDoc)
            }.addOnSuccessListener {
                Log.d("$TAG$dbFunction", "Rating removed successfully")
            }.addOnFailureListener { e ->
                Log.w("$TAG$dbFunction", "Error removing rating", e)
            }
        }

    }

    /*
     * Called when a movie detail page is opened
     * Checks if the movie exists in the local cached version of the database
     * If it doesn't, it adds it to the database with default values
     */
    private fun checkIfMovieExists(movieId: Int, onComplete: () -> Unit) {
        val dbFunction = "CheckIfMovieExists"

        val movieDoc = db.collection(MOVIES_COLLECTION).document(movieId.toString())

        movieDoc.get(Source.CACHE).addOnSuccessListener { documentSnapshot ->
            if (!documentSnapshot.exists()) {
                val movieData = MovieMetrics() // Sets movie data to default values
                Log.d("$TAG$dbFunction", "Movie does not exist in local cache, adding it")
                movieDoc.set(movieData).addOnSuccessListener { onComplete() }
            } else {
                onComplete()
                Log.d("$TAG$dbFunction", "Movie already exists in local cache")
            }
        }
    }
}

// Example of using pagination in view model
/*
var lastSnapshot: DocumentSnapshot? = null

fun loadNextPage() {
    getReviews(movieId, lastVisible = lastSnapshot, pageSize = 10) { reviews, newLast ->
        reviewList.addAll(reviews)
        lastSnapshot = newLast
    }
}
 */