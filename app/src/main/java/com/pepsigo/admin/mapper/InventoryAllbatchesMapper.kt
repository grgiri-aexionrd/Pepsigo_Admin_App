package com.pepsigo.admin.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.pepsigo.admin.model.InventoryAllBatchesResponse
import com.pepsigo.admin.model.InventoryAllBatchesUi
import com.pepsigo.admin.model.InventoryBatchDto
import com.pepsigo.admin.model.InventoryBatchUi
import com.pepsigo.admin.utils.safeAmount
import com.pepsigo.admin.utils.safeDate

@RequiresApi(Build.VERSION_CODES.O)
fun InventoryAllBatchesResponse.toUiModel(): InventoryAllBatchesUi {
    return InventoryAllBatchesUi(
        inventoryId = inventoryId,
        itemName = itemName,
        customerId = customerId,
        offerApplied = offerApplied,
        count = count,
        batches = batches.map { it.toUiModel() }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun InventoryBatchDto.toUiModel(): InventoryBatchUi {
    return InventoryBatchUi(
        batchId = batchId,
        expiryDate = expiryDate.safeDate(),
        itemQuantity = itemQuantity,
        soldQuantity = soldQuantity,
        availableQuantity = availableQuantity,
        unit = unit,
        costPrice = costPrice.safeAmount(),
        salePrice = salePrice.safeAmount(),
        retailPrice = retailPrice.safeAmount(),
        purchaseDate = purchaseDate.safeDate(),
        isExpired = isExpired,
        isFreeOffer = isFreeOffer
    )

}



