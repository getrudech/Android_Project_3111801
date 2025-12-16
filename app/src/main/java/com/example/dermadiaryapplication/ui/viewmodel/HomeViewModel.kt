package com.example.dermadiaryapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermadiaryapplication.data.db.entity.JournalEntry
import com.example.dermadiaryapplication.data.db.entity.UserProfile
import com.example.dermadiaryapplication.data.repository.JournalRepository
import com.example.dermadiaryapplication.data.repository.ProfileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val entryCount: Int = 0,
    val allEntries: List<JournalEntry> = emptyList(),
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = true
)

// Handles the business logic for the main dashboard screen
class HomeViewModel(
    private val journalRepository: JournalRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    // The combined state stream for the UI
    val uiState: StateFlow<HomeUiState> = combine(
        journalRepository.allEntries,
        journalRepository.entryCount
    ) { allEntries, entryCount ->
        HomeUiState(
            entryCount = entryCount,
            allEntries = allEntries,
            userProfile = fetchUserProfile(),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    // Function to fetch the user profile (called during initialization)
    private fun fetchUserProfile(): UserProfile? {
        var profile: UserProfile? = null
        viewModelScope.launch {
            profile = profileRepository.loadProfile()
        }
        return profile
    }

    // You can add more dashboard-specific logic here, e.g., refreshing data
}