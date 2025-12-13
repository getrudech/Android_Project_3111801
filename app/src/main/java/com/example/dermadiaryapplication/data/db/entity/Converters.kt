package com.example.dermadiaryapplication.data.db.entity

import androidx.room.TypeConverter

class Converters {

    // Turns a list of products (like Cleanser,Toner) into one big string ("Cleanser,Toner") for the database
    @TypeConverter
    fun fromListString(list: List<String>?): String? {
        // If the list is empty or null, we just save a null
        return list?.joinToString(separator = ",")
    }

    // Turns the big string ("Cleanser,Toner") back into a list when we load the data
    @TypeConverter
    fun toListString(data: String?): List<String> {
        //if the data is null or empty, just return an empty list
        return data?.split(",")?.map { it.trim() } ?: emptyList()
    }
}