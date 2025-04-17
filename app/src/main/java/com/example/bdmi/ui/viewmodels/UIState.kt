package com.example.bdmi.ui.viewmodels

import com.example.bdmi.data.api.APIError

sealed interface UIState {
    val isLoading: Boolean
    val error: APIError?

    data class Loading(override val isLoading: Boolean = true) : UIState {
        override val error: APIError? = null
    }

    data class Error(override val error: APIError) : UIState {
        override val isLoading: Boolean = false
    }
}
