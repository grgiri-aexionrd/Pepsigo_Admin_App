package com.pepsigo.admin.screens.splash

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.repository.AuthRepository
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class LogInState {
    object Loading : LogInState()
    object LoggedIn : LogInState()
    data class LoggedOut(
        val msg: String? = "Please Log in"
    ) : LogInState()
}
class CheckScreenViewModel(private val repository: AuthRepository) : ViewModel() {
    private val _isLoggedIn = MutableStateFlow<LogInState>(LogInState.Loading)
    val isLoggedIn: StateFlow<LogInState> = _isLoggedIn.asStateFlow()

    init {
        checkLoginStatus()
    }

    fun checkLoginStatus() {
        viewModelScope.launch {
            val result = repository.checkLogin()
            result.onSuccess{
                if (it.authenticated){
                    _isLoggedIn.value = LogInState.LoggedIn
                } else {
                    _isLoggedIn.value = LogInState.LoggedOut()
                }

            }
                .onFailure { error ->
                    _isLoggedIn.value = LogInState.LoggedOut(
                        msg = (error as AppError).userFriendlyMessage
                    )
                }

        }

    }

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY] as AdminAppApplication)
                val repository = application.container.authRepository
                CheckScreenViewModel(repository)
            }
        }
    }

}

