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

    fun getLists(userId: String) {
        Log.d(TAG, "Getting lists for user: $userId")

        viewModelScope.launch {
            watchlistRepository.getLists(userId) { lists ->
                Log.d(TAG, "Lists retrieved: $lists")
                _lists.value = lists
            }
        }
    }

    fun createList(userId: String, list: CustomList) {
        Log.d(TAG, "Creating a list for user: $userId")
        _lists.value = _lists.value + list

        viewModelScope.launch {
            watchlistRepository.createList(userId, list) { success ->
                Log.d(TAG, "List created successfully")
            }
        }
    }

    fun deleteList(userId: String, listId: String) {
        Log.d(TAG, "Removing list for user: $userId")
        _lists.value = _lists.value.filter { it.listId != listId }

        viewModelScope.launch {
            watchlistRepository.deleteList(userId, listId) { success ->
                Log.d(TAG, "List removed successfully")

            }
        }
    }
}