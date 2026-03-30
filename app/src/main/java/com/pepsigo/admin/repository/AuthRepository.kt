package com.pepsigo.admin.repository

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.pepsigo.admin.data.UserPreferenceRepository
import com.pepsigo.admin.model.CheckLoginResponse
import com.pepsigo.admin.model.FCMTokenUpdateRequest
import com.pepsigo.admin.model.FCMTokenUpdateResponse
import com.pepsigo.admin.model.LoginRequest
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException

class AuthRepository (
    private val apiService: ApiService,
    private val userPreferenceRepository: UserPreferenceRepository
) {
    suspend fun login(email: String, password: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            wrapError {
                val response = apiService.login(LoginRequest(email, password))
                val token = response.token

                if (token.isBlank()) throw IllegalStateException("Login Failed")

                userPreferenceRepository.saveToken(token)
                Unit
            }
        }
    }

    suspend fun syncFcmToken() = withContext(Dispatchers.IO) {
        try {
            // Fetch latest token from Firebase
            val newToken = FirebaseMessaging.getInstance().token.await()
            Log.d("FCM", "Fetched token: $newToken")

            // Get stored token
            val savedToken = userPreferenceRepository.getFCMTokenOnce()

            // Compare
            if (savedToken.isBlank() || savedToken != newToken) {

                // Save locally
                userPreferenceRepository.saveFCMToken(newToken)
                Log.d("FCM", "Saved new token locally")

                // Update backend
                val result = updateFcmToken(newToken)

                result.onSuccess {
                    Log.d("FCM", "FCM token updated on backend")
                }.onFailure {
                    Log.e("FCM", "Backend update failed: ${it.message}")
                }
            } else {
                Log.d("FCM", "Token unchanged, skipping update")
            }

        } catch (e: Exception) {
            Log.e("FCM", "Token sync failed: ${e.message}")
        }
    }


    suspend fun updateFcmToken(token: String): Result<FCMTokenUpdateResponse>{
        return wrapError {
            val response = apiService.updateFcmToken(FCMTokenUpdateRequest(token))
            Log.d("LGSAuthRepository", "FCM token update API response: $response")
            response
        }

    }

    suspend fun checkLogin(): Result<CheckLoginResponse> {
        return withContext( Dispatchers.IO) {
            wrapError {
                apiService.checkLogin()
            }
        }
    }

    suspend fun logout() {
        userPreferenceRepository.clearToken()
        Log.d("LGSAuthRepository", "User logged out, preferences cleared")
    }


}