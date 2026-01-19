package com.pepsigo.admin.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TokenProvider(userPreferenceRepository: UserPreferenceRepository,
    appScope: CoroutineScope) {
    @Volatile
    private var cachedToken: String? = null

    init {
        // Keep in-memory copy always up-to-date
        appScope.launch {
            userPreferenceRepository.token.collect { value ->
                cachedToken = value.takeIf { it.isNotBlank() }
            }
        }
    }

    fun hasToken(): Boolean = !cachedToken.isNullOrBlank()

    fun getToken(): String? = cachedToken
}