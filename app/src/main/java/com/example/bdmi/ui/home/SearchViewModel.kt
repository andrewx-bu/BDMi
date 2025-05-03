package com.example.bdmi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.APIError
import com.example.bdmi.data.api.models.Movie
import com.example.bdmi.data.api.toAPIError
import com.example.bdmi.data.repositories.MovieRepository
import com.example.bdmi.data.utils.UIState
import com.example.bdmi.ui.home.HomeViewModel.HomeUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {
    data class SearchUIState(
        val searchQuery: String = "",
        val movies: List<Movie> = emptyList(),
        override val isLoading: Boolean = false,
        override val error: APIError? = null
    ) : UIState

    private val _uiState = MutableStateFlow(SearchUIState())
    val searchUIState = _uiState.asStateFlow()

    fun refresh() {
        _uiState.update {
            SearchUIState(
                isLoading = true,
                movies = emptyList(),
                error = null,
                searchQuery = ""
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun executeSearch() {
        val query = _uiState.value.searchQuery
        if (query.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            movieRepository.searchMovies(query)
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            movies = response.results,
                            isLoading = false
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            error = e.toAPIError(),
                            isLoading = false
                        )
                    }
                }
        }
    }
}
