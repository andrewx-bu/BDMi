package com.example.bdmi.ui.friends

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.ui.viewmodels.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FriendInfo(
    val userId: String = "", // Provide a default value
    val displayName: String? = null,
    val profilePicture: String? = null,
    val friendCount: Long? = null,
    val listCount: Long? = null,
    val reviewCount: Long? = null,
    val isPublic: Boolean? = null,
)

private const val TAG = "FriendViewModel"

@HiltViewModel
class FriendViewModel @Inject constructor(private val friendRepository: FriendRepository) : ViewModel() {
    private val _friends = MutableStateFlow<MutableList<FriendInfo>>(mutableListOf())
    val friends: StateFlow<MutableList<FriendInfo>?> = _friends.asStateFlow()

    fun loadFriends(userId: String) {
        Log.d(TAG, "Loading friends for user: $userId")
        viewModelScope.launch {
            friendRepository.getFriends(userId) { friendsList ->
                _friends.value = friendsList as MutableList<FriendInfo>
            }
        }
    }

    fun addFriend(userId: String, friendId: String, onComplete: (Boolean) -> Unit) {
        Log.d(TAG, "Adding friend with ID: $friendId")

        viewModelScope.launch {
            friendRepository.addFriend(userId, friendId) { newFriend ->
                if (newFriend != null) {
                    _friends.value = _friends.value.toMutableList().apply {
                        add(newFriend)
                    }
                    Log.d(TAG, "New friend added: ${newFriend.displayName}")
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }
        }

    }
}