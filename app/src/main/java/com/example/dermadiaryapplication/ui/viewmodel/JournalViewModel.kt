package com.example.dermadiaryapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dermadiaryapplication.data.db.entity.JournalEntry
import com.example.dermadiaryapplication.data.repository.JournalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

// holds all the variables for the Journal screen
data class JournalUiState(
    val selectedMoodIndex: Int = 0,
    val stressLevel: Float = 5f,
    val sleepHours: String = "8",
    val waterGlasses: String = "8",
    val dietNotes: String = "",
    val skincareProducts: List<String> = listOf("Facial Cleanser", "Daily Moisturizer", "Vitamin C Serum", "Acne Spot Treatment"),
    val productsUsedState: List<Boolean> = List(4) { false },
    val isSaving: Boolean = false, // NEW
    val saveSuccess: Boolean = false // NEW
)

// handles all the logic for the Journal screen.
class JournalViewModel(private val repository: JournalRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState

    // ... (All update functions remain the same)
    fun updateMood(index: Int) {
        _uiState.update { it.copy(selectedMoodIndex = index) }
    }
    fun updateStressLevel(level: Float) {
        _uiState.update { it.copy(stressLevel = level) }
    }
    fun updateSleepHours(hours: String) {
        _uiState.update { it.copy(sleepHours = hours) }
    }
    fun updateWaterGlasses(glasses: String) {
        _uiState.update { it.copy(waterGlasses = glasses) }
    }
    fun updateDietNotes(notes: String) {
        _uiState.update { it.copy(dietNotes = notes) }
    }
    fun updateProductUsed(index: Int, isChecked: Boolean) {
        _uiState.update { currentState ->
            val newProductStates = currentState.productsUsedState.toMutableList()
            newProductStates[index] = isChecked
            currentState.copy(productsUsedState = newProductStates)
        }
    }
    // End of existing update functions

    // takes the form data and save it.
    fun saveDailyLog() {
        _uiState.update { it.copy(isSaving = true, saveSuccess = false) } // Set saving state
        viewModelScope.launch {
            val state = _uiState.value
            val moodOptions = listOf("Happy", "Neutral", "Stressed")

            val usedProducts = state.skincareProducts
                .filterIndexed { index, _ -> state.productsUsedState[index] }

            val newEntry = JournalEntry(
                id = 0L,
                entryDate = LocalDate.now(),
                mood = moodOptions[state.selectedMoodIndex],
                stressLevel = state.stressLevel.toInt(),
                sleepHours = state.sleepHours.toDoubleOrNull() ?: 0.0,
                waterIntake = state.waterGlasses.toDoubleOrNull() ?: 0.0,
                generalNotes = state.dietNotes,
                photoUri = null,
                productsUsed = usedProducts,
                hasPhoto = false
            )

            try {
                repository.saveEntry(newEntry)
                _uiState.update { it.copy(isSaving = false, saveSuccess = true) } // Success!
            } catch (e: Exception) {
                // You would typically log the error here
                _uiState.update { it.copy(isSaving = false, saveSuccess = false) }
            }
        }
    }

    fun saveHandled() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}