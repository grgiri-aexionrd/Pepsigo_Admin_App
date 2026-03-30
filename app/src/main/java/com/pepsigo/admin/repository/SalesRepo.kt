package com.pepsigo.admin.repository

import androidx.paging.PagingData
import com.pepsigo.admin.model.SalesDetailUi
import com.pepsigo.admin.model.SalesResponse
import com.pepsigo.admin.model.SalesReturnResponse
import com.pepsigo.admin.model.SalesUiModel
import com.pepsigo.admin.screens.sales.SalesReturnItemList
import kotlinx.coroutines.flow.Flow

interface SalesRepo {
    suspend fun getSales(): Flow<PagingData<SalesUiModel>>
    suspend fun getSalesById(id: Int): Result<SalesDetailUi>
    suspend fun cancelSale(id: Int): Result<SalesResponse<Unit>>
    suspend fun returnSale(returnItem: List<SalesReturnItemList>, id: Int): Result<SalesResponse<SalesReturnResponse>>
}


