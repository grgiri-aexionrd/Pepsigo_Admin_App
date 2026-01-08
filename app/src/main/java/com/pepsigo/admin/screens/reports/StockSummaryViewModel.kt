package com.pepsigo.admin.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.model.StockSummaryData
import com.pepsigo.admin.repository.StockSummaryRepo
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StockSummaryUiState(
    val stockSummary: List<StockSummaryData> = emptyList(),
    val filterStock: List<String> = emptyList(),
    val selectedFilter: String? = null,
    val isRefreshing: Boolean = false,

    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val snackbarMessage: String? = null,
    val emptyCardMessage: String? = null
)
class StockSummaryViewModel( private val stockRepo: StockSummaryRepo): ViewModel() {

    private val _uiState = MutableStateFlow(StockSummaryUiState())
    val stockSummary = _uiState.asStateFlow()

    init {
        getStockSummary()
    }

    fun getStockSummary(refresh: Boolean = false){
        _uiState.value = _uiState.value.copy(
            isRefreshing = refresh,
            isLoading = true
        )
        viewModelScope.launch {
            val result = stockRepo.getStockSummary()
            result.onSuccess { stockSummary ->
                val products = stockSummary.map { it.itemName.substringBefore(" ") }.distinct().sorted()
                _uiState.update {
                    it.copy(
                        stockSummary = stockSummary,
                        filterStock = products,
                        isRefreshing = false,
                        isLoading = false,
                        isError = false,
                        snackbarMessage = null,
                        emptyCardMessage = null
                    )
                }
            }
                .onFailure { error ->
                    val message = (error as AppError).userFriendlyMessage
                    _uiState.update {
                        it.copy(
                            isRefreshing = false,
                            isLoading = false,
                            isError = true,
                            snackbarMessage = message,
                            emptyCardMessage = message
                        )
                    }
                }
        }

    }

    fun refresh() = getStockSummary(refresh = true)

    fun onProductSelected(product: String?) {
        _uiState.update {
            it.copy(selectedFilter = product)
        }
    }

    fun clearSnackbar(){
        _uiState.update {
            it.copy(
                snackbarMessage = null,
                isError = false
            )
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val stockRepo = application.container.stockSummaryRepo
                StockSummaryViewModel(stockRepo)
            }
        }
    }
}