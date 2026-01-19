package com.pepsigo.admin.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.pepsigo.admin.model.BatchUi
import com.pepsigo.admin.model.InventoryItemDetailDto
import com.pepsigo.admin.model.InventoryItemDetailUi
import com.pepsigo.admin.model.InventoryListUi
import com.pepsigo.admin.model.OfferDetailUi
import com.pepsigo.admin.model.StockSummaryUi
import com.pepsigo.admin.utils.safeDate

@RequiresApi(Build.VERSION_CODES.O)
fun InventoryItemDetailDto.toUi(): InventoryItemDetailUi {
    return InventoryItemDetailUi(
        itemDetail = InventoryListUi(
            id = id,
            name = itemName,
            unit = unit,
            gstPercent = gstPercent,
            enabled = enabled
        ),
        offer = offerStatus,
        offerDetail = offerDetails?.let {
            OfferDetailUi(
                salePrice = it.salePrice,
                quantity = it.quantity,
                customerId = it.customerId
            )
        },
        stockSummary = StockSummaryUi(
            totalAvailable = stockSummary.totalAvailable,
            batchesCount = stockSummary.batchesCount,
            nearestExpiry = stockSummary.expiryDate.safeDate()
        ),
        batches = batches.map { batch ->
            BatchUi(
                id = batch.id,
                expiryDate = batch.expiryDate.safeDate(),
                purchasedQuantity = batch.purchasedQuantity,
                soldQuantity = batch.soldQuantity,
                availableQuantity = batch.availableQuantity,
                unit = batch.unit,
                costPrice = batch.costPrice,
                salePrice = batch.salePrice,
                retailPrice = batch.retailPrice,
                purchasedDate = batch.purchasedDate,
                expired = batch.expired
            )
        }
    )
}
