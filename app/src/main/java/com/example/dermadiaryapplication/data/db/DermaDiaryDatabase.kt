package com.example.dermadiaryapplication.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.dermadiaryapplication.data.db.dao.JournalDao
import com.example.dermadiaryapplication.data.db.entity.Converters
import com.example.dermadiaryapplication.data.db.entity.JournalEntry

// This is the main entry point for talking to the entire database
@Database(
    entities = [JournalEntry::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class DermaDiaryDatabase : RoomDatabase() {

    //how the database gives access to DAO interface
    abstract fun journalDao(): JournalDao
    companion object {
        // way to make sure we only ever create one database instance
        //   The companion object is  a static area where the variable lives.
        @Volatile // To allow var to be instantly updated by different threads
        private var INSTANCE: DermaDiaryDatabase? = null

        fun getDatabase(context: Context): DermaDiaryDatabase {
            // If the instance is already made, just give it back right away
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DermaDiaryDatabase::class.java,
                    "derma_diary_db" // The actual file name of the database
                ).build()
                INSTANCE = instance
                // Return the new instance
                instance
            }
        }
    }
}