package com.pepsigo.admin.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.model.PaymentSummaryResponse
import com.pepsigo.admin.repository.DailyCollectionRepo
import com.pepsigo.admin.utils.AppError
import com.pepsigo.admin.utils.safeAmount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class PaymentSummaryUi(
    val isLoading: Boolean = false,
    val data: PaymentSummaryResponse? = null,
    val isDateError: Boolean = false,
    val isError: Boolean = false,
    val snackbarMessage: String? = null,
    val fromDate: String = "",
    val toDate: String = "",
    val footerAmount: String = "₹ 0.00"
)

class PaymentSummaryViewModel(private val repo: DailyCollectionRepo): ViewModel(){
    private val _uiState = MutableStateFlow(PaymentSummaryUi())
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

        _uiState.value = current.copy(isLoading = true, isDateError = false)
        viewModelScope.launch {
            val result = repo.getPaymentSummary(from, to)
            result.onSuccess { resp ->
                val totalAmount =
                    resp.data.sumOf { it.totalAmount.toDouble() }
                _uiState.value = current.copy(isLoading = false, isDateError = false, data = resp,isError = false, footerAmount =  totalAmount.safeAmount())
            }
            result.onFailure { err ->
                _uiState.value = current.copy(isLoading = false, isDateError = false,isError = true, snackbarMessage = (err as AppError).userFriendlyMessage)
            }
        }

    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val repo = application.container.dailyCollectionRepo
                PaymentSummaryViewModel(repo)
            }
        }

    }




}