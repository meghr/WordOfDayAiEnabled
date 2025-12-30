package com.attri.WordOfDay.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class PreferencesRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private val CLICK_COUNT_KEY = intPreferencesKey("click_count")
    private val LAST_CLICK_DATE_KEY = stringPreferencesKey("last_click_date")
    private val API_KEY = stringPreferencesKey("gemini_api_key")

    val clickCount: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[CLICK_COUNT_KEY] ?: 0
    }

    val lastClickDate: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[LAST_CLICK_DATE_KEY]
    }
    
    val apiKey: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[API_KEY]
    }

    suspend fun saveApiKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences[API_KEY] = key
        }
    }

    suspend fun incrementClickCount() {
        context.dataStore.edit { preferences ->
            val current = preferences[CLICK_COUNT_KEY] ?: 0
            preferences[CLICK_COUNT_KEY] = current + 1
        }
    }

    suspend fun resetClickCount(date: String) {
        context.dataStore.edit { preferences ->
            preferences[CLICK_COUNT_KEY] = 0
            preferences[LAST_CLICK_DATE_KEY] = date
        }
    }
    
    suspend fun updateLastClickDate(date: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_CLICK_DATE_KEY] = date
        }
    }
}
