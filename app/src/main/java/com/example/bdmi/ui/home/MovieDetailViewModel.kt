package com.example.bdmi.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.APIError
import com.example.bdmi.data.api.models.MovieDetails
import com.example.bdmi.data.api.models.WatchProvidersResponse
import com.example.bdmi.data.api.toAPIError
import com.example.bdmi.data.repositories.CustomList
import com.example.bdmi.data.repositories.MediaItem
import com.example.bdmi.data.repositories.MovieRepository
import com.example.bdmi.data.repositories.WatchlistRepository
import com.example.bdmi.data.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlinx.coroutines.launch

private const val TAG = "MovieDetailViewModel"

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {
    data class DetailUIState(
        override val isLoading: Boolean = false,
        val details: MovieDetails? = null,
        val providers: WatchProvidersResponse? = null,
        override val error: APIError? = null
    ) : UIState

    private var _lists = MutableStateFlow<List<CustomList>>(emptyList())
    val lists: StateFlow<List<CustomList>> = _lists.asStateFlow()

    private val _detailUIState = MutableStateFlow(DetailUIState())
    val detailUIState = _detailUIState.asStateFlow()

    fun refreshDetails(movieId: Int) {
        _detailUIState.update { DetailUIState(isLoading = true, error = null) }
        loadMovieDetails(movieId)
    }

    private fun loadMovieDetails(movieId: Int) {
        viewModelScope.launch {
            _detailUIState.update { it.copy(isLoading = true, error = null) }
            // Simulate Network Delay
            delay(1000)

            // Start API calls in parallel
            val detailsDeferred = async { movieRepo.getMovieDetails(movieId) }
            val providersDeferred = async { movieRepo.getWatchProviders(movieId) }

            val detailsResult = detailsDeferred.await()
            val providersResult = providersDeferred.await()

            detailsResult.fold(
                onSuccess = { details ->
                    _detailUIState.update { it.copy(details = details) }
                },
                onFailure = { e ->
                    _detailUIState.update { it.copy(error = e.toAPIError()) }
                }
            )

            providersResult.fold(
                onSuccess = { providers ->
                    _detailUIState.update { it.copy(providers = providers) }
                },
                onFailure = { e ->
                    _detailUIState.update { it.copy(error = e.toAPIError()) }
                }
            )

            _detailUIState.update { it.copy(isLoading = false) }
        }
    }

    fun addToWatchlist(userId: String, listId: String, item: MediaItem) {
        Log.d(TAG, "Adding item to watchlist: $item")

        viewModelScope.launch {
            watchlistRepository.addToList(listId, userId, item)
        }
    }

    fun getLists(userId: String) {
        Log.d(TAG, "Getting lists for user: $userId")

        viewModelScope.launch {
            watchlistRepository.getLists(userId) { lists ->
                Log.d(TAG, "Lists retrieved: $lists")
                _lists.value = lists
            }
        }
    }
}