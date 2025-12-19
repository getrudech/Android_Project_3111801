package com.example.dermadiaryapplication.data.db.entity

data class UserProfile(
    val username: String = "Guest",
    val skinType: String = "",
    val gender: String = "",
    val sleepGoal: Int = 0,
    val waterGoal: Int = 0,
    val preexistingConditions: String = "",
    val productRoutine: String = "",
    val dateCreated: String = System.currentTimeMillis().toString(),
    val hasCompletedOnboarding: Boolean = false
)