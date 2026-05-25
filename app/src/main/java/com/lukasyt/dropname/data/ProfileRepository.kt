package com.lukasyt.dropname.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "profile_settings")

class ProfileRepository(private val context: Context) {
    private val gson = Gson()
    
    companion object {
        val PROFILE_KEY = stringPreferencesKey("user_profile")
    }

    val userProfileFlow: Flow<UserProfile?> = context.dataStore.data
        .map { preferences ->
            val profileJson = preferences[PROFILE_KEY]
            if (profileJson != null) {
                try {
                    gson.fromJson(profileJson, UserProfile::class.java)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }

    suspend fun saveProfile(profile: UserProfile) {
        val profileJson = gson.toJson(profile)
        context.dataStore.edit { preferences ->
            preferences[PROFILE_KEY] = profileJson
        }
    }

    suspend fun clearProfile() {
        context.dataStore.edit { preferences ->
            preferences.remove(PROFILE_KEY)
        }
    }
}
