package com.pepsigo.admin.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.pepsigo.admin.model.AmountSummaryUi
import com.pepsigo.admin.model.InventoryUi
import com.pepsigo.admin.model.ItemsDetailUi
import com.pepsigo.admin.model.PurchaseDetailDto
import com.pepsigo.admin.model.PurchaseDetailUi
import com.pepsigo.admin.model.PurchaseUi
import com.pepsigo.admin.utils.safeAmount
import com.pepsigo.admin.utils.safeDate
import com.pepsigo.admin.utils.safeInt
import com.pepsigo.admin.utils.safePercent
import com.pepsigo.admin.utils.safeText

@RequiresApi(Build.VERSION_CODES.O)
fun PurchaseDetailDto.toUi(hasSales: Boolean): PurchaseDetailUi {
    return PurchaseDetailUi(
        purchase = PurchaseUi(
            purchaseId = id.safeInt(),
            invoiceNumber = invoiceNumber.safeText(),
            purchaseDate = purchaseDate.safeDate(),
            invoiceStatus = invoiceStatus.safeText(),
        ),
        vendor = vendor.toDomain(),
        amountSummary = AmountSummaryUi(
            subTotal = subTotal.safeAmount(),
            discountBt = discountBt.safeAmount(),
            discountAt = discountAt.safeAmount(),
            taxAmount = taxAmount.safeAmount(),
            totalAmount = totalAmount.safeAmount(),
        ),
        purchasedItems = items.map{ item ->
            ItemsDetailUi(
                id = item.id.safeInt(),
                purchaseId = item.purchaseId.safeInt(),
                inventoryId = item.inventoryId.safeInt(),
                itemQuantity = item.itemQuantity.safeInt().toString(),
                unit = item.unit.safeText(),
                gstPercent = item.gstPercent.safePercent(),
                costPrice = item.costPrice.safeAmount(),
                salePrice = item.salePrice.safeAmount(),
                retailPrice = item.retailPrice.safeAmount(),
                totalAmount = item.totalAmount.safeAmount(),
                expiryDate = item.expiryDate.safeDate(),
                inventory = InventoryUi(
                    invId = item.inventory.id.safeInt(),
                    name = item.inventory.name.safeText(),
                    openingQuantity = item.inventory.openingQuantity.safeInt().toString(),
                    unit= item.inventory.unit.safeText(),
                    gstPercent = item.inventory.gstPercent.safePercent(),
                )
            )
        },
        hasSales = hasSales
    )

}