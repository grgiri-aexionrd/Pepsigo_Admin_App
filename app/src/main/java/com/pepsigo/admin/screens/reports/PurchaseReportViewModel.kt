package com.pepsigo.admin.screens.reports

import android.util.Log
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
import kotlinx.coroutines.launch

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
                    dropDown = users
                )
            }
            result.onFailure { error ->
                _uiState.value = _uiState.value.copy(
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

    fun updateSelected(selected: DropDownList?) {
        _uiState.value = _uiState.value.copy(
            selected = selected
        )
    }

    fun fetchPurchaseRegister(dateFrom: String, dateTo: String, vendorId: Int?){
        val current = _uiState.value
        if (dateFrom .isEmpty() || dateTo .isEmpty() || vendorId == null ) {
            _uiState.value = current.copy(
                fromDateError = dateFrom .isEmpty(),
                toDateError = dateTo .isEmpty(),
                dropDownError = vendorId == null,
            )
            return
        }


        // todate and fromdate comparison can be added here

        _uiState.value = current.copy(
            fromDateError = false,
            toDateError = false,
            dropDownError = false,
        )
        val id: Int? = if (vendorId == -1) null else vendorId

        Log.d("PurchaseReportViewModel", "fetchPurchaseRegister: $dateFrom, $dateTo, $id")

        _uiState.value = current.copy(isLoading = true)
        viewModelScope.launch {
            val result = salesPurchaseReportUseCase.fetchPurchaseRegister(dateFrom, dateTo,  id)
            Log.d("PurchaseReportViewModel", "fetchPurchaseRegister result: $result")
            result
                .onSuccess{ purchaseList ->
                    _uiState.value = current.copy(
                        isLoading = false,
                        fromDateError = false,
                        toDateError = false,
                        dropDownError = false,
                        report = purchaseList,
                    )
                }
            result.onFailure { error ->
                _uiState.value = current.copy(
                    isLoading = false,
                    fromDateError = false,
                    toDateError = false,
                    dropDownError = false,
                    isError = true,
                    snackbarMessage = (error as AppError).userFriendlyMessage
                )
            }
        }
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