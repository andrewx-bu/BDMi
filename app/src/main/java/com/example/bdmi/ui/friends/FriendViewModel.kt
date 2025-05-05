package com.example.bdmi.ui.friends

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.repositories.FriendRepository
import com.example.bdmi.data.repositories.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FriendViewModel"

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
) : ViewModel() {
    // List of friends for the current user
    private val _friends = MutableStateFlow<List<UserInfo>>(emptyList())
    val friends: StateFlow<List<UserInfo>> = _friends.asStateFlow()

    private val _searchResults = MutableStateFlow<List<UserInfo>>(emptyList())
    val searchResults: StateFlow<List<UserInfo>> = _searchResults.asStateFlow()

    fun loadFriends(userId: String) {
        Log.d(TAG, "Loading friends for user: $userId")

        viewModelScope.launch {
            friendRepository.getFriends(userId) { friendsList ->
                _friends.value = friendsList
            }
        }
    }

    // Search for users by display name
    fun searchUsers(currentUserId: String, displayName: String) {
        Log.d(TAG, "Searching for users with display name: $displayName")

        viewModelScope.launch {
            friendRepository.searchUsers(displayName) { users ->
                _searchResults.value = users.filter { it.userId != currentUserId }
            }
        }
    }
}