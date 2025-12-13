package com.example.dermadiaryapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermadiaryapplication.data.repository.JournalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// hold the key stats the Home screen needs
data class HomeUiState(
    val totalEntries: String = "0",
    val currentStreak: String = "0 Day Streak",
    val isLoading: Boolean = true
)

class HomeViewModel(repository: JournalRepository) : ViewModel() {

    // Getting the total entry count from the database using a Flow.
    // When a new entry is saved, this value updates automatically and tells the UI to refresh
    val uiState: StateFlow<HomeUiState> = repository.entryCount
        .map { count ->
            HomeUiState(
                totalEntries = count.toString(),
                currentStreak = "0 Day Streak",
                isLoading = false
            )
        }
        .stateIn(
            // Tells the Flow how long to stay active
            scope = androidx.lifecycle.viewModelScope,
            // Only start emitting when a collector is present
            started = SharingStarted.WhileSubscribed(5000),
            // What the UI sees while the database query is running
            initialValue = HomeUiState()
        )
}