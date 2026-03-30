package com.pepsigo.admin.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.pepsigo.admin.model.InventoryBestBatchItemUi
import com.pepsigo.admin.model.InventoryBestBatchResponse
import com.pepsigo.admin.model.InventoryBestBatchUi
import com.pepsigo.admin.utils.safeAmount
import com.pepsigo.admin.utils.safeDate

@RequiresApi(Build.VERSION_CODES.O)
fun InventoryBestBatchResponse.toUiModel(): InventoryBestBatchUi {
    return InventoryBestBatchUi(
        message = message,
        inventoryId = inventoryId,
        itemName = itemName,
        customerId = customerId,
        offerApplied = offerApplied,
        isFreeOffer = isFreeOffer,
        batch = batch?.let {
            InventoryBestBatchItemUi(
                batchId = it.batchId,
                expiryDate = it.expiryDate.safeDate(),
                availableQuantity = it.availableQuantity,
                unit = it.unit,
                costPrice = it.costPrice.safeAmount(),
                salePrice = it.salePrice.safeAmount(),
                retailPrice = it.retailPrice.safeAmount(),
                purchaseDate = it.purchaseDate.safeDate()
            )
        }
    )
}
