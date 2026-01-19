package com.pepsigo.admin.screens.splash

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.data.TokenProvider
import com.pepsigo.admin.repository.AuthRepository
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class AuthState {

    /** App launched, auth check in progress */
    object Checking : AuthState()

    /** User is authenticated */
    object Authenticated : AuthState()

    /** User is not authenticated (silent navigation) */
    object Unauthenticated : AuthState()

    /** Session expired or real auth error */
    data class Error(val message: String) : AuthState()
}
class CheckScreenViewModel(
    private val repository: AuthRepository,
    private val tokenProvider: TokenProvider
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Checking)
    val authState: StateFlow<AuthState> = _authState

    init {
        checkLoginStatus()
    }

    fun checkLoginStatus() {
        viewModelScope.launch {

            // 🔹 No token → skip API call (cold start)
            if (!tokenProvider.hasToken()) {
                _authState.value = AuthState.Unauthenticated
                return@launch
            }

            // 🔹 Token exists → verify via API
            val result = repository.checkLogin()

            result
                .onSuccess { response ->
                    if (response.authenticated) {
                        _authState.value = AuthState.Authenticated
                    } else {
                        // Token exists but backend says not authenticated
                        _authState.value = AuthState.Unauthenticated
                    }
                }
                .onFailure { throwable ->
                    val appError = throwable as? AppError

                    _authState.value = AuthState.Error(
                        appError?.userFriendlyMessage ?: "Unable to verify login"
                    )

                }
        }
    }

    /** Called when interceptor forces logout */
    fun onSessionExpired(message: String) {
        _authState.value = AuthState.Error(message)
    }


    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val repository = application.container.authRepository
                val tokenProvider = application.container.tokenProvider
                CheckScreenViewModel(repository, tokenProvider)
            }
        }
    }

}

