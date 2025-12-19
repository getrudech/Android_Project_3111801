package com.example.dermadiaryapplication.data.db.entity

import java.time.LocalDate

data class JournalEntry(
    val id: Long = 0L,
    val entryDate: LocalDate,
    val mood: String,
    val stressLevel: Int,
    val sleepHours: Double,
    val waterIntake: Double,
    val generalNotes: String,
    val photoUri: String? = null,
    val productsUsed: List<String> = emptyList(),
    val hasPhoto: Boolean
)