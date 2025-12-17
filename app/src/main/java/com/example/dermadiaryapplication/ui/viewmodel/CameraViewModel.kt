package com.example.dermadiaryapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermadiaryapplication.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class CameraUiState(
    val isLogging: Boolean = false,
    val logSuccess: Boolean = false,
    val error: String? = null
)

class CameraViewModel(
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    /**
     * Called when the user successfully takes or uploads a photo.
     * Logs a minimal entry in the JournalRepository to count towards the streak.
     */
    fun logPhotoCompletion() {
        _uiState.value = _uiState.value.copy(isLogging = true, logSuccess = false)
        viewModelScope.launch {
            try {
                // Log the photo event for today
                journalRepository.logPhotoEntry(LocalDate.now())
                _uiState.value = _uiState.value.copy(isLogging = false, logSuccess = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLogging = false, error = "Failed to log photo: ${e.message}")
            }
        }
    }

    fun logHandled() {
        _uiState.value = _uiState.value.copy(logSuccess = false, error = null)
    }
}