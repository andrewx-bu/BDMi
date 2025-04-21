package com.example.bdmi.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.repositories.CustomList
import com.example.bdmi.data.repositories.MediaItem
import com.example.bdmi.data.repositories.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.launch

private const val TAG = "MovieDetailViewModel"

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {

    private var _lists = MutableStateFlow<List<CustomList>>(emptyList())
    val lists: StateFlow<List<CustomList>> = _lists.asStateFlow()

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