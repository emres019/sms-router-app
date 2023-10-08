package com.github.emresarincioglu.smsrouter.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import com.github.emresarincioglu.smsrouter.feature.home.model.Sender
import com.github.emresarincioglu.smsrouter.feature.home.model.ui_state.HomeScreenUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Collections.emptyList

internal class HomeViewModel : ViewModel() {

    private lateinit var removedSender: Sender

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    fun onSenderCheckedChanged(sender: Sender, isChecked: Boolean) {

        if (isChecked) {
            enableSenderRouting(sender)
        } else {
            disableSenderRouting(sender)
        }
    }

    fun getSearchResultWithHistory(query: String) {

        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(
                searchResults = emptyList(), searchHistory = emptyList()
            )
        } else {
            // TODO: Get senders by query from database
        }
    }

    fun getSenders() {
        // TODO: Get all senders from database
    }

    fun getSenders(query: String) {

        if (query.isNotBlank()) {
            // TODO: Get senders by query from database
        }
    }

    fun removeSender(context: Context, position: Int) {

        // TODO: Remove sender from database
        val sender = _uiState.value.senders[position]
        val text = context.getString(R.string.format_removed_sender, sender.name)
        removedSender = sender
        _uiState.value = _uiState.value.copy(snackbarText = text)
    }

    fun restoreRemovedSender() {

        if (::removedSender.isInitialized) {
            // TODO: Restore sender
            _uiState.value = _uiState.value.copy(snackbarText = null)
        }
    }

    fun removeSearchHistory(search: String) {
        // TODO: Remove search history from database
    }

    private fun enableSenderRouting(sender: Sender) {
        // TODO: Enable sender
    }

    private fun disableSenderRouting(sender: Sender) {
        // TODO: Disable sender
    }
}