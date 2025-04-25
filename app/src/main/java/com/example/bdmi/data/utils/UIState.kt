package com.example.bdmi.data.utils

import com.example.bdmi.data.api.APIError

interface UIState {
    val isLoading: Boolean
    val error: APIError?
}