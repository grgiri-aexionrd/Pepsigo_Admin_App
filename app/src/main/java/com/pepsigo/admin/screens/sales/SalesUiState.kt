package com.pepsigo.admin.screens.sales

import androidx.paging.PagingData
import com.pepsigo.admin.model.SalesDetailUi
import com.pepsigo.admin.model.SalesReturnResponse
import com.pepsigo.admin.model.SalesUiModel
import com.pepsigo.admin.model.User
import com.pepsigo.admin.utils.AppError
import kotlinx.coroutines.flow.Flow

sealed interface SalesUiState {
    data object Loading : SalesUiState

    data class Success(
        val salesList: Flow<PagingData<SalesUiModel>>
    ) : SalesUiState

    data class Error(val error: AppError) : SalesUiState
}

// Separate state for sales details - independent of list state
data class SalesDetailsState(
    val sale: SalesDetailUi? = null,
    val deliveryExec: User? = null,
    val isReturn: Boolean = false,
    val isReturnSummary: Boolean = false,
    val isReturnSuccess: Boolean = false,
    val returnMessage: String? = null,
    val returnResponse: SalesReturnResponse? = null,
    val returnItemsTotalAmount: Double = 0.0,
    val isPaymentMade: Boolean = false,
    val isLoading: Boolean = false,
    val snackbarMessage: String? = null,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val returnItemList: List<SalesReturnItemList> = emptyList()
)

data class SalesReturnItemList(
    val invId: Int,
    val name: String,
    val quantity: String,
)

// Screen mode to determine what to show
enum class SalesScreenMode {
    LIST,
    DETAILS,
    RETURN,
    RETURN_SUMMARY,
    RETURN_SUCCESS
}