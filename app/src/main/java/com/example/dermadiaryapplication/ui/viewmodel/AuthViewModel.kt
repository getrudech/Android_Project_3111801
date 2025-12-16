package com.example.dermadiaryapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// IMPORTING THE EXISTING USERPROFILE CLASS FROM THE ENTITY PACKAGE
import com.example.dermadiaryapplication.data.db.entity.UserProfile
import com.example.dermadiaryapplication.data.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Data class to represent the UI state of the authentication screen
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            // Note: The repository must load/save UserProfile objects
            val profile = profileRepository.loadProfile()
            _uiState.value = _uiState.value.copy(
                isAuthenticated = profile.hasCompletedOnboarding
            )
        }
    }

    /**
     * Simulates the sign-in process.
     */
    fun signIn(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Username and password cannot be empty.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // 1. Instantiating UserProfile with the correct properties
                val newProfile = UserProfile(
                    username = username, // Using username property
                    skinType = "Normal", // Using default/placeholder
                    dateCreated = System.currentTimeMillis().toString(), // Simple date placeholder
                    hasCompletedOnboarding = true // Mark as authenticated
                )
                // 2. Saving the UserProfile
                profileRepository.saveProfile(newProfile)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Sign in failed: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}