package com.pepsigo.admin.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.pepsigo.admin.model.SalesDto
import com.pepsigo.admin.model.SalesUiModel
import com.pepsigo.admin.utils.formatExpiryDate
import com.pepsigo.admin.utils.toCurrency

@RequiresApi(Build.VERSION_CODES.O)
fun SalesDto.toUiModel(): SalesUiModel{
    return SalesUiModel(
        id = id,
        invoiceNumber =  invoiceNumber ,
        customer = customer.toDomain(),
        deliveryBoy = deliveryBoy.toDomain(),
        saleDate = formatExpiryDate(saleDate),
        subTotal = subTotal.toCurrency(),
        discountBt = discountBt.toCurrency(),
        taxAmount = taxAmount.toCurrency(),
        discountAt = discountAt.toCurrency(),
        totalAmount = totalAmount.toCurrency(),
        invoiceStatus = invoiceStatus
    )
}