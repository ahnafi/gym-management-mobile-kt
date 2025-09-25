package com.triosalak.gymmanagement.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    // Fungsi untuk menyimpan token
    suspend fun saveAuthToken(token: String?) {
        context.dataStore.edit { preferences ->
            if (token != null) {
                preferences[TOKEN_KEY] = token
            } else {
                preferences.remove(TOKEN_KEY)
            }
        }
    }

    // Flow untuk mendapatkan token (realtime)
    val authToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[TOKEN_KEY]
    }

    // Synchronous method for interceptor to prevent coroutine issues
    fun getAuthTokenSync(): String? {
        return try {
            runBlocking {
                authToken.first()
            }
        } catch (e: Exception) {
            null
        }
    }

    // Fungsi untuk menghapus token saat logout
    suspend fun clearAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
        }
    }
}