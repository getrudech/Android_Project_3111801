package com.example.dermadiaryapplication.data.repository

import android.content.Context
import com.example.dermadiaryapplication.data.db.entity.JournalEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.time.LocalDate

// The interface between the storage layer and the ViewModel.
//View Model can only talk to the Repository, not the actual database.
class JournalRepository(context: Context) {
    // We use SharedPreferences to store the data
    private val prefs = context.getSharedPreferences("daily_logs_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val entryType = object : TypeToken<JournalEntry>() {}.type // Gson type reference

    // Utility function to get all entries, converting JSON back to objects
    private fun fetchAllEntriesFromPrefs(): List<JournalEntry> {
        return prefs.all.values
            .mapNotNull { it as? String } // Get all stored JSON strings
            .mapNotNull {
                try {
                    gson.fromJson<JournalEntry>(it, entryType)
                } catch (e: Exception) {
                    null // Ignore corrupted entries
                }
            }
            .sortedByDescending { it.entryDate }
    }

    // Coroutine Flow to observe all entries (simulates Room's Flow)
    val allEntries: Flow<List<JournalEntry>> = flow {
        // Since SharedPreferences has no built-in Flow, we emit the list when requested
        emit(fetchAllEntriesFromPrefs())
    }

    // Getting the total count
    val entryCount: Flow<Int> = flow {
        emit(prefs.all.size)
    }


    // Saves a new daily log.
    suspend fun saveEntry(entry: JournalEntry) = withContext(Dispatchers.IO) {
        // Convert Kotlin object to JSON string
        val json = gson.toJson(entry)

        // Use the date as the unique key
        prefs.edit()
            .putString(entry.entryDate.toString(), json)
            .apply()
    }

    // check to see if they already logged data for a specific day.
    suspend fun getEntryForDate(date: LocalDate): JournalEntry? = withContext(Dispatchers.IO) {
        // Retrieve the JSON string using the date as the key
        val json = prefs.getString(date.toString(), null)

        // Convert JSON string back to JournalEntry object
        return@withContext if (json != null) gson.fromJson<JournalEntry>(json, entryType) else null
    }

    // Deletes an entry by date (since date is the key)
    suspend fun deleteEntry(id: Long) {
        // NOTE: Since the key is the date, we would need to look up the date by ID first.
        // For simplicity, we assume the ViewModel handles finding the entry by ID
        // and calling a delete by date, or you'd pass the date here.
        // For now, we will leave this as a placeholder or remove it if not needed in the UI.
        // We will update the function signature to match the previous Room one (deleteEntry(id: Long))
        // Since SharedPreferences doesn't have an ID index, this Room function can't be directly implemented.
        // We will remove it since it relies on the database's primary key index.
    }
}