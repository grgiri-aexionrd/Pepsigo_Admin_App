package com.pepsigo.admin.utils

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.flow.Flow
import androidx.datastore.preferences.core.edit
import com.pepsigo.admin.dataStore
import kotlinx.coroutines.flow.map


object NotificationPermissionManager {

    private val ASKED_KEY = booleanPreferencesKey("asked_notification_permission")

    fun hasAskedPermission(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { prefs ->
            prefs[ASKED_KEY] ?: false   // default = false
        }
    }

    suspend fun setAsked(context: Context, value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[ASKED_KEY] = value
        }
    }
}