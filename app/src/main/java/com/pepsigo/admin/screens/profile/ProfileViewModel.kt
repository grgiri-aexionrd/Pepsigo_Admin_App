package com.pepsigo.admin.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.model.ProfileUpdateRequest
import com.pepsigo.admin.repository.ProfileRepository
import com.pepsigo.admin.utils.AppError
import com.pepsigo.admin.utils.safeString
import com.pepsigo.admin.utils.toApiNullable
import com.pepsigo.admin.utils.toDoubleApiNullable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Simple UI state holding profile details
sealed class ProfileUiState{
    object Loading : ProfileUiState()
    data class Loaded(val profile: UserProfileUiModel) : ProfileUiState()
    data class EditEmail(
        val email: String,
        val error: String? = null,
        val isError: Boolean = false
    ) : ProfileUiState()

    data class EditProfile(val profile: UserProfileUiModel) : ProfileUiState()
    data class Success(val message: String) : ProfileUiState()
    data class Error(val error: AppError) : ProfileUiState()
    object ChangePassword : ProfileUiState()

}
data class UserProfileUiModel(
    val businessName: String,
    val name: String,
    val mobile: String,
    val email: String,
    val address1: String,
    val address2: String,
    val state: String,
    val pincode: String,
    val latitude: String,
    val longitude: String
)

class ProfileViewModel(private val repository: ProfileRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)

    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        getProfile()
    }

     fun getProfile() {
        Log.d("ProfileViewModel", "Fetching profile data")
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            val result = repository.getProfile()
            Log.d("ProfileViewModel", "Profile fetch result: $result")
            result
                .onSuccess { profile ->
                    val userProfile =  UserProfileUiModel(
                        businessName = profile.business!!,
                        name = profile.name ,
                        mobile = profile.mobile ,
                        email = profile.email ,
                        address1 = profile.address1!!,
                        address2 = profile.address2!!,
                        state = profile.state!!,
                        pincode = profile.pincode!!,
                        latitude = profile.latitude.safeString(),
                        longitude = profile.longitude.safeString()
                    )
                    _uiState.value = ProfileUiState.Loaded(userProfile)
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(error as AppError)
                }
        }

    }
    fun onEditEmailClick(currentEmail: String) {
        _uiState.value = ProfileUiState.EditEmail(currentEmail)
    }

    // Example update functions
    fun updateEmail(newEmail: String, password: String) {
        if (newEmail.isBlank() || password.isBlank() ) {
            _uiState.value = ProfileUiState.EditEmail(
                email = newEmail,
                error = "Email & Password cannot be empty",
                isError = true
            )
            return
        }

        viewModelScope.launch {
            val result = repository.updateEmail(newEmail, password)
            Log.d("ProfileViewModel", "Email update result: $result")
            result
                .onSuccess {response ->
                    _uiState.value = ProfileUiState.Success(response.message)
//                    delay(2000)
                    Log.d("ProfileViewModel", "Email updated successfully: ${response.message}")
                    getProfile() // Refresh profile after update
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(error as AppError)
                    getProfile()
                }
        }
    }

    fun onEditProfileClick(currentProfile: UserProfileUiModel) {
            _uiState.value = ProfileUiState.EditProfile(currentProfile)
    }

    fun updateProfileCoordinates(lat: Double, lng: Double) {
        val current = _uiState.value as? ProfileUiState.EditProfile ?: return

        val updatedProfile = current.profile.copy(
            latitude = lat.toString(),
            longitude = lng.toString()
        )

        _uiState.value = current.copy(profile = updatedProfile)
    }

    fun updateProfile(updatedProfile: UserProfileUiModel) {
        viewModelScope.launch {
            Log.d("ProfileViewModel", "Updating profile with: $updatedProfile")
            val result = repository.updateProfile(
                ProfileUpdateRequest(
                business = updatedProfile.businessName.toApiNullable(),
                name = updatedProfile.name,
                mobile = updatedProfile.mobile,
                address1 = updatedProfile.address1.toApiNullable(),
                address2 = updatedProfile.address2.toApiNullable(),
                state = updatedProfile.state.toApiNullable(),
                pincode = updatedProfile.pincode.toApiNullable(),
                latitude = updatedProfile.latitude.toDoubleApiNullable(),
                longitude = updatedProfile.longitude.toDoubleApiNullable()
            )
            )
            Log.d("ProfileViewModel", "Profile update result: $result")
            result
                .onSuccess { response ->
                    _uiState.value = ProfileUiState.Success(response.message)
                    Log.d("ProfileViewModel", "Profile updated successfully: ${response.message}")
                    getProfile() // Refresh profile after update
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(error as AppError)
                    getProfile()
                }
        }
    }

    fun onChangePasswordClick() {
        _uiState.value = ProfileUiState.ChangePassword

    }

    fun updatePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            val result = repository.updatePassword(currentPassword, newPassword)
            Log.d("ProfileViewModel", "Password update result: $result")
            result.onSuccess { response ->
                _uiState.value = ProfileUiState.Success(response.message)
                getProfile()
            }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(error as AppError)
                    getProfile()
                }

        }
    }

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val repository = application.container.profileRepository
                ProfileViewModel(repository)
            }
        }
    }
}

