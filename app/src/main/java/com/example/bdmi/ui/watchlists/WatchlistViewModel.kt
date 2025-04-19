package com.example.bdmi.ui.watchlists

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
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WatchlistViewModel"

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {
    private val _customLists = MutableStateFlow<List<CustomList>>(emptyList())
    val customLists: StateFlow<List<CustomList>> = _customLists.asStateFlow()

    private val _listItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val listItems: StateFlow<List<MediaItem>> = _listItems.asStateFlow()

    private val _listInfo = MutableStateFlow<CustomList?>(null)
    val listInfo: StateFlow<CustomList?> = _listInfo.asStateFlow()

    fun getList(userId: String, listId: String) {
        Log.d(TAG, "Getting list for user: $userId")

        viewModelScope.launch {
            watchlistRepository.getList(userId, listId) { items ->
                Log.d(TAG, "Items retrieved: $items")
            }
        }
    }

    fun removeItemFromList(userId: String, listId: String, itemId: Int) {
        Log.d(TAG, "Removing item from list for user: $userId")
        _listItems.value = _listItems.value.filter { it.id != itemId }

        viewModelScope.launch {
            watchlistRepository.removeFromList(userId, listId, itemId) {
                Log.d(TAG, "Item removed from list successfully")
            }
        }
    }

    fun updateListInfo(userId: String, listId: String, newList: CustomList) {
        Log.d(TAG, "Updating list info for user: $userId")
        _listInfo.value = newList

        viewModelScope.launch {
            watchlistRepository.updateListInfo(userId, listId, newList) {
                Log.d(TAG, "List info updated successfully")
            }
        }
    }


    // Move to different view model
    /*
    fun createList(userId: String, list: CustomList) {
        Log.d(TAG, "Creating a list for user: $userId")

        viewModelScope.launch {
            watchlistRepository.createList(userId, list) { success ->
                if (success) {
                    Log.d(TAG, "List created successfully")
                } else {
                    Log.d(TAG, "Failed to create list")
                }
            }
        }
    }*/
}