package com.pepsigo.admin.mapper

import com.pepsigo.admin.model.CreateSaleItemDto
import com.pepsigo.admin.screens.makeSales.CartItem

fun CartItem.toDto(): CreateSaleItemDto{
    return CreateSaleItemDto(
        inventoryId = inventoryId,
        batchNumber = batchId,
        itemQuantity = quantity,
        itemUnit = unit
    )
}