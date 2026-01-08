package com.pepsigo.admin.screens.reports

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.pepsigo.admin.AdminAppApplication
import com.pepsigo.admin.domainLayer.BatchStockUseCase
import com.pepsigo.admin.model.BatchStockDetail
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BatchSummaryUiState(
    // inventory dropdown
    val inventory: List<DropDownList> = emptyList(),
    val selectedInventory: DropDownList? = null,
    val selectedInventoryError: Boolean = false,

    // batch stock details
    val batchStock: List<BatchStockDetail> = emptyList(),
    val count: Int = 0,
    val totalAvailableQuantity: Int = 0,

    // error handling and refresh
    val hasFetchedDetails: Boolean = false,
    val isRefreshing: Boolean = false,
    val isError: Boolean = false,
    val snackbarMessage: String? = null,
    val isLoading: Boolean = false,

    val filter : BatchFilter = BatchFilter.ALL
)


class BatchSummaryViewModel(
    private val batchStockUseCase: BatchStockUseCase
): ViewModel()
{
    private val _uiState = MutableStateFlow(BatchSummaryUiState())
    val batchStock = _uiState.asStateFlow()

    init{
        getInventory()
    }

    fun getInventory(refresh: Boolean = false){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isRefreshing = refresh,
//                isLoading = true
            )
            val result = batchStockUseCase.getInventory()
            result.onSuccess { inventory ->
                _uiState.value = _uiState.value.copy(
                    inventory = inventory,
                    selectedInventory = null,
                    selectedInventoryError = false,
                    hasFetchedDetails = false,
                    isRefreshing = false,
//                    filter = BatchFilter.ALL
                )
            }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        selectedInventory = null,
                        selectedInventoryError = false,
                        hasFetchedDetails = false,
                        isRefreshing = false,
                        isError = true,
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                    )
                }

        }

    }

    fun refresh()= getInventory(refresh = true)

    fun updateSelectedInventory(selected: DropDownList?) {
        _uiState.value = _uiState.value.copy(
            selectedInventory = selected,
//            hasFetchedDetails = false
        )
    }

    fun updateFilter(filter: BatchFilter) {
        _uiState.value = _uiState.value.copy(
            filter = filter
        )
    }

    fun fetchBatchStockDetails(selectedId: Int?){
        val current = _uiState.value
        _uiState.value = current.copy(isLoading = true)
        if (selectedId == null) {
            _uiState.value = current.copy(
                isLoading = false,
                selectedInventoryError = true,
            )
            return
        }
        _uiState.value = current.copy(
            selectedInventoryError = false,
        )

        val id: Int? = if (selectedId == -1) null else selectedId
        viewModelScope.launch {
            val result = batchStockUseCase.getBatchStock(id)
            Log.d("BatchSummaryViewModel", "fetchBatchStockDetails result: $result")
            result.onSuccess { batchStock ->
                _uiState.value = current.copy(
                    hasFetchedDetails = true,
                    count = batchStock.count,
                    totalAvailableQuantity = batchStock.totalAvailableQuantity,
                    isLoading = false,
                    batchStock = batchStock.data
                )
            }
                .onFailure { error ->
                    _uiState.value = current.copy(
                        count = 0,
                        totalAvailableQuantity = 0,
                        hasFetchedDetails = true,
                        isLoading = false,
                        batchStock = emptyList(),
                        isError = true,
                        snackbarMessage = (error as AppError).userFriendlyMessage,
                    )
                }
        }

    }

    fun clearSnackbar(){
        _uiState.value = _uiState.value.copy(
            snackbarMessage = null,
            isError = false
        )
    }



    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as AdminAppApplication)
                val batchStockUseCase = application.container.batchStockUseCase
                BatchSummaryViewModel(batchStockUseCase)
            }
        }
    }


}