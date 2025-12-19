
package com.example.dermadiaryapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermadiaryapplication.data.db.entity.UserProfile
import com.example.dermadiaryapplication.data.repository.ProfileRepository
import com.example.dermadiaryapplication.OnboardingActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
)

class OnboardingViewModel(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun saveOnboardingData(data: OnboardingActivity.OnboardingData) {
        _uiState.value = _uiState.value.copy(isSaving = true, error = null, saveSuccess = false)

        viewModelScope.launch {
            try {
                // 1. Fetch the initial profile created during Sign Up (where hasCompletedOnboarding was false)
                val existingProfile = profileRepository.loadProfile()

                if (existingProfile == null) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = "Error: Initial profile not found. Cannot save data."
                    )
                    return@launch
                }

                // --- 2. Process collected Onboarding data into storable strings/ints ---
                val skinConcernsList = listOf(
                    "Acne/Breakouts", "Chronic Dryness", "Excess Oiliness",
                    "Redness/Sensitivity", "Hyperpigmentation"
                ).filterIndexed { index, _ -> data.skinConcerns[index] }

                val routineList = data.productRoutine
                    .filter { it.isUsed }
                    .map { "${it.name}: ${it.brand.ifBlank { "Unspecified" }}" }

                // 3. Create the fully updated UserProfile
                val updatedProfile = existingProfile.copy(
                    skinType = skinConcernsList.joinToString(", "),
                    gender = data.gender,
                    // Parse text inputs safely, default to 8 if invalid
                    sleepGoal = data.sleepGoal.toIntOrNull() ?: 8,
                    waterGoal = data.waterGoal.toIntOrNull() ?: 8,
                    preexistingConditions = data.preexistingConditions,
                    productRoutine = routineList.joinToString(" | "),
                    hasCompletedOnboarding = true // <-- THE KEY UPDATE
                )

                // 4. Save the updated profile to the repository (the local DB/SharedPreferences)
                profileRepository.saveProfile(updatedProfile)

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveSuccess = true, // Signal to the Activity that saving is done
                    error = null
                )

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Failed to save profile: ${e.message}"
                )
            }
        }
    }

    /** Clears the save success flag after navigation has occurred. */
    fun saveHandled() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}