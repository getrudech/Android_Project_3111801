package com.example.dermadiaryapplication.data.repository

import com.example.dermadiaryapplication.data.db.dao.JournalDao
import com.example.dermadiaryapplication.data.db.entity.JournalEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

// The interface between the DAO and the ViewModel.
//View Model can only talk to the Repository, not the actual database.
class JournalRepository(private val journalDao: JournalDao) {
    val allEntries: Flow<List<JournalEntry>> = journalDao.getAllEntries()

    // Getting the total count for the Total Entries card on tth dashboard.
    val entryCount: Flow<Int> = journalDao.getEntryCount()

    //  saves a new daily log.
    suspend fun saveEntry(entry: JournalEntry) {
        journalDao.insertEntry(entry)
    }

    // check to see if they already logged data for a specific day.
    suspend fun getEntryForDate(date: LocalDate): JournalEntry? {
        return journalDao.getEntryByDate(date.toString())
    }
    suspend fun deleteEntry(id: Long) {
        journalDao.deleteEntry(id)
    }
}