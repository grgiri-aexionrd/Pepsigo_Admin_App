package com.pepsigo.admin.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.pepsigo.admin.model.PurchaseDto
import com.pepsigo.admin.model.PurchaseUiModel
import com.pepsigo.admin.utils.formatExpiryDate
import com.pepsigo.admin.utils.toCurrency

@RequiresApi(Build.VERSION_CODES.O)
fun PurchaseDto.toUiModel(): PurchaseUiModel {
    return PurchaseUiModel(
        id = id,
        invoiceNumber = if (!invoiceNumber.isNullOrBlank() ) invoiceNumber else {"--"},
        vendor = vendor.toDomain(),
        purchaseDate = formatExpiryDate(purchaseDate),
        subTotal = subTotal.toCurrency(),
        discountBt = discountBt.toCurrency(),
        taxAmount = taxAmount.toCurrency(),
        discountAt = discountAt.toCurrency(),
        totalAmount = totalAmount.toCurrency(),
        invoiceStatus = invoiceStatus
    )
}

