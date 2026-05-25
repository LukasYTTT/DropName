package com.lukasyt.dropname.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "profile_settings")

class ProfileRepository(private val context: Context) {
    private val gson = Gson()
    private val client = OkHttpClient()
    
    companion object {
        val PROFILE_KEY = stringPreferencesKey("user_profile")
        const val BASE_URL = "https://dropname-api.lukasyt887.workers.dev"
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

    suspend fun uploadProfile(profile: UserProfile): String? = withContext(Dispatchers.IO) {
        try {
            // Remove local ID when uploading to API to ensure a clean request
            val minimalProfile = profile.copy(id = null)
            val json = gson.toJson(minimalProfile)
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = json.toRequestBody(mediaType)
            val request = Request.Builder()
                .url("$BASE_URL/profile")
                .post(requestBody)
                .build()
            
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val respBody = response.body?.string() ?: return@use null
                    val map = gson.fromJson(respBody, Map::class.java)
                    map["id"] as? String
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun fetchProfile(id: String): UserProfile? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$BASE_URL/p/$id")
                .header("Accept", "application/json")
                .build()
            
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val respBody = response.body?.string() ?: return@use null
                    val profile = gson.fromJson(respBody, UserProfile::class.java)
                    profile.copy(id = id) // attach the fetched ID
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
