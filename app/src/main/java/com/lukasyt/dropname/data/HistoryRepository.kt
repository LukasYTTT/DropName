package com.lukasyt.dropname.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.historyDataStore: DataStore<Preferences> by preferencesDataStore(name = "history_settings")

class HistoryRepository(private val context: Context) {
    private val gson = Gson()
    
    companion object {
        val HISTORY_KEY = stringPreferencesKey("contact_history")
    }

    val historyFlow: Flow<List<UserProfile>> = context.historyDataStore.data
        .map { preferences ->
            val json = preferences[HISTORY_KEY]
            if (json != null) {
                try {
                    val type = object : TypeToken<List<UserProfile>>() {}.type
                    gson.fromJson(json, type)
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }

    suspend fun addProfile(profile: UserProfile) {
        context.historyDataStore.edit { preferences ->
            val currentJson = preferences[HISTORY_KEY]
            val currentList = if (currentJson != null) {
                try {
                    val type = object : TypeToken<List<UserProfile>>() {}.type
                    gson.fromJson<List<UserProfile>>(currentJson, type).toMutableList()
                } catch (e: Exception) {
                    mutableListOf<UserProfile>()
                }
            } else {
                mutableListOf()
            }
            
            // Check if already exists (by name, simple check)
            val existingIndex = currentList.indexOfFirst { it.name == profile.name }
            if (existingIndex != -1) {
                currentList[existingIndex] = profile
            } else {
                currentList.add(0, profile) // Add to top
            }
            
            preferences[HISTORY_KEY] = gson.toJson(currentList)
        }
    }

    suspend fun clearHistory() {
        context.historyDataStore.edit { preferences ->
            preferences.remove(HISTORY_KEY)
        }
    }
}
