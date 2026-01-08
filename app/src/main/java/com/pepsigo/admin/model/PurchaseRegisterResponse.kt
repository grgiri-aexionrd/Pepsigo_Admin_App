package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class PurchaseRegisterResponse(
    val count: Int,
    val data: List<PurchaseReportItem>
)

data class PurchaseReportItem(
    val id:Int,
    @SerializedName("invoice_number")
    val invoiceNumber: String?,
    @SerializedName("route_assignment_id")
    val routeAssignmentId: Int?,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("made_by_user_id")
    val madeByUserId: Int,
    @SerializedName("purchase_date")
    val purchaseDate: String,
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
    val vendor: UserDto
)