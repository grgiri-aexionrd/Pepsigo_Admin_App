package com.pepsigo.admin.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class UserPreferenceRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object{
        val AUTH_TOKEN = stringPreferencesKey("jwt_token")
        val FCM_TOKEN = stringPreferencesKey("fcm_token")

        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveToken(token: String) = withContext(Dispatchers.IO){
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
        Log.d(TAG, "Token saved")
    }

    suspend fun saveFCMToken(token: String) = withContext(Dispatchers.IO){
        dataStore.edit { preferences ->
            preferences[FCM_TOKEN] = token
        }

    }

    val token: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences: ", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[AUTH_TOKEN] ?: ""
        }

    val fcmTokenFlow: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading preferences: ", it)
                        emit(emptyPreferences())
                } else {
                    throw it
                }
            }
            .map { preferences ->
                preferences[FCM_TOKEN] ?: ""
            }

    suspend fun getFCMTokenOnce(): String {
        return fcmTokenFlow.first()
    }


    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN)

        }
    }
}