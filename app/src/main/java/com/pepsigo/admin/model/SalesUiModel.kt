package com.pepsigo.admin.model

data class SalesUiModel(
    val id: Int,
    val invoiceNumber: String,
    val customer: User,
    val deliveryBoy: User,
    val saleDate: String,   // e.g. "11 Nov 2025"
    val subTotal: String,       // e.g. "₹ 1,600.00"
    val discountBt: String,     // e.g. "₹ 0.00"
    val taxAmount: String,      // e.g. "₹ 800.00"
    val discountAt: String,     // e.g. "₹ 0.00"
    val totalAmount: String,    // e.g. "₹ 2,400.00"
    val invoiceStatus: String   // (ready to display or chip)
)
