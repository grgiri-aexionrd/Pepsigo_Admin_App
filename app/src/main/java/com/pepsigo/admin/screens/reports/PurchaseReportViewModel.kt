package com.pepsigo.admin.screens.reports

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.domainLayer.SalesPurchaseReportUseCase
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PurchaseReportViewModel( private val salesPurchaseReportUseCase: SalesPurchaseReportUseCase): ViewModel() {

    private val _uiState  = MutableStateFlow(SalesPurchaseReportUiState())
    val purchaseReport = _uiState.asStateFlow()

    init {
        getDropDown()
    }

    fun getDropDown() {
        viewModelScope.launch {
            val result = salesPurchaseReportUseCase.getUsers("vendor")
            Log.d("PurchaseReportViewModel", "getDropDown result: $result")
            result.onSuccess { users ->
                Log.d("PurchaseReportViewModel", "Fetched users: $users")
                _uiState.value = _uiState.value.copy(
                    dropDown = users,
                    isLoading = false,
                    isError = false,
                    snackbarMessage = null,
                    isRefreshing = false
                )
            }
            result.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isRefreshing = false,
                    snackbarMessage = (error as AppError).userFriendlyMessage,
                    isError = true
                )

            }
        }
    }

    fun setFromDate(date: String) {
        _uiState.value = _uiState.value.copy(
            fromDate = date
        )
    }

    fun setToDate(date: String) {
        _uiState.value = _uiState.value.copy(
            toDate = date
        )
    }
    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateSelected(selected: DropDownList?) {
        _uiState.value = _uiState.value.copy(
            selected = selected,
            reportFetched = false
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun fetchPurchaseRegister(dateFrom: String, dateTo: String, vendorId: Int?){
        val current = _uiState.value
        if (dateFrom .isBlank() || dateTo .isBlank() || vendorId == null ) {
            _uiState.value = current.copy(
                DateError = dateFrom .isBlank(),
                dropDownError = vendorId == null,
            )
            return
        }
        // toDate and fromDate comparison can be added here
        val formatter = DateTimeFormatter.ofPattern("yyyy-M-d")
        val isValid = !LocalDate.parse(dateTo, formatter).isBefore(
            LocalDate.parse(dateFrom, formatter)
        )
        if (!isValid) {
            _uiState.value = current.copy(DateError = true)
            return
        }

        _uiState.value = current.copy(
            DateError = false,
            dropDownError = false,
        )
        val id: Int? = if (vendorId == -1) null else vendorId

        _uiState.value = current.copy(isLoading = true)
        viewModelScope.launch {
            val result = salesPurchaseReportUseCase.fetchPurchaseRegister(dateFrom, dateTo,  id)
            Log.d("PurchaseReportViewModel", "fetchPurchaseRegister result: $result")
            result
                .onSuccess{ purchaseList ->
                    _uiState.value = current.copy(
                        isLoading = false,
                        DateError = false,
                        dropDownError = false,
                        report = purchaseList,
                        reportFetched = true,
                        isError = false,
                        snackbarMessage = null
                    )
                }
            result.onFailure { error ->
                _uiState.value = current.copy(
                    isLoading = false,
                    DateError = false,
                    dropDownError = false,
                    reportFetched = false,
                    isError = true,
                    snackbarMessage = (error as AppError).userFriendlyMessage
                )
            }
        }
    }

    fun refresh(){
        _uiState.value = _uiState.value.copy(isRefreshing = true)
        getDropDown()
    }




    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val salesPurchaseReportUseCase = application.container.salesPurchaseReportUseCase
                PurchaseReportViewModel(salesPurchaseReportUseCase)
            }
        }
    }

}