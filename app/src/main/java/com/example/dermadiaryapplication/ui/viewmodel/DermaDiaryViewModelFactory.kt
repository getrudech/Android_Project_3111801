package com.example.dermadiaryapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dermadiaryapplication.data.repository.JournalRepository
import com.example.dermadiaryapplication.data.repository.ProfileRepository

// This factory handles the manual injection of repositories into ViewModels
class DermaDiaryViewModelFactory(
    private val journalRepository: JournalRepository,
    private val profileRepository: ProfileRepository // MUST be present
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(JournalViewModel::class.java) -> {
                JournalViewModel(journalRepository) as T
            }
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                OnboardingViewModel(profileRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(journalRepository, profileRepository) as T
            }
            // Add other ViewModels here as they are created
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}