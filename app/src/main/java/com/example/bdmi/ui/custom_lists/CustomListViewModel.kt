package com.example.bdmi.ui.custom_lists

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.APIError
import com.example.bdmi.data.repositories.CustomList
import com.example.bdmi.data.repositories.MediaItem
import com.example.bdmi.data.repositories.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "WatchlistViewModel"

@HiltViewModel
class CustomListViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {
    data class ListUIState(
        val isLoading: Boolean = false,
        val error: APIError? = null
    )
    private val _listUIState = MutableStateFlow(ListUIState())
    val listUIState: StateFlow<ListUIState> = _listUIState.asStateFlow()

    private val _listItems = MutableStateFlow<List<MediaItem>>(emptyList())
    val listItems: StateFlow<List<MediaItem>> = _listItems.asStateFlow()

    private val _listInfo = MutableStateFlow<CustomList?>(null)
    val listInfo: StateFlow<CustomList?> = _listInfo.asStateFlow()

    private val _displayGridView = MutableStateFlow(true)
    val displayGridView: StateFlow<Boolean> = _displayGridView.asStateFlow()

    private val _editPrivileges = MutableStateFlow(false)
    val editPrivilege: StateFlow<Boolean> = _editPrivileges.asStateFlow()


    fun setEditPrivileges(currentUserId: String, listUserId: String) {
        _editPrivileges.value = currentUserId == listUserId
    }

    // Changes between grid and list view
    fun toggleDisplay() {
        Log.d(TAG, "Toggling display")
        _displayGridView.value = !_displayGridView.value
    }

    fun loadListInfo(userId: String, listId: String) {
        Log.d(TAG, "Getting list info for user: $userId")
        viewModelScope.launch {
            watchlistRepository.getListInfo(userId, listId) { listInfo ->
                _listInfo.value = listInfo
                Log.d(TAG, "List info retrieved: $listInfo")
            }
        }
    }

    fun loadList(userId: String, listId: String) {
        Log.d(TAG, "Getting list for user: $userId")
        _listUIState.update { ListUIState(isLoading = true, error = null) }

        viewModelScope.launch {
            _listUIState.update { it.copy(isLoading = true, error = null) }

            watchlistRepository.getList(userId, listId) { items ->
                Log.d(TAG, "Items retrieved: $items")
                _listUIState.update {
                    it.copy(
                        error = null,
                        isLoading = false
                    )
                }
                _listItems.value = items
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