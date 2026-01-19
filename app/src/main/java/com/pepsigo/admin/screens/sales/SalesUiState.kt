package com.pepsigo.admin.screens.sales

import androidx.paging.PagingData
import com.pepsigo.admin.model.SalesUiModel
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.Flow

sealed interface SalesUiState {
    data object Loading : SalesUiState
    data class Success(
        val sales: Flow<PagingData<SalesUiModel>>
    ) : SalesUiState

    data class Error(val error: AppError) : SalesUiState

    data class SalesDetails(
//        val sale: SalesDetailsModel,
        val isReturn: Boolean = false,
        val isReturnSummary: Boolean = false,
        val isLoading: Boolean = false,
        val snackbarMessage: String? = null,
        val isError: Boolean = false,
//        val returnItemList: List<SalesItem> = emptyList(),
        val errorMessage: String? = null
    ) : SalesUiState
}