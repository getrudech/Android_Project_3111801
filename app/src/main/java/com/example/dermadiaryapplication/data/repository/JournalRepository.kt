package com.example.dermadiaryapplication.data.repository

import android.content.Context
import com.example.dermadiaryapplication.data.db.entity.JournalEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map // <-- NEW IMPORT
import kotlinx.coroutines.flow.onStart // <-- CORRECT IMPORT
import kotlinx.coroutines.withContext
import java.time.LocalDate

// The interface between the storage layer and the ViewModel.
class JournalRepository(context: Context) {
    private val prefs = context.getSharedPreferences("daily_logs_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val entryType = object : TypeToken<JournalEntry>() {}.type

    // Trigger to signal that the underlying data in SharedPreferences has changed
    private val dataChangedFlow = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    // Utility function to get all entries, converting JSON back to objects
    private fun fetchAllEntriesFromPrefs(): List<JournalEntry> {
        return prefs.all.values
            .mapNotNull { it as? String }
            .mapNotNull {
                try {
                    gson.fromJson<JournalEntry>(it, entryType)
                } catch (e: Exception) {
                    null // Ignore corrupted entries
                }
            }
            .sortedByDescending { it.entryDate }
    }

    // Coroutine Flow to observe all entries (FIXED REACTIVE IMPLEMENTATION)
    val allEntries: Flow<List<JournalEntry>> = dataChangedFlow
        .onStart { emit(Unit) } // Emit Unit once on start to trigger initial load
        .map { fetchAllEntriesFromPrefs() } // Map the trigger event to the actual data fetch


    // Getting the total count (FIXED REACTIVE IMPLEMENTATION)
    val entryCount: Flow<Int> = dataChangedFlow
        .onStart { emit(Unit) } // Emit Unit once on start to trigger initial load
        .map { prefs.all.size } // Map the trigger event to the total size


    // Saves a new daily log.
    suspend fun saveEntry(entry: JournalEntry) = withContext(Dispatchers.IO) {
        val json = gson.toJson(entry)

        prefs.edit()
            .putString(entry.entryDate.toString(), json)
            .apply()

        // KEY FIX: Signal that data has changed
        dataChangedFlow.emit(Unit)
    }

    // check to see if they already logged data for a specific day.
    suspend fun getEntryForDate(date: LocalDate): JournalEntry? = withContext(Dispatchers.IO) {
        val json = prefs.getString(date.toString(), null)
        return@withContext if (json != null) gson.fromJson<JournalEntry>(json, entryType) else null
    }

    /**
     * Logs the completion of a photo upload for the current date.
     */
    suspend fun logPhotoEntry(date: LocalDate) = withContext(Dispatchers.IO) {
        val existingEntry = getEntryForDate(date)

        val minimalEntry = JournalEntry(
            id = 0L, entryDate = date, mood = "", stressLevel = 0, sleepHours = 0.0,
            waterIntake = 0.0, generalNotes = "", photoUri = null, productsUsed = emptyList(), hasPhoto = true
        )

        if (existingEntry != null) {
            // Entry already exists, update the photo status
            val updatedEntry = existingEntry.copy(hasPhoto = true)
            // saveEntry handles the SharedPreferences update AND the dataChangedFlow.emit(Unit)
            saveEntry(updatedEntry)
        } else {
            // No entry exists for today, create the minimal entry
            saveEntry(minimalEntry)
        }
        // No need to emit Unit here as saveEntry already does it.
    }
}