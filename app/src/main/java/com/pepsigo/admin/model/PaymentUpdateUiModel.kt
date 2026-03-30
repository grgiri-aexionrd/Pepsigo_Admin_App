package com.pepsigo.admin.model

data class PaymentUpdateUiModel(
    val id: Int,
    val receivedById: Int,
    val purchaseId: Int?,
    val saleId: Int?,
    val customerId: Int,
    val amount: String,          // formatted as "₹ 1,600.00"
    val paymentMethod: String,
    val refNumber: String,
    val transactionType: String,
    val customer: User?,
    val denomination: DenominationUiModel?
)
