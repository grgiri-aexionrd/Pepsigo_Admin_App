package com.pepsigo.admin.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.pepsigo.admin.model.AmountSummaryUi
import com.pepsigo.admin.model.InventoryUi
import com.pepsigo.admin.model.SaleBatchUi
import com.pepsigo.admin.model.SalesDetailDto
import com.pepsigo.admin.model.SalesDetailUi
import com.pepsigo.admin.model.SalesItemsDetailUi
import com.pepsigo.admin.model.SalesUi
import com.pepsigo.admin.utils.safeAmount
import com.pepsigo.admin.utils.safeDate
import com.pepsigo.admin.utils.safeInt
import com.pepsigo.admin.utils.safePercent
import com.pepsigo.admin.utils.safeText

@RequiresApi(Build.VERSION_CODES.O)
fun SalesDetailDto.toSalesUi(): SalesDetailUi {
    return SalesDetailUi(
        sales = SalesUi(
            salesId = id.safeInt(),
            invoiceNumber = invoiceNumber.safeText(),
            saleDate = saleDate.safeDate(),
            invoiceStatus = invoiceStatus.safeText(),
        ),
        customer = customer.toDomain(),
        amountSummary = AmountSummaryUi(
            subTotal = subTotal.safeAmount(),
            discountBt = discountBt.safeAmount(),
            discountAt = discountAt.safeAmount(),
            taxAmount = taxAmount.safeAmount(),
            totalAmount = totalAmount.safeAmount()
        ),
        salesItems = items.map { item ->
            SalesItemsDetailUi(
                id = item.id.safeInt(),
                saleId = item.saleId.safeInt(),
                inventoryId = item.inventoryId.safeInt(),
                batchNumber = item.batchId.safeInt(),
                itemQuantity = item.itemQuantity.safeInt().toString(),
                unit = item.unit.safeText(),
                gstPercent = item.gstPercent.safeAmount(),
                costPrice = item.costPrice.safeAmount(),
                salePrice = item.salePrice.safeAmount(),
                retailPrice = item.retailPrice.safeAmount(),
                totalAmount = item.totalAmount.safeAmount(),
                inventory = InventoryUi(
                    invId = item.inventory.id.safeInt(),
                    name = item.inventory.name.safeText(),
                    openingQuantity = item.inventory.openingQuantity.safeInt().toString(),
                    unit = item.inventory.unit.safeText(),
                    gstPercent = item.inventory.gstPercent.safePercent(),
                ),
                batch = SaleBatchUi(
                    id = item.batch.id.safeInt(),
                    invoiceNumber = item.batch.invoiceNumber.safeText(),
                    vendorId = item.batch.vendorId.safeInt(),
                    purchaseDate = item.batch.purchaseDate.safeDate(),
                    subTotal = item.batch.subTotal.safeAmount(),
                    discountBt = item.batch.discountBt.safeAmount(),
                    taxAmount = item.batch.taxAmount.safeAmount(),
                    discountAt = item.batch.discountAt.safeAmount(),
                    totalAmount = item.batch.totalAmount.safeAmount(),
                    invoiceStatus = item.batch.invoiceStatus.safeText(),
                )
            )
        }
    )

}