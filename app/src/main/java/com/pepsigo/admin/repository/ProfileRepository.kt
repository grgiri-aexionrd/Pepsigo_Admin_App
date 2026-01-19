package com.pepsigo.admin.repository

import android.util.Log
import com.pepsigo.admin.model.ProfileRequest
import com.pepsigo.admin.model.EmailUpdateRequest
import com.pepsigo.admin.model.PasswordUpdateRequest
import com.pepsigo.admin.model.ProfileEmailPasswordUpdateResponse
import com.pepsigo.admin.model.ProfileUpdateRequest
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.AppError
import com.pepsigo.admin.utils.safeString
import com.pepsigo.admin.utils.safeText
import retrofit2.HttpException
import java.io.IOException

class ProfileRepository( private val apiService: ApiService) {

    suspend fun getProfile() : Result<ProfileRequest> {
       return try {
           val response = apiService.getProfile()
           Log.d("ProfileRepository", "API response: $response")
           val profile = ProfileRequest(
               id = response.id,
               name = response.name,
               email = response.email,
               mobile = response.mobile,
               role = response.role,
               business = response.business.safeText(),
               address1 = response.address1.safeText(),
               address2 = response.address2.safeText(),
               state = response.state.safeText(),
               pincode = response.pincode.safeText(),
               latitude = response.latitude,
               longitude = response.longitude,
               enabled = response.enabled,
               fullAddress = response.fullAddress
           )
           Result.success(profile)
       } catch (e: IOException) {
           Result.failure(AppError.Network("No internet connection"))
       } catch (e: HttpException) {
           Result.failure(AppError.Server(e.code(), e.message ?: "Server error"))
       } catch (e: Exception) {
           Result.failure(AppError.Unknown("Unexpected error", e))
       }

    }

    suspend fun updateEmail(newEmail:String,password: String) : Result<ProfileEmailPasswordUpdateResponse> {
        return try {
            val response = apiService.updateEmail(EmailUpdateRequest(newEmail, password))
            Log.d("ProfileRepository", "Update Email API response: $response")
            Result.success(response)
        } catch (e: IOException) {
            Result.failure(AppError.Network("No internet connection"))
        } catch (e: HttpException) {
            Result.failure(AppError.Server(e.code(), e.message ?: "Server error"))
        } catch (e: Exception) {
            Result.failure(AppError.Unknown("Unexpected error", e))
        }
    }

    suspend fun updateProfile(updatedProfile: ProfileUpdateRequest) : Result<ProfileEmailPasswordUpdateResponse> {
        return try {
            val response = apiService.updateProfile(updatedProfile)
            Log.d("ProfileRepository", "Update Profile API response: $response")
            Result.success(response)
        } catch (e: IOException) {
            Result.failure(AppError.Network("No internet connection"))
        } catch (e: HttpException) {
            Result.failure(AppError.Server(e.code(), e.message ?: "Server error"))
        } catch (e: Exception) {
            Result.failure(AppError.Unknown("Unexpected error", e))
        }
    }

    suspend fun updatePassword(currentPassword: String, newPassword: String) : Result<ProfileEmailPasswordUpdateResponse> {
        return try {
            val response = apiService.updatePassword(
                PasswordUpdateRequest(
                    currentPassword,
                    newPassword
                )
            )
            Log.d("ProfileRepository", "Update Password API response: $response")
            Result.success(response)
        } catch (e: IOException) {
            Result.failure(AppError.Network("No internet connection"))
        } catch (e: HttpException) {
            Result.failure(AppError.Server(e.code(), e.message ?: "Server error"))
        } catch (e: Exception) {
            Result.failure(AppError.Unknown("Unexpected error", e))
        }
    }
}
