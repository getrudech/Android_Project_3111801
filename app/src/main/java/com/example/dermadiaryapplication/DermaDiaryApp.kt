package com.example.dermadiaryapplication

import android.app.Application
import com.example.dermadiaryapplication.data.repository.JournalRepository
import com.example.dermadiaryapplication.data.repository.ProfileRepository

class DermaDiaryApp : Application() {

    // Lazy initialization ensures the repository is created only when first accessed
    val journalRepository: JournalRepository by lazy {
        JournalRepository(applicationContext)
    }

    val profileRepository: ProfileRepository by lazy {
        ProfileRepository(applicationContext)
    }
}