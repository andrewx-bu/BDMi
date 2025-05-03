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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {
    data class SearchUIState(
        override val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
        val page: Int = 1,
        val totalPages: Int = Int.MAX_VALUE,
        override val error: APIError? = null,
        val query: String = "",
        val includeAdult: Boolean = false,
        val region: String? = null,
        val year: String? = null
    ) : UIState

    private val _uiState = MutableStateFlow(SearchUIState())
    val uiState: StateFlow<SearchUIState> = _uiState.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    init {
        viewModelScope.launch {
            _searchText
                .debounce(500L)
                .distinctUntilChanged()
                .collectLatest { text ->
                    _isSearching.value = true
                    _uiState.update {
                        it.copy(
                            query = text,
                            page = 1,
                            totalPages = Int.MAX_VALUE,
                            movies = emptyList(),
                            error = null
                        )
                    }
                    loadMovies()
                    _isSearching.value = false
                }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
        if (text.isBlank()) {
            _uiState.update {
                it.copy(
                    query = "",
                    page = 1,
                    totalPages = Int.MAX_VALUE,
                    movies = emptyList(),
                    error = null
                )
            }
        }
    }

    fun loadNextPage() {
        val s = _uiState.value
        if (s.isLoading || s.page >= s.totalPages) return
        _uiState.update { it.copy(page = it.page + 1) }
        loadMovies()
    }

    private fun loadMovies() {
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            movieRepository.searchMovies(
                query = s.query,
                page = s.page,
                includeAdult = s.includeAdult,
                primaryReleaseYear = s.year
            ).fold(
                onSuccess = { response ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            movies = if (it.page == 1) response.results else it.movies + response.results,
                            totalPages = response.totalPages
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.toAPIError()) }
                }
            )
        }
    }

    fun setIncludeAdult(include: Boolean) {
        _uiState.update {
            it.copy(
                includeAdult = include,
                page = 1,
                totalPages = Int.MAX_VALUE,
                movies = emptyList()
            )
        }
        loadMovies()
    }

    fun setYear(year: String?) {
        _uiState.update {
            it.copy(year = year, page = 1, totalPages = Int.MAX_VALUE, movies = emptyList())
        }
        loadMovies()
    }
}