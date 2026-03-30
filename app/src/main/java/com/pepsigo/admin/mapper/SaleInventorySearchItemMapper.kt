package com.pepsigo.admin.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.pepsigo.admin.model.SaleInventorySearchItemDto
import com.pepsigo.admin.model.SaleInventorySearchItemUi
import com.pepsigo.admin.model.SaleInventoryStockSummaryUi
import com.pepsigo.admin.utils.safeDate
import com.pepsigo.admin.utils.safeIntPercent
import com.pepsigo.admin.utils.safeOfferAmount

@RequiresApi(Build.VERSION_CODES.O)
fun SaleInventorySearchItemDto.toUiModel(): SaleInventorySearchItemUi{
    return SaleInventorySearchItemUi(
        id = this.id,
        itemName = this.itemName,
        unit = this.unit,
        gstPercent = this.gstPercent.safeIntPercent(),
        offerPrice = this.offerPrice.safeOfferAmount(),
        isFree = this.isFree,
        stockSummary = SaleInventoryStockSummaryUi(
            totalAvailable = this.stockSummary.totalAvailable,
            batchesCount = this.stockSummary.batchesCount,
            nearestExpiry = this.stockSummary.nearestExpiry.safeDate()
        )
    )
}



