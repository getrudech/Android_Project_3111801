package com.example.dermadiaryapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dermadiaryapplication.data.repository.JournalRepository
import com.example.dermadiaryapplication.data.repository.ProfileRepository

// This factory handles the manual injection of repositories into ViewModels
class DermaDiaryViewModelFactory(
    private val journalRepository: JournalRepository,
    private val profileRepository: ProfileRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // Case 1: JournalViewModel
            modelClass.isAssignableFrom(JournalViewModel::class.java) -> {
                JournalViewModel(journalRepository) as T
            }
            // Case 2: OnboardingViewModel (only needs profile)
            modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> {
                OnboardingViewModel(profileRepository) as T
            }
            // Case 3: HomeViewModel (needs both repositories)
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(journalRepository, profileRepository) as T
            }
            // Case 4: AuthViewModel (NEW - only needs profile)
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(profileRepository) as T
            }
            // Add other ViewModels here as they are created
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}