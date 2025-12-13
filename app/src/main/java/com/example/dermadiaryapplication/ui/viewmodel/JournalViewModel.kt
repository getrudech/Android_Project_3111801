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

//holds all the variables for the Journal screen
data class JournalUiState(
    val selectedMoodIndex: Int = 0,
    val stressLevel: Float = 5f,
    val sleepHours: String = "8",
    val waterGlasses: String = "8",
    val dietNotes: String = "",
    val skincareProducts: List<String> = listOf("Facial Cleanser", "Daily Moisturizer", "Vitamin C Serum", "Acne Spot Treatment"),
    val productsUsedState: List<Boolean> = List(4) { false }
)

// handles all the logic  for the Journal screen.
class JournalViewModel(private val repository: JournalRepository) : ViewModel() {
    // A live stream of the current state of the entire form
    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState

    //change the selected mood radio button
    fun updateMood(index: Int) {
        _uiState.update { it.copy(selectedMoodIndex = index) }
    }

    // Updates the stress slider
    fun updateStressLevel(level: Float) {
        _uiState.update { it.copy(stressLevel = level) }
    }

    // Handles text input for sleep
    fun updateSleepHours(hours: String) {
        _uiState.update { it.copy(sleepHours = hours) }
    }

    // Handles text input for water
    fun updateWaterGlasses(glasses: String) {
        _uiState.update { it.copy(waterGlasses = glasses) }
    }

    // Handles text input for diet notes
    fun updateDietNotes(notes: String) {
        _uiState.update { it.copy(dietNotes = notes) }
    }

    // Handles the checklist for skincare products
    fun updateProductUsed(index: Int, isChecked: Boolean) {
        _uiState.update { currentState ->
            val newProductStates = currentState.productsUsedState.toMutableList()
            newProductStates[index] = isChecked
            currentState.copy(productsUsedState = newProductStates)
        }
    }

    // takes the form data and save it.
    fun saveDailyLog() {
        viewModelScope.launch {
            val state = _uiState.value
            val moodOptions = listOf("Happy", "Neutral", "Stressed")

            // Filter down to the products they actually used
            val usedProducts = state.skincareProducts
                .filterIndexed { index, _ -> state.productsUsedState[index] }

            // Create the entry object using the current day's date
            val newEntry = JournalEntry(
                entryDate = LocalDate.now(),
                mood = moodOptions[state.selectedMoodIndex],
                stressLevel = state.stressLevel.toInt(),
                sleepHours = state.sleepHours.toDoubleOrNull() ?: 0.0,
                waterIntake = state.waterGlasses.toDoubleOrNull() ?: 0.0,
                generalNotes = state.dietNotes,
                productsUsed = usedProducts
            )

            repository.saveEntry(newEntry)
        }
    }
}