package com.pepsigo.admin.screens.sales

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.cachedIn
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.repository.SalesRepo
import com.pepsigo.admin.screens.purchase.PurchaseViewModel
import com.pepsigo.admin.utils.AppError
import com.pepsigo.admin.utils.wrapError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SalesViewModel(private val salesRepo: SalesRepo) : ViewModel() {

    private val _uiState = MutableStateFlow<SalesUiState>(SalesUiState.Loading)
    val uiState: StateFlow<SalesUiState> = _uiState.asStateFlow()

    init {
        loadSales()
    }

    fun loadSales() {
        viewModelScope.launch {
            _uiState.value = SalesUiState.Loading

            val result = wrapError {
                salesRepo.getSales()  // Just returns Flow, no network call yet until collected
            }
            result.fold(
                onSuccess = { pagingFlow ->
                    _uiState.value = SalesUiState.Success(
                        sales = pagingFlow.cachedIn(viewModelScope)
                    )
                },
                onFailure = { error ->
                    val appError = when (error) {
                        is AppError -> error
                        else -> AppError.Unknown(error.message ?: "Unknown error", error)
                    }
                    _uiState.value = SalesUiState.Error(appError)
                }
            )
        }
    }

    fun getSaleDetails(item: Any) {
        // TODO: Implement
    }

    fun exitReturnMode() {
        // TODO: Implement
    }

    fun showReturnScreen(sale: Any) {
        // TODO: Implement
    }

    fun toggleItemSelection(item: Any, checked: Boolean) {
        // TODO: Implement
    }

    fun updateQuantity(item: Any, qty: String) {
        // TODO: Implement
    }

    fun showReturnSummary() {
        // TODO: Implement
    }

    fun returnSale(returnItemList: Any, saleId: Any) {
        // TODO: Implement
    }

    fun cancelSale(id: Any) {
        // TODO: Implement
    }

    fun clearSnackbarMessage() {
        // TODO: Implement
    }

    fun retry() {
        loadSales()
    }

//    val selectedItems: Any = TODO()

    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val sales = application.container.salesRepo
                SalesViewModel(sales)
            }
        }
    }
}