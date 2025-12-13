package com.example.dermadiaryapplication.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

//defining the table where all the daily skin logs will live
@Entity(tableName = "daily_logs")
data class JournalEntry(
    //Every log needs its own unique ID so we can find it later
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    //makes sure every entry is unique by the date they log it
    val entryDate: LocalDate,

    // The user's mood level
    val mood: String,

    //The stress slider value
    val stressLevel: Int,

    //The number of hours they slept last night
    val sleepHours: Double,

    //How much water they drank today (in glasses)
    val waterIntake: Double,

    //their diet, skin condition, etc.
    val generalNotes: String,

    // The URI of the photo they took
    val photoUri: String? = null,

    // A list of the products they used today.
    val productsUsed: List<String> = emptyList()
)