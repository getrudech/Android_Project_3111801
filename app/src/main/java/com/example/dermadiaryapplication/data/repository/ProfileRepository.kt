package com.example.dermadiaryapplication.data.repository

import android.content.Context
import com.example.dermadiaryapplication.data.db.entity.UserProfile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository to manage the single UserProfile object stored locally
 * using SharedPreferences and Gson.
 */
class ProfileRepository(private val context: Context) {

    // We use a separate SharedPreferences file for the user profile
    private val prefs = context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val profileKey = "USER_PROFILE_DATA" // Unique key to store the profile JSON
    private val profileType = object : TypeToken<UserProfile>() {}.type // Gson type reference

    /**
     * Saves the entire UserProfile object (overwriting any previous one).
     */
    suspend fun saveProfile(profile: UserProfile) = withContext(Dispatchers.IO) {
        // Convert Kotlin object to JSON string
        val json = gson.toJson(profile)

        prefs.edit()
            .putString(profileKey, json)
            .apply()
    }

    /**
     * Loads the UserProfile object. Returns null if no profile exists or if the JSON is corrupted.
     */
    suspend fun loadProfile(): UserProfile? = withContext(Dispatchers.IO) {
        val json = prefs.getString(profileKey, null)

        return@withContext if (json != null) {
            try {
                // If JSON is found, try to deserialize it
                gson.fromJson<UserProfile>(json, profileType)
            } catch (e: Exception) {
                // Return null if deserialization fails (e.g., corrupted data)
                null
            }
        } else {
            // No profile data found
            null
        }
    }


    /**
     * Clears the UserProfile data from SharedPreferences. Used for signing out.
     */
    fun clearProfile() {
        val profilePrefs = context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)
        profilePrefs.edit().clear().apply()

        // 3. Wiping the journal logs so the next user starts at 0
        val journalPrefs = context.getSharedPreferences("daily_logs_prefs", Context.MODE_PRIVATE)
        journalPrefs.edit().clear().apply()
    }
}