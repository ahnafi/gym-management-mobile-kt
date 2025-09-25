package com.triosalak.gymmanagement.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.triosalak.gymmanagement.data.model.entity.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val CURRENT_USER = stringPreferencesKey("current_user")
    }

    private val gson = Gson()

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

    // Fungsi untuk menyimpan user data sebagai JSON string
    suspend fun saveCurrentUser(currentUser: User?) {
        context.dataStore.edit { preferences ->
            if (currentUser?.id != null) {
                val userJson = gson.toJson(currentUser)
                preferences[CURRENT_USER] = userJson
            } else {
                preferences.remove(CURRENT_USER)
            }
        }
    }

    // Flow untuk mendapatkan current user (realtime)
    val currentUser: Flow<User?> = context.dataStore.data.map { preferences ->
        val userJson = preferences[CURRENT_USER]
        if (userJson != null) {
            try {
                gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    // Synchronous method untuk mendapatkan current user
    fun getCurrentUserSync(): User? {
        return try {
            runBlocking {
                currentUser.first()
            }
        } catch (e: Exception) {
            null
        }
    }

    // Fungsi untuk mendapatkan current user JSON string (jika diperlukan)
    fun getCurrentUserJsonSync(): String? {
        return try {
            runBlocking {
                context.dataStore.data.map { preferences ->
                    preferences[CURRENT_USER]
                }.first()
            }
        } catch (e: Exception) {
            null
        }
    }

    // Fungsi untuk clear semua data saat logout
    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.remove(TOKEN_KEY)
            preferences.remove(CURRENT_USER)
        }
    }

    // Fungsi untuk mengecek apakah user sudah login
    suspend fun isUserLoggedIn(): Boolean {
        return try {
            val token = authToken.first()
            val user = currentUser.first()
            !token.isNullOrEmpty() && user != null
        } catch (e: Exception) {
            false
        }
    }

    // Synchronous version untuk mengecek login status
    fun isUserLoggedInSync(): Boolean {
        return try {
            val token = getAuthTokenSync()
            val user = getCurrentUserSync()
            !token.isNullOrEmpty() && user != null
        } catch (e: Exception) {
            false
        }
    }
}