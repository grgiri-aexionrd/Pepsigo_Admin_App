package com.pepsigo.admin.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.model.DailyCollectionResponse
import com.pepsigo.admin.repository.DailyCollectionRepo
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DailyCollectionUiState(
    val isLoading: Boolean = false,
    val data: DailyCollectionResponse? = null,
    val isError: Boolean = false,
    val snackbarMessage: String? = null,
    val fromDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),

)

class DailyCollectionViewModel(private val dailyCollectionRepo: DailyCollectionRepo): ViewModel() {
    private val _uiState = MutableStateFlow(DailyCollectionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Fetch for initial selected date (fromDate)
        fetch(_uiState.value.fromDate)
    }

    fun setFromDate(date: String) {
        _uiState.value = _uiState.value.copy(fromDate = date)
        // fetch for the selected from date
        fetch(date)
    }

    fun fetch(date: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            val result = dailyCollectionRepo.getDailyCollection(date)
            result.onSuccess { resp ->
                _uiState.value = _uiState.value.copy(isLoading = false, data = resp, isError = false)
            }
            result.onFailure { err ->
                _uiState.value = _uiState.value.copy(isLoading = false, isError = true, snackbarMessage = (err as AppError).userFriendlyMessage)
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val repo = application.container.dailyCollectionRepo
                DailyCollectionViewModel(repo)
            }
        }
    }
}
