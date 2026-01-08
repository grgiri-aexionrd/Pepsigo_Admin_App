package com.pepsigo.admin.repository

import android.util.Log
import com.pepsigo.admin.data.UserPreferenceRepository
import com.pepsigo.admin.model.CheckLoginResponse
import com.pepsigo.admin.model.FCMTokenUpdateRequest
import com.pepsigo.admin.model.FCMTokenUpdateResponse
import com.pepsigo.admin.model.LoginRequest
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError
import okio.IOException
import retrofit2.HttpException

class AuthRepository (
    private val apiService: ApiService,
    private val userPreferenceRepository: UserPreferenceRepository
) {
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            Log.d("LSAuthRepository", "calling login API")
            val response = apiService.login(LoginRequest(email, password))
            Log.d("LSApiService", "API response received: $response")

            val token = response.token

            if (token.isNotEmpty()) {
                userPreferenceRepository.saveToken(token)
                Log.d("LSResponse", "Login successful, token,user details saved")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Invalid response"))
            }

        } catch (e: IOException) {
            Log.d("LSAuthRepository", "Network error during login: ${e.localizedMessage}")
            Result.failure(Exception("Network error: ${e.localizedMessage}"))
        } catch (e: HttpException) {
            Log.d("LSAuthRepository", "HTTP error during login: ${e.code()}")
            Result.failure(Exception("Server error: ${e.code()}"))
        }catch (e: Exception) {
            Log.d("LSAuthRepository", "Unexpected error during login: ${e.localizedMessage}")
            Result.failure(Exception("Unexpected error: ${e.localizedMessage}"))
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
        return wrapError {
            val response = apiService.checkLogin()
            Log.d("LGSAuthRepository", "Check login API response: $response")
            response
        }
    }


    suspend fun logout() {
        userPreferenceRepository.clearToken()
        Log.d("LGSAuthRepository", "User logged out, preferences cleared")
    }

    // collecting the token from DataStore to check login status now done via checkLogin API
//    val isLoggedIn: Flow<Boolean> = userPreferenceRepository.token.map { it.isNotEmpty() }


}