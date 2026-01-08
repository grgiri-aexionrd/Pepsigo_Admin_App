package com.pepsigo.admin.repository

import com.pepsigo.admin.model.AddInventoryRequest
import com.pepsigo.admin.model.InventoryItem
import com.pepsigo.admin.model.InventoryItemDetailUi
import com.pepsigo.admin.model.InventoryListUi
import com.pepsigo.admin.model.InventoryResponse

interface InventoryRepo {
    suspend fun fetchInventoryItems(): Result<List<InventoryListUi>>
    suspend fun fetchInventoryDetails(id: Int): Result<InventoryResponse<InventoryItemDetailUi>>
    suspend fun addInventoryItem(item: AddInventoryRequest): Result<InventoryResponse<InventoryItem>>
    suspend fun editInventoryItem(item: InventoryListUi): Result<InventoryResponse<InventoryItem>>
    suspend fun toggleInventoryItemStatus(id: Int): Result<InventoryResponse<Any>>
}