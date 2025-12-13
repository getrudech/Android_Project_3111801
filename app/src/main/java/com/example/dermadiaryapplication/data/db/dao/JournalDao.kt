package com.example.dermadiaryapplication.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dermadiaryapplication.data.db.entity.JournalEntry
import kotlinx.coroutines.flow.Flow

//the brain for saving and reading the daily logs
@Dao
interface JournalDao {

    // The Insert function
    @Insert(onConflict = OnConflictStrategy.REPLACE) //if they log twice on the same day, the new entry overwrites the old one
    suspend fun insertEntry(entry: JournalEntry) // We use 'suspend' because saving data takes time, so it must run on a background thread

    // The Query All function
    // Flow is a stream of data. The UI will automatically update whenever the database changes
    // to be used on for the Insights page.
    @Query("SELECT * FROM daily_logs ORDER BY entryDate DESC")
    fun getAllEntries(): Flow<List<JournalEntry>>

    // The Query by Date function
    //to check if the user has already logged today
    @Query("SELECT * FROM daily_logs WHERE entryDate = :date LIMIT 1")
    suspend fun getEntryByDate(date: String): JournalEntry?

    // The Delete function
    @Query("DELETE FROM daily_logs WHERE id = :entryId")
    suspend fun deleteEntry(entryId: Long)

    //query needed for the Insights stats
    //  gets the total count for the Total Entries card on the home screen.
    @Query("SELECT COUNT(id) FROM daily_logs")
    fun getEntryCount(): Flow<Int>
}