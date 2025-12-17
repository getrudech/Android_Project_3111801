package com.example.dermadiaryapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermadiaryapplication.data.db.entity.UserProfile
import com.example.dermadiaryapplication.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class to represent the UI state of the Profile screen
data class ProfileUiState(
    val profile: UserProfile = UserProfile(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSignedOut: Boolean = false // <-- NEW: Flag for sign-out navigation
)

class ProfileViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        // ... (existing loadUserProfile logic remains the same)
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val profile = profileRepository.loadProfile()

            if (profile != null) {
                _uiState.value = _uiState.value.copy(
                    profile = profile,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "No user profile found. Please register."
                )
            }
        }
    }

    /**
     * Clears the user profile data and sets the signed out flag.
     */
    fun signOut() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            profileRepository.clearProfile()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                isSignedOut = true, // Set flag to trigger navigation
                error = null
            )
        }
    }

    /**
     * Clears the sign-out flag after navigation is handled by the UI.
     */
    fun signOutHandled() {
        _uiState.value = _uiState.value.copy(isSignedOut = false)
    }
}