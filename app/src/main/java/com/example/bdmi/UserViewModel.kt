package com.example.bdmi

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepo: UserRepository) : ViewModel() {

    //Collection of functions from the UserRepository
    fun register(userInformation: HashMap<String, Any>, onComplete: (Boolean) -> Unit) {
        userRepo.createUser(userInformation, onComplete)
    }

    fun login(
        loginInformation: HashMap<String, String>,
        onComplete: (HashMap<String, Any?>?) -> Unit
    ) {
        userRepo.authenticateUser(loginInformation, onComplete)
    }

    fun updateUserInfo(userInfo: HashMap<String, Any>, onComplete: (Boolean) -> Unit) {
        userRepo.updateUserInfo(userInfo, onComplete)
    }

    fun deleteUser(userId: String, onComplete: (Boolean) -> Unit) {
        userRepo.deleteUser(userId, onComplete)
    }

    fun addFriend(userId: String, friendId: String, onComplete: (Boolean) -> Unit) {
        userRepo.addFriend(userId, friendId, onComplete)
    }

    fun removeFriend(userId: String, friendId: String, onComplete: (Boolean) -> Unit) {
        userRepo.removeFriend(userId, friendId, onComplete)
    }
}