package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class SalesRegisterResponse(
    val count: Int,
    val data: List<SalesReportItem>
)

data class SalesReportItem(
    val id:Int,
    @SerializedName("invoice_number")
    val invoiceNumber: String?,
    @SerializedName("route_assignment_id")
    val routeAssignmentId: Int?,
    @SerializedName("customer_id")
    val customerId: Int,
    @SerializedName("made_by_user_id")
    val madeByUserId: Int,
    @SerializedName("sale_date")
    val saleDate: String,
    @SerializedName("sub_total")
    val subTotal: Double,
    @SerializedName("discount_BT")
    val discountBeforeTax: Double,
    @SerializedName("tax_amount")
    val tax: Double,
    @SerializedName("discount_AT")
    val discountAfterTax: Double,
    @SerializedName("total_amount")
    val total: Double,
    @SerializedName("invoice_status")
    val invoiceStatus: String,
    val customer: UserDto
)
