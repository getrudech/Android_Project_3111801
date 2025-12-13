package com.example.dermadiaryapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dermadiaryapplication.data.repository.JournalRepository
import java.lang.IllegalArgumentException

// a helper class, the factory, because my ViewModel needs the Repository in the constructor.
// Android doesn't know how to pass arguments automatically, so I'm teaching it how.
class DermaDiaryViewModelFactory(private val repository: JournalRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournalViewModel::class.java)) {
            // If the requested ViewModel is the JournalViewModel, I'll build it and pass the repo.
            return JournalViewModel(repository) as T
        }
        // If someone asks for a different ViewModel, I'll let them know it's not here yet.
        throw IllegalArgumentException("Unknown ViewModel class requested")
    }
}