package com.example.bdmi.ui.home.movie_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bdmi.data.api.APIError
import com.example.bdmi.data.api.models.Company
import com.example.bdmi.data.api.models.Movie
import com.example.bdmi.data.api.toAPIError
import com.example.bdmi.data.repositories.MovieRepository
import com.example.bdmi.data.utils.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudioViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    data class StudioUIState(
        override val isLoading: Boolean = false,
        val movies: List<Movie> = emptyList(),
        val page: Int = 1,
        val totalPages: Int = Int.MAX_VALUE,
        val company: Company? = null,
        override val error: APIError? = null
    ) : UIState

    private val _uiState = MutableStateFlow(StudioUIState())
    val uiState: StateFlow<StudioUIState> = _uiState.asStateFlow()

    private val studioId: String = savedStateHandle.get<String>("studioId") ?: ""

    init {
        viewModelScope.launch {
            // start both requests in parallel
            _uiState.update { it.copy(isLoading = true, error = null) }

            val id = studioId.toIntOrNull()

            val companyDeferred = id?.let { async { movieRepo.getCompanyDetails(it) } }
            val moviesDeferred = async { movieRepo.discoverMovies(companies = studioId) }

            companyDeferred
                ?.await()
                ?.fold(
                    onSuccess = { comp ->
                        _uiState.update { it.copy(company = comp) }
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(isLoading = false, error = e.toAPIError())
                        }
                    }
                )

            moviesDeferred
                .await()
                .fold(
                    onSuccess = { resp ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                page = 1,
                                movies = resp.results,
                                totalPages = resp.totalPages
                            )
                        }
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(isLoading = false, error = e.toAPIError())
                        }
                    }
                )
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

            movieRepo
                .discoverMovies(
                    page = s.page,
                    companies = studioId.takeIf { it.isNotBlank() }
                )
                .fold(
                    onSuccess = { response ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                movies = if (s.page == 1) response.results else s.movies + response.results,
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
}