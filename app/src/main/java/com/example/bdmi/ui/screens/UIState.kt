package com.example.bdmi.ui.screens

import com.example.bdmi.data.api.APIError

interface UIState {
    val isLoading: Boolean
    val error: APIError?
}