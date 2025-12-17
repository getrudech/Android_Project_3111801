package com.example.dermadiaryapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermadiaryapplication.data.db.entity.JournalEntry
import com.example.dermadiaryapplication.data.db.entity.UserProfile
import com.example.dermadiaryapplication.data.repository.JournalRepository
import com.example.dermadiaryapplication.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow // <-- NEW IMPORT
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

    // 1. Internal state flow for the user profile, loaded once on startup
    private val _userProfile = MutableStateFlow<UserProfile?>(null)

    init {
        // Load the profile when the ViewModel is created
        loadUserProfile()
    }

    // Function to load the user profile asynchronously
    private fun loadUserProfile() {
        viewModelScope.launch {
            // This now correctly suspends until the profile is loaded or determined to be null
            _userProfile.value = profileRepository.loadProfile()
        }
    }

    // 2. The combined state stream for the UI, now including the profile flow
    val uiState: StateFlow<HomeUiState> = combine(
        journalRepository.allEntries,
        journalRepository.entryCount,
        _userProfile // <-- Now combining with the profile flow
    ) { allEntries, entryCount, userProfile -> // <-- Updated lambda parameters
        HomeUiState(
            entryCount = entryCount,
            allEntries = allEntries,
            userProfile = userProfile, // <-- This will be the correctly fetched profile
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    // The old fetchUserProfile function is removed as it was incorrect.
}