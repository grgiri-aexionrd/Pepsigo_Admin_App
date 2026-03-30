package com.pepsigo.admin.model

data class PaymentUiModel(
    val id: Int,
    val receivedById: Int,
    val purchaseId: Int?,
    val saleId: Int?,
    val customerId: Int,
    val amount: String,          // formatted as "₹ 1,600.00"
    val paymentMethod: String,
    val refNumber: String,
    val transactionType: String,
    val customer: User,
    val receivedBy: User,
    val sale: SaleDetailUiModel?,
    val purchase: PurchaseSummaryUiModel?, // null for now, will be mapped later
    val denomination: DenominationUiModel?
)

data class SaleDetailUiModel(
    val id: Int,
    val invoiceNumber: String,
    val routeAssignmentId: Int?,
    val customerId: Int,
    val madeByUserId: Int,
    val saleDate: String,        // formatted date e.g. "11 Nov 2025"
    val subTotal: String,        // formatted as "₹ 1,600.00"
    val discountBT: String,
    val taxAmount: String,
    val discountAT: String,
    val totalAmount: String,
    val invoiceStatus: String
)

data class DenominationUiModel(
    val id: Int,
    val paymentId: Int,
    val denom2000: Int,
    val denom500: Int,
    val denom200: Int,
    val denom100: Int,
    val denom50: Int,
    val denom20: Int,
    val denom10: Int,
    val denom5: Int,
    val denom2: Int,
    val denom1: Int,
    val card: Int,
    val upi: Int,
    val netBanking: Int,
    val cheque: Int,
    val credit: Int
)

// Placeholder for later implementation
data class PurchaseSummaryUiModel(
    val id: Int,
    val invoiceNumber: String?,
    val vendorId: Int,
    val purchaseDate: String,
    val subTotal: String,
    val discountBt: String,
    val taxAmount: String,
    val discountAt: String,
    val totalAmount: String,
    val invoiceStatus: String
)
