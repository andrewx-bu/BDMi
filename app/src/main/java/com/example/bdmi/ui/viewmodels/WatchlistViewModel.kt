package com.example.bdmi.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.bdmi.data.repositories.CustomList
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.bdmi.data.repositories.WatchlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val watchlistRepository: WatchlistRepository
) : ViewModel() {
    private val _customLists = MutableStateFlow<List<CustomList>>(emptyList())
    val customLists: StateFlow<List<CustomList>> = _customLists.asStateFlow()


}