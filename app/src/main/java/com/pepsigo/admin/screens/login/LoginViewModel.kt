package com.pepsigo.admin.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.google.firebase.messaging.FirebaseMessaging
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {
    // ViewModel logic for managing login state and actions
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        // Implement login logic here
        _uiState.value = LoginUiState.Loading
        Log.d("LoginState", "Login called with email,password: $email, $password")

        // Simulate a login process
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Email and password must not be empty")
            return
        }

        viewModelScope.launch {
            try {
                Log.d("LSAttemptLogin"," Attempting login for $email")
                val result = repository.login(email, password)
                Log.d("LSLoginViewModel", "Login result: $result")
                if (result.isSuccess) {
                    // 1️⃣ Fetch FCM token after login
                    val fcmToken = FirebaseMessaging.getInstance().token.await()
                    Log.d("FCM", "Fetched token at login: $fcmToken")

                    // 2️⃣ Send to backend
                    val tokenResult = repository.updateFcmToken(fcmToken)
                    Log.d("LoginVM", "FCM token update result: $tokenResult")

                    // 3️⃣ Optional: if backend fails, still continue login
                    if (tokenResult.isFailure) {
                        Log.e("LoginVM", "Failed to update token on backend")
                    }

                    _uiState.value = LoginUiState.Success // could be token or userId
                } else {
                    _uiState.value = LoginUiState.Error("Invalid credentials")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Something went wrong")
            }

        }
    }


    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val repository = application.container.authRepository
                LoginViewModel(repository)
            }
        }
    }
}
