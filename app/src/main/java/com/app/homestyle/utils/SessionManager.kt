package com.app.homestyle.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para inicializar DataStore
private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class SessionManager(private val context: Context) {

    companion object {
        private val LOGGED_IN_USER_KEY = stringPreferencesKey("logged_in_user")
    }

    // Guardar el usuario logueado
    suspend fun saveUserSession(email: String) {
        context.dataStore.edit { preferences ->
            preferences[LOGGED_IN_USER_KEY] = email
        }
    }

    // Leer el usuario logueado
    val loggedInUser: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LOGGED_IN_USER_KEY]
    }

    // Borrar la sesión del usuario
    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(LOGGED_IN_USER_KEY)
        }
    }
}