package com.pepsigo.admin.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.pepsigo.admin.mapper.toDto
import com.pepsigo.admin.mapper.toUiModel
import com.pepsigo.admin.model.CreateSaleRequestDto
import com.pepsigo.admin.model.InventoryAllBatchesUi
import com.pepsigo.admin.model.InventoryBestBatchUi
import com.pepsigo.admin.model.SaleInventorySearchUi
import com.pepsigo.admin.model.SalesDetailDto
import com.pepsigo.admin.model.SalesReturnResponse
import com.pepsigo.admin.network.ApiService
import com.pepsigo.admin.screens.makeSales.CartItem
import com.pepsigo.admin.utils.AppError
import com.pepsigo.admin.utils.wrapError

class MakeSalesRepoImpl(private val apiService: ApiService): MakeSalesRepo  {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun searchInventory(
        customerId: Int,
        query: String?
    ): Result<SaleInventorySearchUi> {
        return wrapError {
            val result = apiService.searchInventory(customerId, query)
            SaleInventorySearchUi(
                count = result.count,
                data = result.data.map { it.toUiModel() }
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getInventoryBatches(
        invId: Int,
        customerId: Int?
    ): Result<InventoryAllBatchesUi> {
        return wrapError {
            val result = apiService.getInventoryBatches(invId, customerId)
            result.toUiModel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getInventoryBestBatchForCustomer(
        invId: Int,
        customerId: Int?
    ): Result<InventoryBestBatchUi> {
        return wrapError {
            val result = apiService.getInventoryBestBatchForCustomer(invId, customerId)
            Log.d("InventoryBestBatchUi", result.toString())
            result.toUiModel()
        }

    }

    override suspend fun createSale(
        customerId: Int,
        saleDate: String,
        cartItems: List<CartItem>
    ): Result<SalesReturnResponse> {
        return wrapError {
            val result = apiService.createSale(
                CreateSaleRequestDto(
                    customerId = customerId,
                    saleDate = saleDate,
                    items = cartItems.map { it.toDto() }
                )
            )
            // If server returned null data unexpectedly
            val data = result.data ?: throw AppError.Server(
                500,
                result.details ?: "Invalid response: data is null"
            )
            result.data
        }
    }


}