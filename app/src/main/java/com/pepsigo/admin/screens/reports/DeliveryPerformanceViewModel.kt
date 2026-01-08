package com.pepsigo.admin.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication

import com.pepsigo.admin.model.DeliveryPerformanceResponse
import com.pepsigo.admin.repository.DailyCollectionRepo
import com.pepsigo.admin.utils.AppError
import com.pepsigo.admin.utils.safeAmount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import java.time.LocalDate
import java.time.format.DateTimeFormatter


data class DeliveryPerformanceUi(
    val isLoading: Boolean = false,
    val data: DeliveryPerformanceResponse? = null,
    val isDateError: Boolean = false,
    val isError: Boolean = false,
    val snackbarMessage: String? = null,
    val fromDate: String = "",
    val toDate: String = "",
    val footerAmount: String = "₹ 0.00"
)

class DeliveryPerformanceViewModel(private val repo: DailyCollectionRepo): ViewModel() {
    private val _uiState = MutableStateFlow(DeliveryPerformanceUi())
    val uiState = _uiState.asStateFlow()

    fun setFromDate(date: String) {
        _uiState.value = _uiState.value.copy(fromDate = date)

    }
    fun setToDate(date: String) {
        _uiState.value = _uiState.value.copy(toDate = date)
    }

    fun fetch(from: String, to: String) {
        val current = _uiState.value
        if (from.isBlank() || to.isBlank() ) {
            //add error
            _uiState.value = current.copy(isDateError = true)
            return
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
        val isValid = !LocalDate.parse(to, formatter).isBefore(
            LocalDate.parse(from, formatter)
        )
        if(!isValid){
            _uiState.value = current.copy(isDateError = true)
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, isDateError = false)
        viewModelScope.launch {
            val result = repo.getDeliveryPerformance(from,to)
            result.onSuccess { resp ->
                val total = resp.data.sumOf { it.salesTotal.toDoubleOrNull() ?: 0.0 }
                _uiState.value = _uiState.value.copy(isLoading = false, data = resp, footerAmount = total.safeAmount())
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
                DeliveryPerformanceViewModel(repo)
            }
        }
    }
}
