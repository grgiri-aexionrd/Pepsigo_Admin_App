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
import com.pepsigo.admin.model.SalesPurchaseReportUi
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SalesPurchaseReportUiState(
    val fromDate: String="",
    val toDate: String="",
//    val customerId: Int?,
    val selected: DropDownList? = null,
    val dropDown: List<DropDownList> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val fromDateError: Boolean = false,
    val toDateError: Boolean = false,
    val dropDownError: Boolean = false,
    val report: List<SalesPurchaseReportUi> = emptyList(),
    val snackbarMessage: String? = null

)
data class DropDownList(
    val id: Int? = null,
    val name: String
)

class SalesReportViewModel( private val salesPurchaseReportUseCase: SalesPurchaseReportUseCase): ViewModel() {
    private val _uiState  = MutableStateFlow(SalesPurchaseReportUiState())
    val salesReport = _uiState.asStateFlow()

    init {
        getDropDown()
    }

    fun getDropDown() {
        viewModelScope.launch {
            val result = salesPurchaseReportUseCase.getUsers("customer")
            Log.d("SalesRegisterViewModel", "getDropDown result: $result")
            result.onSuccess { users ->
                Log.d("SalesRegisterViewModel", "Fetched users: $users")
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

    fun fetchSalesRegister(dateFrom: String, dateTo: String, customerId: Int?){
        val current = _uiState.value
        if (dateFrom .isEmpty() || dateTo .isEmpty() || customerId == null ) {
            _uiState.value = current.copy(
                fromDateError = dateFrom .isEmpty(),
                toDateError = dateTo .isEmpty(),
                dropDownError = customerId == null,
            )
            return
        }


        // todate and fromdate comparison can be added here

        _uiState.value = current.copy(
            fromDateError = false,
            toDateError = false,
            dropDownError = false,
        )
        val id: Int? = if (customerId == -1) null else customerId

        _uiState.value = current.copy(isLoading = true)
        viewModelScope.launch {
                val result = salesPurchaseReportUseCase.fetchSalesRegister(dateFrom, dateTo,  id)
            Log.d("SalesRegisterViewModel", "fetchSalesRegister result: $result")
            result
                .onSuccess{ salesList ->
                    _uiState.value = current.copy(
                        isLoading = false,
                        fromDateError = false,
                        toDateError = false,
                        dropDownError = false,
                        report = salesList,
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
                SalesReportViewModel(salesPurchaseReportUseCase)
            }
        }
    }

}