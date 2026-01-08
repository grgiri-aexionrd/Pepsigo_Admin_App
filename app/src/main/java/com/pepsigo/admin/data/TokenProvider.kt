package com.pepsigo.admin.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class TokenProvider(userPreferenceRepository: UserPreferenceRepository) {
    @Volatile
    private var cachedToken: String? = null

    init {
        // Keep in-memory copy always up-to-date
        CoroutineScope(Dispatchers.IO).launch {
            userPreferenceRepository.token.collect { value ->
                cachedToken = value
            }
        }
    }

    fun getToken(): String? = cachedToken
}