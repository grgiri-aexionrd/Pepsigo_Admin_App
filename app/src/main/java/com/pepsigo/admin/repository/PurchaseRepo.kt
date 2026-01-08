package com.pepsigo.admin.repository

import androidx.paging.PagingData
import com.pepsigo.admin.model.CreatePurchaseRequest
import com.pepsigo.admin.model.PurchaseDetailUi
import com.pepsigo.admin.model.PurchaseResponse
import com.pepsigo.admin.model.PurchaseReturnResponse
import com.pepsigo.admin.model.PurchaseUiModel
import com.pepsigo.admin.screens.purchase.ReturnItemList
import kotlinx.coroutines.flow.Flow

interface PurchaseRepo {
    suspend fun getPurchases(): Flow<PagingData<PurchaseUiModel>>
    suspend fun getPurchaseById(id: Int): Result<PurchaseDetailUi>

    suspend fun createPurchase(finalItems: CreatePurchaseRequest): Result<PurchaseResponse<PurchaseReturnResponse>>
    suspend fun cancelPurchase(id: Int): Result<PurchaseResponse<Unit>>
    suspend fun returnPurchase(returnItem: List<ReturnItemList>,id: Int): Result<PurchaseResponse<PurchaseReturnResponse>>
}


