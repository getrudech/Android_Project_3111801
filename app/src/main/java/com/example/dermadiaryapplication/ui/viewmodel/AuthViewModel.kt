package com.example.dermadiaryapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val isAuthenticated: Boolean = false,
    val navigateToOnboarding: Boolean = false, // Flag to go to Onboarding
    val navigateToHome: Boolean = false // Flag to go to Home (Main)
)

class AuthViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        // This runs immediately when the ViewModel is created (e.g., when AuthorizationActivity starts)
        checkAuthenticationStatus()
    }

    // Determines where to route the user immediately upon opening the AuthorizationActivity
    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            val profile = profileRepository.loadProfile()
            val isAuthenticated = profile != null
            _uiState.value = _uiState.value.copy(
                isAuthenticated = isAuthenticated,
                // If profile exists but onboarding is NOT complete, navigate to Onboarding
                navigateToOnboarding = (isAuthenticated && !profile!!.hasCompletedOnboarding),
                // If profile exists AND onboarding IS complete, navigate to Home
                navigateToHome = (isAuthenticated && profile!!.hasCompletedOnboarding)
            )
        }
    }

    /**
     * Simulates the SIGN IN process. Only successful for fully onboarded users.
     */
    fun signIn(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Username and password cannot be empty.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // In a real app, you'd validate credentials. Here, we rely on the existence of the saved profile.
                val existingProfile = profileRepository.loadProfile()

                // 1. Check if a profile exists (we assume username matches the one in the saved profile)
                if (existingProfile == null || existingProfile.username != username) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Sign in failed: Invalid username or password."
                    )
                    return@launch
                }

                // 2. Check if the user is fully onboarded
                if (existingProfile.hasCompletedOnboarding) {
                    // Success: Go directly to the main dashboard
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isAuthenticated = true,
                        navigateToHome = true,
                        error = null
                    )
                } else {
                    // Failure: The user registered but must complete onboarding first.
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Profile is incomplete. Please sign up again or skip to Onboarding."
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Sign in failed: ${e.message}"
                )
            }
        }
    }

    /**
     * Simulates the REGISTRATION (Sign Up) process.
     * Creates an initial profile, requires onboarding next.
     */
    fun register(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Username and password cannot be empty.")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            try {
                // 1. Instantiating initial UserProfile with minimal data and KEY flag = false
                val newProfile = UserProfile(
                    username = username,
                    skinType = "",
                    gender = "",
                    sleepGoal = 0,
                    waterGoal = 0,
                    preexistingConditions = "",
                    productRoutine = "",
                    dateCreated = System.currentTimeMillis().toString(),
                    hasCompletedOnboarding = false // KEY: User must complete onboarding next
                )
                // 2. Saving the initial UserProfile
                profileRepository.saveProfile(newProfile)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    navigateToOnboarding = true, // Go to Onboarding after successful registration
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Registration failed: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /** Clears navigation flags after they have been handled by the UI. */
    fun navigationHandled() {
        _uiState.value = _uiState.value.copy(navigateToOnboarding = false, navigateToHome = false)
    }
}