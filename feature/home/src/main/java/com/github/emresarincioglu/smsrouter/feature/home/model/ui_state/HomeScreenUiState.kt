package com.github.emresarincioglu.smsrouter.feature.home.model.ui_state

import com.github.emresarincioglu.smsrouter.feature.home.model.Sender

internal data class HomeScreenUiState(
    val snackbarText: String? = null,
    val senders: List<Sender> = emptyList(),
    val searchHistory: List<String> = emptyList(),
    val searchResults: List<String> = emptyList()
)
