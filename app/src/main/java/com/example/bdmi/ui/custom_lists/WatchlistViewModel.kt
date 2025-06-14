package com.example.bdmi.ui.custom_lists

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.repositories.CustomList
import com.example.bdmi.data.repositories.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import kotlinx.coroutines.launch

private const val TAG = "WatchlistViewModel"

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {
    private var _lists = MutableStateFlow<List<CustomList>>(emptyList())
    val lists: StateFlow<List<CustomList>> = _lists.asStateFlow()

    // Should only be called if visiting another user's watchlist page
    fun getLists(userId: String, publicOnly: Boolean = false) {
        Log.d(TAG, "Getting lists for user: $userId")

        viewModelScope.launch {
            watchlistRepository.getLists(userId, publicOnly) { lists ->
                Log.d(TAG, "Lists retrieved: $lists")
                _lists.value = lists
            }
        }
    }

    fun createList(userId: String, list: CustomList) {
        Log.d(TAG, "Creating a list for user: $userId")

        viewModelScope.launch {
            watchlistRepository.createList(userId, list) { list ->
                Log.d(TAG, "List created successfully")
                if (list != null) {
                    _lists.value = listOf(list) + _lists.value
                } else {
                    Log.d(TAG, "List creation failed")
                }
            }
        }
    }
}