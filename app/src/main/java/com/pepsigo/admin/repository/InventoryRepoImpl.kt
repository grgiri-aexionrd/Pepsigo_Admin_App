package com.pepsigo.admin.repository

import android.util.Log
import com.pepsigo.admin.mapper.toDomain
import com.pepsigo.admin.mapper.toUi
import com.pepsigo.admin.model.AddInventoryRequest
import com.pepsigo.admin.model.EditInventoryRequest
import com.pepsigo.admin.model.InventoryItem
import com.pepsigo.admin.model.InventoryItemDetailUi
import com.pepsigo.admin.model.InventoryListUi
import com.pepsigo.admin.model.InventoryResponse
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.utils.wrapError

class InventoryRepoImpl(private val apiService: ApiService) : InventoryRepo {
    override suspend fun fetchInventoryItems(): Result<List<InventoryListUi>> {
        return wrapError {
            val response = apiService.getInventory()
            response.data.map { it.toDomain() }
        }
    }

    override suspend fun fetchInventoryDetails(id: Int): Result<InventoryResponse<InventoryItemDetailUi>> {
        return wrapError {
            Log.d("InventoryRepoImpl", "Fetching inventory details for ID: $id")
            val response = apiService.getInventoryById(id)
            Log.d("InventoryRepoImpl", "Response: $response")
            response.data?.toUi()
            Log.d("InventoryRepoImpl", "Mapped UI Data: ${response.data?.toUi()}")
            InventoryResponse(
                message = response.message,
                data = response.data?.toUi(),
                details = response.details,
                error = response.error
            )
        }
    }

    override suspend fun addInventoryItem(item: AddInventoryRequest): Result<InventoryResponse<InventoryItem>> {
        return wrapError {
            val response = apiService.addInventory(item)
            response
        }
    }

    override suspend fun editInventoryItem(item: InventoryListUi): Result<InventoryResponse<InventoryItem>> {
        return wrapError {
            Log.d("InventoryRepoImpl", "Editing inventory item: $item")
            val response = apiService.updateInventory(item.id, EditInventoryRequest(
                itemName = item.name,
                gstPercent = item.gstPercent
            )
            )
            response
        }

    }

    override suspend fun toggleInventoryItemStatus(id: Int): Result<InventoryResponse<Any>> {
        return wrapError {
            val response = apiService.toggleInventoryStatus(id)
            response
        }
    }
}