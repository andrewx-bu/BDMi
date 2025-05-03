package com.example.bdmi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.APIError
import com.example.bdmi.data.api.models.Movie
import com.example.bdmi.data.api.toAPIError
import com.example.bdmi.data.repositories.MovieRepository
import com.example.bdmi.data.utils.UIState
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
        override val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
        override val error: APIError? = null,
        val searchQuery: String = ""
    ) : UIState

    private val _searchUIState = MutableStateFlow(SearchUIState())
    val searchUIState = _searchUIState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _searchUIState.update { it.copy(searchQuery = query) }
    }

    fun executeSearch() {
        val query = _searchUIState.value.searchQuery
        if (query.isBlank()) return

        _searchUIState.update {
            it.copy(
                isLoading = true,
                movies = emptyList(),
                error = null
            )
        }

        viewModelScope.launch {
            movieRepository.searchMovies(query).fold(
                onSuccess = { response ->
                    if (response.results.isEmpty()) {
                        _searchUIState.update {
                            it.copy(error = APIError.EmptyResponseError(), isLoading = false)
                        }
                    } else {
                        _searchUIState.update {
                            it.copy(movies = response.results, isLoading = false)
                        }
                    }
                },
                onFailure = { e ->
                    _searchUIState.update {
                        it.copy(error = e.toAPIError(), isLoading = false)
                    }
                }
            )
        }
    }

    fun clearSearch() {
        _searchUIState.update {
            SearchUIState() // Resets to initial state
        }
    }
}