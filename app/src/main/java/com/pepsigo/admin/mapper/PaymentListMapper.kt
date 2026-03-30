package com.pepsigo.admin.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.pepsigo.admin.model.DenominationDto
import com.pepsigo.admin.model.DenominationUiModel
import com.pepsigo.admin.model.PaymentDto
import com.pepsigo.admin.model.PaymentUiModel
import com.pepsigo.admin.model.PaymentUpdateDto
import com.pepsigo.admin.model.PaymentUpdateUiModel
import com.pepsigo.admin.model.SaleDetailDto
import com.pepsigo.admin.model.SaleDetailUiModel
import com.pepsigo.admin.utils.formatExpiryDate
import com.pepsigo.admin.utils.toCurrency

@RequiresApi(Build.VERSION_CODES.O)
fun PaymentDto.toUiModel(): PaymentUiModel {
    return PaymentUiModel(
        id = id,
        receivedById = receivedById,
        purchaseId = purchaseId,
        saleId = saleId,
        customerId = customerId,
        amount = amount.toCurrency(),
        paymentMethod = paymentMethod,
        refNumber = refNumber ?: "—",
        transactionType = transactionType,
        customer = customer.toDomain(),
        receivedBy = receivedBy.toDomain(),
        sale = sale?.toUiModel(),
        purchase = null, // will be mapped later
        denomination = denomination?.toUiModel()
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun SaleDetailDto.toUiModel(): SaleDetailUiModel {
    return SaleDetailUiModel(
        id = id,
        invoiceNumber = invoiceNumber,
        routeAssignmentId = routeAssignmentId,
        customerId = customerId,
        madeByUserId = madeByUserId,
        saleDate = formatExpiryDate(saleDate),
        subTotal = subTotal.toCurrency(),
        discountBT = discountBT.toCurrency(),
        taxAmount = taxAmount.toCurrency(),
        discountAT = discountAT.toCurrency(),
        totalAmount = totalAmount.toCurrency(),
        invoiceStatus = invoiceStatus
    )
}

fun DenominationDto.toUiModel(): DenominationUiModel {
    return DenominationUiModel(
        id = id,
        paymentId = paymentId,
        denom2000 = denom2000,
        denom500 = denom500,
        denom200 = denom200,
        denom100 = denom100,
        denom50 = denom50,
        denom20 = denom20,
        denom10 = denom10,
        denom5 = denom5,
        denom2 = denom2,
        denom1 = denom1,
        card = card,
        upi = upi,
        netBanking = netBanking,
        cheque = cheque,
        credit = credit
    )
}

fun PaymentUpdateDto.toUiModel(): PaymentUpdateUiModel{
    return PaymentUpdateUiModel(
        id = id,
        receivedById = receivedById,
        purchaseId = purchaseId,
        saleId = saleId,
        customerId = customerId,
        amount = amount.toCurrency(),
        paymentMethod = paymentMethod,
        refNumber = refNumber ?: "—",
        transactionType = transactionType,
        customer = customer?.toDomain(),
        denomination = denomination?.toUiModel()
    )

}
