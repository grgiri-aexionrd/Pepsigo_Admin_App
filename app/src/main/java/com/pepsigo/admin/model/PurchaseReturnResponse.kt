package com.pepsigo.admin.model

import com.google.gson.annotations.SerializedName

data class PurchaseReturnResponse(
    val id: Int,
    @SerializedName("invoice_number")
    val invoiceNumber: String,
    @SerializedName("vendor_id")
    val vendorId: Int,
    @SerializedName("purchase_date")
    val purchaseDate: String,
    @SerializedName("sub_total")
    val subTotal: Double,
    @SerializedName("discount_BT")
    val discountBt: Double,
    @SerializedName("tax_amount")
    val taxAmount: Double,
    @SerializedName("discount_AT")
    val discountAt: Double,
    @SerializedName("total_amount")
    val totalAmount: Double,
    @SerializedName("invoice_status")
    val invoiceStatus: String,
    val items: List<ItemsDetailDto>
)