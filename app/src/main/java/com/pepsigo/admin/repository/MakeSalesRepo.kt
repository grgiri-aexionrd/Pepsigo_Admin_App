package com.pepsigo.admin.repository

import com.pepsigo.admin.model.InventoryAllBatchesUi
import com.pepsigo.admin.model.InventoryBestBatchUi
import com.pepsigo.admin.model.SaleInventorySearchUi
import com.pepsigo.admin.model.SalesDetailDto
import com.pepsigo.admin.model.SalesReturnResponse
import com.pepsigo.admin.screens.makeSales.CartItem

interface MakeSalesRepo {
    suspend fun searchInventory(customerId: Int, query: String?): Result<SaleInventorySearchUi>
    suspend fun getInventoryBatches(invId: Int, customerId: Int?): Result<InventoryAllBatchesUi>
    suspend fun getInventoryBestBatchForCustomer(invId: Int, customerId: Int?): Result<InventoryBestBatchUi>
    suspend fun createSale(customerId: Int, saleDate: String, cartItems: List<CartItem>): Result<SalesReturnResponse>

}