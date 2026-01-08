package com.pepsigo.admin.model

data class SalesPurchaseReportUi(
    val id: Int,
    val invoiceNumber: String,
//    val routeAssignmentId: Int?,
    val userId: Int,
    val madeByUserId: Int,
    val saleDate: String,
    val subTotal: Double,
    val discountBeforeTax: Double,
    val tax: Double,
    val discountAfterTax: Double,
    val totalAmount: Double,
    val invoiceStatus: String,
    val name: String,
    val businessName: String
)
